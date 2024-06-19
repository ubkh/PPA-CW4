
/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * The PanelType class is an enumneration associating each statistic with its 
 * String representation. This allows us to easily load, update, switch between 
 * each respected panel.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public enum PanelType
{
    WELCOME("WelcomePanel", "Welcome"), 
    MAP("MapPanel", "Map"), 
    STATISTICS("StatisticsPanel", "Statistics"),
    COMPARISON("ComparisonPanel", "Comparison");
    
    private String fileName;
    private String string;
    
    /**
     * The constructor of enum PanelType
     * @param fileName The file name of the panel type
     * @param string The string equivalent of the panel type
     */
    PanelType(String fileName, String string) {
        this.fileName = fileName;
        this.string = string;
    }
    
    /**
     * @return The file name of the panel type 
     */
    public String getFileName() {return fileName;}
    
    /**
     * @return The string equivalent of the panel type
     */
    public String toString() {return string;}
    
}
