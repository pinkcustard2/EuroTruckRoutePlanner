package Dijkstra;

public class Node<T>
{
    private int key;
    private T node;

    public Node(int key, T node)
    {
        this.key = key;
        this.node = node;
    }

    public int getKey()
    {
        return key;
    }

    public T getValue()
    {
        return node;
    }

    public void setValue(T node)
    {
        this.node = node;
    }

    public void setKey(int key)
    {
        this.key = key;
    }
}
