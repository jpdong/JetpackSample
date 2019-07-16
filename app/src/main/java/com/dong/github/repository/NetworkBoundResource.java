package com.dong.github.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.dong.github.AppExecutors;
import com.dong.github.api.ApiResponse;
import com.dong.github.vo.Resource;

/**
 * Created by dongjiangpeng on 2019/7/13 0013.
 */
public abstract class NetworkBoundResource<ResultType,RequestType> {

    private static final String TAG = "dong";

    private MediatorLiveData<Resource<ResultType>> mResult = new MediatorLiveData<Resource<ResultType>>();
    private AppExecutors mAppExecutors;
    private LiveData<ResultType> mDbSource;

    public NetworkBoundResource(AppExecutors appExecutors) {
        Log.d(TAG, String.format("NetworkBoundResource/NetworkBoundResource:thread(%s)",Thread.currentThread().getName()));
        this.mAppExecutors = appExecutors;
        mResult.setValue(new Resource.Loading(null));
        mDbSource = loadFromDb();
        mResult.addSource(mDbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType data) {
                Log.d(TAG, String.format("NetworkBoundResource/onChanged:thread(%s)",Thread.currentThread().getName()));
                mResult.removeSource(mDbSource);
                if (shouldFetch(data)) {
                    fetchFromNetwork(mDbSource);
                } else {
                    mResult.addSource(mDbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(ResultType data) {
                            Log.d(TAG, String.format("NetworkBoundResource/onChanged:thread(%s)",Thread.currentThread().getName()));
                            mResult.setValue(new Resource.Success<>(data));
                        }
                    });
                }
            }
        });
        mDbSource = loadFromDb();
    }

    private void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        mResult.addSource(dbSource, new Observer<ResultType>() {
            @Override
            public void onChanged(ResultType data) {
                mResult.setValue(new Resource.Loading<>(data));
            }
        });
        mResult.addSource(apiResponse, new Observer<ApiResponse>() {
            @Override
            public void onChanged(ApiResponse response) {
                Log.d(TAG, String.format("NetworkBoundResource/onChanged:thread(%s)",Thread.currentThread().getName()));
                mResult.removeSource(apiResponse);
                mResult.removeSource(dbSource);
                if (response instanceof ApiResponse.Success) {
                    mAppExecutors.mDiskIO.execute(new Runnable() {
                        @Override
                        public void run() {
                            saveCallResult(processResponse((ApiResponse.Success<RequestType>) response));
                            mAppExecutors.mMainThread.execute(new Runnable() {
                                @Override
                                public void run() {
                                    mResult.addSource(loadFromDb(), new Observer<ResultType>() {
                                        @Override
                                        public void onChanged(ResultType data) {
                                            mResult.setValue(new Resource.Success<>(data));
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else if (response instanceof ApiResponse.Empty) {
                    mAppExecutors.mMainThread.execute(new Runnable() {
                        @Override
                        public void run() {
                            mResult.addSource(loadFromDb(), new Observer<ResultType>() {
                                @Override
                                public void onChanged(ResultType data) {
                                    mResult.setValue(new Resource.Success<>(data));
                                }
                            });
                        }
                    });
                } else if (response instanceof ApiResponse.Error) {
                    onFetchFailed();
                    mResult.addSource(mDbSource, new Observer<ResultType>() {
                        @Override
                        public void onChanged(ResultType data) {
                            mResult.setValue(new Resource.Error<>(data,((ApiResponse.Error) response).errorMessage));
                        }
                    });
                }
            }
        });
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return mResult;
    }

    protected abstract void onFetchFailed();

    protected abstract void saveCallResult(RequestType item);

    private RequestType processResponse(ApiResponse.Success<RequestType> response) {
        return response.body;
    }


    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    protected abstract boolean shouldFetch(ResultType data);

    protected abstract LiveData<ResultType> loadFromDb();


}
