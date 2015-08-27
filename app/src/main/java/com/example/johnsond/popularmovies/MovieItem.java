package com.example.johnsond.popularmovies;

/**
 * Created by JohnsonD on 7/27/15.
 */
public class MovieItem {
    private  String OriginalTitle;
    private  int MovieImage;
    private  String OverView; //called overview in themovieapi
    private  String VoteAverage; //called vote_averge in themovieapi
    private  String VoteCount;
    private  String ReleaseDate;

    public MovieItem  (int movieImage) {
        this.MovieImage = movieImage;
    }

    public MovieItem (){}

    //*********** Set Methods ******************//

    public void setOriginalTitle(String OriginalTitle)
    {
        this.OriginalTitle = OriginalTitle;
    }

    public void setMovieImage(int movieImage)
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

    //*********** Get Methods ****************//

    public String getOriginalTitle()
    {
        return OriginalTitle;
    }

    public int getMovieImage() { return MovieImage; }

    public String getOverView()
    {
        return OverView;
    }

    public String getVoteAverageg()
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
