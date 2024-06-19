import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * MouseWheelInput implements the MouseWheelListener interface to listen in for the mouse scroll wheel, it listens
 * for the direction of the scroll. This class is added into the Map class as a listener.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class MouseWheelInput implements MouseWheelListener {

    // Rotation direction of mouseWheel. (1 or -1)
    private int mouseWheelRotation = 0;

    /**
     * Sets the direction of the mouseWheelRotation
     * @param e MouseEven object
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        
        mouseWheelRotation = e.getWheelRotation();

    }

    /**
     * Accessor method for mouseWheelRotation
     * @return Returns the direction that the mousewheel is moved in
     */
    public int getMouseWheelRotation() {
     
        return mouseWheelRotation;
        
    }

    /**
     * Clears the rotation of the mouseWheel
     */
    public void clearMouseWheelRotation() {
        
        mouseWheelRotation = 0;
        
    }

}
