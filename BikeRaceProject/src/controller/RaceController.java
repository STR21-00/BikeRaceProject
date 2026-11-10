package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Organizer;
import model.Race;
import model.RaceResult;
import model.RaceType;
import model.Racer;
import model.Review;
import model.UpgradeNotification;

// RaceController, from the MVC diagram: +createRace(), +submitReview()
// I also added recordRaceResults() here (not on the diagram) so the Observer pattern can run through MVC.
public class RaceController {

    // data is only kept in memory for the session (persistence is for a later iteration)
    private final List<Race> races = new ArrayList<>();

    // the Observer for category upgrades, the controller attaches it to racers
    private final UpgradeNotification upgradeNotification = new UpgradeNotification();

    // Only organizers can create races (design constraint), so it takes an Organizer.
    public Race createRace(Organizer organizer, String raceId, RaceType raceType, boolean official,
                           LocalDate date, double miles, String route,
                           LocalDate registrationDeadline, int participantLimit) {
        for (Race r : races) {
            if (r.getRaceId().equals(raceId)) {
                throw new IllegalArgumentException("A race with id " + raceId + " already exists");
            }
        }
        Race race = organizer.createRace(raceId, raceType, official);
        race.setDate(date);
        race.setMiles(miles);
        race.setRoute(route);
        race.setRegistrationDeadline(registrationDeadline);
        race.setParticipantLimit(participantLimit);
        races.add(race);
        return race;
    }

    // throws IllegalStateException if the racer wasn't registered for the race
    public Review submitReview(Racer racer, Race race, String reviewText) {
        return racer.submitReview(race, reviewText);
    }

    public RaceResult recordRaceResults(Organizer organizer, Race race, Racer racer, int placement) {
        racer.addObserver(upgradeNotification); // Observer pattern: racer tells this notification about upgrades
        return organizer.recordRaceResults(race, racer, placement);
    }

    // returns the category upgrade message if one is waiting (otherwise null)
    public String takeUpgradeNotification() {
        return upgradeNotification.takeNotification();
    }

    public List<Race> getRaces() {
        return races;
    }
}
