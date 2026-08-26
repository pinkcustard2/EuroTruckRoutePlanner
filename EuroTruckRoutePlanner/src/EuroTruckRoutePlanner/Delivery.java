package EuroTruckRoutePlanner;

import java.time.LocalDateTime;

public class Delivery extends DatabaseObject
{
    // wrapper class for the Delivery table in the database
    private int deliveryID;
    private int userID;
    private int startCity;
    private int endCity;
    private float cargoDamage;
    private int estimatedRevenue;
    private int actualRevenue;
    private int cargoWeight;
    private int estimatedDistance;
    private int actualDistance;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Delivery(int deliveryID, int userID, int startCity, int endCity, float cargoDamage, int estimatedRevenue, int actualRevenue, int cargoWeight, int estimatedDistance, int actualDistance, LocalDateTime startDate, LocalDateTime endDate)
    {
        this.deliveryID = deliveryID;
        this.userID = userID;
        this.startCity = startCity;
        this.endCity = endCity;
        this.cargoDamage = cargoDamage;
        this.estimatedRevenue = estimatedRevenue;
        this.actualRevenue = actualRevenue;
        this.cargoWeight = cargoWeight;
        this.estimatedDistance = estimatedDistance;
        this.actualDistance = actualDistance;
        this.startDate = startDate;
        this.endDate = endDate;

        setTableName("Delivery"); // set the table name for this table in the database

        String[] tableAttributes = new String[12];
        tableAttributes[0] = "DeliveryID";
        tableAttributes[1] = "UserID";
        tableAttributes[2] = "StartCity";
        tableAttributes[3] = "EndCity";
        tableAttributes[4] = "CargoDamage";
        tableAttributes[5] = "EstimatedRevenue";
        tableAttributes[6] = "ActualRevenue";
        tableAttributes[7] = "CargoWeight";
        tableAttributes[8] = "EstimatedDistance";
        tableAttributes[9] = "ActualDistance";
        tableAttributes[10] = "StartDate";
        tableAttributes[11] = "EndDate";

        setAttributes(tableAttributes); // set the table attributes for this table in the database
    }

    public int getDeliveryID()
    {
        return deliveryID;
    }

    public void setDeliveryID(int deliveryID)
    {
        this.deliveryID = deliveryID;
    }

    public int getUserID()
    {
        return userID;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
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

    public float getCargoDamage()
    {
        return cargoDamage;
    }

    public void setCargoDamage(float cargoDamage)
    {
        this.cargoDamage = cargoDamage;
    }

    public int getEstimatedRevenue()
    {
        return estimatedRevenue;
    }

    public void setEstimatedRevenue(int estimatedRevenue)
    {
        this.estimatedRevenue = estimatedRevenue;
    }

    public int getActualRevenue()
    {
        return actualRevenue;
    }

    public void setActualRevenue(int actualRevenue)
    {
        this.actualRevenue = actualRevenue;
    }

    public int getCargoWeight()
    {
        return cargoWeight;
    }

    public void setCargoWeight(int cargoWeight)
    {
        this.cargoWeight = cargoWeight;
    }

    public int getEstimatedDistance()
    {
        return estimatedDistance;
    }

    public void setEstimatedDistance(int estimatedDistance)
    {
        this.estimatedDistance = estimatedDistance;
    }

    public int getActualDistance()
    {
        return actualDistance;
    }

    public void setActualDistance(int actualDistance)
    {
        this.actualDistance = actualDistance;
    }

    public LocalDateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate)
    {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate)
    {
        this.endDate = endDate;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (deliveryID, userID, startCity, endCity, cargoDamage, estimatedRevenue, actualRevenue,
        // cargoWeight, estimatedDistance, actualDistance, startDate, endDate) for SQL statements
        Object[] attributeValues = new Object[12];
        attributeValues[0] = getDeliveryID();
        attributeValues[1] = getUserID();
        attributeValues[2] = getStartCity();
        attributeValues[3] = getEndCity();
        attributeValues[4] = getCargoDamage();
        attributeValues[5] = getEstimatedRevenue();
        attributeValues[6] = getActualRevenue();
        attributeValues[7] = getCargoWeight();
        attributeValues[8] = getEstimatedDistance();
        attributeValues[9] = getActualDistance();
        attributeValues[10] = getStartDate();
        attributeValues[11] = getEndDate();

        return attributeValues;
    }
}
