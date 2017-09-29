package pe.kr.rxandroidsample;

import android.app.Application;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;

import timber.log.Timber;

public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
