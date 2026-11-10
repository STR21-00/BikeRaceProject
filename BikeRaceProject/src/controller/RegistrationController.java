package controller;

import model.IneligibleException;
import model.Race;
import model.Racer;
import model.Registration;

// RegistrationController, from the MVC diagram: +registerForRace()
public class RegistrationController {

    // Calls Racer.registerForRace(), which runs the Strategy pattern (official vs unofficial rules).
    // Throws IneligibleException if the racer can't register, the view catches it and shows the message.
    public Registration registerForRace(Racer racer, Race race) {
        // added this check so a racer can't take two spots in the same race
        for (Registration existing : racer.getRegistrations()) {
            if (existing.getRace() == race) {
                throw new IneligibleException("You are already registered for this race");
            }
        }
        return racer.registerForRace(race);
    }
}
