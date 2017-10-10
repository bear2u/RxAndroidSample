package pe.kr.rxandroidsample.models;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.GithubService;
import pe.kr.rxandroidsample.R;

/**
 * Created by dev on 2017-10-10.
 */

public class MultipleRealNetworkingSampleDataCls {
    static final String owner = "ReactiveX";
    static final String repo = "RxJava";
    public static final class CallToRemoteServiceA implements Callable<Flowable<List<Contributor>>> {
        @Override
        public Flowable<List<Contributor>> call() throws Exception {
            System.out.println("A called");
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).toFlowable();
        }
    }

    public static final class CallToRemoteServiceB implements Callable<Flowable<List<Contributor>>> {
        @Override
        public Flowable<List<Contributor>> call() throws Exception {
            System.out.println("B called");
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).toFlowable();
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
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).toFlowable();
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
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).toFlowable();
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
            // simulate fetching data from remote service
            return GithubService.createGitHubApi().contributors(owner , repo).toFlowable();
//            return 5000 + dependencyFromB;
        }
    }
}
