package pe.kr.rxandroidsample.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import pe.kr.rxandroidsample.CommonUtils;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.GithubService;
import pe.kr.rxandroidsample.R;
import timber.log.Timber;

import static florent37.github.com.rxlifecycle.RxLifecycle.disposeOnDestroy;
import static pe.kr.rxandroidsample.LogUtils._log;

public class CachingStrategyListSampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener {
    @BindView(R.id.frag_recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    MyFragmentRecyclerViewAdapter adapter;
    List<Contributor> contributors = new ArrayList<>();

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindViews({R.id.btn_concat, R.id.btn_concatEager, R.id.btn_merge, R.id.btn_multicasting})
    List<Button> buttons;

    @BindView(R.id.seekbar)
    SeekBar seekBar;

    @BindView(R.id.seekbar_title)
    TextView seekbar_title;

    Map<String, Contributor> map = new HashMap<>();
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int default_delay_time = 0;

    public static CachingStrategyListSampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        CachingStrategyListSampleFrag fragment = new CachingStrategyListSampleFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_cache_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (onFragmentTitleListener != null) {
            onFragmentTitleListener.setTitle(getArguments().getString("title"));
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        initEvent();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
        compositeDisposable.clear();
    }

    private void initView() {
        // set up the RecyclerView
//        List<String> items = Arrays.asList(getResources().getStringArray(R.array.items_sample));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyFragmentRecyclerViewAdapter(getActivity(), contributors);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    private void initEvent() {

        for (Button button : buttons) {
            Flowable<Contributor> observable = null;
            switch (button.getId()) {
                case R.id.btn_concat:
                    observable = getConcatObservable();
                    break;
                case R.id.btn_concatEager:
                    observable = getConcatEagerObservable();
                    break;
                case R.id.btn_merge:
                    observable = getMergeObservable();
                    break;
                case R.id.btn_multicasting:
                    observable = getPublisingObservable();
                    break;
            }
            initRxView(button, runObservable(observable));
        }

        RxSeekBar.changes( seekBar )
                .compose( disposeOnDestroy(this))
                .doOnNext( i -> default_delay_time = i)
                .map( i -> String.format(getString(R.string.seekbar_delay_time) , i))
                .subscribe( s -> seekbar_title.setText( s ));
    }

    private void initRxView(Button btn, Action action) {
        RxView.clicks(btn)
                .doOnNext(v -> {
                    contributors.clear();
                    setProgressBarVisible(true);
                })
                .compose(disposeOnDestroy(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> action.run());
    }

    private Action runObservable(Flowable<Contributor> observable) {
        return () -> {
            Disposable disposable = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(getDisposableObserver());
            compositeDisposable.add(disposable);
        };
    }

    private Flowable<Contributor> getConcatObservable() {
        return Flowable.concat(getSlowCachedData(), getNetworkData());
    }

    private Flowable<Contributor> getConcatEagerObservable() {
        List<Flowable<Contributor>> combinedObservables = new ArrayList<>();
        combinedObservables.add(getSlowCachedData());
        combinedObservables.add(getNetworkData());

        return Flowable.concatEager(combinedObservables);
    }

    private Flowable<Contributor> getMergeObservable() {
        return Flowable.merge(getSlowCachedData(), getNetworkData());
    }

    private Flowable<Contributor> getPublisingObservable() {
        return getNetworkData()
                .publish( //같은 값 공유를 위해서 share
                        newItem ->
                                Flowable.merge(
                                        newItem,
                                        //만약 캐싱된 게 늦게 올 경우 폐기함( 대박 )
                                        getSlowCachedData().takeUntil(newItem)
                                )
                );
    }


    @Override
    public void onItemClick(View view, int position) {
        Timber.d("item clicked", position);
    }

    private Flowable<Contributor> getSlowCachedData() {
        return Flowable.timer(default_delay_time, TimeUnit.SECONDS).flatMap(items -> getCachedData());
    }

    private Flowable<Contributor> getCachedData() {

        return Flowable.fromCallable(() -> CommonUtils.readFromfile(getActivity(), "names.txt"))
                .flatMap(Flowable::fromIterable) //리스트를 하나씩 꺼내서 처리
                .map(name -> new Contributor(name, 0L))
                .doOnNext(item -> Timber.d("old Item -> " + item));
    }

    private Flowable<Contributor> getNetworkData() {
        String owner = getString(R.string.rxjava_gist_owner_name);
        String repo = getString(R.string.rxjava_repo_name);
        return GithubService.createGitHubApi().contributors(owner, repo)
                //리스트를 하나씩 꺼내서 처리
                .toFlowable()
                .flatMap(Flowable::fromIterable);
    }

    private void setProgressBarVisible(boolean visible) {
        new Handler(Looper.myLooper()).post(()
                ->
        {
            if (progressBar != null)
                progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        });
    }

    public DisposableSubscriber<Contributor> getDisposableObserver() {
        return new DisposableSubscriber<Contributor>() {
            @Override
            public void onNext(Contributor contributor) {
                setProgressBarVisible(false);
                if (!contributors.contains(contributor)) {
                    contributors.add(contributor);
                    adapter.notifyItemInserted(contributors.size() - 1);
                }
                int pos = 0;
                for (Contributor cb : contributors) {
                    if (cb.equals(contributor)) {
                        contributors.set(pos, contributor);
                        adapter.notifyItemChanged(pos);
                    }
                    pos++;
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                _log("onCompleted");
            }
        };
    }

}
