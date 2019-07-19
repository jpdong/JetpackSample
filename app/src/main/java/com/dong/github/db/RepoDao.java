package com.dong.github.db;

import android.util.SparseArray;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dong.github.vo.Contributor;
import com.dong.github.vo.Repo;
import com.dong.github.vo.RepoSearchResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
@Dao
public abstract class RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract public void insertRepos(List<Repo> repos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract public void insert(RepoSearchResult result);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertContributions(List<Contributor> contributors);


    @Query("select * from RepoSearchResult where `query` = :input")
    public abstract LiveData<RepoSearchResult> search(String input);

    public LiveData<List<Repo>> loadOrdered(List<Integer> repoIds){
        SparseArray<Integer> order = new SparseArray();
        for (int i = 0; i < repoIds.size(); i++) {
            order.put(i,repoIds.get(i).intValue());
        }
        return loadById(repoIds);
        /*return Transformations.map(loadById(repoIds), new Function<List<Repo>, List<Repo>>() {
            @Override
            public List<Repo> apply(List<Repo> input) {
                Collections.sort(input, new Comparator<Repo>() {
                    @Override
                    public int compare(Repo o1, Repo o2) {
                        return order.get(o1.id) - order.get(o2.id);
                    }
                });
                return input;
            }
        });*/
    }

    @Query("select * from Repo where id in (:repoIds)")
    protected abstract LiveData<List<Repo>> loadById(List<Integer> repoIds);

    @Query("select * from RepoSearchResult where `query` = :query")
    public abstract RepoSearchResult findSearchResult(String query);
}
