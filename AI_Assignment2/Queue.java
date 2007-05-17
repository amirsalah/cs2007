public class Queue
{
    /* member class not found */
    class Node {}


    public Queue()
    {
        fHead = null;
        fEnd = null;
        fSpares = null;
    }

    public Object head()
    {
        return fHead.fData;
    }

    public void tail()
    {
        Node node = fHead;
        fHead = fHead.fNext;
        node.fData = null;
        node.fNext = null;
        node.fNext = fSpares;
        fSpares = node;
        if(fHead == null)
            fEnd = null;
    }

    public void add(Object obj)
    {
        Node node = null;
        if(fSpares != null)
        {
            node = fSpares;
            fSpares = fSpares.fNext;
            node.fNext = null;
            node.fData = obj;
        } else
        {
            node = new Node(obj);
        }
        if(fHead == null)
        {
            fHead = node;
            fEnd = node;
        } else
        {
            Node node1 = fEnd;
            node1.fNext = node;
            fEnd = node;
        }
    }

    public boolean isEmpty()
    {
        return fHead == null;
    }

    private Node fHead;
    private Node fEnd;
    private Node fSpares;
}