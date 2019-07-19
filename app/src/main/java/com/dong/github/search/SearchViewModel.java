package com.dong.github.search;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.dong.github.repository.RepoRepository;
import com.dong.github.util.AbsentLiveData;
import com.dong.github.vo.Repo;
import com.dong.github.vo.Resource;

import java.util.List;
import java.util.Locale;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
public class SearchViewModel extends ViewModel {

    private static final String TAG = "dong";

    private RepoRepository mRepository;
    private NextPageHandler mNextPageHandler;
    public MutableLiveData<String> mQueryInternal = new MutableLiveData<String>();
    public LiveData<String> mQuery = mQueryInternal;

    public LiveData<Resource<List<Repo>>> mResults = Transformations.switchMap(mQueryInternal, new Function<String, LiveData<Resource<List<Repo>>>>() {
        @Override
        public LiveData<Resource<List<Repo>>> apply(String input) {
            Log.d(TAG, String.format("SearchViewModel/apply:thread(%s) input(%s)",Thread.currentThread().getName(),input));
            if (TextUtils.isEmpty(input)) {
                return new AbsentLiveData<>();
            } else {
                return mRepository.search(input);
            }
        }
    });

    public LiveData<LoadMoreState> mLoadMoreStateLiveData;


    public SearchViewModel() {

    }

    public void setRepository(RepoRepository repository) {
        this.mRepository = repository;
        mNextPageHandler = new NextPageHandler(mRepository);
    }




    public void loadNextPage() {
        Log.d(TAG, String.format("SearchViewModel/loadNextPage:thread(%s)",Thread.currentThread().getName()));
        String queryString = mQueryInternal.getValue();
        if (!TextUtils.isEmpty(queryString)) {
            mNextPageHandler.queryNextPage(queryString);
        }

    }

    public void setQuery(String query) {
        Log.d(TAG, String.format("SearchViewModel/setQuery:thread(%s)",Thread.currentThread().getName()));
        String input = query.toLowerCase(Locale.getDefault()).trim();
        if (input.equals(mQueryInternal.getValue())) {
            return;
        }
        mNextPageHandler.reset();
        mQueryInternal.setValue(input);
    }

    public LiveData<LoadMoreState> getLoadMoreStateLiveData() {
        return mNextPageHandler.loadMoreState;
    }

    static class NextPageHandler implements Observer<Resource<Boolean>> {

        private LiveData<Resource<Boolean>> nextPageLiveData;
        private MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
        private String query;
        private boolean hasMore = false;
        private RepoRepository repository;

        public NextPageHandler(RepoRepository repository) {
            this.repository = repository;
            reset();
        }

        private void reset() {
            Log.d(TAG, String.format("NextPageHandler/reset:thread(%s)",Thread.currentThread().getName()));
            unregister();
        }

        private void unregister() {
            Log.d(TAG, String.format("NextPageHandler/unregister:thread(%s) hasMore(%s)",Thread.currentThread().getName(),hasMore));
            if (nextPageLiveData != null) {
                nextPageLiveData.removeObserver(this);
                nextPageLiveData = null;
            }
            if (hasMore) {
                query = null;
            }
        }

        public void queryNextPage(String query) {
            Log.d(TAG, String.format("NextPageHandler/queryNextPage:thread(%s)",Thread.currentThread().getName()));
            if (query.equals(this.query)) {
                return;
            }
            unregister();
            this.query = query;
            nextPageLiveData = repository.searchNextPage(query);
            if (nextPageLiveData != null) {
                nextPageLiveData.observeForever(this);
            }

        }

        @Override
        public void onChanged(Resource<Boolean> result) {
            Log.d(TAG, String.format("NextPageHandler/onChanged:thread(%s)",Thread.currentThread().getName()));
            if (result == null) {
                reset();
            } else {
                switch (result.status) {
                    case SUCCESS:
                        Log.d(TAG, String.format("NextPageHandler/onChanged:thread(%s) SUCCESS",Thread.currentThread().getName()));
                        hasMore = result.data;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false,null));
                        break;
                    case ERROR:
                        Log.d(TAG, String.format("NextPageHandler/onChanged:thread(%s) ERROR",Thread.currentThread().getName()));
                        hasMore = true;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false,result.message));
                        break;
                    case LOADING:
                        break;
                        default:
                            break;
                }
            }
        }
    }

    static class LoadMoreState{

        private String errorMessageIfNotHandled;
        private boolean handledError = false;
        public boolean isRunning = false;

        public LoadMoreState(boolean isRunning, String errorMessage) {
            this.isRunning = isRunning;
            this.errorMessageIfNotHandled = errorMessage;
        }

        public String getErrorMessageIfNotHandled() {
            if (handledError) {
                return null;
            } else {
                handledError = true;
                return errorMessageIfNotHandled;
            }
        }
    }

}
