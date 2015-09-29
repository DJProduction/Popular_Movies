package com.example.johnsond.popularmovies;

/**
 * Created by JohnsonD on 7/27/15.
 */
public class MovieItem {
    private  String OriginalTitle;
    private  String MovieImage;
    private  String OverView; //called overview in themovieapi
    private  String VoteAverage; //called vote_averge in themovieapi
    private  String VoteCount;
    private  String ReleaseDate;

    //*********** Constructor Methods ******************//
    public MovieItem  (String movieImage) { this.MovieImage = movieImage;}

    public MovieItem (){}

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

}
