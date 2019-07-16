package com.dong.github.common;

import android.view.View;

import androidx.databinding.BindingAdapter;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
public class BindingAdapters {

    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show? View.VISIBLE: View.GONE);
    }
}
