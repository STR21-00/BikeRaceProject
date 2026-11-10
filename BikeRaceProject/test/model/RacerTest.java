package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RacerTest {

    private Organizer organizer;
    private Racer racer;
    private Race race;

    @Before
    public void setUp() {
        organizer = new Organizer(1, "Chris");
        racer = new Racer(2, "Alex");
        race = organizer.createRace("R-1", RaceType.CRITERIUM, false);
    }

    private void finish(int placement) {
        organizer.recordRaceResults(race, racer, placement);
    }

    // ---- license ----

    @Test
    public void newRacerHasNoLicense() {
        assertNull(racer.getLicense());
    }

    @Test
    public void purchaseLicenseAttachesLicenseToRacer() {
        LocalDate exp = LocalDate.now().plusYears(1);
        License l = racer.purchaseLicense("LIC-2", Category.CAT_5, exp);
        assertSame(l, racer.getLicense());
        assertEquals("LIC-2", l.getLicenseId());
        assertEquals(Category.CAT_5, l.getCategory());
        assertEquals(exp, l.getExpirationDate());
    }

    // ---- reviews ----

    @Test(expected = IllegalStateException.class)
    public void cannotReviewRaceYouWereNotRegisteredFor() {
        racer.submitReview(race, "Great race");
    }

    @Test
    public void registeredRacerCanReview() {
        racer.registerForRace(race);
        Review r = racer.submitReview(race, "Great race");

        assertEquals("Great race", r.getReviewText());
        assertSame(racer, r.getRacer());
        assertSame(race, r.getRace());
        assertEquals(1, racer.getReviews().size());
        assertEquals(1, race.getReviews().size());
    }

    // ---- observer / upgrades ----

    private static class Recorder implements Observer {
        final List<String> messages = new ArrayList<>();
        @Override
        public void update(String message) {
            messages.add(message);
        }
    }

    @Test
    public void fivePodiumsUpgradeCategoryAndNotifyObserver() {
        racer.purchaseLicense("L", Category.CAT_5, LocalDate.now().plusYears(1));
        Recorder rec = new Recorder();
        racer.addObserver(rec);

        for (int i = 0; i < 5; i++) finish(1);

        assertEquals(Category.CAT_4, racer.getLicense().getCategory());
        assertEquals(1, rec.messages.size());
        assertEquals("Alex has been upgraded to CAT_4!", rec.messages.get(0));
    }

    @Test
    public void fourPodiumsDoNotUpgrade() {
        racer.purchaseLicense("L", Category.CAT_5, LocalDate.now().plusYears(1));
        Recorder rec = new Recorder();
        racer.addObserver(rec);

        for (int i = 0; i < 4; i++) finish(2);

        assertEquals(Category.CAT_5, racer.getLicense().getCategory());
        assertTrue(rec.messages.isEmpty());
    }

    @Test
    public void nonPodiumFinishesDoNotCountTowardUpgrade() {
        racer.purchaseLicense("L", Category.CAT_5, LocalDate.now().plusYears(1));
        for (int i = 0; i < 4; i++) finish(1);   // 4 podiums
        for (int i = 0; i < 10; i++) finish(4);  // lots of non-podiums
        assertEquals(Category.CAT_5, racer.getLicense().getCategory());
    }

    @Test
    public void podiumCounterResetsAfterUpgrade() {
        racer.purchaseLicense("L", Category.CAT_5, LocalDate.now().plusYears(1));
        for (int i = 0; i < 5; i++) finish(1);
        assertEquals(Category.CAT_4, racer.getLicense().getCategory());

        for (int i = 0; i < 4; i++) finish(1);
        assertEquals(Category.CAT_4, racer.getLicense().getCategory());

        finish(3);
        assertEquals(Category.CAT_3, racer.getLicense().getCategory());
    }

    @Test
    public void racerWithoutLicenseNeverUpgradesAndDoesNotCrash() {
        for (int i = 0; i < 6; i++) finish(1);
        assertNull(racer.getLicense());
        assertEquals(6, racer.getResults().size());
    }

    @Test
    public void duplicateObserverIsOnlyNotifiedOnce() {
        racer.purchaseLicense("L", Category.CAT_5, LocalDate.now().plusYears(1));
        Recorder rec = new Recorder();
        racer.addObserver(rec);
        racer.addObserver(rec);

        for (int i = 0; i < 5; i++) finish(1);

        assertEquals(1, rec.messages.size());
    }

    @Test
    public void removedObserverIsNotNotified() {
        racer.purchaseLicense("L", Category.CAT_5, LocalDate.now().plusYears(1));
        Recorder rec = new Recorder();
        racer.addObserver(rec);
        racer.removeObserver(rec);

        for (int i = 0; i < 5; i++) finish(1);

        assertTrue(rec.messages.isEmpty());
    }

    @Test
    public void everyObserverGetsTheMessage() {
        Recorder a = new Recorder();
        Recorder b = new Recorder();
        racer.addObserver(a);
        racer.addObserver(b);
        racer.notifyObserver("hello");
        assertEquals("hello", a.messages.get(0));
        assertEquals("hello", b.messages.get(0));
    }
}
