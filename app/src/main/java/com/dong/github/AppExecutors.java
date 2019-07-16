package com.dong.github;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by dongjiangpeng on 2019/7/13 0013.
 */
public class AppExecutors {
    
    public Executor mDiskIO;
    public Executor mNetworkIO;
    public Executor mMainThread;

    private static AppExecutors sInstance;

    private AppExecutors() {
        this.mDiskIO = Executors.newSingleThreadExecutor();
        this.mNetworkIO = Executors.newFixedThreadPool(3);
        this.mMainThread = new MainThreadExecutor();
    }

    public static synchronized AppExecutors getInstance() {
        if (sInstance == null) {
            sInstance = new AppExecutors();
        }
        return sInstance;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainHandler.post(command);
        }
    }

}
