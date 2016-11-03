package com.sauyee333.herospin.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sauyee333.herospin.R;
import com.sauyee333.herospin.adapter.HeroListAdapter;
import com.sauyee333.herospin.adapter.SearchHintAdapter;
import com.sauyee333.herospin.listener.HeroCharacterListener;
import com.sauyee333.herospin.listener.MainListener;
import com.sauyee333.herospin.model.SearchHint;
import com.sauyee333.herospin.network.ProgressSubscriber;
import com.sauyee333.herospin.network.SubscribeOnResponseListener;
import com.sauyee333.herospin.network.marvel.model.characterList.CharacterInfo;
import com.sauyee333.herospin.network.marvel.model.characterList.Data;
import com.sauyee333.herospin.network.marvel.model.characterList.Results;
import com.sauyee333.herospin.network.marvel.rest.MarvelRestClient;
import com.sauyee333.herospin.utils.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by sauyee on 29/10/16.
 */

public class HeroListFragment extends Fragment implements HeroCharacterListener, TextWatcher {

    public interface AddCharacterListener {
        void confirmAddCharacter(Results results);
    }

    public static final int CHARACTER_COUNT_PER_PAGE = 15;
    public static final int CHARACTER_COUNT_PER_ROW = 3;
    public static final int SEARCH_HINT_COUNT_PER_PAGE = 6;

    @Bind(R.id.list)
    RecyclerView mRecyclerListView;

    @Bind(R.id.inputSearch)
    EditText inputSearch;

    @Bind(R.id.suggestList)
    ListView mSuggestListView;

    @Bind(R.id.btnCancel)
    TextView btnCancel;

    private Context mContext;
    private MainListener mListener;
    private HeroListAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    private int mCharacterListTotal = 0;
    private int mCharacterOffset = 0;
    private int mCharacterLimit = 0;
    private int mFetchPage = 0;
    private List<Results> mResultsList;
    private SearchHintAdapter mHintAdapter;


    private SubscribeOnResponseListener onGetCharacterListHandler = new SubscribeOnResponseListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
            if (characterInfo != null) {
                Data data = characterInfo.getData();
                if (data != null) {
                    mCharacterOffset = data.getOffset();
                    mCharacterListTotal = data.getTotal();
                    mCharacterLimit = data.getLimit();

                    refreshCurrentFetchPage();
                    Results[] results = data.getResults();
                    if (results != null) {
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
                    refreshCurrentFetchPage();
                    Results[] results = data.getResults();
                    if (results != null) {
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

    private SubscribeOnResponseListener onSearchCharacterListHandler = new SubscribeOnResponseListener<CharacterInfo>() {
        @Override
        public void onNext(CharacterInfo characterInfo) {
            if (characterInfo != null) {
                Data data = characterInfo.getData();
                if (data != null) {
                    showSearchSuggestList(data.getResults());
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
        setupSearchInput();
        setupSuggestList();
        setupScrollListener();

        getCharacterList(CHARACTER_COUNT_PER_PAGE, mCharacterOffset, false, null);
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
            AddCharacterListener listener;
            try {
                listener = (AddCharacterListener) getTargetFragment();
                if (listener != null) {
                    listener.confirmAddCharacter(results);
                }
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            closePage();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String search = getSearchEditText();
        if (!TextUtils.isEmpty(search)) {
            getCharacterList(SEARCH_HINT_COUNT_PER_PAGE, 0, false, search);
        }
    }

    @OnClick(R.id.btnCancel)
    public void cancelSearchHint() {
        mSuggestListView.setVisibility(View.GONE);
        mRecyclerListView.setVisibility(View.VISIBLE);
        mHintAdapter.clear();
        inputSearch.setText("");
        btnCancel.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.btnUp)
    public void closePage() {
        if (mListener != null) {
            mListener.onFragmentBackPress();
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

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    int newPage = mCharacterOffset + 1;
                    int totalPage = mCharacterListTotal / mCharacterLimit;
                    int remainItems = mCharacterListTotal % mCharacterLimit;
                    if (remainItems > 0) {
                        totalPage += 1;
                    }
                    if (newPage > mFetchPage && newPage <= totalPage) {
                        int newOffset = mCharacterOffset + mCharacterLimit;
                        getCharacterList(CHARACTER_COUNT_PER_PAGE, newOffset, true, null);
                        mFetchPage = newPage;
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

    private void getCharacterList(int limit, int offset, boolean addList, String nameStartsWith) {
        String apiKey = getResources().getString(R.string.marvelPublicKey);
        String timeStamp = getTimeStamp();
        String hash = generateHash(timeStamp, getResources().getString(R.string.marvelPrivateKey), apiKey);
        String orderBy = Constants.MARVEL_QUERY_ORDER_MODIFIED;
        String limitStr = (limit <= 0) ? null : Integer.toString(limit);
        String offsetStr = (limit <= 0 && offset <= 0) ? null : Integer.toString(offset);
        String modifiedSince = Constants.MARVEL_QUERY_MODIFIED_SINCE_DATE;
        Subscriber<CharacterInfo> subscriber;
        if (addList) {
            subscriber = new ProgressSubscriber<>(onAddCharacterListHandler, mContext, true, false);
        } else if (!TextUtils.isEmpty(nameStartsWith)) {
            subscriber = new ProgressSubscriber<>(onSearchCharacterListHandler, mContext, false, false);
        } else {
            subscriber = new ProgressSubscriber<>(onGetCharacterListHandler, mContext, true, false);
        }
        MarvelRestClient.getInstance().getCharacterListApi(subscriber,
                apiKey, timeStamp, hash,
                null, nameStartsWith, orderBy,
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

    private String getSearchEditText() {
        return (inputSearch != null) ? inputSearch.getText().toString() : "";
    }

    private void setupSuggestList() {
        if (mSuggestListView != null) {
            mHintAdapter = new SearchHintAdapter(mContext);
            mSuggestListView.setAdapter(mHintAdapter);
            mSuggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchHint hint = (SearchHint) parent.getItemAtPosition(position);
                    if (hint != null) {
                        onCharacterClick(hint.getResults());
                    }
                }
            });
        }
    }

    private void setupSearchInput() {
        if (inputSearch != null) {
            inputSearch.requestFocus();
            inputSearch.addTextChangedListener(this);

            inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String searchString = getSearchEditText();

                        if (!searchString.isEmpty()) {
                            getCharacterList(SEARCH_HINT_COUNT_PER_PAGE, 0, false, searchString);
                        }
                        handled = true;
                    }
                    return handled;
                }
            });

            inputSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });
        }
    }

    private void showSearchSuggestList(Results[] results) {
        if (isAdded() && !isRemoving()) {
            if (results != null && results.length > 0) {
                if (mRecyclerListView != null) {
                    mRecyclerListView.setVisibility(View.GONE);
                }

                if (btnCancel != null) {
                    btnCancel.setVisibility(View.VISIBLE);
                }

                if (mHintAdapter != null && mSuggestListView != null) {
                    mHintAdapter.clear();
                    for (int i = 0; i < results.length; i++) {
                        Results results1 = results[i];
                        mHintAdapter.add(new SearchHint(results1), results1.getName(), mContext);
                    }
                    mSuggestListView.setAdapter(mHintAdapter);
                    mSuggestListView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
