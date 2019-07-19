package com.dong.github.vo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.dong.github.db.GithubTypeConverters;

import java.util.List;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
@Entity(primaryKeys = "query")
@TypeConverters(GithubTypeConverters.class)
public class RepoSearchResult {
    @NonNull
    public String query;
    public List<Integer> repoIds;
    public int totalCount;
    public int next = 0;

    public RepoSearchResult(@NonNull String query, List<Integer> repoIds, int totalCount, int next) {
        this.query = query;
        this.repoIds = repoIds;
        this.totalCount = totalCount;
        this.next = next;
    }
}
