package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AhliTaniAndAdminCropsAdapter extends FragmentPagerAdapter {
    private final Context context;
    int totalTabs;

    public AhliTaniAndAdminCropsAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ReviewedCropsFragment reviewedCropsFragment= new ReviewedCropsFragment();
                return reviewedCropsFragment;
            case 1:
                ApprovedCropsFragment approvedCropsFragment = new ApprovedCropsFragment();
                return approvedCropsFragment;
            default:
                //why return null?
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
