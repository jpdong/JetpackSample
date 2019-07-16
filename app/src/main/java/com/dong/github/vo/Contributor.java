package com.dong.github.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dongjiangpeng on 2019/7/11 0011.
 */
@Entity(primaryKeys = {"repoName", "repoOwner", "login"},
        foreignKeys = @ForeignKey(entity = Repo.class,
                parentColumns = {"name", "owner_login"},
                childColumns = {"repoName", "repoOwner"},
                onUpdate = ForeignKey.CASCADE,
                deferred = true))
public class Contributor {
    @NonNull
    public String login;
    public int contributions;
    @ColumnInfo(name = "avatar_url")
    @SerializedName("avatar_url")
    public String avatarUrl;
    @NonNull
    public String repoName;
    @NonNull
    public String repoOwner;
}
