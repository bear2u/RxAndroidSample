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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.GithubService;
import pe.kr.rxandroidsample.Helper;
import pe.kr.rxandroidsample.R;
import pe.kr.rxandroidsample.models.MultipleRealNetworkingSampleDataCls;

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

    long start;
    private void start(){
        try {
            start = System.currentTimeMillis();
            run();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void networkRun(){
//        Flowable<List<Contributor>> f1 = new MultipleRealNetworkingSampleDataCls.CallToRemoteServiceA()

    }

    private void run() throws Exception {
        final ExecutorService executor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>());
        try {

            Future<Flowable<List<Contributor>>> f1 = executor.submit(new MultipleRealNetworkingSampleDataCls.CallToRemoteServiceA());
//            Flowable<List<Contributor>> f1Observable = Flowable.fromFuture(f1);
//            Observable<List<Contributor>> f3Observable = f1Observable
//                    .flatMap(s -> {
//                        System.out.println("Observed from f1: " + s);
//                        Future<Flowable<List<Contributor>>> f3 = executor.submit(new MultipleRealNetworkingSampleDataCls.CallToRemoteServiceC(s));
//                        return Observable.fromFuture(f3);
//                    });


//            Future<Flowable<List<Contributor>>> f2 = executor.submit(new MultipleRealNetworkingSampleDataCls.CallToRemoteServiceB());
//            Flowable<List<Contributor>> f2Observable = Observable.fromFuture(f2);
//            Flowable<List<Contributor>> f4Observable = f2Observable
//                    .flatMap(integer -> {
//                        System.out.println("Observed from f2: " + integer);
//                        Future<Flowable<List<Contributor>>> f4 = executor.submit(new MultipleRealNetworkingSampleDataCls.CallToRemoteServiceD(0));
//                        return Observable.fromFuture(f4);
//                    });
//
//            Flowable<List<Contributor>> f5Observable = f2Observable
//                    .flatMap(s -> {
//                        System.out.println("Observed from f2: " + s);
//                        Future<Flowable<List<Contributor>>> f5 = executor.submit(new MultipleRealNetworkingSampleDataCls.CallToRemoteServiceE(0));
//                        return Observable.fromFuture(f5);
//                    });
//
//            Observable.zip(f3Observable, f4Observable, f5Observable, (map1, map2, map3) -> {
//                Map<String, List<Contributor>> map = new HashMap<>();
//                map.put("f3", map1);
//                map.put("f4", map2);
//                map.put("f5", map3);
//                return map;
//            }).subscribe(new Consumer<Map<String, List<Contributor>>>() {
//                @Override
//                public void accept(Map<String, List<Contributor>> map) throws Exception {
//
//                }
//            });

        } finally {
            executor.shutdownNow();
        }
    }


}
