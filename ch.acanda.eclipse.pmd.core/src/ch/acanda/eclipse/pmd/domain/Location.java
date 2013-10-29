package ch.acanda.eclipse.pmd.domain;


public class Location {
    
    private final String path;
    private final LocationContext context;
    
    public Location(final String path, final LocationContext context) {
        this.path = path;
        this.context = context;
    }
    
    public String getPath() {
        return path;
    }
    
    public LocationContext getContext() {
        return context;
    }
    
}