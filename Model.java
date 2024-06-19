import java.util.ArrayList;
import java.util.HashMap;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * The Model class stores the state of the application and contains methods 
 * which are called by controller objects to control said state. It houses
 * the price range handler, statistics handler and comparison handler by
 * providing getter methods for other objects to retrieve these objects.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class Model
{
    private ArrayList<AirbnbListing> listings;
    
    private PriceRangeHandler priceRangeHandler;
    private StatisticsHandler statisticsHandler;
    private ComparisonHandler comparisonHandler;

    public Model() {
        AirbnbDataLoader loader = new AirbnbDataLoader();
        listings = loader.load();

        priceRangeHandler = new PriceRangeHandler(this);
        statisticsHandler = new StatisticsHandler(this);
        comparisonHandler = new ComparisonHandler(this);
    }
    
    /**
     * @return An array list of property listings
     */
    public ArrayList<AirbnbListing> getListings() {
        return listings;
    }
    
    /**
     * @return The price range handler object
     */
    public PriceRangeHandler getPriceRangeHandler() {
        return priceRangeHandler;
    }
    
    /**
     * @return The statistics handler object
     */
    public StatisticsHandler getStatisticsHandler() {
        return statisticsHandler;
    }

    public ComparisonHandler getComparisonHandler() {
        return comparisonHandler;
    }

    /**
     * Takes in an array list of property listings and returns a hashmap where each key is a name of a borough
     * and its value associated with it is an array list of property listings that belong to the borough
     * @param listings The listings to be sorted into a hash map
     * @return A hash map of listings grouped by borough
     */
    public HashMap<String, ArrayList<AirbnbListing>> getListingsByBoroughs(ArrayList<AirbnbListing> listings) {
        HashMap<String, ArrayList<AirbnbListing>> listingsByBoroughs = new HashMap<String, ArrayList<AirbnbListing>>();
        for (AirbnbListing listing : listings) {
            String borough = listing.getNeighbourhood();
            ArrayList<AirbnbListing> listingsByBorough = listingsByBoroughs.get(borough);
            if (listingsByBorough != null) {
                listingsByBorough.add(listing);
            } else {
                listingsByBorough = new ArrayList<AirbnbListing>();
                listingsByBorough.add(listing);
                listingsByBoroughs.put(borough, listingsByBorough);
            }
        }
        return listingsByBoroughs;
    }
}
