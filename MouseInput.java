import java.awt.event.MouseEvent;
import java.awt.MouseInfo;
import javax.swing.event.MouseInputListener;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * MouseInput implements the MouseInputListener interface and listens in for mouse inputs such as
 * pressing and releasing certain mouse buttons. This class is added into the Map class.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class MouseInput implements MouseInputListener {

    // Left and Right mouse buttons
    private boolean leftMouseButtonPressed;
    private boolean rightMouseButtonPressed;

    // Coords of the mouse inside the JPanel
    private Point newCenter = new Point(0,0);

    // Mouse inside or outside the JPanel
    private boolean mouseInside;

    // Unused methods
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}

    /**
     * Mouse entered the JPanel
     * @param e MouseEvent object
     */
    public void mouseEntered(MouseEvent e) {

        mouseInside = true;

    }

    /**
     * Mouse exited the JPanel
     * @param e MouseEvent object
     */
    public void mouseExited(MouseEvent e) {

        mouseInside = false;

    }

    public void mousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            // Left Mouse Button pressed
            leftMouseButtonPressed = true;
            newCenter = new Point(e.getX(), e.getY());
            
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            // Right Mouse Button pressed
            rightMouseButtonPressed = true;
            newCenter.setPoint(MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());

        }

    }

    public void mouseReleased(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            // Left Mouse Button released
            leftMouseButtonPressed = false;

        }
        
        if (e.getButton() == MouseEvent.BUTTON3) {
            // Right Mouse Button released
            rightMouseButtonPressed = false;
            
        }

    }

    /**
     * Accessor method for Left Mouse Button
     * @return Returns Left Mouse Button state (button pressed (true) / button released (false))
     */
    public boolean isLMBPressed() {
        
        return leftMouseButtonPressed;
        
    }

    /**
     * Accessor method for Right Mouse Button
     * @return Returns Right Mouse Button state (button pressed (true) / button released (false))
     */
    public boolean isRMBPressed() {
        
        return rightMouseButtonPressed;
        
    }

    /**
     * Accessor method for centerPoint
     * @return centerPoint Point
     */
    public Point getNewCenter() {
        
        return newCenter;
        
    }

    /**
     * Sets Left Mouse Button state
     * @param state button pressed (true) / button released (false)
     */
    public void setLMB(boolean state) {

        leftMouseButtonPressed = false;

    }

    /**
     * Accessor method for mouseInide value
     * @return boolean value which represents if mouse Inside or Outside JPanel
     */
    public boolean isMouseInside() {

        return mouseInside;

    }

}

