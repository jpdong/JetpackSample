package com.dong.github.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dong.github.api.ApiResponse;
import com.dong.github.api.GithubService;
import com.dong.github.api.RepoSearchResponse;
import com.dong.github.db.GithubDb;
import com.dong.github.vo.Repo;
import com.dong.github.vo.RepoSearchResult;
import com.dong.github.vo.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import retrofit2.Response;

/**
 * Created by dongjiangpeng on 2019/7/18 0018.
 */
public class FetchNextSearchPageTask implements Runnable {

    private static final String TAG = "dong";

    private String mQuery;
    private GithubService mGithubService;
    private GithubDb mDatabase;
    private MutableLiveData<Resource<Boolean>> mLiveDataInternal = new MutableLiveData<Resource<Boolean>>();
    public LiveData<Resource<Boolean>> mLiveData = mLiveDataInternal;

    public FetchNextSearchPageTask(String query, GithubService githubService, GithubDb db) {
        Log.d(TAG, String.format("FetchNextSearchPageTask/FetchNextSearchPageTask:thread(%s)",Thread.currentThread().getName()));
        this.mQuery = query;
        this.mGithubService = githubService;
        this.mDatabase = db;
    }

    @Override
    public void run() {
        RepoSearchResult current = mDatabase.repoDao().findSearchResult(mQuery);
        Log.d(TAG, String.format("FetchNextSearchPageTask/run:thread(%s) current(%s)",Thread.currentThread().getName(),current));
        if (current == null) {
            mLiveDataInternal.postValue(null);
        }
        int nextPage = current.next;
        Log.d(TAG, String.format("FetchNextSearchPageTask/run:thread(%s) nextPage(%s)",Thread.currentThread().getName(),nextPage));
        if (nextPage == 0) {
            mLiveDataInternal.postValue(new Resource.Success<>(false));
            return;
        }
        Resource newValue = null;
        Response response = null;
        try {
            response = mGithubService.searchRepos(mQuery,nextPage).execute();
            ApiResponse<RepoSearchResponse> apiResponse = new ApiResponse<RepoSearchResult>().create(response);
            if (apiResponse instanceof ApiResponse.Success) {
                Log.d(TAG, String.format("FetchNextSearchPageTask/run:thread(%s) Success",Thread.currentThread().getName()));
                List<Integer> ids = new ArrayList<>();
                ids.addAll(current.repoIds);
                Log.d(TAG, String.format("FetchNextSearchPageTask/run:thread(%s) current size(%s)",Thread.currentThread().getName(),ids.size()));
                ids.addAll(((ApiResponse.Success<RepoSearchResponse>) apiResponse).body.items.stream().map(new Function<Repo, Integer>() {
                    @Override
                    public Integer apply(Repo repo) {
                        return repo.id;
                    }
                }).collect(Collectors.toList()));
                Log.d(TAG, String.format("FetchNextSearchPageTask/run:thread(%s) all size(%s)",Thread.currentThread().getName(),ids.size()));
                RepoSearchResult result = new RepoSearchResult(mQuery,ids,((ApiResponse.Success<RepoSearchResponse>) apiResponse).body.total,((ApiResponse.Success<RepoSearchResponse>) apiResponse).nextPage);
                mDatabase.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        mDatabase.repoDao().insert(result);
                        mDatabase.repoDao().insertRepos(((ApiResponse.Success<RepoSearchResponse>) apiResponse).body.items);
                    }
                });
                newValue = new Resource.Success(((ApiResponse.Success<RepoSearchResponse>) apiResponse).nextPage != 0);
            } else if (apiResponse instanceof ApiResponse.Empty) {
                Log.d(TAG, String.format("FetchNextSearchPageTask/run:thread(%s) Empty",Thread.currentThread().getName()));
                newValue = new Resource.Success(false);
            } else if (apiResponse instanceof ApiResponse.Error) {
                Log.d(TAG, String.format("FetchNextSearchPageTask/run:thread(%s) Error",Thread.currentThread().getName()));

                newValue = new Resource.Error(true,((ApiResponse.Error<RepoSearchResponse>) apiResponse).errorMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"FetchNextSearchPageTask/run:" + e.toString());
            newValue = new Resource.Error(true,e.getMessage());
        }
        mLiveDataInternal.postValue(newValue);
    }
}
