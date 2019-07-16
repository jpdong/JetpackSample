package com.dong.github.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.dong.github.vo.User;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(User user);

    @Query("select * from user where login=:login")
    public LiveData<User> findByLogin(String login);
}
