package pe.kr.rxandroidsample.models;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.GithubService;
import pe.kr.rxandroidsample.R;

import static pe.kr.rxandroidsample.LogUtils._log;

/**
 * Created by dev on 2017-10-10.
 */

public class MultipleRealNetworkingSampleDataCls {
    public static final class CallToRemoteServiceA implements Callable<Flowable<List<Contributor>>> {
        @Override
        public Flowable<List<Contributor>> call() throws Exception {
            System.out.println("A called");
            String owner = "ReactiveX";
            String repo = "RxJava";
            // simulate fetching data from remote service
            _log("");
            return GithubService.createGitHubApi()
                    .contributors(owner , repo)
                    .delay(1 , TimeUnit.SECONDS)
                    .doOnSuccess( v -> _log("A success -> " + v))
                    .doOnError(Throwable::printStackTrace)
                    .subscribeOn(Schedulers.io())
                    .toFlowable();
        }
    }

    public static final class CallToRemoteServiceB implements Callable<Flowable<List<Contributor>>> {
        @Override
        public Flowable<List<Contributor>> call() throws Exception {
            System.out.println("B called");
            String owner = "ReactiveX";
            String repo = "rxjs";
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).delay(2 , TimeUnit.SECONDS).toFlowable();
        }
    }

    public static final class CallToRemoteServiceC implements Callable<Flowable<List<Contributor>>> {

        private final String dependencyFromA;

        public CallToRemoteServiceC(String dependencyFromA) {
            this.dependencyFromA = dependencyFromA;
        }

        @Override
        public Flowable<List<Contributor>> call() throws Exception {
            System.out.println("C called");
            String owner = "ReactiveX";
            String repo = "RxSwift";
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).delay(4 , TimeUnit.SECONDS).toFlowable();
//            return "responseB_" + dependencyFromA;
        }
    }

    public static final class CallToRemoteServiceD implements Callable<Flowable<List<Contributor>>> {

        private final Integer dependencyFromB;

        public CallToRemoteServiceD(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Flowable<List<Contributor>> call() throws Exception {
            System.out.println("D called");
            String owner = "ReactiveX";
            String repo = "RxAndroid";
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).delay(3 , TimeUnit.SECONDS).toFlowable();
//            return 40 + dependencyFromB;
        }
    }

    public static final class CallToRemoteServiceE implements Callable<Flowable<List<Contributor>>> {

        private final Integer dependencyFromB;

        public CallToRemoteServiceE(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Flowable<List<Contributor>> call() throws Exception {
            System.out.println("E called");
            String owner = "ReactiveX";
            String repo = "RxKotlin";
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).delay(5 , TimeUnit.SECONDS).toFlowable();
//            return 5000 + dependencyFromB;
        }
    }
}
