package com.sauyee333.herospin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sauyee333.herospin.R;
import com.sauyee333.herospin.network.ProgressSubscriber;
import com.sauyee333.herospin.network.SubscribeOnResponseListener;
import com.sauyee333.herospin.network.marvel.model.characterList.CharacterInfo;
import com.sauyee333.herospin.network.marvel.rest.MarvelRestClient;
import com.sauyee333.herospin.network.omdb.rest.OmdbRestClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.ButterKnife;

/**
 * Created by sauyee on 29/10/16.
 */

public class MainFragment extends Fragment {
    private Context mContext;
    private SubscribeOnResponseListener onGetCharacterListHandler = new SubscribeOnResponseListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
        }

        @Override
        public void onError(String errorMsg) {
            displayErrorMessage(errorMsg);
        }
    };
    private SubscribeOnResponseListener onGetCharacterIdHandler = new SubscribeOnResponseListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
        }

        @Override
        public void onError(String errorMsg) {
            displayErrorMessage(errorMsg);
        }
    };

    private SubscribeOnResponseListener onGetMovieListHandler = new SubscribeOnResponseListener<Void>() {
        @Override
        public void onNext(Void characterInfo) {
        }

        @Override
        public void onError(String errorMsg) {
            displayErrorMessage(errorMsg);
        }
    };

    private SubscribeOnResponseListener onGetMovieDetailHandler = new SubscribeOnResponseListener<Void>() {
        @Override
        public void onNext(Void characterInfo) {
        }

        @Override
        public void onError(String errorMsg) {
            displayErrorMessage(errorMsg);
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();
//        getCharacterList();
        getCharacterId("1011334");
        return view;
    }

    private void getCharacterList() {
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        MarvelRestClient.getInstance().getCharacterListApi(new ProgressSubscriber<CharacterInfo>(onGetCharacterListHandler, mContext, true, true),
                apiKey,
                timeStamp,
                hash,
                null, null, null, null);
    }

    private void getCharacterId(String id) {
        String characterId = id;
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        MarvelRestClient.getInstance().getCharacterIdApi(new ProgressSubscriber<CharacterInfo>(onGetCharacterIdHandler, mContext, true, true),
                characterId,
                apiKey,
                timeStamp,
                hash);
    }

    private void displayErrorMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    private String getTimeStamp() {
        Long tsLong = System.currentTimeMillis();
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


    private void getMovieList(String id) {
        String characterId = id;
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        OmdbRestClient.getInstance().getMovieListApi(new ProgressSubscriber<Void>(onGetMovieListHandler, mContext, true, true),
                "spiderman");
    }
}
