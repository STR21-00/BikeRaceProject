package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class RegistrationTest {

    private Organizer organizer;
    private Racer racer;

    @Before
    public void setUp() {
        organizer = new Organizer(1, "Chris");
        racer = new Racer(2, "Sam");
    }

    private Race officialRace() {
        return organizer.createRace("OFF", RaceType.ROAD_RACE, true);
    }

    private Race unofficialRace() {
        return organizer.createRace("UNOFF", RaceType.GRAVEL, false);
    }

    private void assertIneligible(Racer r, Race race, String expectedMessagePart) {
        try {
            r.registerForRace(race);
            fail("Expected IneligibleException");
        } catch (IneligibleException e) {
            assertTrue("message was: " + e.getMessage(), e.getMessage().contains(expectedMessagePart));
        }
    }

    // ---- Official (Strategy) ----

    @Test
    public void officialRaceRejectsRacerWithNoLicense() {
        assertIneligible(racer, officialRace(), "valid license");
    }

    @Test
    public void officialRaceRejectsExpiredLicense() {
        racer.purchaseLicense("L1", Category.CAT_5, LocalDate.now().minusDays(1));
        assertIneligible(racer, officialRace(), "valid license");
    }

    @Test
    public void officialRaceAcceptsValidLicense() {
        racer.purchaseLicense("L1", Category.CAT_5, LocalDate.now().plusYears(1));
        Race race = officialRace();
        Registration reg = racer.registerForRace(race);

        assertSame(racer, reg.getRacer());
        assertSame(race, reg.getRace());
        assertEquals(LocalDate.now(), reg.getRegistrationDate());
        assertEquals(1, race.getRegistrations().size());
        assertEquals(1, racer.getRegistrations().size());
    }

    @Test
    public void officialRaceRejectsLicenseWithNullCategory() {
        racer.purchaseLicense("L1", null, LocalDate.now().plusYears(1));
        assertIneligible(racer, officialRace(), "category");
    }

    @Test
    public void licenseIsCheckedBeforeDeadline() {
        // no license AND closed deadline -> the license error comes first
        Race race = officialRace();
        race.setRegistrationDeadline(LocalDate.now().minusDays(1));
        assertIneligible(racer, race, "valid license");
    }

    // ---- Unofficial (Strategy) ----

    @Test
    public void unofficialRaceAcceptsRacerWithNoLicense() {
        Race race = unofficialRace();
        racer.registerForRace(race);
        assertEquals(1, race.getRegistrations().size());
    }

    // ---- Deadline / capacity ----

    @Test
    public void registrationAfterDeadlineIsRejected() {
        Race race = unofficialRace();
        race.setRegistrationDeadline(LocalDate.now().minusDays(1));
        assertIneligible(racer, race, "Registration Has Closed");
    }

    @Test
    public void registrationOnDeadlineDayIsAllowed() {
        Race race = unofficialRace();
        race.setRegistrationDeadline(LocalDate.now());
        racer.registerForRace(race);
        assertEquals(1, race.getRegistrations().size());
    }

    @Test
    public void registrationWhenFullIsRejected() {
        Race race = unofficialRace();
        race.setParticipantLimit(1);
        new Racer(3, "Alex").registerForRace(race);
        assertIneligible(racer, race, "Race is Full");
    }

    @Test
    public void failedRegistrationIsNotRecorded() {
        Race race = unofficialRace();
        race.setRegistrationDeadline(LocalDate.now().minusDays(1));
        assertIneligible(racer, race, "Registration Has Closed");
        assertEquals(0, race.getRegistrations().size());
        assertEquals(0, racer.getRegistrations().size());
    }

    @Test
    public void registrationUsesTheStrategyItWasGiven() {
        // Registration itself should delegate to whatever strategy is set (Strategy pattern)
        final boolean[] called = { false };
        Registration reg = new Registration();
        reg.setStrategy(new RegistrationStrategy() {
            @Override
            public void register(Racer r, Race race) {
                called[0] = true;
            }
        });
        reg.register(racer, unofficialRace());
        assertTrue(called[0]);
    }
}
