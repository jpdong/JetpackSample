package com.dong.github.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dong.github.vo.Contributor;
import com.dong.github.vo.Repo;
import com.dong.github.vo.RepoSearchResult;
import com.dong.github.vo.User;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
@Database(entities = {User.class, Repo.class, Contributor.class, RepoSearchResult.class}, version = 1,exportSchema = false)
public abstract class GithubDb extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract RepoDao repoDao();
}
