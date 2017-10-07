package pe.kr.rxandroidsample.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.GithubService;
import pe.kr.rxandroidsample.Helper;
import pe.kr.rxandroidsample.R;

import static florent37.github.com.rxlifecycle.RxLifecycle.disposeOnDestroy;
import static pe.kr.rxandroidsample.LogUtils._log;


public class RetrofitSampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener{
    @BindView(R.id.frag_recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    MyFragmentRecyclerViewAdapter adapter;
    List<Contributor> mContributors = new ArrayList<>();
    Disposable disposable;

    @BindView(R.id.progressBar)
    ProgressBar progressDialog;

    public static RetrofitSampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        RetrofitSampleFrag fragment = new RetrofitSampleFrag();
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
        setData();
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


}
