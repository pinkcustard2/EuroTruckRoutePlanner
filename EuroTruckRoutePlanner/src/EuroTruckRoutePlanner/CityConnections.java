package EuroTruckRoutePlanner;

public class CityConnections extends DatabaseObject
{
    // wrapper class for the CityConnections table in the database
    private int cityConnectionID;
    private int startCity;
    private int endCity;

    public CityConnections(int cityConnectionID, int startCity, int endCity)
    {
        this.cityConnectionID = cityConnectionID;
        this.startCity = startCity;
        this.endCity = endCity;

        setTableName("CityConnections"); // set the table name for this table in the database

        String[] tableAttributes = new String[3];
        tableAttributes[0] = "CityConnectionID";
        tableAttributes[1] = "StartCity";
        tableAttributes[2] = "EndCity";

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

    public int getStartCity()
    {
        return startCity;
    }

    public void setStartCity(int startCity)
    {
        this.startCity = startCity;
    }

    public int getEndCity()
    {
        return endCity;
    }

    public void setEndCity(int endCity)
    {
        this.endCity = endCity;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (cityConnectionID, startCity and endCity) for SQL statements
        Object[] attributeValues = new Object[3];
        attributeValues[0] = getCityConnectionID();
        attributeValues[1] = getStartCity();
        attributeValues[2] = getEndCity();

        return attributeValues;
    }
}
