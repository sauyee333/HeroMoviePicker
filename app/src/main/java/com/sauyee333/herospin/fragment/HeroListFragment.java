package com.sauyee333.herospin.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sauyee333.herospin.R;
import com.sauyee333.herospin.adapter.HeroListAdapter;
import com.sauyee333.herospin.listener.MainListener;
import com.sauyee333.herospin.network.ProgressSubscriber;
import com.sauyee333.herospin.network.SubscribeOnResponseListener;
import com.sauyee333.herospin.network.marvel.model.characterList.CharacterInfo;
import com.sauyee333.herospin.network.marvel.model.characterList.Data;
import com.sauyee333.herospin.network.marvel.model.characterList.Results;
import com.sauyee333.herospin.network.marvel.model.characterList.Thumbnail;
import com.sauyee333.herospin.network.marvel.rest.MarvelRestClient;
import com.sauyee333.herospin.utils.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sauyee on 29/10/16.
 */

public class HeroListFragment extends Fragment {

    public static final int CHARACTER_COUNT_PER_PAGE = 10;

    @Bind(R.id.list)
    RecyclerView mRecyclerListView;

//    @Bind(R.id.inputSearch)
//    EditText inputSearch;

    private Context mContext;
    private MainListener mListener;
    private HeroListAdapter mAdapter;

    private int mCharacterListTotal = 0;
    private int mCharacterOffset = -1;
    private int mCharacterLimit = 0;
    private int mCharacterIndex = -1;
    private CharacterInfo mCharacterInfo;

    private SubscribeOnResponseListener onGetCharacterListHandler = new SubscribeOnResponseListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
            if (characterInfo != null) {
                Data data = characterInfo.getData();
                if (data != null) {
                    Results[] results = data.getResults();
                    if (results != null) {
                        for (int i = 0; i < results.length; i++) {
                            Results results1 = results[i];
                            Thumbnail thumbnail = results1.getThumbnail();
                            String imgUrl;
                            if (thumbnail != null) {
                                imgUrl = generateImageUrl(thumbnail.getPath(), Constants.MARVEL_IMAGE_PORTRAIT_MEDIUM, thumbnail.getExtension());
                            }
//                            _Debug(results1.getName() + " " + results1.getDescription());
//                            _Debug(results1.getId() + " " + results1.getResourceURI());
                        }
                        List<Results> resultsList = Arrays.asList(results);
                        updateHeroList(resultsList);
                    }
                }
            }
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hero_list, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();

        setupListConfig();

        getCharacterList(CHARACTER_COUNT_PER_PAGE, 0);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MainListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainListener");
        }
    }

    private void setupListConfig() {
        mRecyclerListView.setHasFixedSize(true);
//        GridLayoutManager layoutManager = new GridLayoutManager(
//                mContext, 2);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerListView.setLayoutManager(layoutManager);
    }

    private void updateHeroList(List<Results> resultsList) {
        if (isAdded() && !isRemoving()) {
            mAdapter = new HeroListAdapter(mContext, getActivity(), mListener, resultsList);
            mRecyclerListView.setAdapter(mAdapter);
        }
    }

    private void getCharacterList(int limit, int offset) {
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        String modified = Constants.MARVEL_QUERY_ORDER_MODIFIED;
        String limitStr = (limit <= 0) ? null : Integer.toString(limit);
        String offsetStr = (limit <= 0 && offset <= 0) ? null : Integer.toString(offset);
        MarvelRestClient.getInstance().getCharacterListApi(new ProgressSubscriber<CharacterInfo>(onGetCharacterListHandler, mContext, true, true),
                apiKey, timeStamp, hash,
                null, null, modified,
                limitStr, offsetStr, null);
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
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
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

    private void resetCharacterInfo() {
        mCharacterListTotal = 0;
        mCharacterOffset = -1;
        mCharacterLimit = 0;
        mCharacterIndex = -1;
        mCharacterInfo = null;
    }
}
