package com.dong.github.util;

import androidx.lifecycle.LiveData;

/**
 * Created by dongjiangpeng on 2019/7/16 0016.
 */
public class AbsentLiveData<T> extends LiveData<T> {

    public AbsentLiveData() {
        postValue(null);
    }
}
