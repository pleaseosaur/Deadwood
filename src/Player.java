public class Player {
    // fields
    private String name;
    private String color;
    private int rank;
    private int credits;
    private int dollars;
    private Role role;
    private int practiceChips;
    private Location location;
    private boolean hasMoved;
    private boolean hasUpgraded;
    private boolean hasActed;
    private boolean hasRehearsed;
    private boolean hasTakenRole;
    private int playerX;
    private int playerY;


    // constructor
    public Player(String name, int rank, int credits, int dollars, String color){
        setName(name);
        setRank(rank);
        setCredits(credits);
        setDollars(dollars);
        this.color = color;
        setRole();
        resetPracticeChips();
        setHasMoved(false);
        setHasUpgraded(false);
        setHasActed(false);
        setHasRehearsed(false);
        setHasTakenRole(false);
    }


    // getters and setters
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }


    public void setRank(int rank) {
        this.rank = rank;
    }
    public int getRank() {
        return rank;
    }


    public void setCredits(int credits) {
        this.credits = credits;
    }
    public int getCredits() {
        return credits;
    }
    public void addCredits(int credits) {
        setCredits(getCredits() + credits);
    }


    public void setDollars(int dollars) {
        this.dollars = dollars;
    }
    public int getDollars() {
        return dollars;
    }
    public void addDollars(int dollars) {
        setDollars(getDollars() + dollars);
    }


    public void setRole() {
        this.role = null;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public Role getRole() {
        return role;
    }
    public boolean hasRole() {
        return this.role != null;
    }


    public void addPracticeChips() {
        this.practiceChips += 1;
    }
    public void resetPracticeChips() {
        this.practiceChips = 0;
    }
    public int getPracticeChips() {
        return practiceChips;
    }


    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() {
        return location;
    }


    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    public boolean getHasMoved() {
        return hasMoved;
    }


    public void setHasUpgraded(boolean hasUpgraded) {
        this.hasUpgraded = hasUpgraded;
    }
    public boolean getHasUpgraded() {
        return hasUpgraded;
    }


    public void setHasActed(boolean hasActed) {
        this.hasActed = hasActed;
    }
    public boolean getHasActed() {
        return hasActed;
    }


    public void setHasRehearsed(boolean hasRehearsed) {
        this.hasRehearsed = hasRehearsed;
    }
    public boolean getHasRehearsed() {
        return hasRehearsed;
    }


    public void setHasTakenRole(boolean b) {
        this.hasTakenRole = b;
    }
    public boolean getHasTakenRole() {
        return hasTakenRole;
    }

    public void setPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
    }

    public int[] getPosition() {
        return new int[]{playerX, playerY};
    }

    public String getColor() {
        return color;
    }
}
