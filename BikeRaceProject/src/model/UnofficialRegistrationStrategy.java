package model;

// unofficialRegistrationStrategy, from the DP diagram: no restrictions. 
public class UnofficialRegistrationStrategy implements RegistrationStrategy {
    @Override
    public void register(Racer racer, Race race) {
        // No license or category required for unofficial races.
    }
}
