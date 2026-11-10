package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Race, from the DP diagram. raceId isn't on the diagram but every race needs an identifier. 
public class Race {

    private final String raceId; // aded this since it needs an identifier
    private LocalDate date;
    private double miles;
    private String route;
    private int participantLimit;
    private LocalDate registrationDeadline;
    private boolean official;
    private RaceType raceType;

    // association arrows into race so they become fields and there lists since there can be more than 1+
    private final List<Registration> registrations = new ArrayList<>();
    private final List<RaceResult> results = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();

    public Race(String raceId, RaceType raceType, boolean official) {
        this.raceId = raceId;
        this.raceType = raceType;
        this.official = official;
    }

    // Public per the diagram: true only if registration is still open on both counts below. 
    public boolean isRegistrationOpen() {
        return !isPastDeadline() && !isFull();
    }

    /*
      Not on the diagram, but the Registration.register() needs the two failure reasons split apart
      so it can raise the two distinct errors that the Activity Diagram called out ("Registration Has
      closed" vs "Race is Full")
     */
    boolean isPastDeadline() {
        return registrationDeadline != null && LocalDate.now().isAfter(registrationDeadline);
    }

    boolean isFull() {
        return participantLimit > 0 && registrations.size() >= participantLimit;
    }

    public boolean isOfficial() { 
        return official; 
    }
    public RaceType getRaceType() { 
        return raceType; 
    }

    // Called by Registration.register() once the deadline/capacity/strategy checks all pass. 
    void receiveRegistration(Registration registration) { 
        registrations.add(registration);
     }
    // Called by RaceResult.recordResult(). 
    void receiveResult(RaceResult result) { 
        results.add(result); 
    }
    // Called by Review.submitReview(). 
    void receiveReview(Review review) { 
        reviews.add(review); 
    }

    //Getters and Setters:
    public String getRaceId() { 
        return raceId; 
    }
    public LocalDate getDate() { 
        return date; 
    }
    public void setDate(LocalDate date) { 
        this.date = date; 
    }
    public double getMiles() { 
        return miles; 
    }
    public void setMiles(double miles) {
         this.miles = miles; 
        }
    public String getRoute() { 
        return route;
    }
    public void setRoute(String route) { 
        this.route = route; 
    }
    public int getParticipantLimit() { 
        return participantLimit; 
    }
    public void setParticipantLimit(int participantLimit) {
         this.participantLimit = participantLimit; 
        }
    public LocalDate getRegistrationDeadline() { 
        return registrationDeadline; 
    }
    public void setRegistrationDeadline(LocalDate registrationDeadline) {
         this.registrationDeadline = registrationDeadline;
    }

    public List<Registration> getRegistrations() { 
        return registrations; 
    }
    public List<RaceResult> getResults() {
         return results; 
        }
    public List<Review> getReviews() { 
        return reviews; 
    }
}
