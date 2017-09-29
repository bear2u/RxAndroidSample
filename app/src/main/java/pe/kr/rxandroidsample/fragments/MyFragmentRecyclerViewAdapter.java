package pe.kr.rxandroidsample.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.kr.rxandroidsample.Contributor;
import pe.kr.rxandroidsample.R;

public class MyFragmentRecyclerViewAdapter extends RecyclerView.Adapter<MyFragmentRecyclerViewAdapter.ViewHolder> {
        private Context context;
        private List<Contributor> mData = Collections.emptyList();
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;

        // data is passed into the constructor
        public MyFragmentRecyclerViewAdapter(Context context, List<Contributor> data) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.view_item_w_image, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        // binds the data to the textview in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Contributor contributor = mData.get(position);
            holder.personId.setText(contributor.name);
            holder.personDesc.setText("contribution : " + contributor.contributions);

            RequestOptions options = new RequestOptions();
            options.placeholder(R.drawable.icon_no_image_w_human);

            Glide.with(context)
                    .load( contributor.avatar_url )
                    .apply(RequestOptions.circleCropTransform())
                    .apply(options)
                    .into(holder.avatar);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.person_id)
            public TextView personId;

            @BindView(R.id.person_photo)
            public ImageView avatar;

            @BindView(R.id.person_desc)
            public TextView personDesc;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this , itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        public Contributor getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        public void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }