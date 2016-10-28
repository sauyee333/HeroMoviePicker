package com.sauyee333.herospin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sauyee333.herospin.R;
import com.sauyee333.herospin.network.ProgressSubscriber;
import com.sauyee333.herospin.network.SubscribeOnNextListener;
import com.sauyee333.herospin.network.marvel.model.characterList.CharacterInfo;
import com.sauyee333.herospin.network.marvel.rest.MarvelRestClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.ButterKnife;

/**
 * Created by sauyee on 29/10/16.
 */

public class MainFragment extends Fragment {
    private Context mContext;
    private SubscribeOnNextListener onGetCharacterListNext = new SubscribeOnNextListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
        }
    };
    private SubscribeOnNextListener onGetCharacterIdNext = new SubscribeOnNextListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();
//        getCharacterList();
        getCharacterId();
        return view;
    }

    private void getCharacterList() {
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        MarvelRestClient.getInstance().getCharacterListApi(new ProgressSubscriber<CharacterInfo>(onGetCharacterListNext, mContext, true, true),
                apiKey,
                timeStamp,
                hash,
                null, null, null, null);
    }

    private void getCharacterId() {
        String characterId = "1011334";
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        MarvelRestClient.getInstance().getCharacterIdApi(new ProgressSubscriber<CharacterInfo>(onGetCharacterIdNext, mContext, true, true),
                characterId,
                apiKey,
                timeStamp,
                hash);
    }

    private String getTimeStamp() {
        Long tsLong = System.currentTimeMillis() / 1000;
        return tsLong.toString();
    }

    private String generateMd5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String generateHash(String timeStamp, String privateKey, String publicKey) {
        String hash = "";
        if (!TextUtils.isEmpty(timeStamp) && !TextUtils.isEmpty(privateKey) && !TextUtils.isEmpty(publicKey)) {
            hash = generateMd5(timeStamp + privateKey + publicKey);
        }
        return hash;
    }

    private String generateImageUrl(String path, String variant, String extension) {
        String imageUrl = "";
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(extension) && !TextUtils.isEmpty(variant)) {
            imageUrl = path + "/" + variant + "." + extension;
        }
        return imageUrl;
    }
}
