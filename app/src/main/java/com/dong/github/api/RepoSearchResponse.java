package com.dong.github.api;

import com.dong.github.vo.Repo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dongjiangpeng on 2019/7/13 0013.
 */
public class RepoSearchResponse {
    @SerializedName("total_count")
    public int total;
    @SerializedName("items")
    public List<Repo> items;
    public int nextPage;
}
