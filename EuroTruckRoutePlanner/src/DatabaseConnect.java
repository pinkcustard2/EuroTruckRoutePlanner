import java.sql.*;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DatabaseConnect
{
    private Connection connection = null;

    public DatabaseConnect()
    {
        try
        {
            Class.forName("org.sqlite.JDBC"); // specify the SQLite Java driver
            connection = DriverManager.getConnection("jdbc:sqlite:EuroTruckRoutePlanner.db"); // specify the database, since relative in the main project folder
            connection.setAutoCommit(false); // controls when data is written (changes not automatically committed)
            System.out.println("Opened database successfully");
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to open database
            System.exit(0);
        }
    }

    public void insertInto(String tableName, String[] attributes, Object[] attributeValues)
    {
        // inserts values into a table
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            StringBuilder insertInto = new StringBuilder("INSERT INTO " + tableName + " ("); // start the insert into statement

            insertInto.append(attributes[0]);
            for(int i = 1; i < attributes.length; i++) // add all attributes to the insert into statement (first line of insert into)
            {
                insertInto.append(", ");
                insertInto.append(attributes[i]);
            }
            insertInto.append(")"); // end the first line of the insert into

            StringBuilder values = new StringBuilder("VALUES ("); // start the 2nd line of the insert into statement (attribute values)
            if(attributeValues[0] instanceof String || attributeValues[0] instanceof LocalDateTime) // check if the value requires quotation marks around
            {
                values.append("'");
                values.append(attributeValues[0]); // add first value to the 2nd line of the insert into statement
                values.append(("'"));
            }
            else
            {
                if((int) attributeValues[0] != -1) // check if value is not equal to -1 (should autoincrement if equal to -1)
                {
                    values.append(attributeValues[0]); // add first value to the 2nd line of the insert into statement
                }
                else
                {
                    values.append("null"); // inserting null allows autoincrement to occur
                }
            }

            for(int i = 1; i < attributeValues.length; i++) // add all attributes to the insert into statement (first line of insert into)
            {
                values.append(",");

                if(attributeValues[i] instanceof String || attributeValues[i] instanceof LocalDateTime) // check if the value requires quotation marks around
                {
                    values.append("'");
                    values.append(attributeValues[i]); // add value to the 2nd line of the insert into statement
                    values.append(("'"));
                }
                else
                {
                    values.append(attributeValues[i]); // add value to the 2nd line of the insert into statement
                }
            }

            values.append(");");


            String sql = insertInto + "\n" + values; // create the full sql statement

            statement.executeUpdate(sql); // execute the sql statement
            statement.close();

            connection.commit(); // commit the changes

            System.out.println("Added successfully");
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to add values to table
        }
    }

    public void deleteFrom(String tableName, String[] identifierAttributes, Object[] attributeValues)
    {
        // delete a record from a table where the record's identifier attribute is equal to the attribute value inputted
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            StringBuilder deleteFrom = new StringBuilder("DELETE FROM "); // first line of delete from statement
            deleteFrom.append(tableName);

            StringBuilder where = new StringBuilder("WHERE "); // 2nd line of delete from statement
            where.append(identifierAttributes[0]);
            where.append(" = ");

            if(attributeValues[0] instanceof String || attributeValues[0] instanceof LocalDateTime) // check if the value requires quotation marks around
            {
                where.append("'");
                where.append(attributeValues[0]); // add value to the 2nd line of the delete from statement
                where.append(("'"));
            }
            else
            {
                where.append(attributeValues[0]); // add value to the 2nd line of the delete from statement
            }

            for(int i = 1; i < identifierAttributes.length; i++) // add any extra identifiers to where clause
            {
                where.append(" AND ");
                where.append(identifierAttributes[i]);
                where.append(" = ");

                if(attributeValues[i] instanceof String || attributeValues[i] instanceof LocalDateTime) // check if the value requires quotation marks around
                {
                    where.append("'");
                    where.append(attributeValues[i]); // add value to the 2nd line of the delete from statement
                    where.append(("'"));
                }
                else
                {
                    where.append(attributeValues[i]); // add value to the 2nd line of the delete from statement
                }
            }

            String sql = deleteFrom + "\n" + where; // create the full sql statement

            statement.executeUpdate(sql); // execute the sql statement
            statement.close();

            connection.commit(); // commit the changes

            System.out.println("Deleted successfully");
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to delete values into table
        }
    }

    public void update(String tableName, String[] attributes, Object[] newAttributeValues, String[] identifierAttributes, Object[] identifierAttributeValues)
    {
        // update the attributes to the new attribute values of a record from a table
        // where the record's identifier attribute is equal to the attribute value inputted
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            StringBuilder update = new StringBuilder("UPDATE "); // 1st line of update statement
            update.append(tableName);

            StringBuilder set = new StringBuilder("SET "); // 2nd line of update statement
            set.append(attributes[0]);
            set.append(" = ");

            if(newAttributeValues[0] instanceof String || newAttributeValues[0] instanceof LocalDateTime) // check if the value requires quotation marks around
            {
                set.append("'");
                set.append(newAttributeValues[0]); // add value to the 2nd line of the update statement
                set.append(("'"));
            }
            else
            {
                set.append(newAttributeValues[0]); // add value to the 2nd line of the update statement
            }

            for(int i = 1; i < attributes.length; i++) // add all the attributes to update
            {
                set.append(", ");

                set.append(attributes[i]);
                set.append(" = ");

                if(newAttributeValues[i] instanceof String || newAttributeValues[i] instanceof LocalDateTime) // check if the value requires quotation marks around
                {
                    set.append("'");
                    set.append(newAttributeValues[i]); // add value to the 2nd line of the update statement
                    set.append(("'"));
                }
                else
                {
                    set.append(newAttributeValues[i]); // add value to the 2nd line of the update statement
                }
            }

            StringBuilder where = new StringBuilder("WHERE "); // last line of update statement
            where.append(identifierAttributes[0]);
            where.append(" = ");

            if(identifierAttributeValues[0] instanceof String || identifierAttributeValues[0] instanceof LocalDateTime) // check if the value requires quotation marks around
            {
                where.append("'");
                where.append(identifierAttributeValues[0]); // add value to the 3rd line of the update statement (where)
                where.append(("'"));
            }
            else
            {
                where.append(identifierAttributeValues[0]); // add value to the 3rd line of the update statement (where)
            }

            for(int i = 1; i < identifierAttributes.length; i++)
            {
                where.append(" AND ");

                where.append(identifierAttributes[i]);
                where.append(" = ");

                if(identifierAttributeValues[i] instanceof String || identifierAttributeValues[i] instanceof LocalDateTime) // check if the value requires quotation marks around
                {
                    where.append("'");
                    where.append(identifierAttributeValues[i]); // add value to the 3rd line of the update statement (where)
                    where.append(("'"));
                }
                else
                {
                    where.append(identifierAttributeValues[i]); // add value to the 3rd line of the update statement (where)
                }
            }

            String sql = update + "\n" + set + "\n" + where; // create the full sql statement

            statement.executeUpdate(sql); // execute the sql statement
            statement.close();

            connection.commit(); // commit the changes

            System.out.println("Updated successfully");
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to update values
        }
    }

    public ArrayList<Object[]> findAttributes(String tableName, String[] attributesToFind, String[] identifierAttributes, Object[] identifierAttributeValues)
    {
        // finds an attribute (attributeToFind) for any table based on a known identifierAttribute (identifierAttributeValue)
        ArrayList<Object[]> attributes = new ArrayList<>();

        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            StringBuilder select = new StringBuilder("SELECT "); // 1st line of select statement
            select.append(attributesToFind[0]);

            for(int i = 1; i < attributesToFind.length; i++) // add the rest of the attributes to select to the select statements
            {
                select.append(", ");
                select.append(attributesToFind[i]);
            }

            StringBuilder from = new StringBuilder("FROM "); // 2nd line of select statement
            from.append(tableName);

            StringBuilder where = new StringBuilder("WHERE "); // 3rd line of select statement
            where.append(tableName);
            where.append(".");
            where.append(identifierAttributes[0]);
            where.append("=");

            if(identifierAttributeValues[0] instanceof String || identifierAttributeValues[0] instanceof LocalDateTime) // check if the value requires quotation marks around
            {
                where.append("'");
                where.append(identifierAttributeValues[0]); // add value to the 3rd line of the select statement (where)
                where.append(("'"));
            }
            else
            {
                where.append(identifierAttributeValues[0]); // add value to the 3rd line of the select statement (where)
            }
            // finds the record where the inputted identifier value is equal to the identifier value in a record

            for(int i = 1; i < identifierAttributes.length; i++) // add the rest of the attributes to the where clause of the select statement
            {
                where.append(" AND ");

                where.append(tableName);
                where.append(".");
                where.append(identifierAttributes[i]);
                where.append(" = ");

                if(identifierAttributeValues[i] instanceof String || identifierAttributeValues[i] instanceof LocalDateTime) // check if the value requires quotation marks around
                {
                    where.append("'");
                    where.append(identifierAttributeValues[i]); // add value to the 3rd line of the select statement (where)
                    where.append(("'"));
                }
                else
                {
                    where.append(identifierAttributeValues[i]); // add value to the 3rd line of the se;ect statement (where)
                }
                // finds the record where the inputted identifier value is equal to the identifier value in a record
            }

            String sql = select + "\n" + from + "\n" + where; // create the full sql statement

            ResultSet resultSet = statement.executeQuery(sql); // execute the query

            while(resultSet.next()) // loop through results and add each row to the array list
            {
                Object[] thisRow = new Object[attributesToFind.length]; // stores the information for this row to be added to the array list

                for(int i = 0; i < thisRow.length; i++) // add all information of this row to the thisRow array
                {
                    thisRow[i] = resultSet.getObject(i + 1);
                }

                attributes.add(thisRow); // add to the array list
            }

            statement.close();

            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to get attribute
        }

        return attributes;
    }

    public Object[] totalDeliveryInformation(int userID)
    {
        // returns the total delivery information of a specific user with userID equal to userID
        // where the item in index 0 of the returned string array equal to the total deliveries
        // index 1 = total distance driven
        // index 2 = total revenue earnt
        // index 3 = total weight transported
        // index 4 = total fines received
        Object[] totalDeliveryInformation = new Object[5];

        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT COUNT(Delivery.UserID) AS [Total Deliveries],\n" +
                    "       SUM(Delivery.ActualDistance) AS [Total Distance Driven],\n" +
                    "       SUM(Delivery.ActualRevenue) AS [Total Revenue Earnt],\n" +
                    "       SUM(Delivery.CargoWeight) AS [Total Weight Transported],\n" +
                    "       SUM(Delivery.EstimatedRevenue - Delivery.ActualRevenue) AS [Total Fines Received]";
            // select part of statement of total delivery information query

            String from = "FROM Delivery,\n" +
                    "       User";
            // from part of statement of total delivery information query

            StringBuilder where = new StringBuilder("WHERE User.UserID = Delivery.UserID AND \n" +
                    "       User.UserID =");
            where.append(userID); // set the userID to the inputted userID
            // where part of statement of total delivery information query

            String sql = select + "\n" + from + "\n" + where;

            for(int i = 0; i < totalDeliveryInformation.length; i++) // gets the information from the query
            {
                totalDeliveryInformation[i] = statement.executeQuery(sql).getObject(i + 1);
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to get total delivery information
        }

        return totalDeliveryInformation;
    }

    public ArrayList<Object[]> viewAllDeliveries(int userID)
    {
        // returns an array list with an array stored in each index which stores each delivery
        // each index in the array list stores a row, each index in the array stores an attribute
        // index 0 in the array = deliveryID
        // index 1 = start date
        // index 2 = end date
        // index 3 = estimated distance
        // index 4 = actual distance
        // index 5 = estimated revenue
        // index 6 = actual revenue
        // index 7 = cargo weight
        // index 8 = start city
        // index 9 = end city
        // index 10 = total fines received

        ArrayList<Object[]> deliveries = new ArrayList<>();
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT Delivery.DeliveryID,\n" +
                    "       Delivery.StartDate,\n" +
                    "       Delivery.EndDate,\n" +
                    "       Delivery.EstimatedDistance,\n" +
                    "       Delivery.ActualDistance,\n" +
                    "       Delivery.EstimatedRevenue,\n" +
                    "       Delivery.ActualRevenue,\n" +
                    "       Delivery.CargoWeight,\n" +
                    "       Delivery.StartCity,\n" +
                    "       Delivery.EndCity,\n" +
                    "       (Delivery.EstimatedRevenue - Delivery.ActualRevenue) AS [Total Fines Received]";
            // select part of getting all deliveries statement

            String from = "FROM Delivery,\n" +
                    "       User,\n" +
                    "       City";
            // from part of getting all deliveries statement

            StringBuilder where = new StringBuilder("WHERE User.UserID = Delivery.UserID AND ");
            where.append("User.UserID = ");
            where.append(userID);
            // where part of getting all deliveries statement

            String groupBy = "GROUP BY DeliveryID";
            // group by part of getting all deliveries statement

            String orderBy = "ORDER BY Delivery.EndDate DESC";
            // order by part of getting all deliveries statement

            String sql = select + "\n" + from + "\n" + where + "\n" + groupBy + "\n" + orderBy; // create the sql statement

            ResultSet resultSet = statement.executeQuery(sql); // execute the query

            while(resultSet.next()) // loop through results and add each row to the array list
            {
                Object[] thisRow = new Object[11]; // stores the information for this row to be added to the array list

                for(int i = 0; i < thisRow.length; i++) // add all information of this row to the thisRow array
                {
                    thisRow[i] = resultSet.getObject(i + 1);
                }

                deliveries.add(thisRow); // add to the array list
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to view all deliveries
        }

        return deliveries;
    }

    public ArrayList<Object[]> getRoadsDrivenAcross(int cityConnectionID)
    {
        // returns the road names, distance driven on roads and speed limit for the roads travelled across for a city connection
        // each index in the array list stores the information for a road in the city connection
        // index 0 in the inner array = road name
        // index 1 == distance driven on road
        // index 2 == speed limit
        ArrayList<Object[]> roadsDrivenAcross = new ArrayList<>();
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT Road.RoadName,\n" +
                    "       RoadConnections.DistanceDrivenOnRoad,\n" +
                    "       Road.SpeedLimit";
            // select part of get roads driven across statement

            String from = "FROM Road,\n" +
                    "       RoadConnections,\n" +
                    "       CityConnections";
            // from part of get roads driven across statement

            StringBuilder where = new StringBuilder(" WHERE RoadConnections.RoadID = Road.RoadID AND ");
            where.append("CityConnections.CityConnectionID = RoadConnections.CityConnectionID AND ");
            where.append("CityConnections.CityConnectionID = ");
            where.append(cityConnectionID);
            // where part of get roads driven across statement

            String sql = select + "\n" + from + "\n" + where; // create the full sql statement

            ResultSet resultSet = statement.executeQuery(sql); // execute the query

            while(resultSet.next()) // loop through results and add each row to the array list
            {
                Object[] thisRow = new Object[3]; // stores the information for this row to be added to the array list

                for(int i = 0; i < thisRow.length; i++) // add all information of this row to the thisRow array
                {
                    thisRow[i] = resultSet.getObject(i + 1);
                }

                roadsDrivenAcross.add(thisRow); // add to the array list
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to get roads driven across
        }
        return roadsDrivenAcross;
    }

    public int getEstimatedDistance(int cityConnectionID)
    {
        // returns the total distance driven on road for a city connection with the cityConnectionID equal to the inputted one
        int estimatedDistance = -1;

        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT SUM(RoadConnections.DistanceDrivenOnRoad) AS [Estimated Distance]";
            // select part of get estimated distance statement

            String from = "FROM RoadConnections,\n" +
                    "       CityConnections";
            // from part of get estimated distance statement

            String where = "WHERE RoadConnections.CityConnectionID = CityConnections.CityConnectionID AND \n" +
                    "       CityConnections.CityConnectionID = " + cityConnectionID;
            // where part of get estimated distance statement

            String sql = select + "\n" + from + "\n" + where; // create full sql statement

            estimatedDistance = statement.executeQuery(sql).getInt(1);
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to get estimated distance
        }
        return estimatedDistance;
    }

    public ArrayList<Object[]> viewAllConvoys()
    {
        // returns an array list with an array stored in each index which stores each convoy
        // each index in the array list stores a row, each index in the array stores an attribute
        // index 0 in the array = convoyID
        // index 1 = convoy name
        // index 2 = convoy date
        // index 3 = start city
        // index 4 = end city
        ArrayList<Object[]> allConvoys = new ArrayList<>();
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT Convoy.ConvoyID,\n" +
                    "       Convoy.ConvoyName,\n" +
                    "       Convoy.ConvoyDate,\n" +
                    "       Convoy.StartCity,\n" +
                    "       Convoy.EndCity";
            // select part of view all convoys statement

            String from = "FROM Convoy,\n" +
                    "       City";
            // from part of view all convoys statement

            String groupBy = "GROUP BY Convoy.ConvoyID";
            // group by part of view all convoys statement

            String orderBy = "ORDER BY Convoy.ConvoyDate DESC";
            // order by part of view all convoys statement

            String sql = select + "\n" + from + "\n" + groupBy + "\n" + orderBy; // create full statement

            ResultSet resultSet = statement.executeQuery(sql); // execute the query

            while(resultSet.next()) // loop through results and add each row to the array list
            {
                Object[] thisRow = new Object[5]; // stores the information for this row to be added to the array list

                for(int i = 0; i < thisRow.length; i++) // add all information of this row to the thisRow array
                {
                    thisRow[i] = resultSet.getObject(i + 1);
                }

                allConvoys.add(thisRow); // add to the array list
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to view all convoys
        }
        return allConvoys;
    }

    public Object[] searchForConvoy(int convoyID)
    {
        // returns an array which stores the convoy information of a specific convoy
        // index 0 in the array = convoyID
        // index 1 = convoy name
        // index 2 = convoy date
        // index 3 = start city
        // index 4 = end city
        Object[] convoySearched = new Object[5];
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT Convoy.ConvoyID,\n" +
                    "       Convoy.ConvoyName,\n" +
                    "       Convoy.ConvoyDate,\n" +
                    "       Convoy.StartCity,\n" +
                    "       Convoy.EndCity";
            // select part of view all convoys statement

            String from = "FROM Convoy,\n" +
                    "       City";
            // from part of view all convoys statement

            StringBuilder where = new StringBuilder("WHERE Convoy.ConvoyID = ");
            where.append(convoyID);
            // where part of view all convoys statement

            String groupBy = "GROUP BY Convoy.ConvoyID";
            // group by part of view all convoys statement

            String orderBy = "ORDER BY Convoy.ConvoyDate DESC";
            // order by part of view all convoys statement

            String sql = select + "\n" + from + "\n" + where + "\n" + groupBy + "\n" + orderBy; // create full statement

            for(int i = 0; i < convoySearched.length; i++) // gets the information from the query
            {
                convoySearched[i] = statement.executeQuery(sql).getObject(i + 1);
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to search for a convoy
        }
        return convoySearched;
    }

    public ArrayList<Object[]> viewAllSignedUpForConvoys(int userID)
    {
        // returns an array list that stores all convoys the user has signed up for
        // each index in the array list stores a row, each index in the array stores an attribute
        // index 0 in the array = convoyID
        // index 1 = convoy name
        // index 2 = convoy date
        // index 3 = start city
        // index 4 = end city

        ArrayList<Object[]> allSignedUpForConvoys = new ArrayList<>();
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT Convoy.ConvoyID,\n" +
                    "       Convoy.ConvoyName,\n" +
                    "       Convoy.ConvoyDate,\n" +
                    "       Convoy.StartCity,\n" +
                    "       Convoy.EndCity";
            // select part of get all signed up for convoy statement

            String from = "FROM Convoy,\n" +
                    "       City,\n" +
                    "       ConvoyDrivers,\n" +
                    "       User";
            // from part of get all signed up for convoy statement

            StringBuilder where = new StringBuilder("WHERE ConvoyDrivers.ConvoyID = Convoy.ConvoyID AND ");
            where.append("ConvoyDrivers.UserID = User.UserID AND ");
            where.append("User.UserID = ");
            where.append(userID);
            // where part of get all signed up for convoy statement

            String groupBy = "GROUP BY Convoy.ConvoyID";
            // group by part of get all signed up for convoy statement

            String orderBy = "ORDER BY Convoy.ConvoyDate DESC";
            // order by part of get all signed up for convoy statement

            String sql = select + "\n" + from + "\n" + where + "\n" + groupBy + "\n" + orderBy; // create full statement

            ResultSet resultSet = statement.executeQuery(sql); // execute the query

            while(resultSet.next()) // loop through results and add each row to the array list
            {
                Object[] thisRow = new Object[5]; // stores the information for this row to be added to the array list

                for(int i = 0; i < thisRow.length; i++) // add all information of this row to the thisRow array
                {
                    thisRow[i] = resultSet.getObject(i + 1);
                }

                allSignedUpForConvoys.add(thisRow); // add to the array list
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to view all signed up for convoys
        }
        return allSignedUpForConvoys;
    }

    public ArrayList<Object[]> viewAllRanks()
    {
        // returns an array list that stores all ranks
        // each index in the array list stores a row, each index in the array stores an attribute
        // index 0 in the array = rank title
        // index 1 = revenue requirement
        // index 2 = distance requirement
        ArrayList<Object[]> allRanks = new ArrayList<>();
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT Rank.RankTitle, Rank.RevenueRequirement, Rank.DistanceRequirement";
            // select part of view all ranks statement

            String from = "FROM Rank";
            // from part of view all ranks statement

            String orderBy = "ORDER BY Rank.RevenueRequirement DESC, Rank.DistanceRequirement Desc";
            // orderBy part of view all ranks statement

            String sql = select + "\n" + from + "\n" + orderBy; // create full get ranks statement

            ResultSet resultSet = statement.executeQuery(sql); // execute the query

            while(resultSet.next()) // loop through results and add each row to the array list
            {
                Object[] thisRow = new Object[3]; // stores the information for this row to be added to the array list

                for(int i = 0; i < thisRow.length; i++) // add all information of this row to the thisRow array
                {
                    thisRow[i] = resultSet.getObject(i + 1);
                }

                allRanks.add(thisRow); // add to the array list
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to view all ranks
        }
        return allRanks;
    }

    public Object[] getRank(int userID)
    {
        // calculates the rank for a user with userid equal to the inputted userid and returns an array that stores information about what rank the user is
        // index 0 in the array = rank title
        // index 1 = distance requirement
        // index 2 = revenue requirement
        Object[] rank = new Object[3];
        try
        {
            Statement statement = connection.createStatement(); // create a new sql statement

            String select = "SELECT Rank.RankTitle,\n" +
                    "       MAX(Rank.DistanceRequirement),\n" +
                    "       MAX(Rank.RevenueRequirement) ";
            // select part of get rank statement

            String from = "  FROM Rank,\n" +
                    "       User,\n" +
                    "       Delivery";
            // from part of get rank statement

            StringBuilder where = new StringBuilder(" WHERE (\n" +
                    "           SELECT SUM(Delivery.ActualDistance) \n" +
                    "             FROM Delivery,\n" +
                    "                  User\n" +
                    "            WHERE Delivery.UserID = User.UserID AND \n" +
                    "                  User.UserID = ");
            where.append(userID);
            where.append(")\n" +
                    ">=     Rank.DistanceRequirement AND \n" +
                    "       (\n" +
                    "           SELECT SUM(Delivery.ActualRevenue) \n" +
                    "             FROM Delivery,\n" +
                    "                  User\n" +
                    "            WHERE Delivery.UserID = User.UserID AND \n" +
                    "                  User.UserID = ");
            where.append(userID);
            where.append(")\n" +
                    ">=     Rank.RevenueRequirement");
            // where part of get rank statement

            String sql = select + "\n" + from + "\n" + where;

            for(int i = 0; i < rank.length; i++) // gets the information from the query
            {
                rank[i] = statement.executeQuery(sql).getObject(i + 1);
            }

            statement.close();
            connection.commit(); // commit the changes
        }
        catch (Exception exception)
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage()); // failed to get user rank
        }
        return rank;
    }
}
