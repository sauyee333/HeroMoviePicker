package com.sauyee333.herospin.network.omdb.model.searchapi;

/**
 * Created by sauyee on 29/10/16.
 */

public class SearchInfo {
    private String Year;

    private String Type;

    private String Poster;

    private String imdbID;

    private String Title;

    public String getYear() {
        return Year;
    }

    public void setYear(String Year) {
        this.Year = Year;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String Poster) {
        this.Poster = Poster;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    @Override
    public String toString() {
        return "ClassPojo [Year = " + Year + ", Type = " + Type + ", Poster = " + Poster + ", imdbID = " + imdbID + ", Title = " + Title + "]";
    }
}
