
/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * The StatisticType class is an enumneration associating each statistic with its 
 * String representation. This allows us to effciently update each of the four 
 * statistic boxes, and pass this into the view for each respected statistic.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public enum StatisticType
{
    AVERAGE_NUMBER_OF_REVIEWS_PER_PROPERTY("Average number of reviews per property"), 
    NUMBER_OF_AVAILABLE_PROPERTIES("Number of available properties"),
    NUMBER_OF_ENTIRE_HOME_AND_APARTMENTS("Number of entire home and apartments"),
    MOST_EXPENSIVE_BOROUGH("Most expensive borough"),
    HOST_WITH_MOST_INCOME("Host with most rental income"),
    HOST_IN_MOST_BOROUGHS("Host who owns properties in most boroughs"),
    HOST_WITH_MOST_PRIV_AVAILABLE("Host with most private rooms available per annum");
    
    /**
     * The constructor of enum StatisticType
     * @param string The string equivalent of the statistic type
     */
    StatisticType(String string) {
        this.string = string;
    }
    
    private String string;
    
    /**
     * @return The string equivalent of the statistic type
     */
    public String toString() {
        return string;
    }
}
