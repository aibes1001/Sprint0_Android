package com.example.abenest_upv.appsensorgas;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(FragmentActivity activity){
        super(activity);
    }
    @Override
    public int getItemCount() {
        return 2;
    }
    @Override @NonNull
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new Tab1();
            case 1: return new Tab2();
        }
        return null;
    }
}

