import javafx.scene.layout.Pane;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A WelcomeController contains control logic that receives view update requests,
 * and manipulates the model in order to display the welcome page information.
 * Objects from this class inherit from Abstract Pane.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class WelcomeController extends AbstractPanelController
{
    public void update() {}
    
    public void init(Pane root) {
        setRoot(root);
    }
    
    public void initModel(Model model) {
        setModel(model);
    }
}
