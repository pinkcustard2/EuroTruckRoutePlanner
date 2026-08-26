package Dijkstra;

public class Arc<T>
{
    // an item representing an arc for the node connections hash table
    private int weight;
    private T node;

    public Arc(int weight, T value)
    {
        this.weight = weight;
        this.node = value;
    }

    public int getKey()
    {
        return weight;
    }

    public T getValue()
    {
        return node;
    }

    public void setKey(T node)
    {
        this.node = node;
    }

    public void setValue(int distance)
    {
        this.weight = distance;
    }
}
