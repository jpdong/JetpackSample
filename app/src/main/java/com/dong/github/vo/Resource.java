package com.dong.github.vo;

import com.dong.github.repository.RepoRepository;

import static com.dong.github.vo.Status.ERROR;
import static com.dong.github.vo.Status.LOADING;
import static com.dong.github.vo.Status.SUCCESS;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
public class Resource<T> {

    public Status status;
    public T data;
    public String message;

    public Resource(Status status,T data,String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static class Success<V> extends Resource<V> {

        public Success(V data) {
            super(SUCCESS, data, null);
        }
    }

    public static class Error<V> extends Resource<V> {

        public Error(V data, String message) {
            super(ERROR, data, message);
        }
    }


    public static class Loading<V> extends Resource<V> {

        public Loading(V data) {
            super(LOADING, data, null);
        }
    }


}
