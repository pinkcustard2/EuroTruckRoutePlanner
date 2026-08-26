package EuroTruckRoutePlanner;

public class Road extends DatabaseObject
{
    // wrapper class for the Road table in the database
    private int roadID;
    private int roadLength;
    private int speedLimit;
    private String roadName;

    public Road(int roadID, int roadLength, int speedLimit, String roadName)
    {
        this.roadID = roadID;
        this.roadLength = roadLength;
        this.speedLimit = speedLimit;
        this.roadName = roadName;

        setTableName("Road"); // set the table name for this table in the database

        String[] tableAttributes = new String[4];
        tableAttributes[0] = "RoadID";
        tableAttributes[1] = "RoadLength";
        tableAttributes[2] = "SpeedLimit";
        tableAttributes[3] = "RoadName";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public int getRoadID()
    {
        return roadID;
    }

    public void setRoadID(int roadID)
    {
        this.roadID = roadID;
    }

    public int getRoadLength()
    {
        return roadLength;
    }

    public void setRoadLength(int roadLength)
    {
        this.roadLength = roadLength;
    }

    public int getSpeedLimit()
    {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit)
    {
        this.speedLimit = speedLimit;
    }

    public String getRoadName()
    {
        return roadName;
    }

    public void setRoadName(String roadName)
    {
        this.roadName = roadName;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (roadID, roadLength, speedLimit and roadName) for SQL statements
        Object[] attributeValues = new Object[4];
        attributeValues[0] = getRoadID();
        attributeValues[1] = getRoadLength();
        attributeValues[2] = getSpeedLimit();
        attributeValues[3] = getRoadName();

        return attributeValues;
    }
}
