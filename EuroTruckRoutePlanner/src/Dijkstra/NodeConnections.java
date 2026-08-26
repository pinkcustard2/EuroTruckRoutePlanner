package Dijkstra;

import java.util.ArrayList;

public class NodeConnections<S>
{
    // inner hash table for graph - stores the connections between nodes (arcs)
    private int maxSize;
    private Arc<S>[] nodeConnections;
    private int tempDistance = 0; // temp distance for dijkstra
    private ArrayList<Integer> tempPath = new ArrayList<>(); // temp path for dijkstra - stores city id for each connection

    public NodeConnections(int maxSize)
    {
        this.maxSize = maxSize;
        nodeConnections = new Arc[maxSize];
    }

    public void add(int key, S value)
    {
        // adds a node to the graph
        // throws an error if the hash table is full
        int index = key % maxSize; // calculate index the arc should be hashed to

        if(nodeConnections[index] == null || nodeConnections[index].getValue() == null) // check collision
        {
            nodeConnections[index] = new Arc<S>(key, value);// no collision so create new arc at index
        }
        else // collision has occurred
        {
            boolean collision = true;

            for(int i = 1; i < maxSize; i++) // loop through hash table until empty index is found or hash table has been fully explored
            {
                if(nodeConnections[(index + i) % maxSize] == null || nodeConnections[(index + i) % maxSize].getValue() == null) // empty index
                {
                    nodeConnections[(index + i) % maxSize] = new Arc<S>(key, value); // create new arc at new index as the new index is empty
                    collision = false;
                    break;
                }
                else if(nodeConnections[(index + i) % maxSize].getKey() == key) // arc has already been added
                {
                    collision = false;
                    break;
                }
            }

            if(collision) // hash table has been looped through and no empty indexes have been found (hash table is full)
            {
                throw new UnsupportedOperationException("No empty addresses.");
            }
        }
    }


    public S item(int key)
    {
        // gets the value of the arc (the node the arc is travelling to)
        // should return null if there is no node with the inputted key
        int index = key % maxSize; // calculate the index the arc should be located at
        S value = null;

        for(int i = 0; i < maxSize; i++) // loop through hash table until arc is found or hash table has been fully explored
        {
            if(nodeConnections[(index + i) % maxSize] == null) // an empty index has been found so the arc is not present
            {
                break;
            }
            else if(nodeConnections[(index + i) % maxSize].getKey() == key) // arc found
            {
                value = nodeConnections[(index + i) % maxSize].getValue(); // set value to the value of the arc (node arc is travelling to)
                break;
            }
        }

        return value;
    }

    public int length()
    {
        // returns the amount of arcs in the hash table
        int length = 0;

        for(int i = 0; i < maxSize; i++) // loop through hash table
        {
            if(nodeConnections[i] != null) // arc is present so increment length
            {
                length++;
            }
        }

        return length;
    }

    public int index(int key)
    {
        // gets the index in the hash table that the node is stored at
        // should return -1 if there is no node in the hash table with that index
        int index = key % maxSize; // calculate the index the node should be located at

        for(int i = 0; i < maxSize; i++) // loop through hash table until node is found or hash table has been fully explored
        {
            if(nodeConnections[(index) % maxSize] == null) // an empty index has been found so the node is not present
            {
                index = -1;
                break;
            }
            else if(nodeConnections[(index) % maxSize].getKey() == key) // node found
            {
                break;
            }
            index++;
        }

        if(nodeConnections[index % maxSize].getKey() != key) // check if node has been found
        {
            index = -1;
        }

        return index;
    }

    public int getTempDistance()
    {
        return tempDistance;
    }

    public void setTempDistance(int tempDistance)
    {
        this.tempDistance = tempDistance;
    }

    public ArrayList<Integer> getTempPath()
    {
        return tempPath;
    }

    public void setTempPath(ArrayList<Integer> tempPath)
    {
        this.tempPath = tempPath;
    }


    public boolean isEmpty()
    {
        // return true if the nodeConnections hash table is empty
        return length() == 0;
    }

    public Arc<S>[] getNodeConnections()
    {
        return nodeConnections;
    }

    public int getMaxSize()
    {
        return maxSize;
    }
}
