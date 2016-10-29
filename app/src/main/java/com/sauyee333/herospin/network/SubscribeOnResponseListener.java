package com.sauyee333.herospin.network;

/**
 * Created by sauyee on 28/10/16.
 */

public interface SubscribeOnResponseListener<T> {
    void onNext(T t);

    void onError(String errorMsg);
}
