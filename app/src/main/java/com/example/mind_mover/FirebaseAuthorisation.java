package com.example.mind_mover;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class FirebaseAuthorisation {

    static ConnectivityManager CM;
    static NetworkInfo ninfo;


    public static void check (final Context context) {
        CM = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        ninfo = CM.getActiveNetworkInfo();
        if (!(ninfo != null && ninfo.isConnected())) {
            new AlertDialog.Builder(context).setTitle("Alert").setCancelable(false).setMessage("Please connect to the Internet.")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Redirector.from(context).goToLogin();
                        }
                    }).show();
        }
        else {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                new AlertDialog.Builder(context).setTitle("Alert").setCancelable(false).setMessage("Please login again.").setPositiveButton("Okay.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Redirector.from(context).goToLogin();
                    }
                }).show();
            }
        }
    }
}

