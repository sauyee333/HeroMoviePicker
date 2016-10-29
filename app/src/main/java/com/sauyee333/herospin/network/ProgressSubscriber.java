package com.sauyee333.herospin.network;

import android.content.Context;

import com.sauyee333.herospin.utils.ErrorUtility;

import rx.Subscriber;

/**
 * Created by sauyee on 28/10/16.
 */

public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressChangeListener {
    private SubscribeOnResponseListener mSubscribeOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;
    private Context mContext;

    public ProgressSubscriber(SubscribeOnResponseListener subscribeOnNextListener, Context context, boolean showDialog, boolean dialogCancel) {
        mSubscribeOnNextListener = subscribeOnNextListener;
        mContext = context;
        if (showDialog && context != null) {
            mProgressDialogHandler = new ProgressDialogHandler(context, this, dialogCancel);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    @Override
    public void onStart() {
        showProgressDialog();
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        dismissProgressDialog();
        String errorMsg = ErrorUtility.parseApiError(throwable);
        if (mSubscribeOnNextListener != null) {
            mSubscribeOnNextListener.onError(errorMsg);
        }
    }

    @Override
    public void onNext(T t) {
        if (mSubscribeOnNextListener != null) {
            mSubscribeOnNextListener.onNext(t);
        }
    }

    @Override
    public void onCancelProgress() {
        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }
}
