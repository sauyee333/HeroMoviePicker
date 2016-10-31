package com.sauyee333.herospin.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sauyee333.herospin.R;
import com.sauyee333.herospin.adapter.HeroListAdapter;
import com.sauyee333.herospin.listener.HeroCharacterListener;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by sauyee on 29/10/16.
 */

public class HeroListFragment extends Fragment implements HeroCharacterListener {

    public static final int CHARACTER_COUNT_PER_PAGE = 15;
    public static final int CHARACTER_COUNT_PER_ROW = 3;

    @Bind(R.id.list)
    RecyclerView mRecyclerListView;

//    @Bind(R.id.inputSearch)
//    EditText inputSearch;

    private Context mContext;
    private MainListener mListener;
    private HeroListAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    private int mCharacterListTotal = 0;
    private int mCharacterOffset = 0;
    private int mCharacterLimit = 0;
    private CharacterInfo mCharacterInfo;
    private int mFetchPage = 0;
    private List<Results> mResultsList;

    private SubscribeOnResponseListener onGetCharacterListHandler = new SubscribeOnResponseListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
            if (characterInfo != null) {
                Data data = characterInfo.getData();
                if (data != null) {
                    mCharacterOffset = data.getOffset();
                    mCharacterListTotal = data.getTotal();
                    mCharacterLimit = data.getLimit();
//                    _Debug("onnext offset: " + mCharacterOffset);
//                    _Debug("onnext total: " + mCharacterListTotal);
//                    _Debug("onnext limit: " + mCharacterLimit);

                    refreshCurrentFetchPage();
                    Results[] results = data.getResults();
                    if (results != null) {
                        for (int i = 0; i < results.length; i++) {
                            Results results1 = results[i];
                            Thumbnail thumbnail = results1.getThumbnail();
                            String imgUrl;
                            if (thumbnail != null) {
                                imgUrl = generateImageUrl(thumbnail.getPath(), Constants.MARVEL_IMAGE_PORTRAIT_MEDIUM, thumbnail.getExtension());
//                                _Debug("imgUrl: " + imgUrl);
                            }
//                            _Debug(results1.getName() + " " + results1.getDescription());
//                            _Debug(results1.getId() + " " + results1.getResourceURI());
                        }
                        mResultsList = new ArrayList<>(Arrays.asList(results));
                        updateHeroList(mResultsList);
                    }
                }
            }
        }

        @Override
        public void onError(String errorMsg) {
            displayErrorMessage(errorMsg);
        }
    };

    private SubscribeOnResponseListener onAddCharacterListHandler = new SubscribeOnResponseListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
            if (characterInfo != null) {
                Data data = characterInfo.getData();
                if (data != null) {
                    mCharacterOffset = data.getOffset();
                    mCharacterListTotal = data.getTotal();
                    mCharacterLimit = data.getLimit();
//                    _Debug("2. onnext offset: " + mCharacterOffset);
//                    _Debug("2. onnext total: " + mCharacterListTotal);
//                    _Debug("2. onnext limit: " + mCharacterLimit);
                    refreshCurrentFetchPage();
                    Results[] results = data.getResults();
                    if (results != null) {
                        for (int i = 0; i < results.length; i++) {
                            Results results1 = results[i];
                            Thumbnail thumbnail = results1.getThumbnail();
                            String imgUrl;
                            if (thumbnail != null) {
                                imgUrl = generateImageUrl(thumbnail.getPath(), Constants.MARVEL_IMAGE_PORTRAIT_MEDIUM, thumbnail.getExtension());
//                                _Debug("imgUrl: " + imgUrl);
                            }
//                            _Debug(results1.getName() + " " + results1.getDescription());
//                            _Debug(results1.getId() + " " + results1.getResourceURI());
                        }

                        List<Results> newResults = new ArrayList<>(Arrays.asList(results));
                        mResultsList.addAll(newResults);
                        mAdapter.notifyDataSetChanged();
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
        setupScrollListener();

        getCharacterList(CHARACTER_COUNT_PER_PAGE, mCharacterOffset, false);
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

    @Override
    public void onCharacterClick(Results results) {
        if (results != null) {
            Thumbnail thumbnail = results.getThumbnail();
            String imgUrl;
            if (thumbnail != null) {
                imgUrl = generateImageUrl(thumbnail.getPath(), Constants.MARVEL_IMAGE_PORTRAIT_MEDIUM, thumbnail.getExtension());
            }
        }
    }

    private void setupListConfig() {
        mRecyclerListView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(
                mContext, CHARACTER_COUNT_PER_ROW);
        mRecyclerListView.setLayoutManager(mLayoutManager);
    }

    private void refreshCurrentFetchPage() {
        mFetchPage = mCharacterOffset / mCharacterLimit;
//        _Debug("refreshCurrentFetchPage mFetchPage :" + mFetchPage);
    }

    private void setupScrollListener() {
        mRecyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

//                _Debug("\nvisibleItemCount: " + visibleItemCount + " " + firstVisibleItemPosition + " " + totalItemCount);
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    int newPage = mCharacterOffset + 1;
                    int totalPage = mCharacterListTotal / mCharacterLimit;
                    int remainItems = mCharacterListTotal % mCharacterLimit;
//                    _Debug("\ncalc: " + mCharacterListTotal + " " + mCharacterLimit);
//                    _Debug("total remain: " + totalPage + " " + remainItems);
                    if (remainItems > 0) {
                        totalPage += 1;
//                        _Debug("total remain: " + totalPage + " " + remainItems);
                    }
//                    _Debug("new: " + newPage + " " + mFetchPage + " " + totalPage);
                    if (newPage > mFetchPage && newPage <= totalPage) {
                        int newOffset = mCharacterOffset + mCharacterLimit;
//                        _Debug("newoffset: " + newOffset);
                        getCharacterList(CHARACTER_COUNT_PER_PAGE, newOffset, true);
                        mFetchPage = newPage;
//                        _Debug("nneed fetch more");
                    }
                }
            }
        });
    }

    private void updateHeroList(List<Results> resultsList) {
        if (isAdded() && !isRemoving()) {
            mAdapter = new HeroListAdapter(mContext, getActivity(), HeroListFragment.this, resultsList);
            mRecyclerListView.setAdapter(mAdapter);
        }
    }

    private void getCharacterList(int limit, int offset, boolean addList) {
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        String modified = Constants.MARVEL_QUERY_ORDER_MODIFIED;
        String limitStr = (limit <= 0) ? null : Integer.toString(limit);
        String offsetStr = (limit <= 0 && offset <= 0) ? null : Integer.toString(offset);
        String modifiedSince = Constants.MARVEL_QUERY_MODIFIED_SINCE_DATE;
        Subscriber<CharacterInfo> subscriber;
        if (addList) {
            subscriber = new ProgressSubscriber<CharacterInfo>(onAddCharacterListHandler, mContext, true, true);
        } else {
            subscriber = new ProgressSubscriber<CharacterInfo>(onGetCharacterListHandler, mContext, true, true);
        }
        MarvelRestClient.getInstance().getCharacterListApi(subscriber,
                apiKey, timeStamp, hash,
                null, null, modified,
                limitStr, offsetStr, modifiedSince);
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
        mCharacterOffset = 0;
        mCharacterInfo = null;
    }
}
