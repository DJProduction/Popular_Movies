package com.example.johnsond.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnsonD on 9/29/15.
 */
public class MovieItemListSaved implements Parcelable {

    private  String OriginalTitle;
    private  String MovieImage;
    private  String OverView; //called overview in themovieapi
    private  String VoteAverage; //called vote_averge in themovieapi
    private  String VoteCount;
    private  String ReleaseDate;

    //*********** Constructor Methods ******************//
    public MovieItemListSaved  (String movieImage) { this.MovieImage = movieImage;}

    public MovieItemListSaved (){}

    //*********** Setter Methods ******************//

    public void setOriginalTitle(String OriginalTitle)
    {
        this.OriginalTitle = OriginalTitle;
    }

    public void setMovieImage(String movieImage)
    {
        this.MovieImage = movieImage;
    }

    public void setOverView(String overView)
    {
        this.OverView = overView;
    }

    public void setVoteAverage(String voteAverage)
    {
        this.VoteAverage = voteAverage;
    }

    public void setVoteCount(String voteCount)
    {
        this.VoteCount = voteCount;
    }

    public void setReleaseDate(String releaseDate)
    {
        this.ReleaseDate = releaseDate;
    }

    //*********** Getter Methods ****************//

    public String getOriginalTitle()
    {
        return OriginalTitle;
    }

    public String getMovieImage() { return MovieImage; }

    public String getOverView()
    {
        return OverView;
    }

    public String getVoteAverage()
    {
        return VoteAverage;
    }

    public String getVoteCount()
    {
        return VoteCount;
    }

    public String getReleaseDate()
    {
        return ReleaseDate;
    }


    private MovieItemListSaved(Parcel in) {
        OriginalTitle = in.readString();
        MovieImage = in.readString();
        OverView = in.readString();
        VoteAverage = in.readString();
        VoteCount = in.readString();
        ReleaseDate = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(OriginalTitle);
        out.writeString(MovieImage);
        out.writeString(OverView);
        out.writeString(VoteAverage);
        out.writeString(VoteCount);
        out.writeString(ReleaseDate);
    }

    public static final Parcelable.Creator<MovieItemListSaved> CREATOR = new Parcelable.Creator<MovieItemListSaved>() {
        public MovieItemListSaved createFromParcel(Parcel in) {
            return new MovieItemListSaved(in);
        }

        public MovieItemListSaved[] newArray(int size) {
            return new MovieItemListSaved[size];
        }
    };
}
