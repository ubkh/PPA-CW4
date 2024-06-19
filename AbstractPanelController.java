import javafx.scene.layout.Pane;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A AbstractPanelController contains control logic that receives view update requests,
 * and manipulates the model in order to display a flexible range of things, due to the
 * nature of it being abstract. The class manages user interaction between elements on 
 * the particular Pane.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public abstract class AbstractPanelController
{
    private Pane root;
    private Model model;
    
    /**
     * Manipulates the view based on changes made to the model
     */
    abstract void update();
    
    /**
     * Sets the controller object's root node and initialises GUI elements within the view
     * @param root The root node of the controller 
     */
    abstract void init(Pane root);
    
    /**
     * Sets the controller object's model and calls methods pertaining to the model's state
     * @param model The model object
     */
    abstract void initModel(Model model);
    
    /**
     * @return The model object
     */
    public Model getModel() {
        return model;
    }
    
    /**
     * Sets the model object
     * @param model The model object
     */
    public void setModel(Model model) {
        this.model = model;
    }
    
    /**
     * @return The root node of the controller object
     */
    public Pane getRoot() {
        return root;
    }
    
    /**
     * Sets the root node of the controller object
     * @param root The root node
     */
    public void setRoot(Pane root) {
        this.root = root;
    }
}
