package com.sauyee333.herospin.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sauyee333.herospin.R;
import com.sauyee333.herospin.listener.MainListener;
import com.sauyee333.herospin.network.ProgressSubscriber;
import com.sauyee333.herospin.network.SubscribeOnResponseListener;
import com.sauyee333.herospin.network.marvel.model.characterList.CharacterInfo;
import com.sauyee333.herospin.network.marvel.model.characterList.Data;
import com.sauyee333.herospin.network.marvel.model.characterList.Results;
import com.sauyee333.herospin.network.marvel.model.characterList.Thumbnail;
import com.sauyee333.herospin.network.marvel.rest.MarvelRestClient;
import com.sauyee333.herospin.network.omdb.model.imdb.ImdbInfo;
import com.sauyee333.herospin.network.omdb.model.searchapi.MovieInfo;
import com.sauyee333.herospin.network.omdb.model.searchapi.SearchInfo;
import com.sauyee333.herospin.network.omdb.rest.OmdbRestClient;
import com.sauyee333.herospin.utils.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import butterknife.ButterKnife;

/**
 * Created by sauyee on 29/10/16.
 */

public class MoviePickFragment extends Fragment {

    private Context mContext;
    private MainListener mListener;

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

                        if (mCharacterOffset == -1) {
                            mCharacterListTotal = data.getTotal();
//                            _Debug("mCharacterListTotal: " + mCharacterListTotal);
                            //generate random number and get a character
                            int limit = 10;
                            mCharacterOffset = new Random().nextInt(mCharacterListTotal);
                            getCharacterList(limit, mCharacterOffset);
                        } else {
                            mCharacterLimit = results.length;
                            mCharacterInfo = characterInfo;
                            if (mCharacterLimit > 0) {
                                int chosenIndex = -1;
                                for (int i = 0; i < mCharacterLimit; i++) {
                                    Results results1 = results[i];
                                    Thumbnail thumbnail = results1.getThumbnail();
                                    String imgUrl;
                                    if (thumbnail != null) {
                                        imgUrl = generateImageUrl(thumbnail.getPath(), Constants.MARVEL_IMAGE_PORTRAIT_MEDIUM, thumbnail.getExtension());
//                                        _Debug("imgUrl: (" + imgUrl + ")");

                                        if (chosenIndex < 0 && !imgUrl.contains(getResources().getString(R.string.imageNotAvailable))) {
                                            chosenIndex = i;
                                        }
                                    }
//                                    _Debug(results1.getName() + " " + results1.getDescription());
//                                    _Debug(results1.getId() + " " + results1.getResourceURI());
                                }
//search movie for chosen character
                                if (chosenIndex >= 0) {
                                    mCharacterIndex = chosenIndex;
//                                    _Debug("results[chosenIndex].getName(): " + results[chosenIndex].getName());
                                    getMovieList(results[chosenIndex].getName());
                                } else {
                                    //shows no image error
                                }
                            } else {
                                resetCharacterInfo();
                                //show get character list error
                            }
                        }
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

    private SubscribeOnResponseListener onGetMovieListHandler = new SubscribeOnResponseListener<MovieInfo>() {
        @Override
        public void onNext(MovieInfo movieInfo) {
            if (movieInfo != null) {
                String response = movieInfo.getResponse();
                if (!TextUtils.isEmpty(response)) {
                    if (response.equals("False")) {
                        displayErrorMessage(movieInfo.getError());
                    } else {
                        String total = movieInfo.getTotalResults();
                        int totalInt = Integer.parseInt(total);
                        if (totalInt > 0) {
                            SearchInfo[] searchInfo = movieInfo.getSearch();
                            SearchInfo searchInfo1 = searchInfo[0];
                            String imdb = searchInfo1.getImdbID();
                            if (!TextUtils.isEmpty(imdb)) {
                                getMovieDetail(imdb);
                            }
                        } else {
                            //not found
//                    search again

                        }
                    }
                }
            }
        }

        @Override
        public void onError(String errorMsg) {
            displayErrorMessage(errorMsg);
        }
    };

    private SubscribeOnResponseListener onGetMovieDetailHandler = new SubscribeOnResponseListener<ImdbInfo>() {
        @Override
        public void onNext(ImdbInfo imdbInfo) {
            String response = imdbInfo.getResponse();
            if (!TextUtils.isEmpty(response)) {
                if (response.equals("False")) {
                    displayErrorMessage(imdbInfo.getError());
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_STRING_CONTENTS, new Gson().toJson(imdbInfo));
                    loadMovieDetailPage(bundle);
                }
            }
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
//        getCharacterId("1011334");
//        getMovieList("Batman");
//        getMovieDetail("tt1922373");

//        generateHash("1477755055051", getResources().getString(R.string.marvelPrivateKey), getResources().getString(R.string.marvelPublicKey));
        initGetCharacterTotal();
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

    private void loadMovieDetailPage(Bundle bundle) {
        Fragment fragment = new MovieDetailFragment();
        fragment.setArguments(bundle);
        mListener.onShowFragment(fragment, false);
    }

    private void resetCharacterInfo() {
        mCharacterListTotal = 0;
        mCharacterOffset = -1;
        mCharacterLimit = 0;
        mCharacterIndex = -1;
        mCharacterInfo = null;
    }

    private void initGetCharacterTotal() {
        int limit = 1;
        int offset = 0;
        resetCharacterInfo();
        //get one item just to get the total character count
        getCharacterList(limit, offset);
    }

    private void getCharacterList(int limit, int offset) {
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        String modified = Constants.MARVEL_QUERY_ORDER_MODIFIED;
        String limitStr = (limit <= 0) ? null : Integer.toString(limit);
        String offsetStr = (limit <= 0 && offset <= 0) ? null : Integer.toString(offset);
        String modifiedSince = Constants.MARVEL_QUERY_MODIFIED_SINCE_DATE;
        MarvelRestClient.getInstance().getCharacterListApi(new ProgressSubscriber<CharacterInfo>(onGetCharacterListHandler, mContext, true, true),
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


    private void getMovieList(String search) {
        if (!TextUtils.isEmpty(search)) {
            OmdbRestClient.getInstance().getMovieListApi(new ProgressSubscriber<MovieInfo>(onGetMovieListHandler, mContext, true, true),
                    search);
        }
    }

    private void getMovieDetail(String imdbId) {
        if (!TextUtils.isEmpty(imdbId)) {
            OmdbRestClient.getInstance().getMovieDetailApi(new ProgressSubscriber<ImdbInfo>(onGetMovieDetailHandler, mContext, true, true),
                    imdbId);
        }
    }
}
