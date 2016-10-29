package com.sauyee333.herospin.network.omdb.model.search;

import com.sauyee333.herospin.network.omdb.model.search.Search;

/**
 * Created by sauyee on 29/10/16.
 */

public class SearchResponse {
    private com.sauyee333.herospin.network.omdb.model.search.Search[] Search;

    private String totalResults;

    private String Response;

    public Search[] getSearch() {
        return Search;
    }

    public void setSearch(Search[] Search) {
        this.Search = Search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return Response;
    }

    public void setResponse(String Response) {
        this.Response = Response;
    }

    @Override
    public String toString() {
        return "ClassPojo [Search = " + Search + ", totalResults = " + totalResults + ", Response = " + Response + "]";
    }
}
