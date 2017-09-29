package pe.kr.rxandroidsample.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFrag extends Fragment{
    public interface OnFragmentTitleListener{
        void setTitle(String title);
    }

    public OnFragmentTitleListener onFragmentTitleListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentTitleListener) {
            try {
                this.onFragmentTitleListener = (OnFragmentTitleListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString());
            }
        }
    }

}
