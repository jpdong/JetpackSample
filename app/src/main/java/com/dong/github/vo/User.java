package com.dong.github.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
@Entity(primaryKeys = {"login"})
public class User {
    @NonNull
    public String login;
    @ColumnInfo(name = "avatar_url")
    @SerializedName("login")
    public String avatarUrl;
    public String name;
    public String company;
    @ColumnInfo(name = "repo_url")
    @SerializedName("repo_url")
    public String reposUrl;
    public String blog;
}
