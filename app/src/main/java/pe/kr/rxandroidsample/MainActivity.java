package pe.kr.rxandroidsample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.kr.rxandroidsample.fragments.ListSampleFrag;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // data to populate the RecyclerView with
        List<String> items = Arrays.asList(getResources().getStringArray(R.array.items));

        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public void onItemClick(View view, int position) {
        Fragment fragment = getFragment(position);
        if(fragment!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace( android.R.id.content , fragment);
            ft.addToBackStack(null);
            ft.commit();
            Log.d("KTH" , "clicked");
        }
    }

    public Fragment getFragment(int pos) {
        Fragment fragment = null;
        switch (pos){
            case 0:
                fragment = new ListSampleFrag();
                break;
        }

        return fragment;
    }

}

