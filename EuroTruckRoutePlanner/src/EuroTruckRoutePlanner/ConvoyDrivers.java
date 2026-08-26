package EuroTruckRoutePlanner;

public class ConvoyDrivers extends DatabaseObject
{
    // wrapper class for the ConvoyDrivers table in the database
    private int userID;
    private int convoyID;

    public ConvoyDrivers(int userID, int convoyID)
    {
        this.userID = userID;
        this.convoyID = convoyID;

        setTableName("ConvoyDrivers"); // set the table name for this table in the database

        String[] tableAttributes = new String[2];
        tableAttributes[0] = "UserID";
        tableAttributes[1] = "ConvoyID";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public int getUserID()
    {
        return userID;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    public int getConvoyID()
    {
        return convoyID;
    }

    public void setConvoyID(int convoyID)
    {
        this.convoyID = convoyID;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (userId and convoyID) for SQL statements
        Object[] attributeValues = new Object[2];
        attributeValues[0] = getUserID();
        attributeValues[1] = getConvoyID();

        return attributeValues;
    }
}
