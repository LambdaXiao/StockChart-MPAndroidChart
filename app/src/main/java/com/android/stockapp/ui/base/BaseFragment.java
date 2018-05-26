package com.android.stockapp.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    public Activity mAct;
    private View view;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAct = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(setLayoutId(), container, false);
        ButterKnife.bind(this,view);
        initBase(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    protected abstract int setLayoutId();

    protected abstract void initBase(View view);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected void startToActivity(Class<?> cls){
        Intent intent = new Intent(mAct, cls);
        mAct.startActivity(intent);
    }

    Toast toast;
    public void showToast(String string) {
        if(toast == null){
            toast = Toast.makeText(mAct, string, Toast.LENGTH_SHORT);
        }else{
            toast.setText(string);
        }
        toast.show();
    }

    @Override
    public void onDestroy() {
        if(toast != null){
            toast.cancel();
        }
        super.onDestroy();
    }

}
