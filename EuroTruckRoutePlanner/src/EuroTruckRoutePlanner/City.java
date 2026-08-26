package EuroTruckRoutePlanner;

import java.util.ArrayList;

public class City extends DatabaseObject
{
    // wrapper class for the City table in the database
    private int cityID;
    private int countryID;
    private String cityName;

    public City(int cityID, int countryID, String cityName)
    {
        this.cityID = cityID;
        this.countryID = countryID;
        this.cityName = cityName;

        setTableName("City"); // set the table name for this table in the database

        String[] tableAttributes = new String[3];
        tableAttributes[0] = "CityID";
        tableAttributes[1] = "CountryID";
        tableAttributes[2] = "CityName";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public void setCityID(int cityID)
    {
        this.cityID = cityID;
    }

    public int getCityID()
    {
        return cityID;
    }

    public int getCountryID()
    {
        return countryID;
    }

    public void setCountryID(int countryID)
    {
        this.countryID = countryID;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (cityID, countryID and cityName) for SQL statements
        Object[] attributeValues = new Object[3];
        attributeValues[0] = getCityID();
        attributeValues[1] = getCountryID();
        attributeValues[2] = getCityName();

        return attributeValues;
    }
}
