package pe.kr.rxandroidsample.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import io.reactivex.subscribers.DisposableSubscriber;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.GithubService;
import pe.kr.rxandroidsample.Helper;
import pe.kr.rxandroidsample.R;

import static florent37.github.com.rxlifecycle.RxLifecycle.disposeOnDestroy;
import static pe.kr.rxandroidsample.LogUtils._log;


public class SearchExampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener{
    Unbinder unbinder;
    MyFragmentRecyclerViewAdapter adapter;
    List<Contributor> mContributors = new ArrayList<>();
    Disposable disposable;

    @BindView(R.id.progressBar)
    ProgressBar progressDialog;
    @BindView(R.id.frag_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.search_bar)
    EditText search_bar;
    @BindView(R.id.tv_nt_call_count)
    TextView tv_nt_call_count;

    PublishSubject<String> behaviorSubject = PublishSubject.create();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_nt_call_count.setText( String.format(getString(R.string.bt_call_count) , msg.what ) );
        }
    };

    public static SearchExampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        SearchExampleFrag fragment = new SearchExampleFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_list_w_search , container , false);
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
        initSubject();
        initEvent();
    }

    private void initView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyFragmentRecyclerViewAdapter(getActivity(), mContributors);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        mHandler.sendEmptyMessage( 0 );

    }

    private void initEvent(){
        RxTextView.textChanges( search_bar )
                .compose( disposeOnDestroy(this))
                .observeOn(Schedulers.io())
                .subscribe(cs -> {
                    behaviorSubject.onNext(cs.toString());
                }, Throwable::printStackTrace , () -> behaviorSubject.onComplete());
    }

    private void initSubject(){


        String owner = getString(R.string.rxjava_gist_owner_name);
        String repo_name = getString(R.string.rxjava_repo_name);


        String token = null; // 오류가 걸리면 Github api token 입력해야함
        Observable<List<Contributor>> observable = GithubService.createGitHubApi( token )
                .contributors( owner , repo_name)
                .doOnSuccess( list -> mHandler.sendEmptyMessage( atomicInteger.incrementAndGet() ))
                .toObservable();

        disposable = behaviorSubject
                .doOnNext( text -> _log("subject push -> " + text + ", current thread -> "+ Thread.currentThread().getName()))
                .debounce( 300 , TimeUnit.MILLISECONDS )
                //만약 빠른 변화에 따라 이전 작업은 취소를 하고 새로운 작업을 진행하기 위해 switchMap 사용
                .switchMap( text ->
                    observable
                            .map(
                                    list -> {
                                        if(StringUtils.isEmpty( text )){
                                            return list;
                                        }

                                        List<Contributor> newList = new ArrayList<>();
                                        for (Contributor contributor : list) {
                                            if (contributor.name.contains( text )) {
                                                newList.add(contributor);
                                            }
                                        }
                                        return newList;
                                    }
                            )
                            .doOnDispose( () -> _log("dipose event -> " + text))
                            .subscribeOn(Schedulers.io())
                )
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

    public DisposableObserver<List<Contributor>> getDisposableObservaer(){
        return new DisposableObserver<List<Contributor>>() {

            @Override
            public void onNext(List<Contributor> items) {
                if(!Helper.isUIMainThread()) {
                    return;
                }
                _log("Results size -> " + items.size() + " || " + ArrayUtils.toString(items));
                mContributors.clear();
                progressDialog.setVisibility(View.GONE);
                mContributors.addAll(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                _log("onCompleted subject");
            }
        };
    }



}
