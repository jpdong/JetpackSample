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

    public SearchViewModel() {
    }

    public MutableLiveData<String> mQueryInternal = new MutableLiveData<String>();

    public LiveData<String> mQuery = mQueryInternal;

    public void setRepository(RepoRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<Resource<List<Repo>>> mResults = Transformations.switchMap(mQueryInternal, new Function<String, LiveData<Resource<List<Repo>>>>() {
        @Override
        public LiveData<Resource<List<Repo>>> apply(String input) {
            Log.d(TAG, String.format("SearchViewModel/apply:thread(%s) input(%s)",Thread.currentThread().getName(),input));
            if (TextUtils.isEmpty(input)) {
                return new LiveData<Resource<List<Repo>>>() {
                    @Override
                    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super Resource<List<Repo>>> observer) {
                        super.observe(owner, observer);
                    }
                };
            } else {
                return mRepository.search(input);
            }
        }
    });


    public void loadNextPage() {

    }

    public void setQuery(String query) {
        Log.d(TAG, String.format("SearchViewModel/setQuery:thread(%s)",Thread.currentThread().getName()));
        String input = query.toLowerCase(Locale.getDefault()).trim();
        if (input.equals(mQueryInternal.getValue())) {
            return;
        }
        mQueryInternal.setValue(input);
    }
}
