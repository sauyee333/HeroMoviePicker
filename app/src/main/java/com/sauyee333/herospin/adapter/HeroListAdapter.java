package com.sauyee333.herospin.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sauyee333.herospin.R;
import com.sauyee333.herospin.listener.MainListener;
import com.sauyee333.herospin.network.marvel.model.characterList.Results;
import com.sauyee333.herospin.network.marvel.model.characterList.Thumbnail;
import com.sauyee333.herospin.utils.Constants;

import java.util.List;

/**
 * Created by sauyee on 30/10/16.
 */

public class HeroListAdapter extends RecyclerView.Adapter<HeroListAdapter.ViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<Results> mHeroData;
    private MainListener mListener;

    public HeroListAdapter(Context context, Activity activity, MainListener listener, List<Results> heroData) {
        mContext = context;
        mActivity = activity;
        mListener = listener;
        this.mHeroData = heroData;
    }

    @Override
    public HeroListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hero, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HeroListAdapter.ViewHolder holder, final int rowPosition) {
        Results results = mHeroData.get(rowPosition);
        holder.title.setText(results.getName());

        Thumbnail thumbnail = results.getThumbnail();
        String imgUrl = null;
        if (thumbnail != null) {
            imgUrl = generateImageUrl(thumbnail.getPath(), Constants.MARVEL_IMAGE_PORTRAIT_MEDIUM, thumbnail.getExtension());
        }

        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(mContext.getResources().getDrawable(R.drawable.holder_portrait_medium))
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mHeroData.size();
    }

    private String generateImageUrl(String path, String variant, String extension) {
        String imageUrl = "";
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(extension) && !TextUtils.isEmpty(variant)) {
            imageUrl = path + "/" + variant + "." + extension;
        }
        return imageUrl;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        ImageView image;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
