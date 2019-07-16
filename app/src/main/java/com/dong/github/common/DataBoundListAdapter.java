package com.dong.github.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
public abstract class DataBoundListAdapter<T,V extends ViewDataBinding> extends ListAdapter<T,DataBoundViewHolder<V>> {

    protected DataBoundListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public DataBoundViewHolder<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        V binding = createBinding(parent);
        return new DataBoundViewHolder<>(binding);
    }

    public abstract V createBinding(ViewGroup viewGroup);

    @Override
    public void onBindViewHolder(@NonNull DataBoundViewHolder<V> holder, int position) {
        bind(holder.binding, getItem(position));
        holder.binding.executePendingBindings();
    }

    protected abstract void bind(V binding, T item);
}
