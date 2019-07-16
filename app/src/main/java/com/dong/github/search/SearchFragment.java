package com.dong.github.search;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dong.github.AppExecutors;
import com.dong.github.R;
import com.dong.github.api.GithubService;
import com.dong.github.databinding.SearchFragmentBinding;
import com.dong.github.db.GithubDb;
import com.dong.github.repository.RepoRepository;
import com.dong.github.util.LiveDataCallAdapterFactory;
import com.dong.github.vo.Repo;
import com.dong.github.vo.Resource;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
public class SearchFragment extends Fragment {

    private static final String TAG = "dong";

    private SearchFragmentBinding mSearchFragmentBinding;
    private SearchViewModel mSearchViewModel;
    private RepoListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSearchFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false);
        return mSearchFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        GithubService githubService = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(GithubService.class);
        mSearchViewModel.setRepository(RepoRepository.getInstance(AppExecutors.getInstance(),
                Room.databaseBuilder(getContext(), GithubDb.class,"github-db").build(),
                githubService));
        mSearchFragmentBinding.setLifecycleOwner(this);
        initRecyclerView();
        mAdapter = new RepoListAdapter(true, new RepoListAdapter.RepoClickCallback() {
            @Override
            public void onClick(Repo item) {

            }
        });
        mSearchFragmentBinding.setQuery(mSearchViewModel.mQuery);
        mSearchFragmentBinding.repoList.setAdapter(mAdapter);
        initSearchInputListener();
    }

    private void initSearchInputListener() {
        mSearchFragmentBinding.input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch(v);
                    return true;
                }
                return false;
            }
        });
    }

    private void doSearch(TextView textView) {
        Log.d(TAG, String.format("SearchFragment/doSearch:thread(%s)",Thread.currentThread().getName()));
        String query = mSearchFragmentBinding.input.getText().toString();
        dismissKeyboard(textView.getWindowToken());
        mSearchViewModel.setQuery(query);
    }

    private void dismissKeyboard(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);

    }

    private void initRecyclerView() {
        mSearchFragmentBinding.repoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastPosition == mAdapter.getItemCount() - 1) {
                    mSearchViewModel.loadNextPage();
                }
            }
        });
        mSearchFragmentBinding.setSearchResult(mSearchViewModel.mResults);
        mSearchViewModel.mResults.observe(getViewLifecycleOwner(), new Observer<Resource<List<Repo>>>() {
            @Override
            public void onChanged(Resource<List<Repo>> listResource) {
                mAdapter.submitList(listResource.data);
            }
        });
    }
}
