package model;

// <<interface>> RegistrationStrategy, from the DP diagram. 
public interface RegistrationStrategy {
    // Throws IneligibleException if this racer/race combination isn't allowed. 
    void register(Racer racer, Race race);
    
    
}
