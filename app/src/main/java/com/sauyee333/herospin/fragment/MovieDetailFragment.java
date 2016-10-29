package com.sauyee333.herospin.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sauyee333.herospin.R;
import com.sauyee333.herospin.listener.MainListener;
import com.sauyee333.herospin.network.ProgressSubscriber;
import com.sauyee333.herospin.network.SubscribeOnResponseListener;
import com.sauyee333.herospin.network.omdb.model.imdb.ImdbInfo;
import com.sauyee333.herospin.network.omdb.rest.OmdbRestClient;
import com.sauyee333.herospin.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sauyee on 29/10/16.
 */

public class MovieDetailFragment extends Fragment {

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String jsonMyObject = bundle.getString(Constants.BUNDLE_STRING_CONTENTS);
            mImdbInfo = new Gson().fromJson(jsonMyObject, ImdbInfo.class);
            updateMovieDetail(mImdbInfo);
        }

        if (mImdbInfo == null) {
//            getMovieDetail("tt1922373");
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
                    + " must implement TabletLoadingListener");
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

    private void updateMovieDetail(ImdbInfo imdbInfo){
        if(imdbInfo != null) {
            actors.setText(imdbInfo.getActors());
            genre.setText(imdbInfo.getGenre());
            language.setText(imdbInfo.getLanguage());
            year.setText(imdbInfo.getYear());
            imdb.setText(imdbInfo.getImdbRating());
            plot.setText(imdbInfo.getPlot());
        }
    }
}
