
/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A ComparisonListing represents a single listing of a statistic comparison
 * between two boroughs.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public class ComparisonListing {

    // define fields
    private final ComparisonType comparisonType; // type of statistic we are comparing
    private final String value1; // value from the first borough
    private final String value2; // value from the second borough

    /**
     * Constructor for a ComparisonListing.
     *
     * @param comparisonType A given type of statistic to compare.
     * @param value1 A given value of this data for the first borough.
     * @param value2 A given value of this data for the second borough.
     */
    public ComparisonListing(ComparisonType comparisonType, String value1, String value2) {
        this.comparisonType = comparisonType;
        this.value1 = value1;
        this.value2 = value2;
    }

    /**
     * Accessor method returning a string of the comparison statistic.
     *
     * @return String of the comparison statistic.
     */
    public String getStat() {
        return this.comparisonType.toString();
    }

    /**
     * Accessor method returning the statistic value for the first borough,
     * as a string.
     *
     * @return String of statistic value for the first borough.
     */
    public String getValue1() {
        return this.value1;
    }

    /**
     * Accessor method returning the statistic value for the second borough,
     * as a string.
     *
     * @return String of statistic value for the second borough.
     */
    public String getValue2() {
        return this.value2;
    }
}
