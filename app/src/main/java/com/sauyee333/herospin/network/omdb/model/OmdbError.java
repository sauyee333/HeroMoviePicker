package com.sauyee333.herospin.network.omdb.model;

/**
 * Created by sauyee on 29/10/16.
 */

public class OmdbError {
    private String Response;

    private String Error;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String Response) {
        this.Response = Response;
    }

    public String getError() {
        return Error;
    }

    public void setError(String Error) {
        this.Error = Error;
    }

    @Override
    public String toString() {
        return "ClassPojo [Response = " + Response + ", Error = " + Error + "]";
    }
}
