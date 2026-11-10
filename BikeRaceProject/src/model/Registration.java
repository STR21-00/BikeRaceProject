package model;

import java.time.LocalDate;

// Registration, from the DP diagram: -registrationDate, -strategy; +register(), +setStrategy().
 
public class Registration {

    private LocalDate registrationDate;
    private RegistrationStrategy strategy;
    private Racer racer;
    private Race race;

    public void setStrategy(RegistrationStrategy strategy) {
        this.strategy = strategy;
    }

    public void register(Racer racer, Race race) {
        strategy.register(racer, race); // Strategy pattern: official vs. unofficial eligibility rules

        if (race.isPastDeadline()) {
            throw new IneligibleException("Registration Has Closed");
        }
        if (race.isFull()) {
            throw new IneligibleException("Race is Full");
        }

        this.racer = racer;
        this.race = race;
        this.registrationDate = LocalDate.now();
        race.receiveRegistration(this);
    }

    public LocalDate getRegistrationDate() { 
        return registrationDate; 
    }
    public Racer getRacer() { 
        return racer; 
    }
    public Race getRace() { 
        return race; 
    }
}
