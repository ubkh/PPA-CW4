import java.util.ArrayList;

import java.util.HashSet;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A ComparisonHandler handles functional logic comparisons between two boroughs,
 * and is a helper class for ComparisonController.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public class ComparisonHandler
{
    private Model model;
    private PriceRangeHandler priceRangeHandler;
    
    private ArrayList<String> favouriteBoroughs = new ArrayList<>();
    
    private String boroughOne;
    private String boroughTwo;
    
    private ArrayList<ComparisonListing> comparisonListings;
    
    /**
     * The constructor of the ComparisonHandler class
     * @param model The model object of the application
     */
    public ComparisonHandler(Model model)
    {
        this.model = model;
        priceRangeHandler = model.getPriceRangeHandler();
        comparisonListings = new ArrayList<>();
    }
    
    /**
     * Sets the comparison values for each comparison type enum
     */
    private void setComparisonValues() {
        comparisonListings.add(
            new ComparisonListing(
                ComparisonType.NUMBER_OF_PROPERTIES, 
                getNumberOfProperties(boroughOne), 
                getNumberOfProperties(boroughTwo)
            )
        );
        
        comparisonListings.add(
            new ComparisonListing(
                ComparisonType.AVERAGE_PRICE, 
                getAveragePrice(boroughOne), 
                getAveragePrice(boroughTwo)
            )
        );
        
        comparisonListings.add(
            new ComparisonListing(
                ComparisonType.AVERAGE_REVIEWS, 
                getAverageReviews(boroughOne), 
                getAverageReviews(boroughTwo)
            )
        );
        
        comparisonListings.add(
            new ComparisonListing(
                ComparisonType.ANNUAL_INCOME, 
                getAnnualIncome(boroughOne), 
                getAnnualIncome(boroughTwo)
            )
        );
        
        comparisonListings.add(
            new ComparisonListing(
                ComparisonType.NUMBER_OF_HOSTS, 
                getNumberOfHosts(boroughOne), 
                getNumberOfHosts(boroughTwo)
            )
        );
        
        comparisonListings.add(
            new ComparisonListing(
                ComparisonType.NUMBER_OF_PRIVATE_ROOMS, 
                getNumberOfPrivateRooms(boroughOne), 
                getNumberOfPrivateRooms(boroughTwo)
            )
        );
    }
    
    /**
     * @return A list of comparison listings
     */
    public ArrayList<ComparisonListing> getComparisonListings() {
        return comparisonListings;
    }
    
    /**
     * @return A list of favourited boroughs
     */
    public ArrayList<String> getFavouriteBoroughs() {
        return favouriteBoroughs;
    }
    
    /**
     * Adds or removes the passed borough from the list of favourited boroughs
     * @param borough The borough which will be favourited/unfavourited
     */
    public void toggleFavouriteBorough(String borough) {
        if (favouriteBoroughs.contains(borough)) {
            favouriteBoroughs.remove(borough);
        } else {
            favouriteBoroughs.add(borough);
        }
    }
    
    /**
     * Clears the listings and sets the comparison values for a new comparison
     * @param boroughOne The first borough to be compared
     * @param boroughTwo The second borough to be compared
     */
    public void setBoroughs(String boroughOne, String boroughTwo) {
        this.boroughOne = boroughOne;
        this.boroughTwo = boroughTwo;

        // clear ArrayList now for new comparison
        comparisonListings.clear();
        setComparisonValues();
    }
    
    /**
     * @return The total number of properties within the borough
     * @param borough The borough which will be queried
     * @return The number of properties within the borough
     */
    public String getNumberOfProperties(String borough) {
        int count = priceRangeHandler.getListingsInPriceRangeFromBorough(borough).size();
        
        return Integer.toString(count);
    }
    
    /**
     * Gets the average price of the borough by finding the total price across all properties
     * and then dividing it by the number of properties within the borough
     * @param borough The borough to be queried
     * @return The average price of each property within the borough
     */
    public String getAveragePrice(String borough) {
        double totalPrice = 0.0;
        ArrayList<AirbnbListing> listings = priceRangeHandler.getListingsInPriceRangeFromBorough(borough);
        
        for (AirbnbListing listing : listings) {
            totalPrice += listing.getPrice();
        }
        
        double average = totalPrice / listings.size();
        
        average = ((double)Math.round(average * 100.0)) / 100.0;
        
        return "£" + Double.toString(average);
        
    }
    
    /**
     * Gets the average number of reviews of the borough by finding the total number of reviews
     * and then dividng it by the number of properties within the borough
     * @param borough The borough to be queried
     * @return The average number of reviews within the borough
     */
    public String getAverageReviews(String borough) {
        double totalReviews = 0;
        ArrayList<AirbnbListing> listings = priceRangeHandler.getListingsInPriceRangeFromBorough(borough);
        
        for (AirbnbListing listing : listings) {
            totalReviews += listing.getNumberOfReviews();
        }
        
        double average = totalReviews / listings.size();
        
        average = ((double)Math.round(average * 100.0)) / 100.0;
        
        return Double.toString(average);
        
    }
    
    /**
     * Gets the total annual income of the borough from all its properties by totalling the price multiplied by 
     * its availiblity 365 for each listing
     * @param borough The borough to be queried
     * @return The total annual income of the borough
     */
    public String getAnnualIncome(String borough) {
        int totalIncome = 0;
        
        for (AirbnbListing listing : priceRangeHandler.getListingsInPriceRangeFromBorough(borough)) {
            totalIncome += listing.getPrice() * listing.getAvailability365();
        }
        
        return "£" + Integer.toString(totalIncome);
    }
    
    /**
     * Gets the number of unique hosts participating in the borough
     * @param borough The borough to be queried
     * @return The number of hosts participating in the borough
     */
    public String getNumberOfHosts(String borough) {
        HashSet<String> hosts = new HashSet<>();
        
        for (AirbnbListing listing : priceRangeHandler.getListingsInPriceRangeFromBorough(borough)) {
            hosts.add(listing.getHost_id());
        }
        
        return Integer.toString(hosts.size());
    }
    
    /**
     * Gets the number of private rooms in the borough
     * @param borough The borough to be queried
     * @return The number of private rooms in the borough
     */
    public String getNumberOfPrivateRooms(String borough) {
        int count = 0;
        
        for (AirbnbListing listing : priceRangeHandler.getListingsInPriceRangeFromBorough(borough)) {
            if (listing.getRoom_type().equals("Private room")) count += 1;
        }
        
        return Integer.toString(count);
    }
}
