package com.sauyee333.herospin.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

/**
 * Created by sauyee on 28/10/16.
 */

public class ProgressDialogHandler extends Handler {
    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;

    private Context mContext;
    private boolean mCancelable;
    private ProgressChangeListener mProgressChangeListener;
    private ProgressDialog mProgressDialog;

    public ProgressDialogHandler(Context context, ProgressChangeListener progressChangeListener, boolean cancelable) {
        super();
        mContext = context;
        mProgressChangeListener = progressChangeListener;
        mCancelable = cancelable;
    }

    private void initProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setCancelable(mCancelable);
        }
        if (mCancelable) {
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mProgressChangeListener.onCancelProgress();
                }
            });
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
                initProgressDialog();
                break;
            case DISMISS_PROGRESS_DIALOG:
                dismissProgressDialog();
                break;
        }
    }
}
