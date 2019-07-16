package com.dong.github.api;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Response;

/**
 * Created by dongjiangpeng on 2019/7/13 0013.
 */
public class ApiResponse<T> {

    private static final String TAG = "dong";

    public Error<T> create(Throwable error) {
        return new Error<>(error.getMessage());
    }

    public ApiResponse<T> create(Response<T> response) {
        Log.d(TAG, String.format("ApiResponse/create:thread(%s) code(%s)",Thread.currentThread().getName(),response.code()));
        if (response.isSuccessful()) {
            T body = response.body();
            if (body == null || response.code() == 204) {
                return new Empty<>();
            } else {
                return new Success<>(body, response.headers().get("link"));
            }
        } else {
            String msg = null;
            try {
                msg = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(msg)) {
                msg = response.message();
            }
            return new Error<>(msg);
        }
    }

    public static class Empty<T> extends ApiResponse<T> {

    }

    public static class Success<T> extends ApiResponse<T> {
        public T body;
        public Map<String,String> links;
        private Pattern LINK_PATTERN = Pattern.compile("");

        public Success(T body, String linkHeader) {
            this.body = body;
            this.links = extractLinks(linkHeader);
        }

        private Map<String, String> extractLinks(String linkHeader) {
            LinkedHashMap<String, String> result = new LinkedHashMap<>();
            return null;
        }
    }

    public static class Error<T> extends ApiResponse<T> {

        public String errorMessage;

        public Error(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}


