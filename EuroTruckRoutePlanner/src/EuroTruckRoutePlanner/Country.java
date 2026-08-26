package EuroTruckRoutePlanner;

public class Country extends DatabaseObject
{
    // wrapper class for the Country table in the database
    private int countryID;
    private String countryName;

    public Country(int countryID, String countryName)
    {
        this.countryID = countryID;
        this.countryName = countryName;

        setTableName("Country");  // set the table name for this table in the database

        String[] tableAttributes = new String[2];
        tableAttributes[0] = "CountryID";
        tableAttributes[1] = "CountryName";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public int getCountryID()
    {
        return countryID;
    }

    public void setCountryID(int countryID)
    {
        this.countryID = countryID;
    }

    public String getCountryName()
    {
        return countryName;
    }

    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (countryID and countryName) for SQL statements
        Object[] attributeValues = new Object[2];
        attributeValues[0] = getCountryID();
        attributeValues[1] = getCountryName();

        return attributeValues;
    }
}
