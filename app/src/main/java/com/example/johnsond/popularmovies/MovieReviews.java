package com.example.johnsond.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JohnsonD on 2/22/16.
 */
public class MovieReviews implements Parcelable {

    public static final Parcelable.Creator<MovieReviews> CREATOR = new Parcelable.Creator<MovieReviews>() {
        public MovieReviews createFromParcel(Parcel in) {
            return new MovieReviews(in);
        }

        public MovieReviews[] newArray(int size) {
            return new MovieReviews[size];
        }
    };
    private String author;

    //*********** Constructor Methods ******************//
    private String review;
    ;

    //*********** Setter Methods ******************//

    public MovieReviews() {
    }

    private MovieReviews(Parcel in) {
        author = in.readString();
        review = in.readString();
    }

    //*********** Getter  Methods ******************//

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(author);
        out.writeString(review);
    }
}
