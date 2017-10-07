package pe.kr.rxandroidsample.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import pe.kr.rxandroidsample.datas.Item;
import pe.kr.rxandroidsample.models.ItemModel;

import static pe.kr.rxandroidsample.LogUtils._log;


public class DiffUtilSampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener{
    @BindView(R.id.frag_recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;
    DiffAdapter adapter;

    Disposable disposable;

    @BindView(R.id.progressBar)
    ProgressBar progressDialog;

    public static DiffUtilSampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        DiffUtilSampleFrag fragment = new DiffUtilSampleFrag();
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

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initView(){

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity() , 7));
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        adapter = new DiffAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setBackgroundColor(Color.WHITE);

    }


    private void setData(){
        List<Item> itemList = new ArrayList<>();
//        itemList.addAll( ItemModel.randomItems());
        adapter.setItems(itemList);
//        adapter.notifyDataSetChanged();

        Pair<List<Item> , DiffUtil.DiffResult> initialPair = Pair.of( itemList , null);
        //2초동안 마지막내용만 가져옴
        disposable = ItemModel
                .latestThings(2, TimeUnit.SECONDS)
//                .doOnNext( v -> _log("item emiited #111 -> " + v))
                .scan( initialPair , (pair , next) -> {
                    MydiffCallback callback = new MydiffCallback(pair.getLeft() , next);
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
                    return Pair.of(next , result);
                })
                .skip(1)
//                .doOnNext( v -> _log("item emiited #222 -> " + v))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( listDiffResultPair -> {
                    progressDialog.setVisibility(View.GONE);
                    adapter.setItems( listDiffResultPair.getLeft() );
                    listDiffResultPair.getRight().dispatchUpdatesTo(adapter);
                } , Throwable::printStackTrace);

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "clicked pos -> " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(disposable!=null)
            disposable.dispose();
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
//                mContributors.addAll(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        };
    }

    //Adapter
    private class DiffAdapter extends RecyclerView.Adapter<ItemViewHolder>{
        private List<Item> items = new ArrayList<>();

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext()).inflate(android.R.layout.simple_list_item_1 , parent , false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            Item item = items.get(position);
            _log("current item -> " + item.toString());
            holder.bindView(item);
        }

        @Override
        public int getItemCount() {
            _log("getItemCount -> " + items.size());
            return items.size();
        }

//        public void setItems(List<Item> items){
//            this.items = items;
//        }
            public void setItems(List<Item> items){
                this.items = items;
//                _log("#218 add item #111 -> " + this.items);
//                this.items.clear();
//                this.items.addAll(items);
//                _log("#218 add item #222 -> " + items);
            }
    }

    //ViewHolder
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            _log("#229 itemViewHolder start");
            tv = itemView.findViewById(android.R.id.text1);
        }

        public void bindView(Item item) {
            _log("bindView -> " + item.toString());
            itemView.setBackgroundColor(item.getColor());
            tv.setText(item.getText());
        }

    }

    private class MydiffCallback extends DiffUtil.Callback {
        private List<Item> current;
        private List<Item> next;

        public MydiffCallback(List<Item> current , List<Item> next) {
            this.current = current;
            this.next = next;
        }

        @Override
        public int getOldListSize() {
            return current.size();
        }

        @Override
        public int getNewListSize() {
            return next.size();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Item currentItem = current.get(oldItemPosition);
            Item nextItem = next.get(newItemPosition);
//            _log(" areContentsTheSame -> " + currentItem.equals( nextItem ));
            return currentItem.equals( nextItem );
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Item currentItem = current.get(oldItemPosition);
            Item nextItem = next.get(newItemPosition);
//            _log(" areItemsTheSame -> " + (currentItem.getId() == nextItem.getId()));
            return currentItem.getId() == nextItem.getId();
        }
    }



}
