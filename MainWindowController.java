import javafx.scene.layout.*;
import javafx.event.*;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A MainWindowController contains control logic that receives view update requests,
 * and manipulates the model in order to display the from and to price choiceboxes, 
 * as well as next and back buttons, and a central container for panels.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class MainWindowController
{
    private BorderPane root;
    private Stage stage;
    
    private Model model;
    private PriceRangeHandler priceRangeHandler;
    
    private final PanelType[] panelTypes = {
        PanelType.WELCOME,
        PanelType.MAP,
        PanelType.STATISTICS,
        PanelType.COMPARISON
    };
    
    private final int priceSelectionInterval = 25;
    private final int intervals = 12; 
    
    @FXML private ChoiceBox<String> fromBox;
    
    @FXML private ChoiceBox<String> toBox;
    
    @FXML private Button backButton;
    
    @FXML private Button nextButton;

    private Pane[] panels = new Pane[panelTypes.length];
    private AbstractPanelController[] panelControllers = new AbstractPanelController[panelTypes.length];
    private int panelIndex = 0;
    
    /**
     * Sets the stage and root object, and sets up the event handlers of the choice boxes
     */
    public void init(Stage stage, BorderPane root) {
        this.stage = stage;
        this.root = root;

        fromBox.setOnAction(this::handleFromBox);
        toBox.setOnAction(this::handleToBox);
    }
    
    /**
     * Sets the model object, and updates the view accordingly. 
    */
    public void initModel(Model model) throws java.io.IOException {
        this.model = model;

        priceRangeHandler = model.getPriceRangeHandler();
        updateDirectionButtons();
        
        setPanelLoaders();
        setPriceSelectionChoices();
        updatePanel();
    }
    
    /**
     * Enables the direction buttons if the price ranges have been set else disable them
     */
    private void updateDirectionButtons() {
        backButton.setDisable(!priceRangeHandler.isPriceRangeSet());
        nextButton.setDisable(!priceRangeHandler.isPriceRangeSet());
    }
    
    /**
     * Adds the price selection choices into its choice box
     */
    private void setPriceSelectionChoices() {
        for (int i = 0; i <= intervals; i++) {
            String price = "£" + priceSelectionInterval * i;
            fromBox.getItems().add(price);
            toBox.getItems().add(price);
        }
    }
    
    /**
     * Takes a money string value (e.g. "£25") and parses it into an integer (25)
     * @param money The string value of money to be parsed 
     * @return The parsed value of the string of money
     */
    public int parseMoney(String money) {
        String parsedString = money.substring(1);
        int value = Integer.parseInt(parsedString);
        return value;
    }
    
    /**
     * Retrieves the price value within a particular choice box
     * @param event The event object which references the choice box
     * @return The price value within the choice box
     */
    private int getPriceWithinBox(ActionEvent event) {
        ChoiceBox choiceBox = (ChoiceBox) event.getSource();
        int value = parseMoney(choiceBox.getValue().toString());
        return value;
    }
    
    /**
     * Handles the setting of the from price field when a choice is
     * selected from the choice box. If an invalid price is set e.g.
     * from price is greater or equal to to price, then the selected
     * choice reverts to its previous value and creates an alert.
     * @param event The event object passed by the action
     */
    public void handleFromBox(ActionEvent event) {
        if (((ChoiceBox) event.getSource()).getValue() == null) {return;}
        int value = getPriceWithinBox(event);
        
        if (priceRangeHandler.getToPrice() == null || value < priceRangeHandler.getToPrice()) {
            priceRangeHandler.setFromPrice(value);
        } else {
            ChoiceBox fromBox = (ChoiceBox) event.getSource();
            
            if (priceRangeHandler.getFromPrice() != null) {
                String previousString = "£" + priceRangeHandler.getFromPrice();
                fromBox.setValue(previousString);               
            } else {
                fromBox.getSelectionModel().clearSelection();
            }

            showInvalidRangeAlert();
        }

        updateDirectionButtons();
        updatePanelControllers();

    }
    
    /**
     * Handles the setting of the to price field when a choice is
     * selected from the choice box. If an invalid price is set e.g.
     * to price is lesser or equal to from price, then the selected
     * choice reverts to its previous value and creates an alert.
     * @param event The event object passed by the action
     */
    public void handleToBox(ActionEvent event) {
        if (((ChoiceBox) event.getSource()).getValue() == null) {return;}
        int value = getPriceWithinBox(event);
        
        if (priceRangeHandler.getFromPrice() == null || value > priceRangeHandler.getFromPrice()) {
            priceRangeHandler.setToPrice(value);
        } else {
            ChoiceBox toBox = (ChoiceBox) event.getSource();
            
            if (priceRangeHandler.getToPrice() != null) {
                String previousString = "£" + priceRangeHandler.getToPrice();
                toBox.setValue(previousString);                
            } else {
                toBox.getSelectionModel().clearSelection();
            }

            showInvalidRangeAlert();
        }

        updateDirectionButtons();
        updatePanelControllers();

    }
    
    /**
     * Creates and shows an alert to explain to the user that the current price range
     * selected is invalid
     */
    public void showInvalidRangeAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Invalid Price Range!");
        alert.setContentText("Please set the maximum price range to a value greater or equal to the minimum.");
        alert.showAndWait();
    }
    
    /**
     * Loads the FXML files of each panel within the application and stores it
     * within an array, and also its equivalent controller object
     */
    private void setPanelLoaders() throws java.io.IOException {
        for (int i = 0; i < panelTypes.length; i++) {
            String panelFileName = panelTypes[i].getFileName();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + panelFileName + ".fxml"));
            Pane root = loader.load();
            panels[i] = root;
            
            AbstractPanelController controller = (AbstractPanelController) loader.getController();
            panelControllers[i] = controller;
            controller.init(root);
            controller.initModel(model);
        }
    }
    
    /**
     * Increments the index to point to the next panel and updates the current panel displayed accordingly
     */
    public void handleNextButton(ActionEvent event) throws java.io.IOException {
        panelIndex += 1;
        
        if (panelIndex == panels.length) {
            panelIndex = 0;
        }
        
        updatePanel();
        updatePanelControllers();
    }
    
    /**
     * Decrements the index to point to the next panel and updates the current panel displayed accordingly
     */
    public void handleBackButton(ActionEvent event) throws java.io.IOException {
        panelIndex -= 1;
        
        if (panelIndex == -1) {
            panelIndex = panels.length - 1;
        }
        
        updatePanel();
        updatePanelControllers();
    }
    
    /**
     * Updates the center of the border to the panel that the index is currently pointing to. Additionally
     * changes the title of the stage to reflect the current panel being displayed.
     */
    private void updatePanel() {
        root.setCenter(panels[panelIndex]);
        stage.setTitle("Property Hunter - " + panelTypes[panelIndex].toString());

    }
    
    /**
     * Calls the update method on all panel controller objects
     */
    private void updatePanelControllers() {
        for (AbstractPanelController controller : panelControllers) {
            controller.update();
        }
    }
}
