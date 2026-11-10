# Bike Race Registration System

A console-based Java application for registering for bike races, built for SER315 (Software Enterprise) by Team Arsenic.

## What it does

- **Three roles:** Racer, Organizer, and Administrator, each with its own menu.
- **Race registration:** racers register for official or unofficial races. Official races require a valid license with a category; both kinds check the registration deadline and the participant limit.
- **Licenses:** racers can purchase a one-year license in a category from CAT 5 (entry) to CAT 1 (top).
- **Category upgrades:** five podium finishes upgrade a racer's license category and send a notification.
- **Organizers** create races and record results. **Administrators** manage users.
- Data is kept in memory for the session (no database).

## Design

- **MVC architecture:** `view` (the only code that prints or reads input), `controller` (takes requests from the view and calls the model), and `model` (business rules).
- **Strategy pattern:** official and unofficial races use different registration rules.
- **Observer pattern:** a racer notifies an observer when a category upgrade happens.
- **SOLID principles** guided the class design.

```
BikeRaceProject/
  src/
    model/        Racer, Organizer, Administrator, Race, License, Registration, strategies, ...
    controller/   RaceController, RegistrationController, LicenseController, AccountController
    view/         View (console menus)
    app/          Main (interactive app), DemoMain (scripted demo)
  test/           JUnit tests for the model and controllers
  lib/            put the JUnit jars here to run the tests
```

## How to run

Requires a recent JDK (tested with JDK 24). From the top folder of this repository:

```
cd BikeRaceProject
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out app.Main        # interactive menus
java -cp out app.DemoMain    # scripted demo of every registration case
```

`Main` starts with sample data: Chris (organizer), Morgan (administrator), and racers Sam, Alex (already has a license), and Jordan.

## Tests

Tests live in `BikeRaceProject/test` (JUnit 4) and were written by a teammate. They cover licenses and categories, race deadlines and limits, registration rules, the category-upgrade notification, and the controllers.

To run them, put `junit-4.13.2.jar` and `hamcrest-core-1.3.jar` (both on Maven Central) in `BikeRaceProject/lib`, then from the top folder of this repository:

```
cd BikeRaceProject
mkdir -p out
javac -cp "lib/*" -d out $(find src test -name "*.java")
java -cp "out:lib/*" org.junit.runner.JUnitCore model.CategoryTest model.LicenseTest model.RaceTest model.RacerTest model.RegistrationTest model.ResultsAndNotificationTest model.UserTest controller.ControllersTest
```

## Notes

Developed with AI assistance as it was allowed; the team reviewed, tested, and made the design decisions.