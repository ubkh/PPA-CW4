import java.awt.*;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * The borough Class is treated as a structure, where every Borough Object has certain data to then be
 * presented onto the JPanel. A borough object is created per borough for the Map class. These are then drawn
 * to the screen using their Points.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class Borough {

    private Point[] boroughPoints;
    private String boroughName;
    private Color boroughColor;
    private Point boroughLabelLocation;
    private String[] boroughLabel;

    /**
     * Constructor for borough, each borough has its path, name, colour, label location
     * and an Array of String for drawing its name on screen
     * @param boroughPoints Array of Points to create the shape of a borough
     * @param boroughName String which represents the borough's name
     * @param boroughColor Color which represents the borough's color
     * @param boroughLabelLocation Point which indicates the location of the label
     * @param boroughLabel Array of String which allow for the name to be drawn on screen
     */
    public Borough(Point[] boroughPoints, String boroughName, Color boroughColor, Point boroughLabelLocation, String[] boroughLabel) {
        
        this.boroughPoints = boroughPoints;
        this.boroughName = boroughName;
        this.boroughColor = boroughColor;
        this.boroughLabelLocation = boroughLabelLocation;
        this.boroughLabel = boroughLabel;
        
    }

    /**
     * Constructor overloading, Array of String name doesn't need to be specified.
     * @param boroughPoints Array of Points to create the shape of a borough
     * @param boroughName String which represents the borough's name
     * @param boroughColor Color which represents the borough's color
     * @param boroughLabelLocation Point which indicates the location of the label
     */
    public Borough(Point[] boroughPoints, String boroughName, Color boroughColor, Point boroughLabelLocation) {

        this.boroughPoints = boroughPoints;
        this.boroughName = boroughName;
        this.boroughColor = boroughColor;
        this.boroughLabelLocation = boroughLabelLocation;
        boroughLabel = new String[] {boroughName};

    }

    /**
     * Accessor method for boroughPoints
     * @return Returns the ArrayList of points which represent the shape of the borough
     */
    public Point[] getBoroughPoints() {

        return boroughPoints;
        
    }

    /**
     * Accessor method for boroughName
     * @return Returns the borough's name
     */
    public String getBoroughName() {
        
        return boroughName;
        
    }

    /**
     * Accessor method for boroughColor
     * @return Returns the borough's color
     */
    public Color getBoroughColor() {

        return boroughColor;

    }

    /**
     * Accessor method for boroughLabelLocation
     * @return Returns a Point for where the start location of the label is
     */
    public Point getBoroughLabelLocation() {

        return boroughLabelLocation;

    }

    /**
     * Accessor method for boroughLabel Array
     * @return Returns Array of String which contain lines to be drawn
     */
    public String[] getBoroughLabel() {

        return boroughLabel;

    }

    /**
     * Setter method for boroughLabel
     * @param newBoroughLabel The new label it draws
     */
    public void setBoroughLabel(String[] newBoroughLabel) {

        boroughLabel = newBoroughLabel;

    }
    
    // public void generateHexagonPoints(Point center) {
        
        //    |      /|         r - radius                 
        //    |     / |         i - angle                                            
        //    |  r /  |         x - xOffset                                    
        //    |   /   | y       y - yOffset                                  
        //    |  /    |                     
        //    | /\    |           x = r * sin(i)           
        //    |/i_)___|____       y = r * cos(i);                   
        //        x                      
        
        // points.add(center.translatePoint(radius, 0));
        // points.add(center.translatePoint(radius/2, (Math.sqrt(3) / 2) * radius));
        // points.add(center.translatePoint(-radius/2, (Math.sqrt(3) / 2) * radius));
        // points.add(center.translatePoint(-radius, 0));
        // points.add(center.translatePoint(-radius/2, -(Math.sqrt(3) / 2) * radius));
        // points.add(center.translatePoint(radius/2, -(Math.sqrt(3) / 2) * radius));

    // }

}