package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class RaceTest {

    private Race race;

    @Before
    public void setUp() {
        race = new Race("R-1", RaceType.ROAD_RACE, false);
    }

    @Test
    public void constructorStoresBasics() {
        assertEquals("R-1", race.getRaceId());
        assertEquals(RaceType.ROAD_RACE, race.getRaceType());
        assertFalse(race.isOfficial());
    }

    @Test
    public void raceWithNoDeadlineAndNoLimitIsOpen() {
        assertTrue(race.isRegistrationOpen());
    }

    @Test
    public void deadlineInThePastClosesRegistration() {
        race.setRegistrationDeadline(LocalDate.now().minusDays(1));
        assertFalse(race.isRegistrationOpen());
    }

    @Test
    public void deadlineTodayIsStillOpen() {
        race.setRegistrationDeadline(LocalDate.now());
        assertTrue(race.isRegistrationOpen());
    }

    @Test
    public void raceBecomesClosedWhenLimitReached() {
        race.setParticipantLimit(1);
        assertTrue(race.isRegistrationOpen());
        new Racer(1, "Sam").registerForRace(race);
        assertFalse(race.isRegistrationOpen());
    }

    @Test
    public void limitOfZeroMeansUnlimited() {
        race.setParticipantLimit(0);
        for (int i = 0; i < 20; i++) {
            new Racer(i, "R" + i).registerForRace(race);
        }
        assertTrue(race.isRegistrationOpen());
    }
}
