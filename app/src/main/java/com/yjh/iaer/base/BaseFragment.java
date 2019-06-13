package com.yjh.iaer.base;

import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleRegistryOwner;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.squareup.leakcanary.RefWatcher;
import com.yjh.iaer.R;
import com.yjh.iaer.injection.Injectable;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment
        implements LifecycleRegistryOwner, Injectable {

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    private Unbinder mUnBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(), container, false);
        mUnBinder = ButterKnife.bind(this, v);
        initView();

        return v;
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    protected void replaceFragment(BaseFragment newFragment) {
        replaceFragment(newFragment, false);
    }

    protected void replaceFragment(BaseFragment newFragment, boolean addToBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }
}
