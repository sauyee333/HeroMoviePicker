package com.sauyee333.herospin.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sauyee333.herospin.R;
import com.sauyee333.herospin.fragment.MainFragment;
import com.sauyee333.herospin.listener.MainListener;

public class MainActivity extends AppCompatActivity implements MainListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showMainFragment();
    }

    private void showMainFragment() {
        Fragment fragment = new MainFragment();
        showFragment(fragment, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }

    private void showFragment(Fragment frag, boolean force) {
        String fragmentTag = frag.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = force ? false : manager.popBackStackImmediate(fragmentTag, 0);

        if ((!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) || force) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content, frag, fragmentTag);
            transaction.addToBackStack(fragmentTag);
            transaction.commit();
        }
    }

    @Override
    public void onShowFragment(Fragment fragment, boolean force) {
        showFragment(fragment, force);
    }
}
