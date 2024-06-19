
/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A ComparisonType is an enumeration of different statistics that
 * we compare in the ComparisonController.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public enum ComparisonType
{
    NUMBER_OF_PROPERTIES("Number of properties"), 
    AVERAGE_PRICE("Average price"),
    AVERAGE_REVIEWS("Average reviews"),
    ANNUAL_INCOME("Annual income"),
    NUMBER_OF_HOSTS("Number of hosts"),
    NUMBER_OF_PRIVATE_ROOMS("Number of private rooms");
    
    /**
     * Constructor for enum ComparisonType
     * @param string The string equivalent of the ComparisonType
     */
    ComparisonType(String string) {
        this.string = string;
    }
    
    private String string;
    
    /**
     * @return The string equivalent of the ComparisonType
     */
    public String toString() {
        return string;
    }
}
