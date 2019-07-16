package com.dong.github;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.dong.github.search.SearchFragment;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;

public class MainActivity extends AppCompatActivity {

    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingAndroidInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchFragment searchFragment = new SearchFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fl_content,searchFragment).commit();
    }


}
