package com.dong.github.common;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
class DataBoundViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public T binding;

    public DataBoundViewHolder(@NonNull T binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
