package EuroTruckRoutePlanner;

public class User extends DatabaseObject
{
    // wrapper class for the User table in the database
    private int userID;
    private String userAccessLevel;
    private String username;
    private String password;

    public User(int userID, String userAccessLevel, String username, String password)
    {
        this.userID = userID;
        this.userAccessLevel = userAccessLevel;
        this.username = username;
        this.password = password;

        setTableName("User"); // set the table name for this table in the database

        String[] tableAttributes = new String[4];
        tableAttributes[0] = "UserID";
        tableAttributes[1] = "UserAccessLevel";
        tableAttributes[2] = "Username";
        tableAttributes[3] = "Password";

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

    public String getUserAccessLevel()
    {
        return userAccessLevel;
    }

    public void setUserAccessLevel(String userAccessLevel)
    {
        this.userAccessLevel = userAccessLevel;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public Object[] getAttributeValues()
    {
        // return all attribute values (userID, userAccessLevel, username and password) for SQL statements
        Object[] attributeValues = new Object[4];
        attributeValues[0] = getUserID();
        attributeValues[1] = getUserAccessLevel();
        attributeValues[2] = getUsername();
        attributeValues[3] = getPassword();

        return attributeValues;
    }
}
