package model;

// Review, from the DP diagram: -reviewText; +submitReview().
public class Review {

    private final Racer racer;
    private final Race race;
    private final String reviewText;

    public Review(Racer racer, Race race, String reviewText) {
        this.racer = racer;
        this.race = race;
        this.reviewText = reviewText;
    }

    // Files this review into both the racer's and the race's review lists.
    public void submitReview() {
        racer.receiveReview(this);
        race.receiveReview(this);
    }

    public String getReviewText() {
        return reviewText;
    }
    public Racer getRacer() {
        return racer;
    }
    public Race getRace() {
        return race;
    }
}
