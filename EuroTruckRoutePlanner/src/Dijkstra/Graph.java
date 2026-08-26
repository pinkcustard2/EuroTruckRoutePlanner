package Dijkstra;

public class Graph<S>
{
    // class for the graph abstract data type - outer hash table of graph
    private int maxSize;
    private Node<S>[] hashTable;

    public Graph(int maxSize)
    {
        this.maxSize = maxSize;
        hashTable = new Node[maxSize];
    }

    public void add(int key, S value)
    {
        // adds a node to the graph
        // throws an error if the hash table is full
        int index = key % maxSize; // calculate index the node should be hashed to

        if(hashTable[index] == null || hashTable[index].getValue() == null) // check collision
        {
            hashTable[index] = new Node<S>(key, value); // no collision so create new node at index
        }
        else // collision has occurred
        {
            boolean collision = true;

            for(int i = 1; i < maxSize; i++) // loop through hash table until empty index is found or hash table has been fully explored
            {
                if(hashTable[(index + i) % maxSize] == null || hashTable[(index + i) % maxSize].getValue() == null) // empty index
                {
                    hashTable[(index + i) % maxSize] = new Node<S>(key, value); // create new node at new index as new index is empty
                    collision = false;
                    break;
                }
                else if(hashTable[(index + i) % maxSize].getKey() == key) // node has already been added
                {
                    break;
                }
            }

            if(collision) // hash table has been looped through and no empty indexes have been found (hash table is full)
            {
                throw new UnsupportedOperationException("No empty addresses.");
            }
        }
    }

    public void addArc (int startNodeKey, int weight, int endNodeKey)
    {
        // adds an arc to the graph
        S startNodeConnections = item(startNodeKey); // gets node connections for the start node
        S endNodeConnections = item(endNodeKey); // gets node connections for the end node

        if(endNodeConnections == null) // node travelling to does not exist, add it
        {
            endNodeConnections = (S) new NodeConnections(maxSize);
            this.add(endNodeKey, endNodeConnections);
        }


        if(startNodeConnections == null) // current node does not exist, add it
        {
            startNodeConnections = (S) new NodeConnections(maxSize);
            this.add(startNodeKey, startNodeConnections);
        }

        NodeConnections nodeConnections = (NodeConnections<S>) startNodeConnections;
        nodeConnections.add(weight, endNodeKey); // create arc
    }


    public S item(int key)
    {
        // gets the value of the node (the nodeConnections for the node)
        // should return null if there is no node with the inputted key
        int index = key % maxSize; // calculate the index the node should be located at
        S value = null;

        for(int i = 0; i < maxSize; i++) // loop through hash table until node is found or hash table has been fully explored
        {
            if(hashTable[(index + i) % maxSize] == null) // an empty index has been found so the node is not present
            {
                break;
            }
            else if(hashTable[(index + i) % maxSize].getKey() == key) // node found
            {
                value = hashTable[(index + i) % maxSize].getValue(); // set value to the value of the node (the nodeConnections for that node)
                break;
            }
        }

        return value;
    }

    public int index(int key)
    {
        // gets the index in the hash table that the node is stored at
        // should return -1 if there is no node in the hash table with that index
        int index = key % maxSize; // calculate the index the node should be located at

        for(int i = 0; i < maxSize; i++) // loop through hash table until node is found or hash table has been fully explored
        {
            if(hashTable[(index) % maxSize] == null) // an empty index has been found so the node is not present
            {
                index = -1;
                break;
            }
            else if(hashTable[(index) % maxSize].getKey() == key) // node found
            {
                break;
            }
            index++;
        }

        if(hashTable[index % maxSize].getKey() != key) // check if node has been found
        {
            index = -1;
        }

        return index;
    }

    public int length()
    {
        // returns the amount of nodes in the hash table
        int length = 0;

        for(int i = 0; i < maxSize; i++) // loop through hash table
        {
            if(hashTable[i] != null) // node is present so increment length
            {
                length++;
            }
        }

        return length;
    }

    public int getMaxSize()
    {
        return maxSize;
    }
}

