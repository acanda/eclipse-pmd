package ch.acanda.eclipse.pmd.domain;

public class RuleSetModel {
    
    private final String name;
    private final Location location;
    
    public RuleSetModel(final String name, final Location location) {
        this.name = name;
        this.location = location;
    }
    
    public String getName() {
        return name;
    }
    
    public Location getLocation() {
        return location;
    }
    
}