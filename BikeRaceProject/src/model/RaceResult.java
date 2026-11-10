package model;

// RaceResult, from the DP diagram: -placement; +recordResult(), +isPodiumFinish().
public class RaceResult {

    private final Racer racer;
    private final Race race;
    private final int placement;
    private final Category categoryAtRace;

    public RaceResult(Racer racer, Race race, int placement, Category categoryAtRace) {
        this.racer = racer;
        this.race = race;
        this.placement = placement;
        this.categoryAtRace = categoryAtRace;
    }

    /** Files this result into both the racer's and the race's result lists. */
    public void recordResult() {
        racer.receiveResult(this);
        race.receiveResult(this);
    }

    public boolean isPodiumFinish() {
        return placement >= 1 && placement <= 3;
    }

    public int getPlacement() { return placement; }
    public Racer getRacer() { return racer; }
    public Race getRace() { return race; }
    public Category getCategoryAtRace() { return categoryAtRace; }
}
