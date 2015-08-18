package com.example.johnsond.popularmovies;

/**
 * Created by JohnsonD on 7/27/15.
 */
public class MovieItem {
    private  String OriginalTitle="";
    private  int MovieImage;
    private  String Url="";
    private  String Description=""; //called overview in themovieapi
    private  String UserRating=""; //called vote_averge in themovieapi
    private  String ReleaseDate="";

    //*********** Set Methods ******************//

    public void setOriginalTitle(String OriginalTitle)
    {
        this.OriginalTitle = OriginalTitle;
    }

    public void setMovieImage(int MovieImage)
    {
        this.MovieImage = MovieImage;
    }

    public void setUrl(String Url)
    {
        this.Url = Url;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public void setUserRating(String userRating)
    {
        UserRating = userRating;
    }

    public void setReleaseDate(String releaseDate)
    {
        ReleaseDate = releaseDate;
    }

    //*********** Get Methods ****************//

    public String getOriginalTitle()
    {
        return OriginalTitle;
    }

    public int getMovieImage() { return MovieImage; }

    public String getUrl()
    {
        return Url;
    }

    public String getDescription()
    {
        return Description;
    }

    public String getUserRating()
    {
        return UserRating;
    }

    public String getReleaseDate()
    {
        return ReleaseDate;
    }

}
