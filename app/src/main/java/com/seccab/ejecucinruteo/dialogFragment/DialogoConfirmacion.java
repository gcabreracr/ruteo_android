package com.seccab.ejecucinruteo.dialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class DialogoConfirmacion extends DialogFragment {
    private String msg;
    private String titulo;

    public DialogoConfirmacion setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public DialogoConfirmacion setTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public interface DialogoConfirmacionListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    DialogoConfirmacionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (DialogoConfirmacionListener) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle args) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setTitle(titulo)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(DialogoConfirmacion.this);

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogNegativeClick(DialogoConfirmacion.this);
            }
        });
        return builder.create();

    }


}
