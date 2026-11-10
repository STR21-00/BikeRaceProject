package model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Racer, from the DP diagram. Racer is the Observer Subject 
public class Racer extends User { // class extends user

    private String creditCardInfo;
    private License license; //  added this since racer can have zero or one license also has realtionship arrow with license

    /* below are array list since they have 0..* multiplcies and they are final so they 
    cant be accidentally reassigned to some other list object */

    private final List<Registration> registrations = new ArrayList<>();
    private final List<RaceResult> results = new ArrayList<>();
    private final List<Review> reviews = new ArrayList<>();
    private final List<Observer> observers = new ArrayList<>();

    private static final int PODIUMS_REQUIRED_FOR_UPGRADE = 5; // design constraint
    private int podiumsSinceLastUpgrade = 0;

    public Racer(int userId, String name) {
        super(userId, name);  // calling the user constructor
    }

  
    public Registration registerForRace(Race race) {
        RegistrationStrategy strategy;
        if(race.isOfficial()){
            strategy = new OfficialRegistrationStrategy();
        } else {
            strategy = new UnofficialRegistrationStrategy();
        }

        Registration registration = new Registration(); // this creates it 
        registration.setStrategy(strategy); // this calls the method
        registration.register(this, race); // this can throw illegal exception and also calls register
        registrations.add(registration);
        
        return registration;
    }

    public License purchaseLicense(String licenseId, Category category, LocalDate expirationDate) {
        this.license = new License(licenseId, expirationDate, category);
        return this.license;
    }

    // Only allowed if this racer has a Registration for the given race. 
    public Review submitReview(Race race, String reviewText) {
        
        boolean wasRegistered = false;
        for(Registration r : registrations){
            if(r.getRace() == race){
                wasRegistered = true;
                break;
            }
        }

        if(!wasRegistered){
            throw new IllegalStateException("Racer must be registered for the race to review it.");
        }

        Review review = new Review(this, race, reviewText);
        review.submitReview();
        
        return review;
    }

   
    public void checkCategoryUpgrade() {
        if (license == null) 
            return;
        if (podiumsSinceLastUpgrade >= PODIUMS_REQUIRED_FOR_UPGRADE) {
            license.upgradeCategory();
            podiumsSinceLastUpgrade = 0; // this resets the podiums back to 0 for the new category
            notifyObserver(name + " has been upgraded to " + license.getCategory() + "!");
        }
    }

    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Pushes the event message out to every attached Observer (UpgradeNotification)
    public void notifyObserver(String message) {
        for (Observer observer : observers) observer.update(message);
    }

    // Called by RaceResult.recordResult() as soon as a result is filed against this racer. 
    void receiveResult(RaceResult result) {
        results.add(result);
        if (result.isPodiumFinish()) {
            podiumsSinceLastUpgrade++; // top-3 finish counts toward the next category upgrade
        }
    }

    // Called by Review.submitReview(). 
    void receiveReview(Review review) { reviews.add(review); }

    public License getLicense() { 
        return license;
     }
    public String getCreditCardInfo() { 
        return creditCardInfo; 
    }
    public void setCreditCardInfo(String creditCardInfo) { 
        this.creditCardInfo = creditCardInfo; 
    }
    public List<Registration> getRegistrations() { 
        return registrations; 
    }
    public List<RaceResult> getResults() { 
        return results; 
    }
    public List<Review> getReviews() { 
        return reviews; }
}
