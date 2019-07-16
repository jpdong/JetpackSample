package com.dong.github.vo;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
@Entity(primaryKeys = {"name","owner_login"})
public class Repo {
    public int id;
    @NonNull
    public String name;
    @SerializedName("full_name")
    public String fullName;
    public String description;
    @Embedded(prefix = "owner_")
    @NonNull
    public Owner owner;
    @SerializedName("stargazers_count")
    public int stars;

    public static class Owner {
        @NonNull
        public String login;
        public String url;
    }
}
