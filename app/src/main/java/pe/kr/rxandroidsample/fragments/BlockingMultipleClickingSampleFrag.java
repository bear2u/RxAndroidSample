package pe.kr.rxandroidsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import pe.kr.rxandroidsample.R;
import timber.log.Timber;

public class BlockingMultipleClickingSampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener{

    PublishSubject<View> publishSubject = PublishSubject.create();

    Unbinder unbinder;
    Disposable disposable;

    @BindView(R.id.txt)
    TextView resultTxt;

    public static BlockingMultipleClickingSampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        BlockingMultipleClickingSampleFrag fragment = new BlockingMultipleClickingSampleFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_blocking_multiple_clicking , container , false);
        unbinder = ButterKnife.bind(this , view);

        if (onFragmentTitleListener != null) {
            onFragmentTitleListener.setTitle(getArguments().getString("title"));
        }

        initSubject();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(disposable!=null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void initSubject(){
        publishSubject
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view -> {
                    Timber.d("clicked -> " + view.getId());
                    resultTxt.setText("clicked id : " + view.getId());
                    Toast.makeText(getActivity(),view.getId()+"",Toast.LENGTH_SHORT).show();
                });
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3})
    public void clickButton(View view){
        publishSubject.onNext(view);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
