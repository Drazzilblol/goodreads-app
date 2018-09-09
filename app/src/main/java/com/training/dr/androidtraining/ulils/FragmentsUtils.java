package com.training.dr.androidtraining.ulils;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public final class FragmentsUtils {


    public static String getTopEntryName(final FragmentManager fm) {
        return fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
    }


    public static void addFragment(@NonNull FragmentManager fragmentManager,
                                   @IdRes int container,
                                   @NonNull Fragment fragment,
                                   boolean addToBackStack,
                                   boolean clearBackStack,
                                   @Nullable String tag) {

        FragmentTransaction ftr = fragmentManager.beginTransaction();
        ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (clearBackStack) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        ftr.add(container, fragment, tag);
        if (addToBackStack) {
            ftr.addToBackStack(tag);
        }
        ftr.commit();
    }


}