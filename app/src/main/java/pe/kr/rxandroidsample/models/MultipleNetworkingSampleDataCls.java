package pe.kr.rxandroidsample.models;

import java.util.concurrent.Callable;

/**
 * Created by dev on 2017-10-10.
 */

public class MultipleNetworkingSampleDataCls {
    public static final class CallToRemoteServiceA implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("A called");
            // simulate fetching data from remote service
            Thread.sleep(1000);
            return "responseA";
        }
    }

    public static final class CallToRemoteServiceB implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("B called");
            // simulate fetching data from remote service
            Thread.sleep(400);
            return 100;
        }
    }

    public static final class CallToRemoteServiceC implements Callable<String> {

        private final String dependencyFromA;

        public CallToRemoteServiceC(String dependencyFromA) {
            this.dependencyFromA = dependencyFromA;
        }

        @Override
        public String call() throws Exception {
            System.out.println("C called");
            // simulate fetching data from remote service
            Thread.sleep(3000);
            return "responseB_" + dependencyFromA;
        }
    }

    public static final class CallToRemoteServiceD implements Callable<Integer> {

        private final Integer dependencyFromB;

        public CallToRemoteServiceD(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println("D called");
            // simulate fetching data from remote service
            Thread.sleep(140);
            return 40 + dependencyFromB;
        }
    }

    public static final class CallToRemoteServiceE implements Callable<Integer> {

        private final Integer dependencyFromB;

        public CallToRemoteServiceE(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println("E called");
            // simulate fetching data from remote service
            Thread.sleep(550);
            return 5000 + dependencyFromB;
        }
    }
}
