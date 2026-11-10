package app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.AccountController;
import controller.LicenseController;
import controller.RaceController;
import controller.RegistrationController;
import model.Administrator;
import model.Category;
import model.Organizer;
import model.RaceType;
import model.Racer;
import view.View;

// Use this file to run 
// Starts the console app. Sets up some starting data in memory, builds the controllers and view, then runs the menu.
// Run this one to try the whole system (login as a racer, organizer or administrator).
public class Main {

    public static void main(String[] args) {

        // controllers
        AccountController accountController = new AccountController();
        RaceController raceController = new RaceController();
        RegistrationController registrationController = new RegistrationController();
        LicenseController licenseController = new LicenseController();

        // starting users
        Organizer organizer = new Organizer(1, "Chris");
        Administrator administrator = new Administrator(2, "Morgan");
        List<Racer> racers = new ArrayList<>();
        Racer sam = new Racer(3, "Sam");       // no license
        Racer alex = new Racer(4, "Alex");     // gets a license below
        Racer jordan = new Racer(5, "Jordan"); // no license
        racers.add(sam);
        racers.add(alex);
        racers.add(jordan);

        licenseController.purchaseLicense(alex, Category.CAT_5, "4111-1111-1111-1111");

        // starting races so there is something to register for
        raceController.createRace(organizer, "R-1", RaceType.ROAD_RACE, true,
                LocalDate.now().plusDays(40), 64, "Mulholland Circuit Loop",
                LocalDate.now().plusDays(30), 2);
        raceController.createRace(organizer, "R-2", RaceType.GRAVEL, false,
                LocalDate.now().plusDays(40), 35, "Desert Gravel Trail",
                LocalDate.now().plusDays(30), 0);

        View view = new View(new Scanner(System.in), accountController, raceController,
                registrationController, licenseController, racers, organizer, administrator);
        view.start();
    }
}
