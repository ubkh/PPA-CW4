import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * MouseMotionInput implements the MouseMotionListener interface to listen in for the movement of the mouse,
 * this allows for coordinates inside the JPanel to be retrieved and highlight the correct aspects. This class
 * is added to the Map class.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class MouseMotionInput implements MouseMotionListener {

    // Location of mouse
    private Point mousePoint = new Point(0,0);

    // Unused method
    public void mouseDragged(MouseEvent e) {}

    /**
     * Sets location of mouse inside JPanel
     * @param e MouseEvent object
     */
    public void mouseMoved(MouseEvent e) {
        
        mousePoint = new Point(e.getX(), e.getY());
        
    }

    /**
     * Accessor method for mousePoint
     * @return mousePoint Point object
     */
    public Point getMousePoint() {
        
        return mousePoint;
        
    }

    /**
     * Sets the mouse point location
     * @param point Point object which sets the location of the mouse in JPanel
     */
    public void setMousePoint(Point point) {
        
        mousePoint = point;
        
    }
    
}