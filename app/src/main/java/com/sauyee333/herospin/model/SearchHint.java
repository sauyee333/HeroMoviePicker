package com.sauyee333.herospin.model;

import com.sauyee333.herospin.network.marvel.model.characterList.Results;

/**
 * Created by sauyee on 1/11/16.
 */

public class SearchHint {
    private Results results;

    public SearchHint(Results results) {
        super();
        this.results = results;
    }

    public String getSearchWord() {
        return results.getName();
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

}
