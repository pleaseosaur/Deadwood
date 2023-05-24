// imports

import java.util.List;

public class CastingOffice extends Location {
    // fields
    private final List<Upgrade> upgrades;

    // constructor
    public CastingOffice(String name, List<String> neighbors, Area area, List<Upgrade> upgrades) {
        super(name, neighbors, area);
        this.upgrades = upgrades;
    }

    // getters and setters
    public List<Upgrade> getUpgrades(){
        return this.upgrades;
    }

}
