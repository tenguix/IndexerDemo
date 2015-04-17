import java.util.Iterator;

public class Chain<T> {

      /* Data fields */

    private Node head;
    private int count;

      /* Constructor */

    public Chain() {
        this.head = new Node(null, null);
        this.count = 0;
    } //end ctor

      /* Private inner classes */

    private class Node {

        public T data;
        public Node next;

        public Node(T data, Node next) {
            this.data = data;
            this.next = next;
        } //end ctor

    } //end Node

    private class ChainIterator implements Iterator<T> {

          /* Data fields */

        private Node cur;

          /* Constructor */

        private ChainIterator() {
            this.cur = head;
        } //end ctor

          /* Public methods */

        public boolean hasNext() {
            return cur.next != null;
        } //end hasNext

        public T next() {
            return cur.next == null ? null : (cur = cur.next).data;
        } //end next

        public void remove() {
            throw new UnsupportedOperationException
                    ("This iterator does not support the 'remove' operation.");
        } //end remove

    } //end ChainIterator

      /* Public methods */

    public boolean isEmpty() {
        return count == 0;
    } //end isEmpty

    public boolean add(T data) {
        Node cur = head;
        while(cur.next != null) {
            if(cur.next.data.equals(data))
                return false; //We don't store duplicates
            else
                cur = cur.next;
        } //end while
        cur.next = new Node(data, cur.next);
        ++count;
        return true;
    } //end add

    public boolean contains(T data) {
        Node cur = head;
        while(cur.next != null) {
            if(cur.next.data.equals(data))
                return true;
            cur = cur.next;
        } //end while
        return false;
    } //end contains

    public T get() { //Only get the first item
        return head.next == null ? null : head.next.data;
    } //end get

    public T remove() { //Only remove first item
        if(head.next == null)
            return null;
        T retval = head.next.data;
        head.next = head.next.next;
        --count;
        return retval;
    } //end remove

    public boolean remove(T data) { //Remove a specific item
        for(Node cur = head; cur.next != null; cur = cur.next) {
            if(cur.next.data.equals(data)) {
                cur.next = cur.next.next;
                --count;
                return true;
            } //end for:if
        } //end for
        return false;
    } //end remove

    public Iterator<T> iterator() {
        return new ChainIterator();
    } //end chainIterator

    public Object[] toArray() {
        Object[] retval = new Object[count];
        Iterator<T> it = iterator();
        for(int idx = 0; idx < count; ++idx)
            retval[idx] = it.next();
        return retval;
    } //end toArray

    public String toString() {
        if(isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        Iterator<T> it = iterator();
        sb.append(it.next());
        while(it.hasNext())
            sb.append(", ").append(it.next());
        return sb.toString();
    } //end toString

} //end Chain
