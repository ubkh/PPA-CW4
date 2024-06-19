import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * This class tests the functionality of StatisticsHandler objects.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class StatisticsHandlerTest
{
    private Model model = new Model();
    private StatisticsHandler statisticsHandler = model.getStatisticsHandler();
    private PriceRangeHandler priceRangeHandler = model.getPriceRangeHandler();
    private static final int TO_PRICE = 300;
    private static final int FROM_PRICE = 0;
    
    /** Default constructor for test class StatisticsHandlerTest
     */
    public StatisticsHandlerTest()
    {       
        priceRangeHandler.setToPrice(TO_PRICE);
        priceRangeHandler.setFromPrice(FROM_PRICE);
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
    }

    @Test
    public void testGetStatisticValues()
    {
        String avgReviewsPerProperty = StatisticType.AVERAGE_NUMBER_OF_REVIEWS_PER_PROPERTY.toString();
        assertEquals("Average number of reviews per property", avgReviewsPerProperty);
    }
    
    @Test
    public void testFiltersInit(){
        HashMap<String, Double> filters = statisticsHandler.getFilters();
        for (Double filterValue : filters.values()){
            assertEquals(0.0, filterValue);
        }
    }
    
    @Test
    public void testAverageNumberOfReviewsPerProperty()
    {
        assertEquals("18.0", statisticsHandler.getAverageNumberOfReviewsPerProperty());
    }
    
    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testNumberOfAvailableProperties()
    {
        assertEquals("36718", statisticsHandler.getNumberOfAvailableProperties());
    }
    
    @Test
    public void testNumberOfEntireHomeAndApartments(){
        assertEquals("19057", statisticsHandler.getNumberOfEntireHomeAndApartments());
    }
    
    @Test
    public void testMostExpensiveBorough(){
        assertEquals("Westminster", statisticsHandler.getMostExpensiveBorough());
    }
    
    @Test
    public void testHostWithMostIncome(){
        assertEquals("Nathan", statisticsHandler.getHostWithMostIncome());
    }
    
    @Test
    public void testHostInMostBoroughs(){
        assertEquals("Kremena And Alex", statisticsHandler.getHostInMostBoroughs());
    }
    
    @Test
    public void testHostWithMostPrivRoomsAvailable(){
        assertEquals("Emran", statisticsHandler.getHostWithMostPrivRoomsAvailable());
    }
    
    @Test
    public void testNegativeWithDiffPriceRange(){
        priceRangeHandler.setToPrice(150);
        priceRangeHandler.setFromPrice(100);
        
        String hostWithMostIncome = statisticsHandler.getHostWithMostIncome();
        //It should actually be Nathan
        assertFalse(hostWithMostIncome.equals("Katarina"));
    }
}


