package com.dong.github.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.dong.github.AppExecutors;
import com.dong.github.api.ApiResponse;
import com.dong.github.api.GithubService;
import com.dong.github.api.RepoSearchResponse;
import com.dong.github.db.GithubDb;
import com.dong.github.util.AbsentLiveData;
import com.dong.github.vo.Repo;
import com.dong.github.vo.RepoSearchResult;
import com.dong.github.vo.Resource;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
public class RepoRepository {

    private static final String TAG = "dong";

    private static RepoRepository sInstance;
    private AppExecutors mAppExecutors;
    private GithubDb mGithubDb;
    private GithubService mGithubService;

    private RepoRepository(AppExecutors appExecutors,GithubDb db,GithubService githubService) {
        this.mAppExecutors = appExecutors;
        this.mGithubDb = db;
        this.mGithubService = githubService;
    }

    public static synchronized RepoRepository getInstance(AppExecutors appExecutors,GithubDb db,GithubService githubService) {
        if (sInstance == null) {
            sInstance = new RepoRepository(appExecutors,db,githubService);
        }
        return sInstance;
    }


    public LiveData<Resource<List<Repo>>> search(String input) {
        Log.d(TAG, String.format("RepoRepository/search:thread(%s)",Thread.currentThread().getName()));
        return new NetworkBoundResource<List<Repo>, RepoSearchResponse>(mAppExecutors){

            @Override
            protected void onFetchFailed() {
                Log.d(TAG, String.format("RepoRepository/onFetchFailed:thread(%s)",Thread.currentThread().getName()));
            }

            @Override
            protected void saveCallResult(RepoSearchResponse item) {
                Log.d(TAG, String.format("RepoRepository/saveCallResult:thread(%s)",Thread.currentThread().getName()));
                List<Integer> repoIds = item.items.stream().map(new Function<Repo, Integer>() {
                    @Override
                    public Integer apply(Repo repo) {
                        return repo.id;
                    }
                }).collect(Collectors.toList());
                RepoSearchResult repoSearchResponse = new RepoSearchResult(input, repoIds, item.total, item.nextPage);
                mGithubDb.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        mGithubDb.repoDao().insertRepos(item.items);
                        mGithubDb.repoDao().insert(repoSearchResponse);
                    }
                });
            }

            @Override
            protected LiveData<ApiResponse<RepoSearchResponse>> createCall() {
                Log.d(TAG, String.format("RepoRepository/createCall:thread(%s)",Thread.currentThread().getName()));
                return mGithubService.searchRepos(input);
            }

            @Override
            protected boolean shouldFetch(List<Repo> data) {
                Log.d(TAG, String.format("RepoRepository/shouldFetch:thread(%s) data(%s)",Thread.currentThread().getName(),data));
                return data == null;
            }

            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                Log.d(TAG, String.format("RepoRepository/loadFromDb:thread(%s)",Thread.currentThread().getName()));
                return Transformations.switchMap(mGithubDb.repoDao().search(input), new androidx.arch.core.util.Function<RepoSearchResult, LiveData<List<Repo>>>() {
                    @Override
                    public LiveData<List<Repo>> apply(RepoSearchResult result) {
                        if (result == null) {
                            return new AbsentLiveData<>();
                        } else {
                            return mGithubDb.repoDao().loadOrdered(result.repoIds);
                        }
                    }
                });
            }
        }.asLiveData();
    }
}
