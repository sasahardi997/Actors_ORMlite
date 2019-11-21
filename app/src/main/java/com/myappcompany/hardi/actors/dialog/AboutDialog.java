package com.myappcompany.hardi.actors.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AboutDialog extends AlertDialog.Builder {


    public AboutDialog(Context context) {
        super(context);

        setTitle("About");
        setMessage("Aleksandar Hardi");
        setCancelable(false);

        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public AlertDialog prepareDialog(){
        AlertDialog dialog=create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
