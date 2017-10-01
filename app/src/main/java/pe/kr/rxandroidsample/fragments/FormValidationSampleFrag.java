package pe.kr.rxandroidsample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import org.apache.commons.lang3.math.NumberUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import pe.kr.rxandroidsample.R;

import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;
import static pe.kr.rxandroidsample.LogUtils._log;

/**
 * Created by dev on 2017-09-27.
 */

public class FormValidationSampleFrag extends BaseFrag implements MyFragmentRecyclerViewAdapter.ItemClickListener{
    @BindView(R.id.demo_combl_email)
    EditText et_email;

    @BindView(R.id.demo_combl_num)
    EditText et_num;

    @BindView(R.id.demo_combl_password)
    EditText et_password;

    @BindView(R.id.btn_demo_form_valid)
    Button btnDemoFormValid;

    Flowable<CharSequence> _emailFlowable;
    Flowable<CharSequence> _numberFlowable;
    Flowable<CharSequence> _passwordFlowable;

    Unbinder unbinder;

    public static FormValidationSampleFrag newInstance(String title) {

        Bundle args = new Bundle();
        args.putString("title" , title);

        FormValidationSampleFrag fragment = new FormValidationSampleFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_form_validation , container , false);
        unbinder = ButterKnife.bind(this , view);

        if (onFragmentTitleListener != null) {
            onFragmentTitleListener.setTitle(getArguments().getString("title"));
        }

        initView();
        return view;
    }

    @OnClick(R.id.btn_demo_form_valid)
    public void checkValidation(){

    }

    private void initView(){
        _emailFlowable = RxTextView.textChanges(et_email).skip(1).toFlowable(BackpressureStrategy.LATEST);
        _numberFlowable = RxTextView.textChanges(et_num).skip(1).toFlowable(BackpressureStrategy.LATEST);
        _passwordFlowable = RxTextView.textChanges(et_password).skip(1).toFlowable((BackpressureStrategy.LATEST));

        Flowable.combineLatest( _emailFlowable , _passwordFlowable , _numberFlowable  , (newEmail , newPassword , newNumber) ->{
            _log("contents : " + newEmail + "," + newPassword + "," + newNumber);

            boolean isEmailValid = !isEmpty(newEmail) && EMAIL_ADDRESS.matcher(newEmail).matches();
            if(!isEmailValid) {
                et_email.setError( "Email Error" );
            }

            boolean isPasswordValid = !isEmpty(newPassword) && newPassword.length() > 8;
            if(!isPasswordValid) {
                et_password.setError( "Password Error" );
            }

            boolean isNumbericTest = NumberUtils.isCreatable( newNumber.toString() );
            _log("isNumber-> " + isNumbericTest + "," + newNumber);

            boolean isNumberic = !isEmpty(newNumber) && NumberUtils.isCreatable( newNumber.toString() );
            if(!isNumberic){
                et_num.setError("Numberic Error");
            }

            _log("validation -> " + isEmailValid);

            return isEmailValid && isPasswordValid && isNumberic;
        })
        .subscribe(validationResult -> {
            if(validationResult){
                btnDemoFormValid.setBackgroundColor(ContextCompat.getColor(getContext() , R.color.DeepSkyBlue));
            }else{
                btnDemoFormValid.setBackgroundColor(ContextCompat.getColor(getContext() , R.color.LightGrey));
            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {

    }
}
