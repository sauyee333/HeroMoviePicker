package com.sauyee333.herospin.network.omdb.model.searchapi;

/**
 * Created by sauyee on 29/10/16.
 */

public class MovieInfo {
    private SearchInfo[] Search;

    private String totalResults;

    private String Response;

    public SearchInfo[] getSearch() {
        return Search;
    }

    public void setSearch(SearchInfo[] Search) {
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
        return "ClassPojo [SearchInfo = " + Search + ", totalResults = " + totalResults + ", Response = " + Response + "]";
    }
}
