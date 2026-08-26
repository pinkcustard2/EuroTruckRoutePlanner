package EuroTruckRoutePlanner;

public class Rank extends DatabaseObject
{
    // wrapper class for the Rank table in the database
    private int rankID;
    private int revenueRequirement;
    private int distanceRequirement;
    private String rankTitle;

    public Rank(int rankID, int revenueRequirement, int distanceRequirement, String rankTitle)
    {
        this.rankID = rankID;
        this.revenueRequirement = revenueRequirement;
        this.distanceRequirement = distanceRequirement;
        this.rankTitle = rankTitle;

        setTableName("Rank"); // set the table name for this table in the database

        String[] tableAttributes = new String[4];
        tableAttributes[0] = "RankID";
        tableAttributes[1] = "RevenueRequirement";
        tableAttributes[2] = "DistanceRequirement";
        tableAttributes[3] = "RankTitle";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public int getRankID()
    {
        return rankID;
    }

    public void setRankID(int rankID)
    {
        this.rankID = rankID;
    }

    public int getRevenueRequirement()
    {
        return revenueRequirement;
    }

    public void setRevenueRequirement(int revenueRequirement)
    {
        this.revenueRequirement = revenueRequirement;
    }

    public int getDistanceRequirement()
    {
        return distanceRequirement;
    }

    public void setDistanceRequirement(int distanceRequirement)
    {
        this.distanceRequirement = distanceRequirement;
    }

    public String getRankTitle()
    {
        return rankTitle;
    }

    public void setRankTitle(String rankTitle)
    {
        this.rankTitle = rankTitle;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (rankID, revenueRequirement, distanceRequirement and rankTitle) for SQL statements
        Object[] attributeValues = new Object[4];
        attributeValues[0] = getRankID();
        attributeValues[1] = getRevenueRequirement();
        attributeValues[2] = getDistanceRequirement();
        attributeValues[3] = getRankTitle();

        return attributeValues;
    }
}
