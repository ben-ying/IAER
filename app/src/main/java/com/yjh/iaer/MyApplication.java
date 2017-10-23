package com.yjh.iaer;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.yjh.iaer.injection.AppInjector;
import com.yjh.iaer.room.entity.User;
import com.yjh.iaer.util.CrashLibrary;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

public class MyApplication extends Application implements HasActivityInjector {

    public static User sUser;

    private RefWatcher mRefWatcher;
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
//        else {
//            Timber.plant(new CrashReportingTree());
//        }
        // dagger
        AppInjector.init(this);

        mRefWatcher = LeakCanary.install(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            CrashLibrary.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    CrashLibrary.logError(t);
                } else if (priority == Log.WARN) {
                    CrashLibrary.logWarning(t);
                }
            }
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }
}
