package com.sauyee333.herospin.network.marvel.model.characterList;

/**
 * Created by sauyee on 29/10/16.
 */

public class Urls {
    private String type;

    private String url;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ClassPojo [type = " + type + ", url = " + url + "]";
    }
}
