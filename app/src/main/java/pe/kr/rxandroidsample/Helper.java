package pe.kr.rxandroidsample;

import android.os.Looper;

public class Helper {
    public static boolean isUIMainThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
