import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A PropertyListingController contains control logic that receives view update requests,
 * and manipulates the model in order to list details about properties within a selected
 * borough. This controller is also responsible for the property description window.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public class PropertyListingController extends AbstractPanelController {

    // define fields
    private ComparisonHandler comparisonHandler; // local comparisonHandler for dealing with favourites
    
    private String selectedBorough; // the current selected borough's name
    private TableView<AirbnbListing> table; // table for listing properties
    private Label count; // total number of properties in table

    private ChoiceBox<String> sort; // choicebox for filtering properties

    @FXML private Button mapButton; // button for displaying property in Google Maps
    
    @FXML private ToggleButton favouriteButton; // button for toggling whether a borough as a favourite

    /**
     * Initialises the controller on first construction. Called once only.
     *
     * @param root The root node of the controller
     */
    @Override
    void init(Pane root) {
        setRoot(root);

        table = (TableView) getRoot().lookup("#table");
        count = (Label) getRoot().lookup("#count");
        sort = (ChoiceBox) getRoot().lookup("#sort");

        sort.getItems().add("Host name (alphabetically)");
        sort.getItems().add("Price");
        sort.getItems().add("Review count");

        sort.setOnAction(this::handleSortBox);

        TableColumn host = table.getColumns().get(0);
        TableColumn price = table.getColumns().get(1);
        TableColumn reviewCount = table.getColumns().get(2);
        TableColumn minNights = table.getColumns().get(3);

        // we match the values with variables and accessor methods
        // with the same name in every AirbnbListing object
        host.setCellValueFactory(new PropertyValueFactory<>("host_name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        reviewCount.setCellValueFactory(new PropertyValueFactory<>("numberOfReviews"));
        minNights.setCellValueFactory(new PropertyValueFactory<>("minimumNights"));

        // set an event that is fired when a row is selected
        table.setRowFactory(tv -> {
            TableRow<AirbnbListing> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    // get the listing represented by this row,
                    // and display its details in a new window
                    AirbnbListing data = row.getItem();
                    displayPropertyDescription(data);
                }
            });
            return row;
        });
    }

    /**
     * Initialises the model this controller will reference, and additional
     * code that requires the model to be set first. Called once only.
     *
     * @param model The model object
     */
    @Override
    void initModel(Model model) {
        setModel(model);
        favouriteButton.setOnAction(this::toggleFavouriteBorough);
        comparisonHandler = model.getComparisonHandler();
    }

    /**
     * Get all listings within the specified price range, add them to the table,
     * update the properties count and the favourite button. This method is
     * called every time the price range changes, or the Back/Next buttons are
     * clicked.
     */
    @Override
    void update() {
        // get listings in price range
        HashMap<String, ArrayList<AirbnbListing>> listings = getModel().getListingsByBoroughs(
                getModel().getPriceRangeHandler().getListingsInPriceRange()
        );
        // get listings within specified borough
        ArrayList<AirbnbListing> boroughListings = listings.get(selectedBorough);
        // remove any items there already
        table.getItems().clear();

        if (boroughListings == null) { return; }
        boroughListings.forEach(l -> table.getItems().add(l));

        table.sort();
        updateListingCount(boroughListings);
        updateFavouriteButton();
    }

    /**
     * Toggles this borough as a favourite borough, or otherwise.
     *
     * @param event The event associated with clicking the Favourite button.
     */
    public void toggleFavouriteBorough(ActionEvent event) {
        comparisonHandler.toggleFavouriteBorough(selectedBorough);
        updateFavouriteButton();
    }

    /**
     * Updates the Favourite button depending on whether this borough is
     * a favourite or not.
     */
    private void updateFavouriteButton() {
        if (comparisonHandler.getFavouriteBoroughs().contains(selectedBorough)) {
            favouriteButton.setText("Unfavourite");
            favouriteButton.setSelected(true);
        } else {
            favouriteButton.setText("Favourite");
            favouriteButton.setSelected(false);
        }
    }

    /**
     * Mutator method to set the given borough as the selected one.
     *
     * @param selectedBorough A given borough's name.
     */
    public void setSelectedBorough(String selectedBorough) {
        this.selectedBorough = selectedBorough;
    }

    /**
     * Updates the current number of properties in the borough on the GUI.
     *
     * @param listings All listings within the selected borough.
     */
    private void updateListingCount(ArrayList<AirbnbListing> listings) {
        count.setText(listings.size() + " properties available");
    }

    /**
     * Displays a new window revealing a selected property description,
     * and a button to open its location in Google Maps.
     *
     * @param listing A given property listing.
     */
    private void displayPropertyDescription(AirbnbListing listing) {
        // insert actual window
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PropertyDescriptionWindow.fxml"));
            Pane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Property description");
            stage.setScene(new Scene(root));
            root.getScene().getStylesheets().add("app.css");

            stage.setResizable(false);
            stage.show();

            Label neighbourhood = (Label) root.lookup("#neighbourhood");
            Button mapButton = (Button) root.lookup("#mapButton");

            mapButton.setOnAction(e -> handleMapButton(listing.getLatitude(), listing.getLongitude()));
            neighbourhood.setText("Room in " + listing.getNeighbourhood());

            // list of details of this property
            ListView<String> listView = (ListView) root.lookup("#list");
            ObservableList<String> list = FXCollections.observableList(new ArrayList<>());
            list.add("Room type: " + listing.getRoom_type());
            list.add("Price: £" + listing.getPrice());
            list.add("Description: " + listing.getName());
            list.add("Minimum nights: " + listing.getMinimumNights());
            list.add("Availability in year: " + listing.getAvailability365());
            list.add("Longitude: " + listing.getLongitude());
            list.add("Latitude: " + listing.getLatitude());
            list.add("Review count: " + listing.getNumberOfReviews());
            list.add("Reviews per month: " + listing.getReviewsPerMonth());
            list.add("Last review: " + listing.getLastReview());
            list.add("Host name: " + listing.getHost_name());
            list.add("Host listings: " + listing.getCalculatedHostListingsCount());

            listView.setItems(list);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts the listing table in a selected sorting order from the ChoiceBox.
     * Called when the sorting ChoiceBox selection is changed.
     *
     * @param event The event associated with the sorting ChoiceBox.
     */
    public void handleSortBox(ActionEvent event) {
        if (((ChoiceBox) event.getSource()).getValue() == null) {return;}

        // we associate the indices of the ChoiceBox with the column indices
        // of the table
        int index = ((ChoiceBox<?>) event.getSource()).getSelectionModel().getSelectedIndex();
        TableColumn column = table.getColumns().get(index);

        column.setSortType(TableColumn.SortType.ASCENDING);
        column.setSortable(true);
        table.getSortOrder().setAll(column);
        table.sort();
        column.setSortable(false);
    }

    /**
     * Opens a location in Google Maps using the browser.
     * Called when the "Open in Google Maps" button is clicked.
     *
     * @param latitude A given latitude of the location.
     * @param longitude A given longitude of the location.
     */
    public void handleMapButton(double latitude, double longitude) {
        try {
            Desktop.getDesktop().browse(new URI("https://maps.google.com?q=" + latitude + "," + longitude));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
