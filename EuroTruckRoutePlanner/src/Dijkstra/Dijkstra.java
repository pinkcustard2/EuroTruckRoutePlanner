package Dijkstra;

import java.util.ArrayList;

public class Dijkstra
{
    private Graph<NodeConnections<Integer>> graph; // graph for dijkstra
    private PriorityQueue<Integer> priorityQueue = new PriorityQueue<Integer>(); // priority queue for dijkstra

    public Dijkstra(Graph<NodeConnections<Integer>> graph)
    {
        this.graph = graph;
    }

    private NodeConnections<Integer> findNode(int key)
    {
        // finds a node with a given key
        NodeConnections<Integer> nodeConnectionsChecked = null;

        for(int i = 0; i < graph.getMaxSize(); i++) // loops through graph
        {
            if(graph.item(i) != null && key == i) // checks if node being checked key is equal to the key argument
            {
                nodeConnectionsChecked = graph.item(i); // node found
                break;
            }
        }

        return nodeConnectionsChecked;
    }

    public void setGraph(Graph<NodeConnections<Integer>> graph) // allow for Dijkstra graph to be reset
    {
        this.graph = graph;
    }

    public int pathLength(int startNode, int endNode)
    {
        // returns shortest path length from start node to end node
        priorityQueue.add(graph.index(startNode), 0); // add start node to priority queue

        for(int i = 0; i < graph.getMaxSize(); i++) // loop through graph
        {
            if(graph.item(i) != null && i != graph.index(startNode)) // add the rest of the nodes to the priority queue
            {
                priorityQueue.add(i, 1000000);
            }
        }

        while(!priorityQueue.isEmpty())
        {
            int currentNode = priorityQueue.pop(); // get the current node with the lowest priority
            NodeConnections<Integer> nodeConnectionsChecked = this.findNode(currentNode); // get the node connections for the node with the lowest priority

            for(int i = 0; i < nodeConnectionsChecked.getMaxSize(); i++) // loop through all arcs for current node
            {
                if(nodeConnectionsChecked.getNodeConnections()[i] != null) // arc found
                {
                    int location = nodeConnectionsChecked.getNodeConnections()[i].getValue(); // get the node the arc is travelling to
                    location = priorityQueue.index(location); // find where in priority queue node the arc is travelling to is

                    if(location != -1) // node arc is travelling to has not yet been fully explored so is still in the priority queue
                    {
                        int arcEndNode = priorityQueue.pop(location); // get the key of the node the arc is travelling to

                        if(graph.item(arcEndNode).getTempDistance() == 0 || graph.item(arcEndNode).getTempDistance() > graph.item(currentNode).getTempDistance() + nodeConnectionsChecked.getNodeConnections()[i].getKey())
                        {
                            // either node has not yet been visited or a shorter path to the node has been found than the previous shortest path
                            // so the new shorter distance should be set to the temporary distance to that node from the start node
                            graph.item(arcEndNode).setTempDistance(graph.item(currentNode).getTempDistance() + nodeConnectionsChecked.getNodeConnections()[i].getKey());
                        }
                        priorityQueue.add(arcEndNode, graph.item(arcEndNode).getTempDistance()); // re-add the new node with the new distance as the priority
                    }
                }
            }
        }

        return graph.item(endNode).getTempDistance(); // return the final distance of the end node
    }

    public ArrayList<Integer> path(int startNode, int endNode)
    {
        // returns nodes travelled for the shortest path from the start node to the end node
        priorityQueue.add(graph.index(startNode), 0); // add start node to priority queue

        for(int i = 0; i < graph.getMaxSize(); i++) // loop through graph
        {
            if(graph.item(i) != null && i != graph.index(startNode)) // add the rest of the nodes to the priority queue
            {
                priorityQueue.add(i, 1000000);
            }
        }

        while(!priorityQueue.isEmpty())
        {
            int currentNode = priorityQueue.pop(); // get the current node with the lowest priority
            NodeConnections<Integer> nodeConnectionsChecked = this.findNode(currentNode); // get the node connections for the node with the lowest priority

            for(int i = 0; i < graph.getMaxSize(); i++) // loop through all arcs for current node
            {
                if(nodeConnectionsChecked.getNodeConnections()[i] != null) // arc found
                {
                    int location = nodeConnectionsChecked.getNodeConnections()[i].getValue(); // get the node the arc is travelling to
                    location = priorityQueue.index(location); // find where in priority queue node the arc is travelling to is

                    if(location != -1) // node arc is travelling to has not yet been fully explored so is still in the priority queue
                    {
                        int arcEndNode = priorityQueue.pop(location); // get the key of the node the arc is travelling to

                        if(graph.item(arcEndNode).getTempDistance() == 0 || graph.item(arcEndNode).getTempDistance() > graph.item(currentNode).getTempDistance() + nodeConnectionsChecked.getNodeConnections()[i].getKey())
                        {
                            // either node has not yet been visited or a shorter path to the node has been found than the previous shortest path
                            // so the new shorter distance should be set to the temporary distance to that node from the start node and add the arcEnd node to the current path
                            graph.item(arcEndNode).setTempDistance(graph.item(currentNode).getTempDistance() + nodeConnectionsChecked.getNodeConnections()[i].getKey());

                            ArrayList<Integer> tempPath = new ArrayList<>(graph.item(currentNode).getTempPath());
                            tempPath.add(arcEndNode);
                            graph.item(arcEndNode).setTempPath(tempPath);

                        }
                        priorityQueue.add((arcEndNode), graph.item(arcEndNode).getTempDistance()); // re-add the new node with the new distance as the priority
                    }
                }
            }
        }
        ArrayList<Integer> path = new ArrayList<>();
        path.add(startNode);
        path.addAll(graph.item(endNode).getTempPath());

        return path; // return the final path from the start node to the end node
    }
}
