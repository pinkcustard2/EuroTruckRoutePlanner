package EuroTruckRoutePlanner;

import java.time.LocalDateTime;
import java.util.Date;

public class Convoy extends DatabaseObject
{
    // wrapper class for the Convoy table in the database
    private int convoyID;
    private int startCity;
    private int endCity;
    private String convoyName;
    private LocalDateTime convoyDate;

    public Convoy(int convoyID, int startCity, int endCity, String convoyName, LocalDateTime convoyDate)
    {
        this.convoyID = convoyID;
        this.startCity = startCity;
        this.endCity = endCity;
        this.convoyName = convoyName;
        this.convoyDate = convoyDate;

        setTableName("Convoy"); // set the table name for this table in the database

        String[] tableAttributes = new String[5];
        tableAttributes[0] = "ConvoyID";
        tableAttributes[1] = "StartCity";
        tableAttributes[2] = "EndCity";
        tableAttributes[3] = "ConvoyName";
        tableAttributes[4] = "ConvoyDate";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public int getConvoyID()
    {
        return convoyID;
    }

    public void setConvoyID(int convoyID)
    {
        this.convoyID = convoyID;
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

    public String getConvoyName()
    {
        return convoyName;
    }

    public void setConvoyName(String convoyName)
    {
        this.convoyName = convoyName;
    }

    public LocalDateTime getConvoyDate()
    {
        return convoyDate;
    }

    public void setConvoyDate(LocalDateTime convoyDate)
    {
        this.convoyDate = convoyDate;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (convoyID, startCity, endCity, convoyName, convoyDate) for SQL statements
        Object[] attributeValues = new Object[5];
        attributeValues[0] = getConvoyID();
        attributeValues[1] = getStartCity();
        attributeValues[2] = getEndCity();
        attributeValues[3] = getConvoyName();
        attributeValues[4] = getConvoyDate();

        return attributeValues;
    }
}
