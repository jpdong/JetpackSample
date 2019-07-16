package com.dong.github;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by dongjiangpeng on 2019/7/10 0010.
 */
public class GithubApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
