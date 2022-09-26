package com.example.final_project_cs561.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.final_project_cs561.R;
import com.example.final_project_cs561.Client.SecondActivity;
import com.example.final_project_cs561.Utils.CaptureSignatureView;

public class SignaturePad extends Fragment implements View.OnClickListener, View.OnTouchListener {
    private LinearLayout llSignaturePad;
    private SecondActivity activity;
    CaptureSignatureView captureSignatureView;
    private Button btnClear,btnDone;
    private TextView tvMsg;
    private RelativeLayout rrBtn;
    public SignaturePad() {}
    public static SignaturePad getInstance(){
        SignaturePad signaturePad= new SignaturePad();
        return signaturePad;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signutare_pad, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVar();
        setListener();
    }
    private void setListener() {
        btnDone.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        tvMsg.setOnTouchListener(this);
    }

    private void initView(View view){
      llSignaturePad = view.findViewById(R.id.llSignaturePad);
      btnClear = view.findViewById(R.id.btnClear);
      btnDone = view.findViewById(R.id.btnDone);
      tvMsg = view.findViewById(R.id.tvMsg);
      rrBtn = view.findViewById(R.id.rrBtn);
    }
    private void initVar(){
        activity = (SecondActivity) getActivity();
        if(activity == null)
            return;
        captureSignatureView = new CaptureSignatureView(activity, null);
        llSignaturePad.addView(captureSignatureView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    private void getAndSaveImage(){
        activity.SaveImage(captureSignatureView.getBitmap());
        activity.getFileUriAndStore();
    }

    private void alertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setCancelable(true)
                .setTitle("Confirmation")
                .setMessage("Are sure to save this Signature?")
                .setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getAndSaveImage();
                        captureSignatureView.ClearCanvas();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                captureSignatureView.ClearCanvas();
            }
        });

       final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface ar) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(activity.getResources().getColor(R.color.grey3_E));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(activity.getResources().getColor(R.color.grey3_E));
            }
        });
        dialog.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnClear : captureSignatureView.ClearCanvas();
                 break;
            case R.id.btnDone :  alertDialog();
                 break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.tvMsg) {
            tvMsg.setVisibility(View.GONE);
            rrBtn.setVisibility(View.VISIBLE);
        }
        return false;
    }

}
