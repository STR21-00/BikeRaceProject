package app;

import java.time.LocalDate;

import controller.LicenseController;
import controller.RaceController;
import controller.RegistrationController;
import model.*;

// Scripted version (no typing needed). Goes through the controllers like the real app does
// and shows the registration workflow with every error case, plus both patterns.
public class DemoMain {

    public static void main(String[] args) {

        RaceController raceController = new RaceController();
        RegistrationController registrationController = new RegistrationController();
        LicenseController licenseController = new LicenseController();

        Organizer organizer = new Organizer(1, "Chris");

        // Official race 
        System.out.println("OFFICIAL RACE TEST");

        Race official = raceController.createRace(organizer, "R-OFFICIAL-1", RaceType.ROAD_RACE, true,
                LocalDate.now().plusDays(40), 64, "Tucson Foothills Road Loop",
                LocalDate.now().plusDays(30), 1); // only 1 spot so we can test race full

        Racer sam = new Racer(2, "Sam");
        // sam has no license, this should get rejected
        attemptRegistration(registrationController, sam, official, "Sam - no license");

        Racer alex = new Racer(3, "Alex");
        licenseController.purchaseLicense(alex, Category.CAT_5, "4111-1111-1111-1111");
        attemptRegistration(registrationController, alex, official, "Alex - has valid license");

        // race only had 1 spot and alex just took it, so jordan should fail
        Racer jordan = new Racer(4, "Jordan");
        licenseController.purchaseLicense(jordan, Category.CAT_5, "4111-1111-1111-2222");
        attemptRegistration(registrationController, jordan, official, "Jordan - race is full now");

        Race closedRace = raceController.createRace(organizer, "R-OFFICIAL-2", RaceType.CRITERIUM, true,
                LocalDate.now().plusDays(10), 20, "Tempe Town Lake Criterium Loop",
                LocalDate.now().minusDays(1), 0); // deadline already passed
        attemptRegistration(registrationController, alex, closedRace, "Alex - deadline already passed");


        // Unofficial race 
        System.out.println();
        System.out.println("UNOFFICIAL RACE TEST");

        Race unofficial = raceController.createRace(organizer, "R-UNOFFICIAL-1", RaceType.GRAVEL, false,
                LocalDate.now().plusDays(40), 35, "Sedona Red Rock Gravel Loop",
                LocalDate.now().plusDays(30), 0);
        // unofficial races don't need a license
        attemptRegistration(registrationController, sam, unofficial, "Sam - no license but unofficial so its fine");

        Race unofficialClosed = raceController.createRace(organizer, "R-UNOFFICIAL-2", RaceType.GRAVEL, false,
                LocalDate.now().plusDays(10), 35, "Flagstaff Forest Gravel Trail",
                LocalDate.now().minusDays(1), 0);
        attemptRegistration(registrationController, sam, unofficialClosed, "Sam - unofficial but deadline passed");

        // Observer pattern test (category upgrade) 
        System.out.println();
        System.out.println("CATEGORY UPGRADE TEST (Observer pattern)");
        System.out.println("Alex starting category: " + alex.getLicense().getCategory());

        // need 5 podium finishes to upgrade, so record 5 first place results
        for (int i = 0; i < 5; i++) {
            raceController.recordRaceResults(organizer, official, alex, 1);
            System.out.println("Alex finished in position 1 at race " + official.getRaceId());
            String notification = raceController.takeUpgradeNotification();
            if (notification != null) {
                System.out.println("[Category Upgrade Notification] " + notification);
            }
        }

        System.out.println("Alex category after 5 podiums: " + alex.getLicense().getCategory());
    }

    // helper so I don't have to copy paste the try/catch every time
    private static void attemptRegistration(RegistrationController controller, Racer racer, Race race, String label) {
        System.out.println(label + ":");
        try {
            controller.registerForRace(racer, race);
            System.out.println("  -> registered successfully");
        } catch (IneligibleException e) {
            System.out.println("  -> rejected: " + e.getMessage());
        }
    }
}
