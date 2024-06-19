
/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * The Point class is treated as a structure class, where each point has a XY coordinate,
 * this allows it to be drawn on a graph (JPanel) and display the boroughs accordingly.
 * Boroughs use this class as they are created from multiple points.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class Point {
    
    private double x, y;

    /**
     * Constructor for Point, takes X and Y coord
     * @param x X coordinate
     * @param y Y coordinate
     */
    public Point(double x, double y) {
        
        this.x = x; this.y = y;
        
    }

    /**
     * Accessor method for X coordinate
     * @return Returns the X coordinate
     */
    public double getPointX() {
        
        return x;
        
    }

    /**
     * Accessor method for Y coordinate
     * @return Returns the Y coordinate
     */
    public double getPointY() {
        
        return y;
        
    }

    /**
     * Sets the X and Y coords to the new values specified
     * @param newX new X value to be set
     * @param newY new Y value to be set
     */
    public void setPoint(double newX, double newY) {
        
        x = newX; y = newY;
        
    }

    /**
     * Applies offset to the existing X and Y coords
     * @param xVal x Offset value
     * @param yVal y Offset value
     */
    public void applyPointOffset(double xVal, double yVal) {
        
        x += xVal; y += yVal;    
        
    }

    /**
     * Applies offset to the existing X and Y coords
     * @param point Point XY value to add
     */
    public void applyPointOffset(Point point) {
        
        x += point.getPointX(); y += point.getPointY();
        
    }

    /**
     * Translates a point using an X and Y offset
     * @param xVal xOffset to be added
     * @param yVal yOffset to be added
     * @return Returns a new point with the offset values added
     */
    public Point translatePoint(double xVal, double yVal) {
        
        return new Point(x + xVal, y + yVal);
        
    }

    /**
     * Translates a point using an X and Y offset in a point
     * @param point Point which has X and Y value
     * @return Returns a new Point with the added X and Y offsets
     */
    public Point translatePoint(Point point) {
        
        return new Point(x + point.getPointX(), y + point.getPointY());
        
    }

    /**
     * Scales the points by a constant or value
     * @param scale value to scale coords by
     * @return Returns a new Point after scale factor applied
     */
    public Point scalePoint(double scale) {
        
        return new Point(x * scale, y * scale);
        
    }

}
