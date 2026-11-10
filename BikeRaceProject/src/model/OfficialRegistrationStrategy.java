package model;

/* officialRegistrationStrategy, from the DP diagram.
 Mirrors the Activity Diagram's official-race branch: valid license first, then category.
 */
public class OfficialRegistrationStrategy implements RegistrationStrategy {

    @Override
    public void register(Racer racer, Race race) {
        validateLicense(racer);      // Activity Diagram: "Is the racer's license valid?"
        validateCategory(racer, race); // Activity Diagram: "Is the racer's license category eligible?"
    }

    public void validateLicense(Racer racer) {
        License license = racer.getLicense();
        if (license == null || !license.isValid()) {
            throw new IneligibleException("A valid license is required for official races.");
        }
    }

    public void validateCategory(Racer racer, Race race) {
        
        // Design constraint: racers must be in a category (CAT_1-CAT_5) to race officially.
        if (racer.getLicense().getCategory() == null) {
            throw new IneligibleException("License category is not eligible for this race.");
        }
    }
}
