package EuroTruckRoutePlanner;

public class DatabaseObject
{
    // subroutines and variables present in all wrapper classes
    private String tableName; // name of table
    private String[] attributes; // array of all the attributes

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String[] getAttributes()
    {
        return attributes;
    }

    public void setAttributes(String[] attributes)
    {
        this.attributes = attributes;
    }

    public Object[] getAttributeValues() // returns all attribute values
    {
        return null;
    }
}
