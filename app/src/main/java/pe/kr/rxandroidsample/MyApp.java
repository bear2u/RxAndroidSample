package pe.kr.rxandroidsample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by dev on 2017-09-27.
 */

public class MyApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
}
