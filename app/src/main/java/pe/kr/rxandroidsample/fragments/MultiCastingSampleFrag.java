package pe.kr.rxandroidsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pe.kr.rxandroidsample.R;

public class MultiCastingSampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener{

    Unbinder unbinder;

    @Override
    public void onItemClick(View view, int position) {

    }

    public static MultiCastingSampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        MultiCastingSampleFrag fragment = new MultiCastingSampleFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_multicasting , container , false);
        unbinder = ButterKnife.bind(this , view);

        if (onFragmentTitleListener != null) {
            onFragmentTitleListener.setTitle(getArguments().getString("title"));
        }

        initView();
        return view;
    }

    private void initView() {

    }


}
