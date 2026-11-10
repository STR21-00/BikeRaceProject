package view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import controller.AccountController;
import controller.LicenseController;
import controller.RaceController;
import controller.RegistrationController;
import model.Administrator;
import model.Category;
import model.IneligibleException;
import model.License;
import model.Organizer;
import model.Race;
import model.RaceResult;
import model.RaceType;
import model.Racer;
import model.Registration;
import model.User;

// View, from the MVC diagram: +displayRacerView(), +displayOrganizerView(), +displayAdministratorView()
// This is the only place that prints to the console or reads the user input. 
// Flow is View -> Controller -> Model, the view calls the controllers and prints whatever comes back.
// The diagram's display methods have no parameters, I made them take the logged in user.
public class View {

    private final Scanner input;
    private final AccountController accountController;
    private final RaceController raceController;
    private final RegistrationController registrationController;
    private final LicenseController licenseController;

    private final List<Racer> racers;
    private final Organizer organizer;
    private final Administrator administrator;

    public View(Scanner input, AccountController accountController, RaceController raceController,
                RegistrationController registrationController, LicenseController licenseController,
                List<Racer> racers, Organizer organizer, Administrator administrator) {
        this.input = input;
        this.accountController = accountController;
        this.raceController = raceController;
        this.registrationController = registrationController;
        this.licenseController = licenseController;
        this.racers = racers;
        this.organizer = organizer;
        this.administrator = administrator;
    }

    // The Main Menu to pick a log in area.

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("--- Bike Race Registration System ---");
            System.out.println("Log in as:");
            System.out.println("  1. Racer");
            System.out.println("  2. Organizer");
            System.out.println("  3. Administrator");
            System.out.println("  0. Exit");
            int choice = readInt("Choose an option: ");

