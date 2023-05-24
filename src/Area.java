public class Area {

    // fields
    private int x;
    private int y;
    private int h;
    private int w;

    // constructor
    public Area(int x, int y, int h, int w) {
        setCoordinates(x, y);
        setDimensions(h, w);
    }

    // getters and setters
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(int h, int w) {
        this.h = h;
        this.w = w;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return y;
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }
}
