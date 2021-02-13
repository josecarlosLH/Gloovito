package com.example.gloovito.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gloovito.R;
public class ChargeFragment extends Dialog {
    public ImageView image;

    public ChargeFragment(@NonNull Context context) {
        super(context);
    }

    public ChargeFragment(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ChargeFragment(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}