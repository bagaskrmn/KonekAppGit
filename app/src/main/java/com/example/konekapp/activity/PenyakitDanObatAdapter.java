package com.example.konekapp.activity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.konekapp.fragment.ObatFragment;
import com.example.konekapp.fragment.PenyakitFragment;

public class PenyakitDanObatAdapter extends FragmentPagerAdapter {
    //why final?
    private final Context context;
    int totalTabs;

    public PenyakitDanObatAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

//    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                PenyakitFragment penyakitFragment= new PenyakitFragment();
                return penyakitFragment;
            case 1:
                ObatFragment obatFragment = new ObatFragment();
                return obatFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
