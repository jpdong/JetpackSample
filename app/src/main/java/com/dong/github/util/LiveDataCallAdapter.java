package com.dong.github.util;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.dong.github.api.ApiResponse;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dongjiangpeng on 2019/7/16 0016.
 */
public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    private static final String TAG = "dong";

    private Type mResponseType;

    public LiveDataCallAdapter(Type responseType) {
        this.mResponseType = responseType;
    }

    @Override
    public Type responseType() {
        return mResponseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(Call<R> call) {
        Log.d(TAG, String.format("LiveDataCallAdapter/adapt:thread(%s)",Thread.currentThread().getName()));
        return new LiveData<ApiResponse<R>>() {
            private AtomicBoolean started = new AtomicBoolean(false);

            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                            postValue(new ApiResponse<R>().create(response));
                        }

                        @Override
                        public void onFailure(Call<R> call, Throwable t) {
                            postValue(new ApiResponse<R>().create(t));
                        }
                    });
                }
            }
        };
    }
}
