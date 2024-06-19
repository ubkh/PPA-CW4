import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.event.*;
import javafx.scene.control.Label;
import java.util.ArrayList;

import javafx.scene.control.ChoiceBox;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A StatisticsPanelController contains control logic that receives view update requests,
 * and manipulates the model in order to display: A toolbar with several filters (as choiceboxes),
 * a Bar Chart showing one statistic value, and seven other statistic values which  are viewed 
 * from four different sections of a GridPane. These seven stats can be flicked through using 
 * next and back buttons. Objects from this class inherit from Abstract Pane.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class StatisticsPanelController extends AbstractPanelController
{
    private ArrayList<StatisticType> activeStatistics = new ArrayList<>();
    private ArrayList<StatisticType> inactiveStatistics = new ArrayList<>(); 
    
    private Model model;
    private StatisticsHandler statisticsHandler;
    
    @FXML
    private BarChart<String, Integer> barChart;
    
    private ArrayList<XYChart.Data<String, Integer>> barChartData = new ArrayList<>();
    
    private final int choiceBoxIntervals = 15;
    
    @FXML
    private ChoiceBox<String> nightsChoiceBox;
    
    @FXML
    private ChoiceBox<String> reviewsChoiceBox;
    
    //private HashMap<String, ChoiceBox<String>> choiceBoxes = new HashMap<>();
    
    //private HashMap<String, Double> choiceBoxSelectionIntervals = new HashMap<>();
    
    private ArrayList<StatisticsChoiceBox> choiceBoxes = new ArrayList<>();
    
    /**
     * Creates intervals for the choice boxes and connects an action event listener to them
     * @param root The root node object 
     */
    public void init(Pane root) {
        setRoot(root);       
        setStatistics();
        
        StatisticsChoiceBox minNights = new StatisticsChoiceBox("minimumNights", 1.0, nightsChoiceBox);
        StatisticsChoiceBox minReviews = new StatisticsChoiceBox("minimumReviews", 0.5, reviewsChoiceBox);
        choiceBoxes.add(minNights);
        choiceBoxes.add(minReviews);
        
        setChoiceBoxHandlers();
    }
    
    /**
     * Instantiates data objects and is inserted into the bar chart that will
     * display the data
     */
    private void initBarChart() {
        XYChart.Series series = new XYChart.Series();
        //series.setName("Room types");
        
        barChartData.add(new XYChart.Data("Private room", 0));
        barChartData.add(new XYChart.Data("Shared room", 0));
        barChartData.add(new XYChart.Data("Entire home/apt", 0));
        
        series.getData().add(barChartData.get(0));
        series.getData().add(barChartData.get(1));
        series.getData().add(barChartData.get(2));
        
        barChart.getData().add(series);
    }
    
    /**
     * Updates the bar chart's model 
     */
    private void updateBarChart() {
        int[] roomTypeDistribution = statisticsHandler.getRoomTypeDistribution();
        
        for (int i = 0; i <= barChartData.size() - 1; i++) {
            barChartData.get(i).setYValue(roomTypeDistribution[i]);
        }
    }
           
    /**
     * Creates an action event listener for each choice box and handles these
     * listened events by setting its associated filter with the choice selected
     */
    private void setChoiceBoxHandlers() {
        for (StatisticsChoiceBox choiceBox : choiceBoxes) {
            choiceBox.getChoiceBoxType().setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    double value = Double.parseDouble(choiceBox.getChoiceBoxType().getValue());
                    statisticsHandler.setFilter(choiceBox.toString(), value);
                    update();
                }
            });
        }
    }
    
    /**
     * Initialises the bar chart and statistic boxes 
     * @param model The model object of the application
     */
    public void initModel(Model model) {
        setModel(model);
        statisticsHandler = getModel().getStatisticsHandler();
        initStatisticBoxes();
        initBarChart();
        updateBarChart();
    }
    
    /**
     * Intialises each statistic box by connecting action event listeners
     * to their previous and next buttons as well as updating the labels' state
     */
    private void initStatisticBoxes() {
        for (int i = 0; i <= 3; i++) {
            String id = "#stat" + i;
            BorderPane statisticBox = (BorderPane) getRoot().lookup(id);
            
            ((Button) statisticBox.getLeft()).setOnAction(this::handleBackButton);
            ((Button) statisticBox.getRight()).setOnAction(this::handleNextButton);
            
            updateStatisticBox(statisticBox, i);
        }
    }
    
    /**
     * Iterates through each statistic box and calls the update method
     */
    private void updateStatisticBoxes() {
        for (int i = 0; i <= 3; i++) {
            String id = "#stat" + i;
            BorderPane statisticBox = (BorderPane) getRoot().lookup(id);
            updateStatisticBox(statisticBox, i);
        }
    }
    
    /**
     * Updates the specified statistic box's label states
     * @param statisticBox The statistic box to be updated
     * @param i The index of activeStatistics
     */
    private void updateStatisticBox(BorderPane statisticBox, int i) {
        StatisticType statisticType = activeStatistics.get(i);

        String statisticName = statisticType.toString();
        String statisticValue = statisticsHandler.getStatisticValue(statisticType);

        ((Label) statisticBox.getTop()).setText(statisticName);
        ((Label) statisticBox.getCenter()).setText(statisticValue);
    }
    
    /**
     * Calls the swap statistic back method on the statistic box when
     * the back button is clicked on
     * @param event The event object 
     */
    private void handleBackButton(ActionEvent event) {
        BorderPane statisticBox = (BorderPane) ((Node) event.getSource()).getParent();
        String id = statisticBox.getId();
        int index = Character.getNumericValue(id.charAt(4));
        swapStatisticBack(statisticBox, index);
    }
    
    /**
     * Calls the swap statistic next method on the statistic box when
     * the next button is clicked on
     * @param event The event object 
     */
    private void handleNextButton(ActionEvent event) {
        BorderPane statisticBox = (BorderPane) ((Node) event.getSource()).getParent();
        String id = statisticBox.getId();
        int index =  Character.getNumericValue(id.charAt(4));
        swapStatisticNext(statisticBox, index);
    }
    
    /**
     * Swaps the last statistic viewed into view, and the current statistic in view to out of view
     */
    private void swapStatisticBack(BorderPane statisticBox, int index) {
        StatisticType statisticToRemove = activeStatistics.remove(index);
        inactiveStatistics.add(0, statisticToRemove);
        StatisticType statisticToAdd = inactiveStatistics.remove(inactiveStatistics.size() - 1);
        activeStatistics.add(index, statisticToAdd);
        updateStatisticBox(statisticBox, index);
    }
    
    /**
     * Swaps the next statistic to view into view, and the current statistic in view to out of view
     */
    private void swapStatisticNext(BorderPane statisticBox, int index) {
        StatisticType statisticToRemove = activeStatistics.remove(index);
        inactiveStatistics.add(statisticToRemove);
        StatisticType statisticToAdd = inactiveStatistics.remove(0);
        activeStatistics.add(index, statisticToAdd);    
        updateStatisticBox(statisticBox, index);
    }
    
    /**
     * Sets up active statistics and inactive statistics, whose contents will later be swapped between each other
     */
    private void setStatistics() {
        activeStatistics.add(StatisticType.AVERAGE_NUMBER_OF_REVIEWS_PER_PROPERTY);
        activeStatistics.add(StatisticType.NUMBER_OF_AVAILABLE_PROPERTIES);
        activeStatistics.add(StatisticType.NUMBER_OF_ENTIRE_HOME_AND_APARTMENTS);
        activeStatistics.add(StatisticType.MOST_EXPENSIVE_BOROUGH);
        
        inactiveStatistics.add(StatisticType.HOST_WITH_MOST_INCOME);
        inactiveStatistics.add(StatisticType.HOST_IN_MOST_BOROUGHS);
        inactiveStatistics.add(StatisticType.HOST_WITH_MOST_PRIV_AVAILABLE);
    }
    
    /**
     * Updates the statistic boxes and bar chart
     */
    public void update() {
        updateBarChart();
        updateStatisticBoxes();
    }
}