package pe.kr.rxandroidsample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pe.kr.rxandroidsample.fragments.BaseFrag;
import pe.kr.rxandroidsample.fragments.CachingStrategyListSampleFrag;
import pe.kr.rxandroidsample.fragments.DiffUtilSampleFrag;
import pe.kr.rxandroidsample.fragments.FormValidationSampleFrag;
import pe.kr.rxandroidsample.fragments.MultipleNetworkingSampleFrag;
import pe.kr.rxandroidsample.fragments.RetrofitSampleFrag;
import pe.kr.rxandroidsample.fragments.SearchExampleFrag;

public class MainActivity extends AppCompatActivity
        implements MyRecyclerViewAdapter.ItemClickListener , BaseFrag.OnFragmentTitleListener{

    private static final int _USING_RETROFIT_FRAGMENT = 0;
    private static final int _USING_RETROFIT_WITH_CACHING = 1;
    private static final int _USING_VALIDATION_CHECK = 2;
    private static final int _USING_DEBOUNCE_SEARCH_BAR = 3;
    private static final int _USING_DIFFUTIL = 4;
    private static final int _USING_MULTIPLE_NETWORKING_CONNECT = 5;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    MyRecyclerViewAdapter adapter;
    List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // data to populate the RecyclerView with
         items = Arrays.asList(getResources().getStringArray(R.array.items));

        // set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, items);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        initNavigation();
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
        if(pos >= items.size())
            return fragment;

        String title = items.get(pos);
        switch (pos){
            case _USING_RETROFIT_FRAGMENT:
                fragment = RetrofitSampleFrag.newInstance( title );
                break;
            case _USING_RETROFIT_WITH_CACHING:
                fragment = CachingStrategyListSampleFrag.newInstance( title );
                break;
            case _USING_VALIDATION_CHECK:
                fragment = FormValidationSampleFrag.newInstance( title );
                break;
            case _USING_DEBOUNCE_SEARCH_BAR:
                fragment = SearchExampleFrag.newInstance( title );
                break;
            case _USING_DIFFUTIL:
                fragment = DiffUtilSampleFrag.newInstance( title );
                break;
            case _USING_MULTIPLE_NETWORKING_CONNECT:
                fragment = MultipleNetworkingSampleFrag.newInstance( title );
                break;
        }

        return fragment;
    }

    @Override
    public void onBackPressed() {
        setTitle(getString(R.string.app_name));
        super.onBackPressed();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void initNavigation(){
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int stackHeight = getSupportFragmentManager().getBackStackEntryCount();
                if (stackHeight > 0) { // if we have something on the stack (doesn't include the current shown fragment)
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        getSupportFragmentManager().popBackStack();

        return super.onOptionsItemSelected(item);
    }
}