            if (choice == 1) {
                Racer racer = pickRacer();
                if (racer != null) {
                    displayRacerView(racer);
                }
            } else if (choice == 2) {
                displayOrganizerView(organizer);
            } else if (choice == 3) {
                displayAdministratorView(administrator);
            } else if (choice == 0) {
                running = false;
            } else {
                showError("That is not a valid option");
            }
        }
        System.out.println("Goodbye!");
    }

    // RACER VIEW what the racer will see when Racer is picked from the main menu
    // Will see: register, purchase Liscense, submit review, show racer info

    public void displayRacerView(Racer racer) {
        racer.login();
        System.out.println();
        System.out.println("Welcome, " + racer.getName() + " (Racer)");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.println("--- Racer Menu ---");
            System.out.println("  1. View races");
            System.out.println("  2. Register for a race");
            System.out.println("  3. Purchase a license");
            System.out.println("  4. Submit a race review");
            System.out.println("  5. My license and registrations");
            System.out.println("  0. Log out");
            int choice = readInt("Choose an option: ");

            if (choice == 1) {
                showRaces(raceController.getRaces());
            } else if (choice == 2) {
                registerForRace(racer);
            } else if (choice == 3) {
                purchaseLicense(racer);
            } else if (choice == 4) {
                submitReview(racer);
            } else if (choice == 5) {
                showRacerInfo(racer);
            } else if (choice == 0) {
                loggedIn = false;
            } else {
                showError("That is not a valid option");
            }
        }
        racer.logout();
        System.out.println(racer.getName() + " logged out.");
    }

    private void registerForRace(Racer racer) {
        Race race = pickRace(raceController.getRaces(), "Which race do you want to register for?");
        if (race == null) {
            return;
        }
        try {
            Registration registration = registrationController.registerForRace(racer, race);
            System.out.println("Registration confirmed! " + racer.getName() + " is registered for "
                    + race.getRaceId() + " on " + registration.getRegistrationDate() + ".");
            if (race.isOfficial()) {
                System.out.println("Racing in category: " + racer.getLicense().getCategory());
            }
        } catch (IneligibleException e) {
            showError(e.getMessage());
            // from the activity diagram: no valid license for an official race -> link to purchase one
            License license = racer.getLicense();
            if (race.isOfficial() && (license == null || !license.isValid())) {
                System.out.println("Here's a link to purchase an official license: choose option 3 (Purchase a license) in the Racer Menu.");
            }
        }
    }

    private void purchaseLicense(Racer racer) {
        System.out.println("A license is valid for 1 year. Categories go from 5 (entry) to 1 (top).");
        int number = readInt("Which category do you want (1-5)? ");
        if (number < 1 || number > 5) {
            showError("Category must be between 1 and 5");
            return;
        }
        Category category = Category.valueOf("CAT_" + number);
        System.out.print("Enter your credit card number: ");
        String card = readLine().trim();
        if (card.isEmpty()) {
            showError("Payment info is required to buy a license");
            return;
        }
        License license = licenseController.purchaseLicense(racer, category, card);
        System.out.println("Payment processed. License " + license.getLicenseId() + " (" + license.getCategory()
                + ") is valid until " + license.getExpirationDate() + ".");
    }

    private void submitReview(Racer racer) {
        List<Race> registeredRaces = new ArrayList<>();
        for (Registration registration : racer.getRegistrations()) {
            registeredRaces.add(registration.getRace());
        }
        Race race = pickRace(registeredRaces, "Which race do you want to review? (only races you registered for)");
        if (race == null) {
            return;
        }
        System.out.print("Write your review: ");
        String text = readLine().trim();
        if (text.isEmpty()) {
            showError("Review can't be empty");
            return;
        }
        try {
            raceController.submitReview(racer, race, text);
            System.out.println(racer.getName() + " reviewed race " + race.getRaceId() + ": \"" + text + "\"");
        } catch (IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private void showRacerInfo(Racer racer) {
        License license = racer.getLicense();
        if (license == null) {
            System.out.println("License: none");
        } else {
            String status = license.isValid() ? "valid" : "EXPIRED";
            System.out.println("License: " + license.getLicenseId() + ", " + license.getCategory()
                    + ", expires " + license.getExpirationDate() + " (" + status + ")");
        }
        System.out.println("Registrations: " + racer.getRegistrations().size());
        for (Registration registration : racer.getRegistrations()) {
            System.out.println("  - " + registration.getRace().getRaceId() + " (registered " + registration.getRegistrationDate() + ")");
        }
        System.out.println("Results: " + racer.getResults().size());
        for (RaceResult result : racer.getResults()) {
            System.out.println("  - place " + result.getPlacement() + " at " + result.getRace().getRaceId());
        }
        System.out.println("Reviews written: " + racer.getReviews().size());
    }

    // Note for me: Ended here need to work on Organizer menu

    // Organizer View, this what Oraginzer will see when logged in as Organizer

    public void displayOrganizerView(Organizer organizer) {
        organizer.login();
        System.out.println();
        System.out.println("Welcome, " + organizer.getName() + " (Organizer)");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.println("--- Organizer Menu ---");
            System.out.println("  1. Create a race");
            System.out.println("  2. View my races");
            System.out.println("  3. Record a race result");
            System.out.println("  0. Log out");
            int choice = readInt("Choose an option: ");

            if (choice == 1) {
                createRace(organizer);
            } else if (choice == 2) {
                showRaces(organizer.getRacesCreated());
            } else if (choice == 3) {
                recordResult(organizer);
            } else if (choice == 0) {
                loggedIn = false;
            } else {
                showError("That is not a valid option");
            }
        }
        organizer.logout();
        System.out.println(organizer.getName() + " logged out.");
    }

    private void createRace(Organizer organizer) {
        System.out.print("Race id (example R-3): ");
        String raceId = readLine().trim();
        if (raceId.isEmpty()) {
            showError("Race id can't be empty");
            return;
        }

        RaceType[] types = RaceType.values();
        System.out.println("Race types:");
        for (int i = 0; i < types.length; i++) {
            System.out.println("  " + (i + 1) + ". " + types[i]);
        }
        int typeNumber = readInt("Pick a race type: ");
        if (typeNumber < 1 || typeNumber > types.length) {
            showError("That is not a valid race type");
            return;
        }
        RaceType raceType = types[typeNumber - 1];

        System.out.print("Is it an official race? (y/n): ");
        boolean official = readLine().trim().equalsIgnoreCase("y");

        System.out.print("Route / location: ");
        String route = readLine().trim();
        double miles = readDouble("Distance in miles: ");
        int daysUntilRace = readInt("Race is how many days from today? ");
        int daysUntilDeadline = readInt("Registration closes how many days from today? ");
        int limit = readInt("Participant limit (0 = no limit): ");

        if (daysUntilRace < 0 || daysUntilDeadline < 0 || limit < 0 || miles < 0 || daysUntilDeadline > daysUntilRace) {
            showError("Those numbers don't work (nothing can be negative and registration has to close by race day)");
            return;
        }

        try {
            Race race = raceController.createRace(organizer, raceId, raceType, official,
                    LocalDate.now().plusDays(daysUntilRace), miles, route,
                    LocalDate.now().plusDays(daysUntilDeadline), limit);
            System.out.println("Race created: " + describeRace(race));
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void recordResult(Organizer organizer) {
        Race race = pickRace(organizer.getRacesCreated(), "Which race are you entering a result for?");
        if (race == null) {
            return;
        }
        List<Registration> registrations = race.getRegistrations();
        if (registrations.isEmpty()) {
            showError("Nobody is registered for this race yet");
            return;
        }
        System.out.println("Registered racers:");
        for (int i = 0; i < registrations.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + registrations.get(i).getRacer().getName());
        }
        int racerNumber = readInt("Pick a racer (0 to cancel): ");
        if (racerNumber < 1 || racerNumber > registrations.size()) {
            return;
        }
        Racer racer = registrations.get(racerNumber - 1).getRacer();
        int placement = readInt("Finishing position: ");
        if (placement < 1) {
            showError("Position has to be 1 or higher");
            return;
        }

        raceController.recordRaceResults(organizer, race, racer, placement);
        System.out.println(racer.getName() + " finished in position " + placement + " at race " + race.getRaceId());

        // Observer pattern: if this result caused a category upgrade the notification is waiting
        String notification = raceController.takeUpgradeNotification();
        if (notification != null) {
            System.out.println("[Category Upgrade Notification] " + notification);
        }
    }

    // Admin menu when logged in as Adminstrator 

    public void displayAdministratorView(Administrator administrator) {
        administrator.login();
        System.out.println();
        System.out.println("Welcome, " + administrator.getName() + " (Administrator)");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.println("--- Administrator Menu ---");
            System.out.println("  1. Manage a user");
            System.out.println("  0. Log out");
            int choice = readInt("Choose an option: ");

            if (choice == 1) {
                manageUser(administrator);
            } else if (choice == 0) {
                loggedIn = false;
            } else {
                showError("That is not a valid option");
            }
        }
        administrator.logout();
        System.out.println(administrator.getName() + " logged out.");
    }

    private void manageUser(Administrator administrator) {
        List<User> users = new ArrayList<>();
        users.add(organizer);
        users.addAll(racers);
        System.out.println("Users:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + users.get(i).getName());
        }
        int number = readInt("Pick a user (0 to cancel): ");
        if (number < 1 || number > users.size()) {
            return;
        }
        System.out.println(accountController.manageUser(administrator, users.get(number - 1)));
    }

    // helpers 

    private Racer pickRacer() {
        System.out.println("Which racer are you?");
        for (int i = 0; i < racers.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + racers.get(i).getName());
        }
        int number = readInt("Pick a racer (0 to cancel): ");
        if (number < 1 || number > racers.size()) {
            return null;
        }
        return racers.get(number - 1);
    }

    private Race pickRace(List<Race> races, String prompt) {
        if (races.isEmpty()) {
            showError("There are no races to choose from");
            return null;
        }
        System.out.println(prompt);
        for (int i = 0; i < races.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + describeRace(races.get(i)));
        }
        int number = readInt("Pick a race (0 to cancel): ");
        if (number < 1 || number > races.size()) {
            return null;
        }
        return races.get(number - 1);
    }

    private void showRaces(List<Race> races) {
        if (races.isEmpty()) {
            System.out.println("There are no races yet.");
            return;
        }
        for (int i = 0; i < races.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + describeRace(races.get(i)));
        }
    }

    private String describeRace(Race race) {
        String official = race.isOfficial() ? "Official" : "Unofficial";
        String spots;
        if (race.getParticipantLimit() > 0) {
            spots = race.getRegistrations().size() + "/" + race.getParticipantLimit() + " spots taken";
        } else {
            spots = race.getRegistrations().size() + " registered (no limit)";
        }
        String status = race.isRegistrationOpen() ? "OPEN" : "CLOSED";
        return race.getRaceId() + " | " + race.getRaceType() + " | " + official + " | " + race.getRoute()
                + " | " + race.getMiles() + " mi | race day " + race.getDate()
                + " | registration closes " + race.getRegistrationDeadline() + " | " + spots + " | " + status;
    }

    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    // reads a whole line, if the input runs out it returns "0" so menus back out and exit
    private String readLine() {
        try {
            return input.nextLine();
        } catch (NoSuchElementException e) {
            return "0";
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = readLine().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                showError("Please enter a whole number");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = readLine().trim();
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException e) {
                showError("Please enter a number");
            }
        }
    }
}
