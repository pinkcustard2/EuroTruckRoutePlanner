package Dijkstra;
import java.util.Objects;


public class PriorityQueue<S>
{
    private Element<S> front; // first element in the priority queue
    private int length = 0;

    public boolean isEmpty()
    {
        // return true if the priority queue is empty
        return length == 0;
    }

    public void add(S value, int priority)
    {
        // add new element into priority queue
        Element<S> element;

        if(!isEmpty()) // check if this will be at the front of the queue or not
        {
            Element<S> current = front;

            // set current to the element that should be before the new element in the queue according to priority
            // (unless new element should be first then current will be front)
            while(current.getNext() != null && current.getNext().getPriority() <= priority)
            {
                current = current.getNext();
            }

            if(current.equals(front) && current.getPriority() > priority) // new element should be first
            {
                element = new Element<S>(value, null, current, priority); // add new element
                current.setPrevious(element); // update the other elements in the queue
                front = element; // new element should be first in the queue so set it to the front of the queue
            }
            else if(current.getNext() == null) // new element should be last
            {
                element = new Element<S>(value, current, null, priority); // add new element
                current.setNext(element); // update other elements in the queue
            }
            else
            {
                element = new Element<S>(value, current, current.getNext(), priority); // add new element
                current.getNext().setPrevious(element); // update other elements in the queue
                current.setNext(element);
            }
        }
        else // no elements in queue
        {
            element = new Element<S>(value, null, null, priority); // add new element
            front = element; // make new element front of the queue as there are no other elements
        }

        length++; // increment length of priority queue
    }

   
    public S pop()
    {
        // remove the 1st element from the queue, reorganise the queue and return the value of the removed element
        // throw an error if the queue is empty
        Element<S> current;

        if(!isEmpty()) // make sure there are elements in the queue to remove
        {
            current = front;

            front = front.getNext(); // removes front element

            if(front != null) // check if there is a 2nd element in the queue
            {
                front.setPrevious(null); // update 2nd element's previous to null as it is now the front of the queue
            }

            length--; // decrement size of queue as element has been removed
        }
        else // queue is empty so throw error
        {
            throw new UnsupportedOperationException("Queue is empty");
        }

        return current.getValue(); // return the value of the removed element
    }
    
    public S pop(int index)
    {
        // remove the element at the inputted index, reorganise the queue and return the value of the removed element
        // should throw an error if the element at the specified index does not exist
        Element<S> current = front;

        if(index < length()) // make sure the element exists
        {
            for(int i = 0; i < index; i++) // set current to the item to remove
            {
                current = current.getNext();
            }

            Element<S> previous = current.getPrevious();
            Element<S> next = current.getNext();

            if(previous != null && next != null) // removed element is in middle of the queue
            {
                // reorganise queue
                previous.setNext(next);
                next.setPrevious(previous);
            }
            else if(previous == null && next != null) // removed element  is first object in the queue
            {
                // reorganise queue
                front = next;
                next.setPrevious(null);
            }
            else if(previous != null) // removed object is the last element in the queue
            {
                // reorganise queue
                previous.setNext(null);
            }
            else // removed object is the only object in the queue
            {
                // reorganise queue
                front = null;
            }

            length--; // decrement size of queue as element has been removed
        }
        else // the index trying to be accessed is bigger than the number of items in the queue so element does not exist so throw error
        {
            throw new IndexOutOfBoundsException("Element does not exist");
        }

        return current.getValue(); // return the value of the removed element
    }

    
    public int index(S value)
    {
        // returns the position of the first occurrence of the value in the linked list
        // returns -1 if no element is found
        Element<S> current = front;
        int index = -1;

        if(current != null)
        {
            index = 0;

            while(!Objects.equals(current.getValue(), value) && current.getNext() != null)
            // search through priority queue until an element with an equal value to the value argument is found
            {
                current = current.getNext();
                index++; // increment index so the index of the element can be found
            }

            if(current.getNext() == null && !Objects.equals(current.getValue(), value)) // no element was found so set index to -1
            {
                index = -1;
            }
        }

        return index;
    }
    
    public int length()
    {
        // returns the number of elements in the priority queue
        return length;
    }
}
