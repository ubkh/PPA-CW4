
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A PriceRangeHandler contains functional logic that acts as a helper class, used accross 
 * this application. It ensures that the user initially enters a to and from price, and 
 * allows other classes to retrieve the particular AirbnbListings which are in the valid 
 * price range. Objects from this class also deal with associating price range to boroughs
 * in a relative manner.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public class PriceRangeHandler
{
    private Model model;

    private Integer fromPrice;
    private Integer toPrice;
    
    /**
     * The constructor of the PriceRangeHandler class
     * @param model The model object of the application
     */
    public PriceRangeHandler(Model model)
    {
        this.model = model;
    }
    
    /**
     * Sets the from part of price range
     * @param price The price to set the from price to
     */
    public void setFromPrice(int price) {
        fromPrice = price;  
    }
    
    /**
     * Sets the to part of price range
     * @param price The price to set the to price to
     */
    public void setToPrice(int price) {
        toPrice = price;
    }
    
    /**
     * @return The from price
     */
    public Integer getFromPrice() {
        return fromPrice;
    }
    
    /**
     * @return The to price
     */
    public Integer getToPrice() {
        return toPrice;
    }
    
    /**
     * Checks if both from price and to price have been set and are not null
     * @return If the price range has been set
     */
    public boolean isPriceRangeSet() {
        return (fromPrice != null && toPrice != null);
    }
    
    /**
     * Checks if the listing passed in satisfies the price range that has been set
     * @param listing The listing whose price is to be checked
     * @return If the listing is in the price range
     */
    private boolean isListingInPriceRange(AirbnbListing listing) {
        int price = listing.getPrice();

        if (fromPrice == null || toPrice == null){
            return true;//returns all listings when fromPrice and toPrice are null
        }//This is before the user sets it.

        return price >= fromPrice && price <= toPrice;
    }
    
    /**
     * Groups all the listings that satisfy the price range into an array list and returns it
     * @return An array list of listings which satisfy the price range
     */
    public ArrayList<AirbnbListing> getListingsInPriceRange() {
        // return all listings if no price range set
        if (fromPrice == null || toPrice == null) {
            return model.getListings();
        }

        ArrayList<AirbnbListing> listings = new ArrayList<AirbnbListing>();
        
        for (AirbnbListing listing : model.getListings()) {
            if (isListingInPriceRange(listing)) {
                listings.add(listing);
            }
        }
        
        return listings;
    }

    public ArrayList<AirbnbListing> getListingsInPriceRangeFromBorough(String borough) {
        ArrayList<AirbnbListing> listings = new ArrayList<AirbnbListing>();
        
        for (AirbnbListing listing : getListingsInPriceRange()) {
            if (listing.getNeighbourhood().equals(borough)) listings.add(listing);
        }
        
        return listings;
    }
    
    //Borough with most properties has a value of 1 and borough with least properties have a value of 0 (within the price range)
    /**
     * Returns a hash map of boroughs with its associated price range index, a value from 0 - 1, where 0 represents
     * the cheapest borough and 1 represents the most expensive borough
     * @return A hash map of boroughs with its associated price range index
     */
    public HashMap<String, Double> getBoroughPriceRangeIndices() {
        ArrayList<AirbnbListing> listingsInPriceRange = getListingsInPriceRange();
        HashMap<String, ArrayList<AirbnbListing>> listingsByBoroughs = model.getListingsByBoroughs(listingsInPriceRange);
        
        HashMap<String, Double> indices = new HashMap<String, Double>();
        
        int largestNumber = getLargestNumberOfListingsInBorough(listingsByBoroughs);
        
        for (Map.Entry<String, ArrayList<AirbnbListing>> entry : listingsByBoroughs.entrySet()) {
            String borough = entry.getKey();
            double numberOfListings = entry.getValue().size();   
            double index = numberOfListings / largestNumber;
            indices.put(borough, index);
        }
        
        return indices;
    }

    /**
     * Gets the borough with the most number of listings 
     * @param listingsByBoroughs A hash map holding lists of properties grouped by boroughs
     * @return The borough with the most number of listings
     */
    private int getLargestNumberOfListingsInBorough(HashMap<String, ArrayList<AirbnbListing>> listingsByBoroughs) {
        int largestNumber = 0;
        
        for (Map.Entry<String, ArrayList<AirbnbListing>> entry : listingsByBoroughs.entrySet()) {
            int numberOfListings = entry.getValue().size();
            if (numberOfListings > largestNumber) {
                largestNumber = numberOfListings;
            }
        }
        
        return largestNumber;
    }
}
