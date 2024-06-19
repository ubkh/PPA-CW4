
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.HashSet;
import java.util.*;
import java.lang.Double;

import javafx.scene.control.ChoiceBox;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A StatisticsHandler contains functional logic that acts as a helper class, which is called
 * from StatisticsPanelController objects. It deals with returning the relevant statistics
 * in String format, according to the listings available after being filtered in several ways.
 * 
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class StatisticsHandler
{
    private Model model;
    
    private HashMap<String, Double> filters = new HashMap<>();
    
    /**
     * The constructor of class StatisticsHandler which initalises the default filter settings
     * @param model The model object of the application
     */
    public StatisticsHandler(Model model)
    {
       this.model = model;
       initFilters(new String[] {"minimumNights", "minimumReviews"});
    }
    
    /**
     * Creates entries within the filters hash map and defaults its value to 0.0
     * @param keys The keys that will be stored within filters
     */
    private void initFilters(String[] keys) {
        for (String key : keys) {
            filters.put(key, 0.0);
        }
    }
    
    /**
     * Sets the value of an entry within the filters hash map according to the value passed
     * into the method
     * @param key The key of the entry to be updated
     * @param value The value of what the entry will be updated to
     */
    public void setFilter(String key, double value) {
        filters.replace(key, value);
    }

    /**
     * @return Hashmap associating filter name with filter value.
     */
    public HashMap<String, Double> getFilters() {
        return filters;
    }

    /**
     * Gets an array list of listings which fit the filters criteria and is in price range
     * @return An array list of listings which fit the filters criteria and is in price range
     */
    private ArrayList<AirbnbListing> getListings() {
        //ArrayList<AirbnbListing> listings = model.getPriceRangeHandler().getListingsInPriceRange();
        ArrayList<AirbnbListing> listings = new ArrayList<>();
        
        for (AirbnbListing listing : model.getPriceRangeHandler().getListingsInPriceRange()) {
            if (listing.getMinimumNights() <= filters.get("minimumNights").intValue()) {
                continue;
            }
            
            if (listing.getNumberOfReviews() <= filters.get("minimumReviews")) {
                continue;
            }
            
            listings.add(listing);
        }
        
        return listings;
    }
    
    /**
     * Takes in a statistic value and returns the value from the call of its associated method
     * @return String The string value of the statistic value returned
     */
    public String getStatisticValue(StatisticType statisticType) {
        switch (statisticType){
            case AVERAGE_NUMBER_OF_REVIEWS_PER_PROPERTY:
                return getAverageNumberOfReviewsPerProperty();
            case NUMBER_OF_AVAILABLE_PROPERTIES:
                return getNumberOfAvailableProperties();
            case NUMBER_OF_ENTIRE_HOME_AND_APARTMENTS:
                return getNumberOfEntireHomeAndApartments();
            case MOST_EXPENSIVE_BOROUGH:
                return getMostExpensiveBorough();
            case HOST_WITH_MOST_INCOME:
                return getHostWithMostIncome();
            case HOST_IN_MOST_BOROUGHS:
                return getHostInMostBoroughs();
            case HOST_WITH_MOST_PRIV_AVAILABLE:
                return getHostWithMostPrivRoomsAvailable();
            default:
                return null;
        }
    }
    
    /**
     * Gets the average number of reviews per property
     * @return The string value of the average number of reviews per property
     */
    public String getAverageNumberOfReviewsPerProperty() {
        int total = 0;
        
        for (AirbnbListing listing : getListings()) {
            total += listing.getNumberOfReviews();
        }
        double average = total / getListings().size();
        
        return Double.toString(average);
    }
     
    /**
     * Counts and returns the total number of available properties
     * @return The number of available properties
     */
    public String getNumberOfAvailableProperties() {
        int count = getListings().size();
        
        return Integer.toString(count);
    }
    
    /**
     * Counts and returns the total number of entire home/apt properties
     * @return The number of entire home and apartments
     */
    public String getNumberOfEntireHomeAndApartments() {
        int count = 0;
        
        for (AirbnbListing listing : getListings()) {
            if (listing.getRoom_type().equals("Entire home/apt")) {
                count += 1;
            }
        }
        
        return Integer.toString(count);
    }
    
    /**
     * Gets most expensive borough by totalling the prices of all properties within each borough
     * @return The most expensive borough
     */
    public String getMostExpensiveBorough() {
        HashMap<String, Integer> boroughPrices = boroughPrices = getBoroughPrices();

        //less operations with lambda 
        int mostExpensiveBoroughPrice = Collections.max(boroughPrices.values());
        String mostExpensiveBorough = "";
        
        for (Map.Entry<String, Integer> entry : boroughPrices.entrySet()) {
            if (entry.getValue() == mostExpensiveBoroughPrice) {
                mostExpensiveBorough = entry.getKey();
            }
        }
        
        return mostExpensiveBorough;
    }
    
    /**
     * Gets the host with most income by calculating all hosts' incomes and choosing the largest
     * @return The name of the host with the most income
     */
    public String getHostWithMostIncome() {
        HashMap<String, Integer> hostAnnualIncomes = getHostAnnualIncomes();

        //less operations with lambda 
        int hostWithMostAnnualIncomeValue = Collections.max(hostAnnualIncomes.values());
        String hostWithMostAnnualIncome = "";
        
        for (Map.Entry<String, Integer> entry : hostAnnualIncomes.entrySet()) {
            if (entry.getValue() == hostWithMostAnnualIncomeValue) {
                hostWithMostAnnualIncome = entry.getKey();
            }
        }

        String hostName = getHostNameFromId(hostWithMostAnnualIncome);
        
        return hostName;
    }
    
    /**
     * Gets the host that owns property in the most number of boroughs 
     * @return The host in most boroughs
     */
    public String getHostInMostBoroughs() {
        //The number of boroughs each host owns a property in.
        HashMap<String, HashSet<String>> hostsBoroughs = getHostsBoroughs();
        String hostInMostBoroughs = "";
        int hostInMostBoroughsValue = 0;
        
        
        for (Map.Entry<String, HashSet<String>> entry : hostsBoroughs.entrySet()){
            
            int numberOfBoroughs = entry.getValue().size();
            
            if (numberOfBoroughs > hostInMostBoroughsValue) {
                hostInMostBoroughsValue = numberOfBoroughs;
                hostInMostBoroughs = entry.getKey();
            }
        }

        String hostName = getHostNameFromId(hostInMostBoroughs);
        
        return hostName;
    }
    
    /**
     * Gets a hash map that associates each host with a set of boroughs they own property in
     * @return A hash map that associates each host with a set of boroughs they own property in
     */
    private HashMap<String, HashSet<String>> getHostsBoroughs() {
        HashMap<String, HashSet<String>> hostsBoroughs = 
                    new HashMap<String, HashSet<String>>();
        
        for (AirbnbListing listing : getListings()) {
            String hostId = listing.getHost_id();
            String borough = listing.getNeighbourhood();
            if (hostsBoroughs.containsKey(hostId)){
                hostsBoroughs.get(hostId).add(borough);
            }
            else{
                HashSet<String> boroughs = new HashSet<>();
                boroughs.add(borough);
                hostsBoroughs.put(hostId, boroughs);
            }
        } 
        
        return hostsBoroughs;
    }
    
    /**
     * Gets the host who has the highest total availability of properties which are of room type private rooms
     * @return The host who has the highest total availability of properties which are of room type private rooms
     */
    public String getHostWithMostPrivRoomsAvailable() {
        HashMap<String, Integer> hostsPrivRoomNights = getHostsPrivRoomNights();
        
        int hostWithMostPrivRoomNightsValue = Collections.max(hostsPrivRoomNights.values());
        String hostWithMostPrivRoomNights = "";
        
        for (Map.Entry<String, Integer> entry : hostsPrivRoomNights.entrySet()) {
            if (entry.getValue() == hostWithMostPrivRoomNightsValue) {
                hostWithMostPrivRoomNights = entry.getKey();
            }
        }

        String hostName = getHostNameFromId(hostWithMostPrivRoomNights);
        
        return hostName;
    }
    
    /**
     * Gets a hash map that associates host with their number of private room nights
     * @return A hash map that associates host with their number of private room nights
     */
    private HashMap<String, Integer> getHostsPrivRoomNights() {
        HashMap<String, Integer> hostsPrivRoomNights = new HashMap<String, Integer>();
        
        for (AirbnbListing listing : getListings()) {
            if (listing.getRoom_type().equals("Private room")){
                String hostId = listing.getHost_id();
                int roomAvailability = listing.getAvailability365();
                Integer hostsNightCount = hostsPrivRoomNights.get(hostId);
                
                if (hostsNightCount == null){
                    hostsPrivRoomNights.put(hostId, roomAvailability);
                }
                else{
                    hostsPrivRoomNights.replace(hostId, hostsNightCount + roomAvailability);    
                }
                
            }   
        } 
        
        return hostsPrivRoomNights;
    }
    
    /**
     * Gets an array where each at each index, the number of a particular
     * room type is stored (0 for private rooms, 1 for shared rooms, 2 for entire home/apartments)
     * @return 
     */
    public int[] getRoomTypeDistribution() {
        int[] roomTypeDistribution = new int[3];
        
        for (AirbnbListing listing : getListings()) {
            String roomType = listing.getRoom_type();
            if (roomType.equals("Private room")) {
                roomTypeDistribution[0] += 1;
            } else if (roomType.equals("Shared room")) {
                roomTypeDistribution[1] += 1;
            } else if (roomType.equals("Entire home/apt")) {
                roomTypeDistribution[2] += 1;
            }
        }
        
        return roomTypeDistribution;
    }
    
    /**
     * Gets a hash map that associates hosts with their annual incomes where a host's annual income
     * is the total price of all their properties multiplied by each of their 365 availability 
     * @return A hash map that associates hosts with their annual incomes
     */
    private HashMap<String, Integer> getHostAnnualIncomes() {
        HashMap<String, Integer> hostAnnualIncomes = new HashMap<String, Integer>();
        
         for (AirbnbListing listing : getListings()) {
            String hostId = listing.getHost_id();
            int price = listing.getPrice() * listing.getAvailability365();
            Integer hostAnnualIncome = hostAnnualIncomes.get(hostId);
            if (hostAnnualIncome == null) {
                hostAnnualIncomes.put(hostId, price);
            } else {
                hostAnnualIncomes.replace(hostId, hostAnnualIncome + price);
            }
        }       
        
        return hostAnnualIncomes;
    }
    
    /**
     * Gets a hash map that associates boroughs with the total price of all the properties within them
     * @return A hash map that associates boroughs with the total price of all the properties within them
     */
    private HashMap<String, Integer> getBoroughPrices() {
        HashMap<String, Integer> boroughPrices = new HashMap<String, Integer>();
        
         for (AirbnbListing listing : getListings()) {
            String boroughName = listing.getNeighbourhood();
            int price = listing.getPrice() * listing.getMinimumNights();
            Integer boroughPrice = boroughPrices.get(boroughName);
            if (boroughPrice == null) {
                boroughPrices.put(boroughName, price);
            } else {
                boroughPrices.replace(boroughName, boroughPrice + price);
            }
        }       
        
        return boroughPrices;
    }
    
    /**
     * Gets the host name from their id
     * @return The host name from their id
     */
    private String getHostNameFromId(String id) {
        String hostNameFromId = "";
        
        for (AirbnbListing listing : getListings()) {
            String hostId = listing.getHost_id();
            if (hostId.equals(id)) {
                hostNameFromId = listing.getHost_name();  
            }
        }
        
        return hostNameFromId;
    }
}