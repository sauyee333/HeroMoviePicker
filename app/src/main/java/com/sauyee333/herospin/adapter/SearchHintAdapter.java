package com.sauyee333.herospin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sauyee333.herospin.R;
import com.sauyee333.herospin.model.SearchHint;

/**
 * Created by sauyee on 1/11/16.
 */

public class SearchHintAdapter extends ArrayAdapter<SearchHint> {
    private LayoutInflater inflater;
    String searchT;
    Context context;

    public void add(SearchHint object, String searchText, Context con) {
        this.searchT = searchText;
        this.context = context;
        super.add(object);
    }

    public SearchHintAdapter(Context context) {
        super(context, R.layout.item_search_hint);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertedView, ViewGroup parent) {
        View view = convertedView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_search_hint, null);
        }

        final SearchHint item = getItem(position);
        TextView suggestText = ((TextView) view.findViewById(R.id.suggestText));
        suggestText.setText(item.getSearchWord());

        return view;
    }
}
