package EuroTruckRoutePlanner;

public class RoadConnections extends DatabaseObject
{
    // wrapper class for the RoadConnections table in the database
    private int cityConnectionID;
    private int roadID;
    private int distanceDrivenOnRoad;

    public RoadConnections(int cityConnectionID, int roadID, int distanceDrivenOnRoad)
    {
        this.cityConnectionID = cityConnectionID;
        this.roadID = roadID;
        this.distanceDrivenOnRoad = distanceDrivenOnRoad;

        setTableName("RoadConnections"); // set the table name for this table in the database

        String[] tableAttributes = new String[3];
        tableAttributes[0] = "CityConnectionID";
        tableAttributes[1] = "RoadID";
        tableAttributes[2] = "DistanceDrivenOnRoad";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public int getCityConnectionID()
    {
        return cityConnectionID;
    }

    public void setCityConnectionID(int cityConnectionID)
    {
        this.cityConnectionID = cityConnectionID;
    }

    public int getRoadID()
    {
        return roadID;
    }

    public void setRoadID(int roadID)
    {
        this.roadID = roadID;
    }

    public int getDistanceDrivenOnRoad()
    {
        return distanceDrivenOnRoad;
    }

    public void setDistanceDrivenOnRoad(int distanceDrivenOnRoad)
    {
        this.distanceDrivenOnRoad = distanceDrivenOnRoad;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (cityConnectionID, roadID and distanceDrivenOnRoad) for SQL statements
        Object[] attributeValues = new Object[3];
        attributeValues[0] = getCityConnectionID();
        attributeValues[1] = getRoadID();
        attributeValues[2] = getDistanceDrivenOnRoad();

        return attributeValues;
    }
}
