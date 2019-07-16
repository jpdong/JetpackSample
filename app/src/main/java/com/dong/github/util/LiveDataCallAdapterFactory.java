package com.dong.github.util;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.dong.github.api.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created by dongjiangpeng on 2019/7/16 0016.
 */
public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    private static final String TAG = "dong";
    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Log.d(TAG, String.format("LiveDataCallAdapterFactory/get:thread(%s)",Thread.currentThread().getName()));

        if (getRawType(returnType) != LiveData.class) {
            return null;
        } else {
            Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
            Class rawObservableType =getRawType(observableType);
            if (rawObservableType != ApiResponse.class) {
                throw new IllegalArgumentException("type must be a resource");
            }
            if (observableType instanceof ParameterizedType) {
                Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
                return new LiveDataCallAdapter(bodyType);
            } else {
                throw new IllegalArgumentException("resource must be parameterized");
            }
        }
    }
}
