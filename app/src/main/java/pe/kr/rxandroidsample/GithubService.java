package pe.kr.rxandroidsample;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.apache.commons.lang3.StringUtils;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.reactivex.annotations.Nullable;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.String.format;

public class GithubService {
    private final static String baseUrl = "https://api.github.com";
    private static final int CONNECT_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 30;

    public static GithubApi createGitHubApi() {
        return createGitHubApi(null);
    }

    public static GithubApi createGitHubApi(String githubToken){
        Retrofit.Builder builder =
                new Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient(githubToken))
                    .baseUrl(baseUrl);

        return builder.build().create(GithubApi.class);
    }

    private static OkHttpClient getClient(String githubToken) {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(
                BuildConfig.DEBUG ?
                        HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

//        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

         OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS) //연결 타임아웃 시간 설정
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS) //쓰기 타임아웃 시간 설정
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS) //읽기 타임아웃 시간 설정
                .addInterceptor(httpLoggingInterceptor); //http 로그 확인

        if(!StringUtils.isEmpty( githubToken )) {
            builder.addInterceptor(
                    chain -> {
                        Request request = chain.request();
                        Request newReq =
                                request
                                        .newBuilder()
                                        .addHeader("Authorization", format("token %s", githubToken))
                                        .build();
                        return chain.proceed(newReq);
                    });
        }

        return builder.build();
    }
}
