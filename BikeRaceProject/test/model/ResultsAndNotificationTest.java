package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class ResultsAndNotificationTest {

    private Organizer organizer;
    private Racer racer;
    private Race race;

    @Before
    public void setUp() {
        organizer = new Organizer(1, "Chris");
        racer = new Racer(2, "Sam");
        race = organizer.createRace("R-1", RaceType.TIME_TRIAL, false);
    }

    // ---- RaceResult ----

    @Test
    public void podiumIsPlacementsOneThroughThree() {
        assertFalse(new RaceResult(racer, race, 0, null).isPodiumFinish());
        assertTrue(new RaceResult(racer, race, 1, null).isPodiumFinish());
        assertTrue(new RaceResult(racer, race, 2, null).isPodiumFinish());
        assertTrue(new RaceResult(racer, race, 3, null).isPodiumFinish());
        assertFalse(new RaceResult(racer, race, 4, null).isPodiumFinish());
        assertFalse(new RaceResult(racer, race, -1, null).isPodiumFinish());
    }

    @Test
    public void recordResultFilesItWithRacerAndRace() {
        RaceResult r = new RaceResult(racer, race, 2, Category.CAT_5);
        r.recordResult();
        assertTrue(racer.getResults().contains(r));
        assertTrue(race.getResults().contains(r));
    }

    // ---- Organizer ----

    @Test
    public void createRaceTracksRacesCreated() {
        assertEquals(1, organizer.getRacesCreated().size());
        Race second = organizer.createRace("R-2", RaceType.GRAVEL, true);
        assertEquals(2, organizer.getRacesCreated().size());
        assertTrue(second.isOfficial());
    }

    @Test
    public void recordRaceResultsCapturesCategoryAtTimeOfRace() {
        racer.purchaseLicense("L", Category.CAT_3, LocalDate.now().plusYears(1));
        RaceResult r = organizer.recordRaceResults(race, racer, 1);
        assertEquals(Category.CAT_3, r.getCategoryAtRace());
        assertEquals(1, r.getPlacement());
        assertSame(racer, r.getRacer());
        assertSame(race, r.getRace());
    }

    @Test
    public void recordRaceResultsWithNoLicenseHasNullCategory() {
        RaceResult r = organizer.recordRaceResults(race, racer, 5);
        assertNull(r.getCategoryAtRace());
    }

    // ---- UpgradeNotification ----

    @Test
    public void noNotificationInitially() {
        assertNull(new UpgradeNotification().takeNotification());
    }

    @Test
    public void notificationIsDeliveredExactlyOnce() {
        UpgradeNotification n = new UpgradeNotification();
        n.update("Sam has been upgraded to CAT_4!");
        assertEquals("Sam has been upgraded to CAT_4!", n.takeNotification());
        assertNull(n.takeNotification());
    }

    @Test
    public void newerMessageReplacesOlderUnreadOne() {
        UpgradeNotification n = new UpgradeNotification();
        n.update("first");
        n.update("second");
        assertEquals("second", n.takeNotification());
    }
}
