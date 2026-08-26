package Dijkstra;

public class Element<S> {
    // an element of the priority queue
    private S value;
    private Element previous;
    private Element next;
    private int priority;
        
    public Element(S value, Element previous, Element next, int priority)
    {
        this.value = value;
        this.previous = previous;
        this.next = next;
        this.priority = priority;
    }

    public S getValue()
    {
        return value;              
    }
   
    public Element getPrevious()
    {
        return previous;                
    }
    
    public void setPrevious(Element value)
    {
        previous = value;                
    }
    
    public Element getNext()
    {
        return next;            
    }    
    
    public void setNext(Element value)
    {
        next = value;                
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }
}

