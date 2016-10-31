package com.sauyee333.herospin.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sauyee333.herospin.R;
import com.sauyee333.herospin.listener.MainListener;
import com.sauyee333.herospin.network.ProgressSubscriber;
import com.sauyee333.herospin.network.SubscribeOnResponseListener;
import com.sauyee333.herospin.network.marvel.model.characterList.Results;
import com.sauyee333.herospin.network.omdb.model.imdb.ImdbInfo;
import com.sauyee333.herospin.network.omdb.model.searchapi.MovieInfo;
import com.sauyee333.herospin.network.omdb.model.searchapi.SearchInfo;
import com.sauyee333.herospin.network.omdb.rest.OmdbRestClient;
import com.sauyee333.herospin.utils.Constants;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sauyee on 29/10/16.
 */

public class MovieDetailFragment extends Fragment implements HeroListFragment.AddCharacterListener {

    private static final int MSG_SEARCH_HERO_MOVIE = 100;

    @Bind(R.id.actors)
    TextView actors;

    @Bind(R.id.genre)
    TextView genre;

    @Bind(R.id.language)
    TextView language;

    @Bind(R.id.year)
    TextView year;

    @Bind(R.id.imdb)
    TextView imdb;

    @Bind(R.id.plot)
    TextView plot;

    @Bind(R.id.poster)
    ImageView poster;

    @Bind(R.id.title)
    TextView title;

    private final CustomHandler mHandler = new CustomHandler(this);
    private Activity mActivity;
    private Context mContext;
    private MainListener mListener;

    private ImdbInfo mImdbInfo;

    private SubscribeOnResponseListener onGetMovieDetailHandler = new SubscribeOnResponseListener<ImdbInfo>() {
        @Override
        public void onNext(ImdbInfo imdbInfo) {
            String response = imdbInfo.getResponse();
            if (!TextUtils.isEmpty(response)) {
                if (response.equals("False")) {
                    displayErrorMessage(imdbInfo.getError());
                } else {
                    mImdbInfo = imdbInfo;
                    updateMovieDetail(imdbInfo);
                }
            }
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mActivity = getActivity();
        mContext = getContext();
        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String jsonMyObject = bundle.getString(Constants.BUNDLE_STRING_CONTENTS);
            mImdbInfo = new Gson().fromJson(jsonMyObject, ImdbInfo.class);
            updateMovieDetail(mImdbInfo);
        }

        if (mImdbInfo == null) {
            getMovieDetail("tt1922373");
        }
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @OnClick(R.id.btnClose)
    public void closePage() {
        if (mListener != null) {
            mListener.onFragmentBackPress();
        }
    }

    @OnClick(R.id.btnCharacter)
    public void chooseCharacter() {
        loadHeroListFragment();
    }

    @OnClick(R.id.btnHome)
    public void loadMoviePickFragment() {
        closePage();
    }

    @OnClick(R.id.btnSpin)
    public void spinAgain() {
//        getMovieList(search);
    }

    @Override
    public void confirmAddCharacter(Results results) {
        if (results != null) {
            String hero = results.getName();
            if (!TextUtils.isEmpty(hero)) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_STRING_HERO, hero);
                sendMessageWithBundle(MSG_SEARCH_HERO_MOVIE, bundle);
            }
        }
    }

    private void sendMessageWithBundle(int msgID, Bundle bundle) {
        Message msg = new Message();
        msg.what = msgID;
        if (bundle != null) {
            msg.setData(bundle);
        }
        mHandler.sendMessage(msg);
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

    private void displayErrorMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    private void updateMovieDetail(ImdbInfo imdbInfo) {
        if (imdbInfo != null) {
            actors.setText(imdbInfo.getActors());
            genre.setText(imdbInfo.getGenre());
            language.setText(imdbInfo.getLanguage());
            year.setText(imdbInfo.getYear());
            imdb.setText(imdbInfo.getImdbRating());
            plot.setText(imdbInfo.getPlot());
            title.setText(imdbInfo.getTitle());

            Glide.with(mContext)
                    .load(imdbInfo.getPoster())
                    .into(poster);
        }
    }

    private void loadHeroListFragment() {
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = new HeroListFragment();
                    fragment.setTargetFragment(MovieDetailFragment.this, 0);
                    mListener.onShowFragment(fragment, false);
                }
            });
        }
    }

    private void handleMessage(Message message) {
        switch (message.what) {
            case MSG_SEARCH_HERO_MOVIE: {
                Bundle bundle = message.getData();
                String hero = bundle.getString(Constants.BUNDLE_STRING_HERO);
                getMovieList(hero);
            }
            break;
        }
    }

    static class CustomHandler extends Handler {
        WeakReference<MovieDetailFragment> mFrag;

        CustomHandler(MovieDetailFragment aFragment) {
            mFrag = new WeakReference<>(aFragment);
        }

        @Override
        public void handleMessage(Message message) {
            MovieDetailFragment theFrag = mFrag.get();
            if (theFrag != null) {
                theFrag.handleMessage(message);
            }
        }
    }
}
