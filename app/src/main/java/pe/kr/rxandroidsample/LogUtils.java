package pe.kr.rxandroidsample;

import timber.log.Timber;


public class LogUtils {
    private static void setup(){
        Timber.tag("RXJAVA");
    }
    public static void _log(String msg){
        setup();
        Timber.d(msg);
    }
}
