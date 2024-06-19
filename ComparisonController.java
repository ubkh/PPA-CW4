import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.event.*;

import javafx.fxml.FXML;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A ComparisonController contains control logic that receives view update requests,
 * and manipulates the model in order to display a simple data comparison between 2
 * of the user's selected favourite boroughs.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public class ComparisonController extends AbstractPanelController
{
    // define fields
    @FXML private ChoiceBox<String> borough1; // the ChoiceBox for the first borough selection
    @FXML private ChoiceBox<String> borough2; // the ChoiceBox for the second borough selection
    @FXML private TableView table; // the table in which the comparison is to be displayed

    private TableColumn statsColumn; // the column to show the names of comparison data
    private TableColumn borough1Column; // the column to show data from borough 1
    private TableColumn borough2Column; // the column to show data from borough 2

    private ComparisonHandler comparisonHandler; // // local comparisonHandler for dealing with favourites

    private static final double FIXED_CELL_SIZE = 63.6; // size of cells in the table
    private static final String STATS_COLOR_HEX = "#2b2b2b"; // colour of cells in the stats column

    /**
     * Initialises the controller on first construction. Called once only.
     *
     * @param root The root node of the controller
     */
    @Override
    public void init(Pane root) {
        setRoot(root);

        // selections are not allowed in this table
        table.setSelectionModel(null);

        statsColumn = (TableColumn) table.getColumns().get(0);
        borough1Column = (TableColumn) table.getColumns().get(1);
        borough2Column = (TableColumn) table.getColumns().get(2);

        // we match the values with variables and accessor methods
        // with the same name in every ComparisonListing object
        statsColumn.setCellValueFactory(new PropertyValueFactory<>("stat"));
        borough1Column.setCellValueFactory(new PropertyValueFactory<>("value1"));
        borough2Column.setCellValueFactory(new PropertyValueFactory<>("value2"));

        table.setFixedCellSize(FIXED_CELL_SIZE);
        statsColumn.setStyle("-fx-background-color: " + STATS_COLOR_HEX + ";");

        borough1.setOnAction(this::handleBorough1Box);
        borough2.setOnAction(this::handleBorough2Box);
    }

    /**
     * Initialises the model this controller will reference, and additional
     * code that requires the model to be set first. Called once only.
     *
     * @param model The model object
     */
    @Override
    public void initModel(Model model) {
        setModel(model);
        comparisonHandler = model.getComparisonHandler();
    }

    /**
     * Clears any possible borough ChoiceBox selections, and updates them in
     * line with the current state of the user's favourite boroughs. This method is
     * called every time the price range changes, or the Back/Next buttons are
     * clicked.
     */
    @Override
    public void update() {
        borough1.getItems().clear();
        borough2.getItems().clear();
        comparisonHandler.getFavouriteBoroughs().forEach(b -> borough1.getItems().add(b));
        comparisonHandler.getFavouriteBoroughs().forEach(b -> borough2.getItems().add(b));
    }

    /**
     * Create a listing of each comparison type in the table.
     */
    private void createListings() {
        if (borough1.getValue() == null || borough2.getValue() == null) { return; }
        // if the user tries to compare a borough to itself, an alert is thrown
        if (borough1.getValue().equals(borough2.getValue())) {
            showInvalidSelectionAlert();
            // update to clear the invalid choice the user just made
            update();
            return;
        }

        // remove anything in the table currently
        table.getItems().clear();

        // both choices selected...
        borough1Column.setText(borough1.getValue());
        borough2Column.setText(borough2.getValue());

        // let the comparisonHandler handle the creation of ComparisonListing objects
        // with the given boroughs
        comparisonHandler.setBoroughs(borough1.getValue(), borough2.getValue());
        // place these ComparisonListing objects into the table
        comparisonHandler.getComparisonListings()
                        .forEach(l -> table.getItems().add(l));
    }

    /**
     * Display an invalid borough selection alert.
     */
    private void showInvalidSelectionAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Invalid Borough Selection!");
        alert.setContentText("You cannot compare the same borough to itself.");
        alert.showAndWait();
    }

    /**
     * Call the method to create listings when the selected first borough
     * is changed.
     *
     * @param event The event associated with the ChoiceBox.
     */
    private void handleBorough1Box(ActionEvent event) {
        createListings();
    }

    /**
     * Call the method to create listings when the selected second borough
     * is changed.
     *
     * @param event The event associated with the ChoiceBox.
     */
    private void handleBorough2Box(ActionEvent event) {
        createListings();
    }
}
