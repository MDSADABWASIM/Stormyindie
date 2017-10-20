package com.example.user.stormyindie;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;


public class AlertDialog extends DialogFragment {
    public Dialog OnCreateDialog(Bundle savedInstanceState){
        Context context = getActivity();
    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(R.string.title_for_dialog)
                .setMessage(R.string.Message_for_dialog)
                .setPositiveButton(R.string.poso , null);
        android.app.AlertDialog dialog = builder.create();
        return dialog;
}
}
