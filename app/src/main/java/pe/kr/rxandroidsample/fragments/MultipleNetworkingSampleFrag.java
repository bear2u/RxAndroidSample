package pe.kr.rxandroidsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.GithubService;
import pe.kr.rxandroidsample.Helper;
import pe.kr.rxandroidsample.R;

import static pe.kr.rxandroidsample.LogUtils._log;


public class MultipleNetworkingSampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener{
    @BindView(R.id.frag_recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    MyFragmentRecyclerViewAdapter adapter;
    List<Contributor> mContributors = new ArrayList<>();
    Disposable disposable;

    @BindView(R.id.progressBar)
    ProgressBar progressDialog;

    public static MultipleNetworkingSampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        MultipleNetworkingSampleFrag fragment = new MultipleNetworkingSampleFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_list_fetch , container , false);
        unbinder = ButterKnife.bind(this , view);

        if(onFragmentTitleListener != null){
            onFragmentTitleListener.setTitle( getArguments().getString("title"));
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        //setData();
        start();
    }

    private void initView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyFragmentRecyclerViewAdapter(getActivity(), mContributors);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    private void setData(){
        String owner = getString(R.string.rxjava_gist_owner_name);
        String repo_name = getString(R.string.rxjava_repo_name);

        Single<List<Contributor>> observable = GithubService.createGitHubApi()
                .contributors( owner , repo_name);

        disposable = observable
                .doOnSubscribe(disposable -> progressDialog.setVisibility(View.VISIBLE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getDisposableObservaer());
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "clicked pos -> " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDetach() {
        if(disposable!=null)
            disposable.dispose();
        super.onDetach();
    }

    public DisposableSingleObserver<List<Contributor>> getDisposableObservaer(){
        return new DisposableSingleObserver<List<Contributor>>() {

            @Override
            public void onSuccess(List<Contributor> items) {
                if(!Helper.isUIMainThread()) {
                    return;
                }
                _log("Results -> " + ArrayUtils.toString(items));
                progressDialog.setVisibility(View.GONE);
                mContributors.addAll(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        };
    }

    private void start(){
        try {
            long start = System.currentTimeMillis();
            run();
            System.out.println("Finished in: " + (System.currentTimeMillis() - start) + "ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() throws Exception {
        final ExecutorService executor = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        try {

            Future<String> f1 = executor.submit(new CallToRemoteServiceA());
            Observable<String> f1Observable = Observable.fromFuture(f1);
            Observable<String> f3Observable = f1Observable
                    .flatMap(s -> {
                        System.out.println("Observed from f1: " + s);
                        Future<String> f3 = executor.submit(new CallToRemoteServiceC(s));
                        return Observable.fromFuture(f3);
                    });


            Future<Integer> f2 = executor.submit(new CallToRemoteServiceB());
            Observable<Integer> f2Observable = Observable.fromFuture(f2);
            Observable<Integer> f4Observable = f2Observable
                    .flatMap(integer -> {
                        System.out.println("Observed from f2: " + integer);
                        Future<Integer> f4 = executor.submit(new CallToRemoteServiceD(integer));
                        return Observable.fromFuture(f4);
                    });

            Observable<Integer> f5Observable = f2Observable
                    .flatMap(integer -> {
                        System.out.println("Observed from f2: " + integer);
                        Future<Integer> f5 = executor.submit(new CallToRemoteServiceE(integer));
                        return Observable.fromFuture(f5);
                    });

            Observable.zip(f3Observable, f4Observable, f5Observable, new Function3<String, Integer, Integer, Map<String,String>>() {
                @Override
                public Map<String, String> apply(String s, Integer integer, Integer integer2) throws Exception {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("f3", s);
                    map.put("f4", String.valueOf(integer));
                    map.put("f5", String.valueOf(integer2));
                    return map;
                }
            }).subscribe(new Consumer<Map<String, String>>() {
                @Override
                public void accept(Map<String, String> map) throws Exception {
                    System.out.println(map.get("f3") + " => " + (Integer.valueOf(map.get("f4")) * Integer.valueOf(map.get("f5"))));
                }
            });

        } finally {
            executor.shutdownNow();
        }
    }

    private static final class CallToRemoteServiceA implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("A called");
            // simulate fetching data from remote service
            Thread.sleep(100);
            return "responseA";
        }
    }

    private static final class CallToRemoteServiceB implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("B called");
            // simulate fetching data from remote service
            Thread.sleep(40);
            return 100;
        }
    }

    private static final class CallToRemoteServiceC implements Callable<String> {

        private final String dependencyFromA;

        public CallToRemoteServiceC(String dependencyFromA) {
            this.dependencyFromA = dependencyFromA;
        }

        @Override
        public String call() throws Exception {
            System.out.println("C called");
            // simulate fetching data from remote service
            Thread.sleep(60);
            return "responseB_" + dependencyFromA;
        }
    }

    private static final class CallToRemoteServiceD implements Callable<Integer> {

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

    private static final class CallToRemoteServiceE implements Callable<Integer> {

        private final Integer dependencyFromB;

        public CallToRemoteServiceE(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println("E called");
            // simulate fetching data from remote service
            Thread.sleep(55);
            return 5000 + dependencyFromB;
        }
    }

}
