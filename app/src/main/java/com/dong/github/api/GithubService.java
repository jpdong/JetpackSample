package com.dong.github.api;

import androidx.lifecycle.LiveData;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by dongjiangpeng on 2019/7/16 0016.
 */
public interface GithubService {
    @GET("search/repositories")
    LiveData<ApiResponse<RepoSearchResponse>> searchRepos(@Query("q") String input);
}
