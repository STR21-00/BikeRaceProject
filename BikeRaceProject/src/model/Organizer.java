package model;

import java.util.ArrayList;
import java.util.List;

// Organizer, from the DP diagram: 1 organizer creates 0..* races. 
public class Organizer extends User {

    private final List<Race> racesCreated = new ArrayList<>();

    public Organizer(int userId, String name) {
        super(userId, name);
    }

    public Race createRace(String raceId, RaceType raceType, boolean official) {
        Race race = new Race(raceId, raceType, official);
        racesCreated.add(race);
        return race;
    }

    // Records a racer's finishing position, then lets that racer check for a category upgrade. 
    public RaceResult recordRaceResults(Race race, Racer racer, int placement) {
        Category categoryAtRace;
        if(racer.getLicense() != null){
            categoryAtRace = racer.getLicense().getCategory();
        } else{
            categoryAtRace = null;
        }
        
        RaceResult result = new RaceResult(racer, race, placement, categoryAtRace);
        result.recordResult();
        racer.checkCategoryUpgrade();
        return result;
    }

    public List<Race> getRacesCreated() { 
        return racesCreated; 
    }
}
