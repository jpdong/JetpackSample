package com.dong.github.search;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;

import com.dong.github.R;
import com.dong.github.common.DataBoundListAdapter;
import com.dong.github.databinding.RepoItemBinding;
import com.dong.github.vo.Repo;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
class RepoListAdapter extends DataBoundListAdapter<Repo, RepoItemBinding> {

    private boolean mShowFullName;
    private RepoClickCallback mClickCallback;

    protected RepoListAdapter(boolean showFullName,RepoClickCallback clickCallback) {
        super(new DiffUtil.ItemCallback<Repo>() {
            @Override
            public boolean areItemsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Repo oldItem, @NonNull Repo newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.mShowFullName = showFullName;
        this.mClickCallback = clickCallback;
    }

    @Override
    public RepoItemBinding createBinding(ViewGroup viewGroup) {
        final RepoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.repo_item, viewGroup, false);
        binding.setShowFullName(mShowFullName);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickCallback.onClick(binding.getRepo());
            }
        });
        return binding;
    }

    @Override
    protected void bind(RepoItemBinding binding, Repo item) {
        binding.setRepo(item);
    }

    interface RepoClickCallback {

        void onClick(Repo item);

    }
}
