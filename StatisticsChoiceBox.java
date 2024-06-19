import javafx.scene.control.ChoiceBox;

/**
 * Class ChoiceBoxes - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public class StatisticsChoiceBox
{
    private String string;
    private Double interval;
    private ChoiceBox<String> choiceBoxType;
    private final int choiceBoxIntervals = 15;

    /**
     * The constructor of the class ChoiceBoxDropDown.
     * @param string The name of the choice box
     * @param interval The number of intervals that the choice box will contain
     * @param choiceBoxType The choicebox object to be operated on
     */
    public StatisticsChoiceBox(String string, Double interval, ChoiceBox<String> choiceBoxType) {
        this.string = string;
        this.interval = interval;
        this.choiceBoxType = choiceBoxType;
        setChoiceBoxChoices();
    }
    
    /**
     * @return The name of the choice box
     */
    public String toString() {
        return string;
    }
    
    /**
     * @return The number of intervals that the choice box will have
     */
    public Double getInterval(){
        return interval;
    }
    
    /**
     * @return The choice box object
     */
    public ChoiceBox<String> getChoiceBoxType(){
        return choiceBoxType;
    }
    
    /**
     * Creates entries/choices within the choice box
     */
    private void setChoiceBoxChoices() {
        for (int k = 0; k <= choiceBoxIntervals - 1; k++) {
            String value = Double.toString(k * interval);
            choiceBoxType.getItems().add(value);
        }
    }
}
