
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;


/**
 * Write a description of class MapPanelController here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class MapPanelController extends AbstractPanelController {
    
    @FXML private SwingNode swingNode;
    @FXML private Slider zoomSlider;

    private Map map;

    private Pane listingRoot;

    private PropertyListingController controller;

    public void update() {
        map.updatePriceRanges();

        if (controller == null) {
            return;

        }

        controller.update();

    }

    
    /**
     * Sets the root node and creates a swing node which contains the map
     */
    public void init(Pane root) {
        setRoot(root);

    }
    
    /**
     * Sets the model object
     */
    public void initModel(Model model) {
        setModel(model);
        createSwingNode(swingNode);

        // temp
        //showListingWindow();
//        filteredListing = model.getListings().stream()
//                .filter(l -> l.getNeighbourhood().equals(selectedBorough))
//                .collect(Collectors.toList());
//
//        filteredListing.forEach((l) -> System.out.println(l.getNeighbourhood()));
    }

    public void showListingWindow(String selectedBorough) {
        try {
            FXMLLoader loader
                    = new FXMLLoader(getClass().getResource("/PropertyListWindow.fxml"));
            listingRoot = loader.load();
            
            controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle(selectedBorough);

            stage.setScene(new Scene(listingRoot));
            listingRoot.getScene().getStylesheets().add("app.css");

            stage.setResizable(false);
            stage.show();

            controller.init(listingRoot);
            controller.initModel(getModel());

            controller.setSelectedBorough(selectedBorough);
            controller.update();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a swing node which contains the map object
     * @param swingNode The swing node object to contain the map
     */
    private void createSwingNode(SwingNode swingNode) {
        map = new Map(getModel(), this);
        swingNode.setContent(map);

    }


    // Up Button
    public void upButtonPressed() throws java.io.IOException {
        map.setUpButtonState(true);
    }

    public void upButtonReleased() throws java.io.IOException {
        map.setUpButtonState(false);

    }


    // Down Button
    public void downButtonPressed() throws java.io.IOException {
        map.setDownButtonState(true);

    }

    public void downButtonReleased() throws java.io.IOException {
        map.setDownButtonState(false);

    }


    // Left Button
    public void leftButtonPressed() throws java.io.IOException {
        map.setLeftButtonState(true);

    }

    public void leftButtonReleased() throws java.io.IOException {
        map.setLeftButtonState(false);

    }


    // Right Button
    public void rightButtonPressed() throws java.io.IOException {
        map.setRightButtonState(true);

    }

    public void rightButtonReleased() throws java.io.IOException {
        map.setRightButtonState(false);

    }


    /**
     * Sends the current percentage of the zoom slider into
     * the Map class to appropriately scale.
     */
    public void handleZoomSlider() {

        map.scrollInput(zoomSlider.getValue());

    }

}
