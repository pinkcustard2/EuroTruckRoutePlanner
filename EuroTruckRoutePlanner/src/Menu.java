import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import Dijkstra.Dijkstra;
import Dijkstra.Graph;
import Dijkstra.NodeConnections;
import EuroTruckRoutePlanner.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Menu
{
    private Scanner scanner = new Scanner(System.in);
    private DatabaseConnect database = new DatabaseConnect();
    private int userID = -1;
    private boolean isAdmin = false; // stores whether the user is an admin or not

    public static void main(String[] args)
    {
        Menu menu = new Menu();

        // login
        System.out.println("Please enter your username: ");
        String username;
        boolean isValid = false;

        while(!isValid) // loop until valid username entered
        {
            username = menu.scanner.next();
            try // attempt to get userID from username for password hashing
            {
                menu.userID = (int) menu.database.findAttributes("User", new String[]{"UserID"}, new String[]{"Username"}, new Object[]{username}).get(0)[0];
                isValid = true;
            }
            catch(Exception exception)
            {
                System.out.println("Invalid username entered, please try again: ");
            }
        }

        System.out.println("Please enter your password: ");
        isValid = false;

        while(!isValid) // loop until valid password is entered
        {
            String unhashedPassword = menu.scanner.next();
            String hashedPassword = menu.hashPassword(unhashedPassword, menu.userID); // hash the inputted password

            String userPassword = null;
            try // get the users password
            {
                userPassword = (String) menu.database.findAttributes("User", new String[]{"Password"}, new String[]{"UserID"}, new Object[]{menu.userID}).get(0)[0];
            }
            catch (Exception exception) // failed to get password
            {
                System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
            }

            if(hashedPassword.equals(userPassword)) // check if users password is equal to inputted password
            {
                System.out.println("Successfully logged in");
                isValid = true;
            }
            else // incorrect password entered
            {
                System.out.println("Incorrect password entered, please try again: ");
            }
        }



        String userAccessLevel = null;
        try // attempt to get userAccessLevel
        {
            userAccessLevel = (String) menu.database.findAttributes("User", new String[]{"UserAccessLevel"}, new String[]{"UserID"}, new Object[]{menu.userID}).get(0)[0];
        }
        catch (Exception exception) // failed to get user access level
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
        }

        if(Objects.equals(userAccessLevel, "admin")) // check if user is an admin
        {
            menu.isAdmin = true;
        }

        menu.mainMenu();
    }

    private String hashPassword(String password, int userIDOfUser)
    {
        // hashes a given password for login or storing a new password
        SecureRandom random = new SecureRandom();
        random.setSeed(userIDOfUser); // sets the seed of the random number to the userID, making it always generate the same salt for each user

        byte[] salt = new byte[16];
        random.nextBytes(salt); // sets the salt to a random combination of 16 bytes

        byte[] hashedPassword = null;
        try // attempt to hash the password
        {
            // creates a new specification - the 65536 is the iteration count for the hashing (how many times the hashing it run)
            // and the 128 is the key length (length of outputted hashed password)
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); // set the password hashing algorithm to PBKDF2WithHmacSHA1

            hashedPassword = factory.generateSecret(spec).getEncoded(); // hash the password and store it
        }
        catch (Exception exception) // failed to hash the password
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
        }

        return Arrays.toString(hashedPassword); // return the hashed password as a string
    }

    private Graph<NodeConnections<Integer>> getGraph()
    {
        // converts cities to nodes in the graph and city connections to arcs in the graph and adds them to the graph. Returns completed graph
        Graph<NodeConnections<Integer>> graph = new Graph<NodeConnections<Integer>>(20); // graph for dijkstra
        try // add nodes to graph
        {
            ArrayList<Object[]> cityConnections = database.findAttributes("CityConnections", new String[]{"CityConnectionID", "StartCity", "EndCity"}, new String[]{"CityConnectionID !"}, new Object[]{0});
            // get all city connections - the where part is set to something that will always be true, so it selects all attributes

            for(int i = 0; i < cityConnections.size(); i++) // add all edges to graph - adds all information the graph requires
            {
                int startNode = (int) cityConnections.get(i)[1]; // gets start node of arc
                int endNode = (int) cityConnections.get(i)[2]; // gets end node of arc
                int distance = database.getEstimatedDistance((int) cityConnections.get(i)[0]); // gets distance for city connection - weight for arc

                graph.addArc(startNode, distance, endNode);
            }
        }
        catch (Exception exception) // failed to add nodes to graph
        {
            System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
        }

        return graph;
    }

    private void mainMenu()
    {
        scanner.useDelimiter("\\n"); // allows spaces in scanner.next()

        // menu regular members will see
        System.out.println("-Euro Truck Route Planner-");
        System.out.println("1) Deliveries");
        System.out.println("2) Total statistics");
        System.out.println("3) Convoys");
        System.out.println("4) Ranks");
        if(isAdmin)
        {
            System.out.println("5) Update map");
            System.out.println("6) Users");
        }
        System.out.println("7) Exit");

        switch (scanner.nextInt())
        {
            case 1:
                // deliveries
                deliveriesMenu();
                break;

            case 2:
                // total statistics
                totalStatisticsMenu();

                System.out.println(database.totalDeliveryInformation(3)[1].toString());
                break;

            case 3:
                // convoys
                convoysMenu();
                break;

            case 4:
                // ranks
                ranksMenu();
                break;

            case 5:
                // update map (admin only)
                if(isAdmin)
                {
                    updateMapMenu();
                }
                else // user attempted to access an admin menu as a member
                {
                    System.out.println("Invalid choice");
                    mainMenu();
                }
                break;

            case 6:
                // users (admin only)
                if(isAdmin)
                {
                    usersMenu();
                }
                else // user attempted to access an admin menu as a member
                {
                    System.out.println("Invalid choice");
                    mainMenu();
                }
                break;

            case 7:
                // exit program
                System.exit(0);
                break;

            default:
                // entered an invalid choice
                System.out.println("Invalid choice");
                mainMenu();
                break;
        }
    }


    private void deliveriesMenu()
    {
        // menu for deliveries
        System.out.println("-Deliveries Menu-");
        System.out.println("1) Add new delivery");
        System.out.println("2) Past deliveries report");
        System.out.println("3) Back to main menu");

        switch (scanner.nextInt())
        {
            case 1:
                // new delivery
                System.out.println("Please enter the name or id of the start city of this delivery: ");
                int startCityID = 0; // get the start city id
                boolean isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name entered
                    {
                        String startCity = scanner.next();

                        try
                        {
                            startCityID = (int) (database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0]);
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the name or id of the end city of this delivery: ");
                int endCityID = 0; // get the end city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name entered
                    {
                        String startCity = scanner.next();

                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter your estimated revenue of this delivery: ");
                int estimatedRevenue = 0; // get estimated revenue
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    try
                    {
                        estimatedRevenue = scanner.nextInt();
                        isValid = true;
                    }
                    catch (Exception exception) // invalid input
                    {
                        System.out.println("Invalid estimated revenue, please try again: ");
                        scanner.next(); // reset scanner
                    }
                }

                System.out.println("Please enter the cargo weight of this delivery: ");
                int cargoWeight = 0; // get cargo weight
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    try
                    {
                        cargoWeight = scanner.nextInt();
                        isValid = true;
                    }
                    catch (Exception exception) // invalid input
                    {
                        System.out.println("Invalid cargo weight, please try again: ");
                        scanner.next(); // reset scanner
                    }
                }

                LocalDateTime startDate = LocalDateTime.now(); // gets current time

                // output route delivery route - the user has inputted all data required pre-delivery
                // so the program should display the delivery route

                Dijkstra dijkstra = new Dijkstra(getGraph()); // create dijkstra class
                int estimatedDistance = dijkstra.pathLength(startCityID, endCityID); // get estimated distance

                // display roads to user
                System.out.println("-Roads to Drive Across-");
                int[] columnWidths = new int[]{20, 22, 16}; // stores column widths for each header for outputting delivery information
                // 5 gap between each header (some are bigger as the data is larger)

                String format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2] + "s%n"; // column offset formats

                System.out.printf(format, "Road Name", "Distance to Drive", "Speed Limit"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line

                dijkstra.setGraph(getGraph()); // reset dijkstra graph

                ArrayList<Integer> pathToTravel = dijkstra.path(startCityID, endCityID); // get the cities travelled across for the shortest path

                ArrayList<Integer> cityConnectionsToTravel = new ArrayList<>();
                for(int i = 0; i < pathToTravel.size() - 1; i++) // get city connections from path to travel
                {
                    cityConnectionsToTravel.add((Integer) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{(pathToTravel.get(i)), pathToTravel.get(i + 1)}).get(0)[0]);
                }

                for(int i = 0; i < cityConnectionsToTravel.size(); i++)
                {
                    ArrayList<Object[]> roads = database.getRoadsDrivenAcross(cityConnectionsToTravel.get(i)); // get roads travelled across

                    for(int j = 0; j < roads.size(); j++) // output roads travelled across
                    {
                        System.out.printf(format, roads.get(j)[0], roads.get(j)[1], roads.get(j)[2]);
                    }
                }

                System.out.println("\nEstimated distance: " + estimatedDistance);

                System.out.println("Please type 'finished' and press enter when you have finished your delivery: ");
                while(!Objects.equals(scanner.next(), "finished"))
                {
                    System.out.println("Please type 'finished' and press enter when you have finished your delivery: ");
                }

                LocalDateTime endDate = LocalDateTime.now(); // gets current time

                System.out.println("Please enter the cargo damage your cargo took: ");
                float cargoDamage = 0;
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    try
                    {
                        cargoDamage = scanner.nextFloat();
                        isValid = true;
                    }
                    catch (Exception exception) // invalid input
                    {
                        System.out.println("Invalid cargo damage, please try again: ");
                        scanner.next(); // reset scanner
                    }
                }

                System.out.println("Please enter your actual revenue earnt: ");
                int actualRevenue = 0;
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    try
                    {
                        actualRevenue = scanner.nextInt();
                        isValid = true;
                    }
                    catch (Exception exception) // invalid input
                    {
                        System.out.println("Invalid cargo revenue, please try again: ");
                        scanner.next(); // reset scanner
                    }
                }

                System.out.println("Please enter the actual distance driven: ");
                int actualDistance = 0;
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    try
                    {
                        actualDistance = scanner.nextInt();
                        isValid = true;
                    }
                    catch (Exception exception) // invalid input
                    {
                        System.out.println("Invalid actual distance, please try again: ");
                        scanner.next(); // reset scanner
                    }
                }

                // create delivery wrapper class
                Delivery delivery = new Delivery(-1, userID, startCityID, endCityID, cargoDamage, estimatedRevenue, actualRevenue, cargoWeight, estimatedDistance, actualDistance, startDate, endDate);

                try
                {
                    database.insertInto("Delivery", delivery.getAttributes(), delivery.getAttributeValues());
                }
                catch (Exception exception)
                {
                    System.out.println("Failed to add delivery.");
                }

                deliveriesMenu();
                break;

            case 2:
                // past deliveries report
                System.out.println("-Past Deliveries-");
                ArrayList<Object[]> deliveries = database.viewAllDeliveries(userID);

                columnWidths = new int[]{15, 25, 25, 23, 20, 22, 20, 19, 15, 13, 19}; // stores column widths for each header for outputting delivery information
                // 5 gap between each header (some are bigger as the data is larger)

                format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2]
                        + "s%-" + columnWidths[3] + "s%-" + columnWidths[4] + "s%-" + columnWidths[5] + "s%-" + columnWidths[6]
                        + "s%-" + columnWidths[7] + "s%-" + columnWidths[8] + "s%-" + columnWidths[9] + "s%-" + columnWidths[10]
                        + "s%n"; // column offset formats

                System.out.printf(format, "DeliveryID", "Start Date", "End Date", "Estimated Distance", "Actual Distance",
                "Estimated Revenue", "Actual Revenue", "Cargo Weight", "Start City", "End City", "Fines Received"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // format for dateTime

                for(int i = 0; i < deliveries.size(); i++) // output all information
                {
                    deliveries.get(i)[1] = (LocalDateTime.parse((String) deliveries.get(i)[1])).format(dtf);
                    // convert start date time to string in correct format

                    deliveries.get(i)[2] = (LocalDateTime.parse((String) deliveries.get(i)[2])).format(dtf);
                    // convert end date time to string in correct format

                    deliveries.get(i)[8] = database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{deliveries.get(i)[8]}).get(0)[0];
                    deliveries.get(i)[9] = database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{deliveries.get(i)[9]}).get(0)[0];
                    // get start and end city name from start and end city id

                    System.out.printf(format, deliveries.get(i)[0], deliveries.get(i)[1], deliveries.get(i)[2], deliveries.get(i)[3],
                            deliveries.get(i)[4], deliveries.get(i)[5], deliveries.get(i)[6], deliveries.get(i)[7], deliveries.get(i)[8],
                            deliveries.get(i)[9], deliveries.get(i)[10]); // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                deliveriesMenu();
                break;

            case 3:
                // back to main menu
                mainMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                deliveriesMenu();
                break;
        }
    }

    private void totalStatisticsMenu()
    {
        // displays total delivery statistics for the current user
        System.out.println("-Total Delivery Information-");
        Object[] deliveryInformation = database.totalDeliveryInformation(userID);

        int[] columnWidths = {19, 24, 24, 28, 25}; // stores column widths for each header for outputting total statistics information
        // 5 gap between each header (some are bigger as the data is larger)

        String format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2]
                + "s%-" + columnWidths[3] + "s%-" + columnWidths[4] +"s%n"; // column offset format

        System.out.printf(format, "Total Deliveries", "Total Distance Driven", "Total Revenue Earnt", "Total Weight Transported",
                "Total Fines Received"); // output column headers

        for(int i = 0; i < columnWidths.length; i++) // output header lines
        {
            for(int j = 0; j < columnWidths[i]; j++)
            {
                System.out.print("_");
            }
        }
        System.out.println(); // new line

        System.out.printf(format, deliveryInformation[0], deliveryInformation[1], deliveryInformation[2], deliveryInformation[3],
                deliveryInformation[4]); // output data in column format

        System.out.println("\nCurrent rank: " + Arrays.toString(database.getRank(userID)) + " (rank title, revenue requirement, distance requirement)");
        // output current rank

        System.out.println("Press anything and press enter to go back:");
        scanner.next();

        mainMenu();
    }

    private void convoysMenu()
    {
        // menu for convoys
        System.out.println("-Convoys Menu-");
        System.out.println("1) View report of all convoys");
        System.out.println("2) View specific convoy");
        System.out.println("3) View signed up and previously attended convoys");
        System.out.println("4) Sign up for a convoy");
        System.out.println("5) Withdraw from a convoy");
        if(isAdmin)
        {
            System.out.println("6) Add a convoy");
            System.out.println("7) Remove a convoy");
            System.out.println("8) Update a convoy");
        }
        System.out.println("9) Back to main menu");

        switch(scanner.nextInt())
        {
            case 1:
                // report of all convoys
                System.out.println("-ALl Convoys-");
                ArrayList<Object[]> convoys = database.viewAllConvoys();

                int[] columnWidths = {13, 22, 22 ,15, 13}; // stores column widths for each header for outputting convoy information
                // 5 gap between each header (some are bigger as the data is larger)

                String format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2]
                        + "s%-" + columnWidths[3] + "s%-" + columnWidths[4] +"s%n"; // column offset formats

                System.out.printf(format, "ConvoyID", "Convoy Name", "Convoy Date", "Start City", "End City"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // format for dateTime

                for(int i = 0; i < convoys.size(); i++) // output all information
                {
                    convoys.get(i)[2] = (LocalDateTime.parse((String) convoys.get(i)[2])).format(dtf);
                    // convert date time to string in correct format

                    convoys.get(i)[3] = database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{convoys.get(i)[3]}).get(0)[0];
                    convoys.get(i)[4] = database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{convoys.get(i)[4]}).get(0)[0];
                    // convert start city and end city to start city name and end city name

                    System.out.printf(format, convoys.get(i)[0], convoys.get(i)[1], convoys.get(i)[2], convoys.get(i)[3],
                            convoys.get(i)[4]); // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                convoysMenu();
                break;

            case 2:
                // view a specific convoy (by name or id)
                System.out.println("Please enter the convoyID or convoy name of the convoy you want to view: ");
                int convoyID = 0;
                boolean isValid = false;

                while(!isValid) // loop until valid information is entered
                {
                    try
                    {
                        if (scanner.hasNextInt()) // convoyID has been entered
                        {
                            convoyID = scanner.nextInt();
                        }
                        else // convoy name has been entered
                        {
                            String convoyName = scanner.next();

                            convoyID = (int) database.findAttributes("Convoy", new String[]{"ConvoyID"}, new String[]{"ConvoyName"}, new Object[]{convoyName}).get(0)[0];
                            // convert convoyName to convoyID
                        }
                        isValid = true;
                    }
                    catch (Exception exception) // invalid convoy name entered
                    {
                        System.out.println("Invalid convoy name entered");
                    }
                }

                dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // format for dateTime

                Object[] convoyInformation = database.searchForConvoy(convoyID); // output convoy information
                System.out.println("ConvoyID: " + convoyInformation[0]);
                System.out.println("Convoy Name: " + convoyInformation[1]);
                System.out.println("Convoy Date: " + (LocalDateTime.parse((String) convoyInformation[2]).format(dtf)));
                System.out.println("Start City: " + database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{convoyInformation[3]}).get(0)[0]);
                System.out.println("End City: " + database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{convoyInformation[4]}).get(0)[0]);

                Dijkstra dijkstra = new Dijkstra(getGraph()); // dijkstra for path and estimated distance

                // get the start and end city id for Dijkstra
                convoyInformation = database.searchForConvoy(convoyID);
                int startCityID = (int) convoyInformation[3];
                int endCityID = (int) convoyInformation[4];

                System.out.println("Estimated Distance: " + dijkstra.pathLength(startCityID, endCityID));
                // output the estimated distance of the convoy route calculated using Dijkstra

                // display roads to user
                System.out.println("-Convoy Route-");
                columnWidths = new int[]{20, 22, 16}; // stores column widths for each header for outputting delivery information
                // 5 gap between each header (some are bigger as the data is larger)

                format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2] + "s%n"; // column offset formats

                System.out.printf(format, "Road Name", "Distance to Drive", "Speed Limit"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line

                dijkstra.setGraph(getGraph()); // reset dijkstra graph

                ArrayList<Integer> pathToTravel = dijkstra.path(startCityID, endCityID);
                // get the shortest path travelled across for the convoy

                ArrayList<Integer> cityConnectionsToTravel = new ArrayList<>();
                for(int i = 0; i < pathToTravel.size() - 1; i++) // get city connections from path to travel
                {
                    cityConnectionsToTravel.add((Integer) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{(pathToTravel.get(i)), pathToTravel.get(i + 1)}).get(0)[0]);
                }

                for(int i = 0; i < cityConnectionsToTravel.size(); i++)
                {
                    ArrayList<Object[]> roads = database.getRoadsDrivenAcross(cityConnectionsToTravel.get(i)); // get roads travelled across

                    for(int j = 0; j < roads.size(); j++) // output roads travelled across
                    {
                        System.out.printf(format, roads.get(j)[0], roads.get(j)[1], roads.get(j)[2]);
                    }
                }

                System.out.println("Please type anything and press enter to go back when you are done viewing the convoy: ");
                scanner.next();

                convoysMenu();
                break;

            case 3:
                // view signed up and previously attended convoys
                System.out.println("-Previously Signed Up For And Attended Convoys-");
                ArrayList<Object[]> convoysAttended = database.viewAllSignedUpForConvoys(userID);

                columnWidths = new int[]{13, 25, 22, 15, 13}; // stores column widths for each header for outputting attended convoy information
                // 5 gap between each header (some are bigger as the data is larger)

                format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2]
                        + "s%-" + columnWidths[3] + "s%-" + columnWidths[4] +"s%n"; // column offset formats

                System.out.printf(format, "ConvoyID", "Convoy Name", "Convoy Date", "Start City", "End City"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line

                dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // format for dateTime

                for(int i = 0; i < convoysAttended.size(); i++) // output all information
                {
                    convoysAttended.get(i)[2] = (LocalDateTime.parse((String) convoysAttended.get(i)[2])).format(dtf);
                    // convert date time to string in correct format

                    convoysAttended.get(i)[3] = database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{convoysAttended.get(i)[3]}).get(0)[0];
                    convoysAttended.get(i)[4] = database.findAttributes("City", new String[]{"CityName"}, new String[]{"CityID"}, new Object[]{convoysAttended.get(i)[4]}).get(0)[0];
                    // convert start city and end city to start city name and end city name

                    System.out.printf(format, convoysAttended.get(i)[0], convoysAttended.get(i)[1], convoysAttended.get(i)[2],
                            convoysAttended.get(i)[3], convoysAttended.get(i)[4]); // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                convoysMenu();
                break;

            case 4:
                // sign up for a convoy
                System.out.println("-Sign up for a convoy-");

                System.out.println("Please enter the convoyID or convoy name of the convoy you want to sign up for: ");
                convoyID = 0;
                isValid = false;

                while(!isValid)
                {
                    try
                    {
                        if(scanner.hasNextInt()) // convoyID has been entered
                        {
                            convoyID = scanner.nextInt();
                        }
                        else // convoy name has been entered
                        {
                            String convoyName = scanner.next();

                            convoyID = (int) database.findAttributes("Convoy", new String[]{"ConvoyID"}, new String[]{"ConvoyName"}, new Object[]{convoyName}).get(0)[0];
                            // convert convoyName to convoyID
                        }

                        LocalDateTime currentTime = LocalDateTime.now(); // get current time
                        LocalDateTime convoyTime = (LocalDateTime.parse((String) database.findAttributes("Convoy", new String[]{"ConvoyDate"}, new String[]{"ConvoyID"}, new Object[]{convoyID}).get(0)[0]));
                        // gets the date time of the convoy

                        // check that convoy has not happened yet
                        if(convoyTime.isAfter(currentTime)) // make sure the convoy time is after current time
                        {
                            // attempt to sign up
                            database.insertInto("ConvoyDrivers", new String[]{"UserID", "ConvoyID"}, new Object[]{userID, convoyID});
                        }
                        else
                        {
                            System.out.println("Convoy has already occurred");
                        }

                        isValid = true; // correctly signed up
                    }
                    catch (Exception exception) // invalid convoyID or convoyName
                    {
                        System.out.println("Invalid convoy name or convoy id, please try again");
                    }
                }

                convoysMenu();
                break;

            case 5:
                // withdraw from a convoy
                System.out.println("Please enter the convoyID or convoy name of the convoy you want to withdraw from:");
                convoyID = -1;
                isValid = false;

                while(!isValid)
                {
                    try
                    {
                        if(scanner.hasNextInt()) // convoyID has been entered
                        {
                            convoyID = scanner.nextInt();
                        }
                        else // convoy name has been entered
                        {
                            String convoyName = scanner.next();

                            convoyID = (int) database.findAttributes("Convoy", new String[]{"ConvoyID"}, new String[]{"ConvoyName"}, new Object[]{convoyName}).get(0)[0];
                            // convert convoyName to convoyID
                        }

                        LocalDateTime currentTime = LocalDateTime.now(); // get current time
                        LocalDateTime convoyTime = LocalDateTime.parse((String) database.findAttributes("Convoy", new String[]{"ConvoyDate"}, new String[]{"ConvoyID"}, new Object[]{convoyID}).get(0)[0]);
                        // gets the date time of the convoy

                        // check that convoy has not happened yet
                        if(convoyTime.isAfter(currentTime)) // make sure the convoy time is after current time
                        {
                            // attempt to withdraw from convoy
                            database.deleteFrom("ConvoyDrivers", new String[]{"UserID", "ConvoyID"}, new Object[]{userID, convoyID});
                        }
                        else
                        {
                            System.out.println("Convoy has already occurred");
                        }

                        isValid = true; // correctly signed up
                    }
                    catch (Exception exception) // invalid convoyID or convoyName
                    {
                        System.out.println("Invalid convoy name or convoy id, please try again");
                    }
                }

                convoysMenu();
                break;

            case 6:
                // add a convoy (admin only)
                if(isAdmin)
                {
                    System.out.println("-Add a Convoy-");
                    System.out.println("Please enter the start city name or id for the convoy: ");

                    startCityID = 0; // get the start city id
                    isValid = false;

                    while(!isValid) // loop until valid information is entered entered
                    {
                        if(scanner.hasNextInt()) // cityID has been entered
                        {
                            startCityID = scanner.nextInt();
                            isValid = true;
                        }
                        else // city name has been entered
                        {
                            String startCity = scanner.next();
                            try
                            {
                                startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                                isValid = true;
                                // finds cityID from inputted city name
                            } catch (Exception exception) // invalid city entered
                            {
                                System.out.println("Invalid city inputted, please try again: ");
                            }
                        }
                    }

                    System.out.println("Please enter the end city name or id for the convoy: ");

                    endCityID = 0; // get the start city id
                    isValid = false;

                    while(!isValid) // loop until valid information is entered entered
                    {
                        if(scanner.hasNextInt()) // cityID has been entered
                        {
                            endCityID = scanner.nextInt();
                            isValid = true;
                        }
                        else // city name has been entered
                        {
                            String endCity = scanner.next();
                            try
                            {
                                endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                                isValid = true;
                                // finds cityID from inputted city name
                            } catch (Exception exception) // invalid city entered
                            {
                                System.out.println("Invalid city inputted, please try again: ");
                            }
                        }
                    }

                    System.out.println("Please enter the name of the convoy: ");
                    String convoyName = scanner.next();

                    System.out.println("Please enter the date of the convoy in the formula dd/mm/yyyy hh:mm:ss: ");
                    LocalDateTime convoyTime = null;

                    isValid = false;
                    while(!isValid)
                    {
                        try
                        {
                            String convoyDateEntered = scanner.next();

                            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // format for dateTime
                            convoyTime = LocalDateTime.parse(convoyDateEntered, dtf); // set the date time in the correct format

                            isValid = true;
                        }
                        catch (Exception exception)
                        {
                            System.out.println("Invalid date entered, please enter a date in the formula dd/mm/yyyy hh:mm:ss: ");
                        }
                    }

                    Convoy newConvoy = new Convoy(-1, startCityID, endCityID, convoyName, convoyTime); // create convoy wrapper object
                    try
                    {
                        database.insertInto("Convoy", newConvoy.getAttributes(), newConvoy.getAttributeValues()); // attempt to add convoy
                    }
                    catch (Exception exception)
                    {
                        System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                    }
                }
                else
                {
                    System.out.println("Invalid choice");
                }

                convoysMenu();
                break;

            case 7:
                // remove a convoy (admin only)
                if(isAdmin)
                {
                    System.out.println("-Remove a Convoy-");

                    System.out.println("Please enter the name or convoyID of the convoy you want to remove: ");
                    convoyID = 0; // get the convoy id

                    isValid = false;
                    while(!isValid)
                    {
                        if(scanner.hasNextInt()) // convoyID has been entered
                        {
                            convoyID = scanner.nextInt();
                            isValid = true;
                        }
                        else // convoy name has been entered
                        {
                            String convoyName = scanner.next();
                            try
                            {
                                convoyID = (int) database.findAttributes("Convoy", new String[]{"ConvoyID"}, new String[]{"ConvoyName"}, new Object[]{convoyName}).get(0)[0];
                                isValid = true;
                                // finds convoyID from inputted convoy name
                            } catch (Exception exception) // invalid city entered
                            {
                                System.out.println("Invalid convoy name inputted, please try again: ");
                            }
                        }

                        try
                        {
                            database.deleteFrom("Convoy", new String[]{"ConvoyID"}, new Object[]{convoyID}); // attempt to delete convoy
                        }
                        catch(Exception exception) // failed to delete convoy
                        {
                            System.out.println("Invalid convoyID entered");
                            isValid = false;
                        }
                    }
                }
                else
                {
                    System.out.println("Invalid choice");
                }

                convoysMenu();
                break;

            case 8:
                // update a convoy
                if(isAdmin)
                {
                    System.out.println("-Update a Convoy-");

                    System.out.println("Please enter the convoyID or convoy name of the convoy you want to update: ");
                    convoyID = 0; // get the convoy id

                    isValid = false;
                    while(!isValid)
                    {
                        if (scanner.hasNextInt()) // convoyID has been entered
                        {
                            convoyID = scanner.nextInt();
                            isValid = true;
                        }
                        else // convoy name has been entered
                        {
                            String convoyName = scanner.next();
                            try
                            {
                                convoyID = (int) database.findAttributes("Convoy", new String[]{"ConvoyID"}, new String[]{"ConvoyName"}, new Object[]{convoyName}).get(0)[0];
                                isValid = true;
                                // finds convoyID from inputted convoy name
                            }
                            catch (Exception exception) // invalid city entered
                            {
                                System.out.println("Invalid convoy name inputted, please try again: ");
                            }
                        }
                    }

                    System.out.println("Please enter the start city name or id for the convoy: ");

                    startCityID = 0; // get the start city id
                    isValid = false;

                    while(!isValid) // loop until valid information is entered entered
                    {
                        if(scanner.hasNextInt()) // cityID has been entered
                        {
                            startCityID = scanner.nextInt();
                            isValid = true;
                        }
                        else // city name has been entered
                        {
                            String startCity = scanner.next();
                            try
                            {
                                startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                                isValid = true;
                                // finds cityID from inputted city name
                            }
                            catch (Exception exception) // invalid city entered
                            {
                                System.out.println("Invalid city inputted, please try again: ");
                            }
                        }
                    }

                    System.out.println("Please enter the end city name or id for the convoy: ");

                    endCityID = 0; // get the start city id
                    isValid = false;

                    while(!isValid) // loop until valid information is entered entered
                    {
                        if(scanner.hasNextInt()) // cityID has been entered
                        {
                            endCityID = scanner.nextInt();
                            isValid = true;
                        }
                        else // city name has been entered
                        {
                            String endCity = scanner.next();
                            try
                            {
                                endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                                isValid = true;
                                // finds cityID from inputted city name
                            }
                            catch (Exception exception) // invalid city entered
                            {
                                System.out.println("Invalid city inputted, please try again: ");
                            }
                        }
                    }

                    System.out.println("Please enter the name of the convoy: ");
                    String convoyName = scanner.next();

                    System.out.println("Please enter the date of the convoy in the formula dd/mm/yyyy hh:mm:ss: ");
                    LocalDateTime convoyTime = null;

                    isValid = false;
                    while(!isValid)
                    {
                        try
                        {
                            String convoyDateEntered = scanner.next();

                            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // format for dateTime
                            convoyTime = LocalDateTime.parse(convoyDateEntered, dtf); // set the date time in the correct format

                            isValid = true;
                        }
                        catch (Exception exception)
                        {
                            System.out.println("Invalid date entered, please enter a date in the formula dd/mm/yyyy hh:mm:ss: ");
                        }
                    }

                    Convoy newConvoy = new Convoy(convoyID, startCityID, endCityID, convoyName, convoyTime); // create convoy wrapper class

                    try
                    {
                        // attempt to update convoy to new values in newConvoy object
                        database.update("Convoy", newConvoy.getAttributes(), newConvoy.getAttributeValues(), new String[]{"ConvoyID"}, new Object[]{convoyID});
                    }
                    catch (Exception exception) // failed to update convoy
                    {
                        throw new RuntimeException(exception);
                    }
                }
                else
                {
                    System.out.println("Invalid choice");
                }

                convoysMenu();
                break;

            case 9:
                // back to main menu
                mainMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                convoysMenu();
                break;
        }
    }

    private void ranksMenu()
    {
        // menu for rank information
        System.out.println("-Ranks Menu-");
        System.out.println("1) View all ranks report");
        System.out.println("2) View current rank");
        if(isAdmin)
        {
            System.out.println("3) Add a rank");
            System.out.println("4) Remove a rank");
            System.out.println("5) Modify a rank");
        }
        System.out.println("6) Back to main menu");

        switch(scanner.nextInt())
        {
            case 1:
                // all ranks report
                // displays total delivery statistics for the current user
                System.out.println("-Total Delivery Information-");
                ArrayList<Object[]> allRanks = database.viewAllRanks();

                int[] columnWidths = {15, 24, 25}; // stores column widths for each header for outputting total statistics information
                // 5 gap between each header (some are bigger as the data is larger)

                String format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2] + "s%n"; // column offset format

                System.out.printf(format, "Rank Title", "Revenue Requirement", "Distance Requirement"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }

                System.out.println(); // new line

                for(int i = 0; i < allRanks.size(); i++) // output all information
                {
                    System.out.printf(format, allRanks.get(i)[0], allRanks.get(i)[1], allRanks.get(i)[2]); // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                ranksMenu();
                break;

            case 2:
                // current user's rank
                System.out.println("Current rank: " + Arrays.toString(database.getRank(userID)) + " (rank title, revenue requirement, distance requirement)");
                // output current rank

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                ranksMenu();
                break;

            case 3:
                // add a rank (admin only)
                if(isAdmin)
                {
                    System.out.println("-Add a New Rank-");

                    System.out.println("Please enter the title of the new rank: ");
                    String rankTitle = scanner.next();

                    System.out.println("Please enter the distance requirement of the new rank: ");
                    int distanceRequirement = scanner.nextInt();

                    System.out.println("Please enter the revenue requirement of the new rank: ");
                    int revenueRequirement = scanner.nextInt();

                    Rank newRank = new Rank(-1, revenueRequirement, distanceRequirement, rankTitle); // create rank wrapper object
                    try
                    {
                        database.insertInto("Rank", newRank.getAttributes(), newRank.getAttributeValues()); // attempt to add new rank
                    } catch (Exception exception)
                    {
                        System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                    }
                }
                else
                {
                    System.out.println("Invalid choice");
                }

                ranksMenu();
                break;

            case 4:
                // remove a rank (admin only)
                if(isAdmin)
                {
                    System.out.println("-Remove a rank-");

                    System.out.println("Please enter the id or title of the rank you want to remove: ");
                    int rankID = 0; // get the rank id

                    boolean isValid = false;
                    while(!isValid)
                    {
                        if(scanner.hasNextInt()) // rankID has been entered
                        {
                            rankID = scanner.nextInt();
                            isValid = true;
                        }
                        else // rank title name has been entered
                        {
                            String rankTitleName = scanner.next();
                            try
                            {
                                rankID = (int) database.findAttributes("Rank", new String[]{"RankID"}, new String[]{"RankTitle"}, new Object[]{rankTitleName}).get(0)[0];
                                isValid = true;
                                // finds rankID from inputted rank title name
                            }
                            catch (Exception exception) // invalid rank entered
                            {
                                System.out.println("Invalid rank name inputted, please try again: ");
                            }
                        }

                        try
                        {
                            database.deleteFrom("Rank", new String[]{"RankID"}, new Object[]{rankID}); // attempt to delete rank
                        }
                        catch(Exception exception) // failed to delete rank
                        {
                            System.out.println("Invalid rankID entered");
                            isValid = false;
                        }
                    }
                }
                else
                {
                    System.out.println("Invalid choice");
                }

                ranksMenu();
                break;

            case 5:
                // modify a rank (admin only)
                if(isAdmin)
                {
                    System.out.println("-Update a Rank-");

                    System.out.println("Please enter the id or title of the rank you want to update: ");
                    int rankID = 0; // get the rank id of rank to update

                    boolean isValid = false;
                    while(!isValid)
                    {
                        if (scanner.hasNextInt()) // rankID has been entered
                        {
                            rankID = scanner.nextInt();
                            isValid = true;
                        }
                        else // rank title name has been entered
                        {
                            String rankTitleName = scanner.next();
                            try
                            {
                                rankID = (int) database.findAttributes("Rank", new String[]{"RankID"}, new String[]{"RankTitle"}, new Object[]{rankTitleName}).get(0)[0];
                                isValid = true;
                                // finds rankID from inputted rank title name
                            } catch (Exception exception) // invalid rank entered
                            {
                                System.out.println("Invalid rank name inputted, please try again: ");
                            }
                        }
                    }

                    System.out.println("Please enter the new rank title: ");
                    String rankTitle = scanner.next();

                    System.out.println("Please enter the new distance requirement: ");
                    int distanceRequirement = scanner.nextInt();

                    System.out.println("Please enter the new revenue requirement: ");
                    int revenueRequirement = scanner.nextInt();

                    Rank newRank = new Rank(rankID, revenueRequirement, distanceRequirement, rankTitle); // create rank wrapper object
                    try // try to update rank
                    {
                        database.update("Rank", newRank.getAttributes(), newRank.getAttributeValues(), new String[]{"RankID"}, new Object[]{rankID});
                    }
                    catch (Exception exception)
                    {
                        System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                    }
                }
                else
                {
                    System.out.println("Invalid choice");
                }

                ranksMenu();
                break;

            case 6:
                // back to main menu
                mainMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                ranksMenu();
                break;
        }
    }

    private void updateMapMenu()
    {
        // update the map (add, remove and modify roads, cities, ect.) (admins only)
        System.out.println("-Map Menu-");
        System.out.println("1) Add");
        System.out.println("2) Remove");
        System.out.println("3) Modify");
        System.out.println("4) View map");
        System.out.println("5) Back to main menu");

        switch(scanner.nextInt())
        {
            case 1:
                // add to map
                addToMapMenu();
                break;

            case 2:
                // remove from map
                removeFromMapMenu();
                break;

            case 3:
                // modify the map
                modifyMapMenu();
                break;

            case 4:
                // view the map
                viewMapMenu();
                break;

            case 5:
                // back to main menu
                mainMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                updateMapMenu();
                break;
        }
    }

    private void addToMapMenu()
    {
        // add something to the map (roads, cities, ect.) (admins only)
        System.out.println("-Add to map-");
        System.out.println("1) Roads");
        System.out.println("2) City Connections");
        System.out.println("3) Road Connections");
        System.out.println("4) Cities");
        System.out.println("5) Countries");
        System.out.println("6) Back to map menu");

        switch(scanner.nextInt())
        {
            case 1:
                // add a road
                System.out.println("-Add a Road-");

                System.out.println("Please enter the road length of the new road: ");
                int roadLength = scanner.nextInt();

                System.out.println("Please enter the speed limit of the new road in km/h: ");
                int speedLimit = scanner.nextInt();

                System.out.println("Please enter the name of the new road: ");
                String roadName = scanner.next();

                Road newRoad = new Road(-1, roadLength, speedLimit, roadName); // create road wrapper object for new road

                try // attempt to add new road
                {
                    database.insertInto("Road", newRoad.getAttributes(), newRoad.getAttributeValues());
                }
                catch (Exception exception) // failed to add new road
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                addToMapMenu();
                break;

            case 2:
                // add a city connection
                System.out.println("-Add a City Connection");

                System.out.println("Please enter the start city id or name for the new city connection");
                int startCityID = 0; // get the start city id
                boolean isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the end city id or name for the new city connection");
                int endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                CityConnections newCityConnections = new CityConnections(-1, startCityID, endCityID); // create city connections wrapper object

                try // attempt to add new city connection
                {
                    database.insertInto("CityConnections", newCityConnections.getAttributes(), newCityConnections.getAttributeValues());
                }
                catch(Exception exception) // failed to add new city connection
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                addToMapMenu();
                break;

            case 3:
                // add road connection
                System.out.println("-Add a Road Connection-");

                System.out.println("Please enter the start city id or name for the start city of the city connection that the new road connection is part of: ");
                startCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the end city id or name for the start city of the city connection that the new road connection is part of: ");
                endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                int cityConnectionID = 0;

                try // get city connection present in road connection that uses the start city id and end city id obtained
                {
                    cityConnectionID = (int) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{startCityID, endCityID}).get(0)[0];
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                System.out.println("Enter roadID or road name of the new road connection: ");
                int roadID = 0; // get road id for new road connection
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // roadID has been entered
                    {
                        roadID = scanner.nextInt();
                        isValid = true;
                    }
                    else // road name has been entered
                    {
                        String getRoadName = scanner.next();
                        try
                        {
                            roadID = (int) database.findAttributes("Road", new String[]{"RoadID"}, new String[]{"RoadName"}, new Object[]{getRoadName}).get(0)[0];
                            isValid = true;
                            // finds roadID from inputted road name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid road inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the distance driven on the road: ");
                int distanceDriven = 0;

                // get the length of the road
                int maxDistance = (int) database.findAttributes("Road", new String[]{"RoadLength"}, new String[]{"RoadID"}, new Object[]{roadID}).get(0)[0];

                isValid = false;
                while(!isValid)
                {
                    distanceDriven = scanner.nextInt();

                    if(distanceDriven < maxDistance) // check that distance driven is shorter than the length of the road
                    {
                        isValid = true;
                    }
                    else
                    {
                        System.out.println("Distance is longer than the road length, please re-enter distance: ");
                    }
                }
                
                RoadConnections roadConnections = new RoadConnections(cityConnectionID, roadID, distanceDriven); // create wrapper object for new road connections

                try // add the new road connection
                {
                    database.insertInto("RoadConnections", roadConnections.getAttributes(), roadConnections.getAttributeValues());
                }
                catch(Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                addToMapMenu();
                break;

            case 4:
                // add a city
                System.out.println("-Add a City-");

                System.out.println("Please enter the countryID or the name of the country the city is in: ");
                int countryID = 0; // get the countryID
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // countryID has been entered
                    {
                        countryID = scanner.nextInt();
                        isValid = true;
                    }
                    else // country name has been entered
                    {
                        String countryName = scanner.next();
                        try
                        {
                            countryID = (int) database.findAttributes("Country", new String[]{"CountryID"}, new String[]{"CountryName"}, new Object[]{countryName}).get(0)[0];
                            isValid = true;
                            // finds countryID from inputted city name
                        }
                        catch (Exception exception) // invalid country entered
                        {
                            System.out.println("Invalid country inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the name of the city: ");
                String cityName = scanner.next();

                City newCity = new City(-1, countryID, cityName); // create wrapper object for new city

                try // attempt to add new city
                {
                    database.insertInto("City", newCity.getAttributes(), newCity.getAttributeValues());
                }
                catch (Exception exception) // failed to add new city
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                addToMapMenu();
                break;

            case 5:
                // add a country
                System.out.println("-Add a Country-");

                System.out.println("Please enter the name of the country: ");
                String countryName = scanner.next();

                Country newCountry = new Country(-1, countryName); // create wrapper object for new country

                try // attempt to add a new country
                {
                    database.insertInto("Country", newCountry.getAttributes(), newCountry.getAttributeValues());
                }
                catch (Exception exception) // failed to add new country
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                addToMapMenu();
                break;

            case 6:
                // back to map menu
                updateMapMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                addToMapMenu();
                break;
        }
    }

    private void removeFromMapMenu()
    {
        // remove something from the map (roads, cities, ect.) (admins only)
        System.out.println("-Remove to map-");
        System.out.println("1) Roads");
        System.out.println("2) City Connections");
        System.out.println("3) Road Connections");
        System.out.println("4) Cities");
        System.out.println("5) Countries");
        System.out.println("6) Back to map menu");

        switch(scanner.nextInt())
        {
            case 1:
                // remove a road
                System.out.println("-Remove a Road-");

                System.out.println("Please enter the name of the road or the roadID of the road you want to remove: ");
                int roadID = 0; // get road id for road to remove
                boolean isValid = false;

                while(!isValid) // loop until valid information is entered
                {
                    if(scanner.hasNextInt()) // roadID has been entered
                    {
                        roadID = scanner.nextInt();
                        isValid = true;
                    }
                    else // road name has been entered
                    {
                        String roadName = scanner.next();
                        try
                        {
                            roadID = (int) database.findAttributes("Road", new String[]{"RoadID"}, new String[]{"RoadName"}, new Object[]{roadName}).get(0)[0];
                            isValid = true;
                            // finds roadID from inputted city name
                        }
                        catch (Exception exception) // invalid road entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                try // attempt to remove road
                {
                    database.deleteFrom("Road", new String[]{"RoadID"}, new Object[]{roadID});
                }
                catch (Exception exception) // failed to remove road
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                removeFromMapMenu();
                break;

            case 2:
                // remove a city connection
                System.out.println("-Remove a City Connection-");

                System.out.println("Please enter the name or id of the start city of the city connection you want to remove: ");
                int startCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the name or id of the end city of the city connection you want to remove: ");
                int endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                int cityConnectionsID = (int) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{startCityID, endCityID}).get(0)[0];
                // get city connection id from start and end city

                try // attempt to remove city connections
                {
                    database.deleteFrom("CityConnections", new String[]{"CityConnectionID"}, new Object[]{cityConnectionsID});
                }
                catch (Exception exception) // failed to remove city connections
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                removeFromMapMenu();
                break;

            case 3:
                // remove a road connection
                System.out.println("-Remove a Road Connection-");

                System.out.println("Please enter the start city id or name for the start city of the city connection that the road connection to remove is part of: ");
                startCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the end city id or name for the start city of the city connection that the road connection to remove is part of: ");
                endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Enter roadID or road name of the road connection to remove: ");
                roadID = 0; // get road id for road connection to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // roadID has been entered
                    {
                        roadID = scanner.nextInt();
                        isValid = true;
                    }
                    else // road name has been entered
                    {
                        String getRoadName = scanner.next();
                        try
                        {
                            roadID = (int) database.findAttributes("Road", new String[]{"RoadID"}, new String[]{"RoadName"}, new Object[]{getRoadName}).get(0)[0];
                            isValid = true;
                            // finds roadID from inputted road name
                        }
                        catch (Exception exception) // invalid road entered
                        {
                            System.out.println("Invalid road inputted, please try again: ");
                        }
                    }
                }

                int cityConnectionID = 0;

                try // get city connection present in road connection that uses the start city id and end city id obtained
                {
                    cityConnectionID = (int) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{startCityID, endCityID}).get(0)[0];
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                try // attempt to remove road connections
                {
                    database.deleteFrom("RoadConnections", new String[]{"CityConnectionID", "RoadID"}, new Object[]{cityConnectionID, roadID});
                }
                catch (Exception exception) // failed to remove road connections
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                removeFromMapMenu();
                break;

            case 4:
                // remove a city
                System.out.println("-Remove a City-");

                System.out.println("Please enter the cityID or the name of the city to remove: ");
                int cityID = 0; // get city id of city to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // cityID has been entered
                    {
                        cityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String cityName = scanner.next();
                        try
                        {
                            cityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{cityName}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city name inputted, please try again: ");
                        }
                    }
                }

                try // attempt to remove city
                {
                    database.deleteFrom("City", new String[]{"CityID"}, new Object[]{cityID});
                }
                catch(Exception exception) // failed to remove city
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                removeFromMapMenu();
                break;

            case 5:
                // remove a country
                System.out.println("-Remove a Country-");

                System.out.println("Please enter the country id or the country name of the country you want to remove: ");
                int countryID = 0; // get country id of country to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // countryID has been entered
                    {
                        countryID = scanner.nextInt();
                        isValid = true;
                    }
                    else // country name has been entered
                    {
                        String countryName = scanner.next();
                        try
                        {
                            countryID = (int) database.findAttributes("Country", new String[]{"CountryID"}, new String[]{"CountryName"}, new Object[]{countryName}).get(0)[0];
                            isValid = true;
                            // finds countryID from inputted city name
                        }
                        catch (Exception exception) // invalid country entered
                        {
                            System.out.println("Invalid city name inputted, please try again: ");
                        }
                    }
                }

                try // attempt to remove country
                {
                    database.deleteFrom("Country", new String[]{"CountryID"}, new Object[]{countryID});
                }
                catch (Exception exception) // failed to remove country
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                removeFromMapMenu();
                break;

            case 6:
                // back to main menu
                updateMapMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                removeFromMapMenu();
                break;
        }
    }

    private void modifyMapMenu()
    {
        // modify something from the map (roads, cities, ect.) (admins only)
        System.out.println("-Modify to map-");
        System.out.println("1) Roads");
        System.out.println("2) City Connections");
        System.out.println("3) Road Connections");
        System.out.println("4) Cities");
        System.out.println("5) Countries");
        System.out.println("6) Back to map menu");

        switch(scanner.nextInt())
        {
            case 1:
                // modify a road
                System.out.println("-Modify a Road-");

                System.out.println("Please enter the roadID or road name of the road you want to modify: ");
                int roadID = 0; // get road id for road to remove
                boolean isValid = false;

                while(!isValid) // loop until valid information is entered
                {
                    if(scanner.hasNextInt()) // roadID has been entered
                    {
                        roadID = scanner.nextInt();
                        isValid = true;
                    }
                    else // road name has been entered
                    {
                        String roadName = scanner.next();
                        try
                        {
                            roadID = (int) database.findAttributes("Road", new String[]{"RoadID"}, new String[]{"RoadName"}, new Object[]{roadName}).get(0)[0];
                            isValid = true;
                            // finds roadID from inputted city name
                        }
                        catch (Exception exception) // invalid road entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the new road length of the road you are updating: ");
                int roadLength = scanner.nextInt();

                System.out.println("Please enter the new speed limit of the road you are updating in km/h: ");
                int speedLimit = scanner.nextInt();

                System.out.println("Please enter the new name of the road you are updating: ");
                String roadName = scanner.next();

                Road newRoad = new Road(roadID, roadLength, speedLimit, roadName); // create road wrapper object for new road

                try // attempt to update road
                {
                    database.update("Road", newRoad.getAttributes(), newRoad.getAttributeValues(), new String[]{"RoadID"}, new Object[]{roadID});
                }
                catch (Exception exception) // failed to update road
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                modifyMapMenu();
                break;

            case 2:
                // modify a city connection
                System.out.println("-Modify a City Connection-");

                System.out.println("Please enter the name or id of the start city of the city connection you want to update: ");
                int startCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the name or id of the end city of the city connection you want to update: ");
                int endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                int cityConnectionsID = (int) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{startCityID, endCityID}).get(0)[0];
                // get city connection id from start and end city

                System.out.println("Please enter the new start city id or name for the city connection you are updating: ");
                startCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the new end city id or name for the city connection you are updating: ");
                endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                CityConnections newCityConnections = new CityConnections(cityConnectionsID, startCityID, endCityID); // create city connections wrapper object

                try // attempt to update city connections
                {
                    database.update("CityConnections", newCityConnections.getAttributes(), newCityConnections.getAttributeValues(), new String[]{"CityConnectionID"}, new Object[]{cityConnectionsID});
                }
                catch (Exception exception) // failed to update city connections
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                modifyMapMenu();
                break;

            case 3:
                // modify a road connection
                System.out.println("-Modify Road Connection-");

                System.out.println("Please enter the start city id or name for the start city of the city connection that the road connection to update is part of: ");
                startCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the end city id or name for the start city of the city connection that the road connection to update is part of: ");
                endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Enter roadID or road name of the road connection to update: ");
                roadID = 0; // get road id for road connection to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // roadID has been entered
                    {
                        roadID = scanner.nextInt();
                        isValid = true;
                    }
                    else // road name has been entered
                    {
                        String getRoadName = scanner.next();
                        try
                        {
                            roadID = (int) database.findAttributes("Road", new String[]{"RoadID"}, new String[]{"RoadName"}, new Object[]{getRoadName}).get(0)[0];
                            isValid = true;
                            // finds roadID from inputted road name
                        }
                        catch (Exception exception) // invalid road entered
                        {
                            System.out.println("Invalid road inputted, please try again: ");
                        }
                    }
                }

                int cityConnectionID = 0;

                try // get city connection present in road connection that uses the start city id and end city id obtained
                {
                    cityConnectionID = (int) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{startCityID, endCityID}).get(0)[0];
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                System.out.println("Please enter the new start city id or name for the start city of the city connection that the road connection you are updating is part of: ");
                startCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        startCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String startCity = scanner.next();
                        try
                        {
                            startCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{startCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the new end city id or name for the start city of the city connection that the road connection that you are updating is part of: ");
                endCityID = 0; // get the start city id
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // cityID has been entered
                    {
                        endCityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String endCity = scanner.next();
                        try
                        {
                            endCityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{endCity}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city inputted, please try again: ");
                        }
                    }
                }

                int newCityConnectionID = 0;

                try // get city connection present in road connection that uses the start city id and end city id obtained
                {
                    newCityConnectionID = (int) database.findAttributes("CityConnections", new String[]{"CityConnectionID"}, new String[]{"StartCity", "EndCity"}, new Object[]{startCityID, endCityID}).get(0)[0];
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                System.out.println("Enter new roadID or road name of the new road connection: ");
                int newRoadID = 0; // get road id for new road connection
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // roadID has been entered
                    {
                        newRoadID = scanner.nextInt();
                        isValid = true;
                    }
                    else // road name has been entered
                    {
                        String getRoadName = scanner.next();
                        try
                        {
                            newRoadID = (int) database.findAttributes("Road", new String[]{"RoadID"}, new String[]{"RoadName"}, new Object[]{getRoadName}).get(0)[0];
                            isValid = true;
                            // finds roadID from inputted road name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid road inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the new distance driven on the road: ");
                int distanceDriven = 0;

                // get the length of the road
                int maxDistance = (int) database.findAttributes("Road", new String[]{"RoadLength"}, new String[]{"RoadID"}, new Object[]{roadID}).get(0)[0];

                isValid = false;
                while(!isValid)
                {
                    distanceDriven = scanner.nextInt();

                    if(distanceDriven < maxDistance) // check that distance driven is shorter than the length of the road
                    {
                        isValid = true;
                    }
                    else
                    {
                        System.out.println("Distance is longer than the road length, please re-enter distance: ");
                    }
                }

                RoadConnections roadConnections = new RoadConnections(newCityConnectionID, newRoadID, distanceDriven); // create wrapper object for new road connections

                try // attempt to update road connection
                {
                    database.update("RoadConnections", roadConnections.getAttributes(), roadConnections.getAttributeValues(), new String[]{"CityConnectionID", "RoadID"}, new Object[]{cityConnectionID, roadID});
                }
                catch(Exception exception) // failed to update road connection
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                modifyMapMenu();
                break;

            case 4:
                // modify a city
                System.out.println("-Modify a City-");

                System.out.println("Please enter the cityID or the name of the city to update: ");
                int cityID = 0; // get city id of city to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // cityID has been entered
                    {
                        cityID = scanner.nextInt();
                        isValid = true;
                    }
                    else // city name has been entered
                    {
                        String cityName = scanner.next();
                        try
                        {
                            cityID = (int) database.findAttributes("City", new String[]{"CityID"}, new String[]{"CityName"}, new Object[]{cityName}).get(0)[0];
                            isValid = true;
                            // finds cityID from inputted city name
                        }
                        catch (Exception exception) // invalid city entered
                        {
                            System.out.println("Invalid city name inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the new countryID or the name of the country of the city you are updating: ");
                int countryID = 0; // get the countryID
                isValid = false;

                while(!isValid) // loop until valid information is entered entered
                {
                    if(scanner.hasNextInt()) // countryID has been entered
                    {
                        countryID = scanner.nextInt();
                        isValid = true;
                    }
                    else // country name has been entered
                    {
                        String countryName = scanner.next();
                        try
                        {
                            countryID = (int) database.findAttributes("Country", new String[]{"CountryID"}, new String[]{"CountryName"}, new Object[]{countryName}).get(0)[0];
                            isValid = true;
                            // finds countryID from inputted city name
                        }
                        catch (Exception exception) // invalid country entered
                        {
                            System.out.println("Invalid country inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the new name of the city you are updating: ");
                String cityName = scanner.next();

                City updatedCity = new City(cityID, countryID, cityName); // create wrapper object for new city

                try // attempt to update city
                {
                    database.update("City", updatedCity.getAttributes(), updatedCity.getAttributeValues(), new String[]{"CityID"}, new Object[]{cityID});
                }
                catch(Exception exception) // failed to update city
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                modifyMapMenu();
                break;

            case 5:
                // modify a country
                System.out.println("Modify a Country-");

                System.out.println("Please enter the country id or the country name of the country you want to update: ");
                countryID = 0; // get country id of country to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // countryID has been entered
                    {
                        countryID = scanner.nextInt();
                        isValid = true;
                    }
                    else // country name has been entered
                    {
                        String countryName = scanner.next();
                        try
                        {
                            countryID = (int) database.findAttributes("Country", new String[]{"CountryID"}, new String[]{"CountryName"}, new Object[]{countryName}).get(0)[0];
                            isValid = true;
                            // finds countryID from inputted city name
                        }
                        catch (Exception exception) // invalid country entered
                        {
                            System.out.println("Invalid city name inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the new name of the country being updated: ");
                String countryName = scanner.next();

                Country updatedCountry = new Country(countryID, countryName); // create wrapper object for new country

                try
                {
                    database.update("Country", updatedCountry.getAttributes(), updatedCountry.getAttributeValues(), new String[]{"CountryID"}, new Object[]{countryID});
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                modifyMapMenu();
                break;

            case 6:
                // back to main menu
                updateMapMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                modifyMapMenu();
                break;
        }
    }

    private void viewMapMenu()
    {
        // modify something from the map (roads, cities, ect.) (admins only)
        System.out.println("-Modify to map-");
        System.out.println("1) Roads");
        System.out.println("2) City Connections");
        System.out.println("3) Road Connections");
        System.out.println("4) Cities");
        System.out.println("5) Countries");
        System.out.println("6) Back to map menu");

        switch(scanner.nextInt())
        {
            case 1:
                // view all roads
                System.out.println("-View All Roads-");

                ArrayList<Object[]> allRoads = database.findAttributes("Road", new String[]{"RoadID", "RoadLength", "SpeedLimit", "RoadName"}, new String[]{"RoadID !"}, new Object[]{-1});
                // get all roads for outputting. Where is set to something that will be true in all roads so all are selected.

                int[] columnWidths = new int[]{11, 16, 16, 14}; // stores column widths for each header for outputting road information
                // 5 gap between each header (some are bigger as the data is larger)

                String format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2]
                        + "s%-" + columnWidths[3] +"s%n"; // column offset formats

                System.out.printf(format, "RoadID", "Road Length", "Speed Limit", "Road Name"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line


                for(int i = 0; i < allRoads.size(); i++) // output all information
                {
                    System.out.printf(format, allRoads.get(i)[0], allRoads.get(i)[1], allRoads.get(i)[2], allRoads.get(i)[3]);
                    // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                viewMapMenu();
                break;

            case 2:
                // view all city connections
                System.out.println("-View All City Connections-");

                ArrayList<Object[]> allCityConnections = database.findAttributes("CityConnections", new String[]{"CityConnectionID", "StartCity", "EndCity"}, new String[]{"CityConnectionID !"}, new Object[]{-1});
                // get all city connections for outputting. Where is set to something that will be true in all city connections so all are selected.

                columnWidths = new int[]{21, 15, 14}; // stores column widths for each header for outputting city connection information
                // 5 gap between each header (some are bigger as the data is larger)

                format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2] +"s%n"; // column offset formats

                System.out.printf(format, "CityConnectionID", "Start City", "End City"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line


                for(int i = 0; i < allCityConnections.size(); i++) // output all information
                {
                    System.out.printf(format, allCityConnections.get(i)[0], allCityConnections.get(i)[1], allCityConnections.get(i)[2]);
                    // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                viewMapMenu();
                break;

            case 3:
                // view all road connections
                System.out.println("-View All Road Connections-");

                ArrayList<Object[]> allRoadConnections = database.findAttributes("RoadConnections", new String[]{"CityConnectionID", "RoadID", "DistanceDrivenOnRoad"}, new String[]{"CityConnectionID !"}, new Object[]{-1});
                // get all road connections for outputting. Where is set to something that will be true in all road connections so all are selected.

                columnWidths = new int[]{21, 11, 28}; // stores column widths for each header for outputting attended road connection information
                // 5 gap between each header (some are bigger as the data is larger)

                format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2] +"s%n"; // column offset formats

                System.out.printf(format, "CityConnectionID", "RoadID", "Distance Driven on Road"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line


                for(int i = 0; i < allRoadConnections.size(); i++) // output all information
                {
                    System.out.printf(format, allRoadConnections.get(i)[0], allRoadConnections.get(i)[1], allRoadConnections.get(i)[2]);
                    // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                viewMapMenu();
                break;

            case 4:
                // view all cities
                System.out.println("-View All Cities-");

                ArrayList<Object[]> allCities = database.findAttributes("City", new String[]{"CityID", "CountryID", "CityName"}, new String[]{"CityID !"}, new Object[]{-1});
                // get all cities for outputting. Where is set to something that will be true in all cities so all are selected.

                columnWidths = new int[]{11, 14, 14}; // stores column widths for each header for outputting attended city information
                // 5 gap between each header (some are bigger as the data is larger)

                format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] + "s%-" + columnWidths[2] +"s%n"; // column offset formats

                System.out.printf(format, "CityID", "CountryID", "City Name"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line


                for(int i = 0; i < allCities.size(); i++) // output all information
                {
                    System.out.printf(format, allCities.get(i)[0], allCities.get(i)[1], allCities.get(i)[2]);
                    // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                viewMapMenu();
                break;

            case 5:
                // view all countries
                System.out.println("-View All Countries-");

                ArrayList<Object[]> allCountries = database.findAttributes("Country", new String[]{"CountryID", "CountryName"}, new String[]{"CountryID !"}, new Object[]{-1});
                // get all countries for outputting. Where is set to something that will be true in all countries so all are selected.

                columnWidths = new int[]{14, 17}; // stores column widths for each header for outputting attended country information
                // 5 gap between each header (some are bigger as the data is larger)

                format = "%-" + columnWidths[0] + "s%-" + columnWidths[1] +"s%n"; // column offset formats

                System.out.printf(format, "CountryID", "Country Name"); // output column headers

                for(int i = 0; i < columnWidths.length; i++) // output header lines
                {
                    for(int j = 0; j < columnWidths[i]; j++)
                    {
                        System.out.print("_");
                    }
                }
                System.out.println(); // new line


                for(int i = 0; i < allCountries.size(); i++) // output all information
                {
                    System.out.printf(format, allCountries.get(i)[0], allCountries.get(i)[1]);
                    // output data in column format
                }

                System.out.println("Press anything and press enter to go back:");
                scanner.next();

                viewMapMenu();
                break;

            case 6:
                // back to main menu
                updateMapMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                modifyMapMenu();
                break;
        }
    }

    private void usersMenu()
    {
        // add, modify and delete users
        System.out.println("-Users Menu-");
        System.out.println("1) Add new user");
        System.out.println("2) Modify existing user");
        System.out.println("3) Delete user");
        System.out.println("4) Back to main menu");

        switch(scanner.nextInt())
        {
            case 1:
                // add new user
                System.out.println("-Add User-");

                System.out.println("Please enter the username of the new user: ");
                String username = scanner.next();

                System.out.println("Please enter the password for the new user: ");
                String passwordUnhashed = scanner.next();

                int maxUserID = 0;
                try // attempt to get the max userID
                {
                    maxUserID = (int) database.findAttributes("User", new String[]{"MAX(UserID)"}, new String[]{"UserID !"}, new Object[]{-1}).get(0)[0];
                    // gets the max userID. The where is set to something that is always false
                }
                catch (Exception exception) // failed to get the max userID
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                String password = hashPassword(passwordUnhashed, maxUserID + 1); // attempt to hash password, new userID should be 1 higher than the maxID because of autoincrement

                System.out.println("Please enter 'admin' if the new user is an admin or 'member' if the new user is a member: ");
                String userAccessLevel = null;
                boolean isValid = false;

                while(!isValid) // loop until valid userAccessLevel is entered
                {
                    userAccessLevel = scanner.next();

                    if(!Objects.equals(userAccessLevel, "member") && !Objects.equals(userAccessLevel, "admin")) // invalid userAccessLevel entered
                    {
                        System.out.println("Invalid user access level entered, please enter 'admin' if the new user is an admin or 'member' if the new user is a member");
                    }
                    else
                    {
                        isValid = true;
                    }
                }

                User newUser = new User(-1, userAccessLevel, username, password); // create user wrapper object
                try // attempt to add new user
                {
                    database.insertInto("User", newUser.getAttributes(), newUser.getAttributeValues());
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                usersMenu();
                break;

            case 2:
                // modify existing user
                System.out.println("-Modify User-");

                System.out.println("Please enter the username or userID of the user you are trying to update: ");
                int userID = -1; // get userID of user to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // userID has been entered
                    {
                        userID = scanner.nextInt();
                        isValid = true;
                    }
                    else // username has been entered
                    {
                        username = scanner.next();
                        try
                        {
                            userID = (int) database.findAttributes("User", new String[]{"UserID"}, new String[]{"Username"}, new Object[]{username}).get(0)[0];
                            isValid = true;
                            // finds userID from inputted username
                        }
                        catch (Exception exception) // invalid username entered
                        {
                            System.out.println("Invalid username inputted, please try again: ");
                        }
                    }
                }

                System.out.println("Please enter the username of the user you are updating: ");
                username = scanner.next();

                System.out.println("Please enter the password for the user you are updating: ");
                passwordUnhashed = scanner.next();
                password = hashPassword(passwordUnhashed, userID); // attempt to hash password

                System.out.println("Please enter 'admin' if the new user is an admin or 'member' if the user you are updating is a member: ");
                userAccessLevel = null;
                isValid = false;

                while(!isValid) // loop until valid userAccessLevel is entered
                {
                    userAccessLevel = scanner.next();

                    if(!Objects.equals(userAccessLevel, "member") && !Objects.equals(userAccessLevel, "admin")) // invalid userAccessLevel entered
                    {
                        System.out.println("Invalid user access level entered, please enter 'admin' if the new user is an admin or 'member' if the new user is a member");
                    }
                    else
                    {
                        isValid = true;
                    }
                }

                User user = new User(userID, userAccessLevel, username, password); // create user wrapper object
                try // attempt to update user
                {
                    database.update("User", user.getAttributes(), user.getAttributeValues(), new String[]{"UserID"}, new Object[]{userID});
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }
                usersMenu();
                break;

            case 3:
                // delete user
                System.out.println("-Delete User-");

                System.out.println("Please enter the username or userID of the user you are trying to delete: ");
                userID = -1; // get userID of user to remove
                isValid = false;

                while (!isValid) // loop until valid information is entered entered
                {
                    if (scanner.hasNextInt()) // userID has been entered
                    {
                        userID = scanner.nextInt();
                        isValid = true;
                    }
                    else // username has been entered
                    {
                        username = scanner.next();
                        try
                        {
                            userID = (int) database.findAttributes("User", new String[]{"UserID"}, new String[]{"Username"}, new Object[]{username}).get(0)[0];
                            isValid = true;
                            // finds userID from inputted username
                        }
                        catch (Exception exception) // invalid username entered
                        {
                            System.out.println("Invalid username inputted, please try again: ");
                        }
                    }
                }

                try
                {
                    database.deleteFrom("User", new String[]{"UserID"}, new Object[]{userID});
                }
                catch (Exception exception)
                {
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                }

                usersMenu();
                break;

            case 4:
                // back to main menu
                mainMenu();
                break;

            default:
                // invalid choice
                System.out.println("Invalid choice");
                usersMenu();
                break;
        }
    }
}
