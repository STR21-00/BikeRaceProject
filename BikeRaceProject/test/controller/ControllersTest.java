package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import model.Administrator;
import model.Category;
import model.IneligibleException;
import model.License;
import model.Organizer;
import model.Race;
import model.RaceType;
import model.Racer;
import model.Registration;
import model.Review;

public class ControllersTest {

    private RaceController raceController;
    private RegistrationController registrationController;
    private LicenseController licenseController;
    private AccountController accountController;
    private Organizer organizer;
    private Racer racer;

    @Before
    public void setUp() {
        raceController = new RaceController();
        registrationController = new RegistrationController();
        licenseController = new LicenseController();
        accountController = new AccountController();
        organizer = new Organizer(1, "Chris");
        racer = new Racer(4, "Alex");
    }

    private Race newRace(String id, boolean official, int limit) {
        return raceController.createRace(organizer, id, RaceType.ROAD_RACE, official,
                LocalDate.now().plusDays(40), 64, "Loop",
                LocalDate.now().plusDays(30), limit);
    }

    // ---- RaceController ----

    @Test
    public void createRaceSetsAllFieldsAndStoresIt() {
        LocalDate date = LocalDate.now().plusDays(40);
        LocalDate deadline = LocalDate.now().plusDays(30);
        Race r = raceController.createRace(organizer, "R-1", RaceType.GRAVEL, true,
                date, 35.5, "Desert Trail", deadline, 10);

        assertEquals("R-1", r.getRaceId());
        assertEquals(RaceType.GRAVEL, r.getRaceType());
        assertTrue(r.isOfficial());
        assertEquals(date, r.getDate());
        assertEquals(35.5, r.getMiles(), 0.0001);
        assertEquals("Desert Trail", r.getRoute());
        assertEquals(deadline, r.getRegistrationDeadline());
        assertEquals(10, r.getParticipantLimit());
        assertTrue(raceController.getRaces().contains(r));
        assertTrue(organizer.getRacesCreated().contains(r));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRaceWithDuplicateIdIsRejected() {
        newRace("R-1", false, 0);
        newRace("R-1", true, 5);
    }

    @Test
    public void duplicateRaceIsNotAddedToOrganizer() {
        newRace("R-1", false, 0);
        try {
            newRace("R-1", false, 0);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals(1, organizer.getRacesCreated().size());
            assertEquals(1, raceController.getRaces().size());
        }
    }

    @Test
    public void submitReviewRequiresRegistration() {
        Race race = newRace("R-1", false, 0);
        try {
            raceController.submitReview(racer, race, "nice");
            fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
            // ok
        }
        registrationController.registerForRace(racer, race);
        Review r = raceController.submitReview(racer, race, "nice");
        assertEquals("nice", r.getReviewText());
    }

    @Test
    public void upgradeNotificationAppearsAfterFifthPodiumOnly() {
        Race race = newRace("R-1", false, 0);
        licenseController.purchaseLicense(racer, Category.CAT_5, "4111");

        for (int i = 0; i < 4; i++) {
            raceController.recordRaceResults(organizer, race, racer, 1);
            assertNull(raceController.takeUpgradeNotification());
        }
        raceController.recordRaceResults(organizer, race, racer, 1);

        assertEquals("Alex has been upgraded to CAT_4!", raceController.takeUpgradeNotification());
        assertNull(raceController.takeUpgradeNotification()); // only shown once
        assertEquals(Category.CAT_4, racer.getLicense().getCategory());
    }

    @Test
    public void recordingResultsManyTimesDoesNotDuplicateNotifications() {
        Race race = newRace("R-1", false, 0);
        licenseController.purchaseLicense(racer, Category.CAT_5, "4111");
        for (int i = 0; i < 5; i++) {
            raceController.recordRaceResults(organizer, race, racer, 1);
        }
        raceController.takeUpgradeNotification();
        assertNull(raceController.takeUpgradeNotification());
    }

    // ---- RegistrationController ----

    @Test
    public void registerForRaceReturnsRegistration() {
        Race race = newRace("R-1", false, 0);
        Registration reg = registrationController.registerForRace(racer, race);
        assertSame(race, reg.getRace());
        assertSame(racer, reg.getRacer());
    }

    @Test
    public void cannotRegisterTwiceForSameRace() {
        Race race = newRace("R-1", false, 0);
        registrationController.registerForRace(racer, race);
        try {
            registrationController.registerForRace(racer, race);
            fail("expected IneligibleException");
        } catch (IneligibleException e) {
            assertTrue(e.getMessage().contains("already registered"));
        }
        assertEquals(1, race.getRegistrations().size());
    }

    @Test
    public void officialRaceNeedsLicenseThroughController() {
        Race race = newRace("R-1", true, 0);
        try {
            registrationController.registerForRace(racer, race);
            fail("expected IneligibleException");
        } catch (IneligibleException expected) {
            // ok
        }
        licenseController.purchaseLicense(racer, Category.CAT_5, "4111");
        registrationController.registerForRace(racer, race);
        assertEquals(1, race.getRegistrations().size());
    }

    // ---- LicenseController ----

    @Test
    public void purchaseLicenseBuildsIdStoresCardAndExpiresInOneYear() {
        License l = licenseController.purchaseLicense(racer, Category.CAT_2, "4111-1111");
        assertEquals("LIC-4", l.getLicenseId());
        assertEquals(Category.CAT_2, l.getCategory());
        assertEquals(LocalDate.now().plusYears(1), l.getExpirationDate());
        assertTrue(l.isValid());
        assertEquals("4111-1111", racer.getCreditCardInfo());
        assertSame(l, racer.getLicense());
    }

    @Test
    public void buyingAgainReplacesTheLicense() {
        License first = licenseController.purchaseLicense(racer, Category.CAT_5, "1");
        License second = licenseController.purchaseLicense(racer, Category.CAT_3, "2");
        assertFalse(first == second);
        assertSame(second, racer.getLicense());
    }

    // ---- AccountController ----

    @Test
    public void manageUserReturnsAdminText() {
        Administrator admin = new Administrator(2, "Morgan");
        assertEquals("Morgan (admin) is managing user Alex", accountController.manageUser(admin, racer));
    }
}
