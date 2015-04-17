import java.util.Iterator;

public class DictChain<K,V> implements Dict<K,V> {

      /* Data fields */

    private Node head; //First key in chain
    private int key_count; //Number of unique keys

      /* Constructor */

    public DictChain() {
        this.head = new Node(null, null, null);
        this.key_count = 0;
    } //end ctor

      /* Private inner classes */

    private class Node {

        public K key;
        public Node next; //Points to next key in chain
        public Chain<V> vchain; //Values associated with key

        public Node(K key, V value, Node next) {
            this.key = key;
            this.vchain = new Chain<V>();
            this.next = next;
            vchain.add(value);
        } //end ctor

    } //end Node

    private class KeyIterator implements Iterator<K> {

          /* Data fields */

        private Node cur;

          /* Constructor */

        private KeyIterator(Node cur) {
            this.cur = cur;
        } //end ctor

          /* Public methods */

        public boolean hasNext() {
            return cur.next != null;
        } //end hasNext

        public K next() {
            return cur.next == null ? null : (cur = cur.next).key;
        } //end next

        public void remove() {
            throw new UnsupportedOperationException
                    ("This iterator does not support the 'remove' operation.");
        } //end remove

    } //end KeyIterator

    private Node getNodeBefore(K key) {
        Node cur = head;
        while(cur.next != null) {
           if(cur.next.key.equals(key))
               return cur;
            cur = cur.next;
        } //end while
        return null;
    } //end getNodeBefore

      /* Public methods */

    public boolean isEmpty() {
        return key_count == 0;
    } //end isEmpty

    public boolean add(K key, V value) {
        Node cur = head;
        while(cur.next != null) {
            if(cur.next.key.equals(key)) //Key is not unique
                return cur.next.vchain.add(value); //True *iff* value is unique
            cur = cur.next;
        } //end while
        cur.next = new Node(key, value, cur.next);
        ++key_count;
        return true;
    } //end add

    public boolean contains(K key) {
        return getNodeBefore(key) != null;
    } //end contains

    public V get(K key) {
        Node cur = getNodeBefore(key);
        return cur == null ? null : cur.next.vchain.get();
    } //end get

    public Object[] getAll(K key) {
        Node cur = getNodeBefore(key);
        return cur == null ? new Object[0] : cur.next.vchain.toArray();
    } //end getValues

    public V remove(K key) {
        Node cur = getNodeBefore(key);
        if(cur == null) //Key not found
            return null;
        V retval = cur.next.vchain.remove();
        if(cur.next.vchain.isEmpty()) { //No values left
            cur.next = cur.next.next; //Unlink key from chain
            --key_count;
        } //end if
        return retval;
    } //end remove

    public Object[] removeAll(K key) {
        Node cur = getNodeBefore(key);
        if(cur == null) //Key not found
            return new Object[0];
        Object[] retval = cur.next.vchain.toArray();
        cur.next = cur.next.next;
        --key_count;
        return retval;
    } //end removeAll

    public Iterator<K> keyIterator() {
        return new KeyIterator(head);
    } //end iterator

    public Iterator<V> valueIterator(K key) {
        Node cur = getNodeBefore(key);
        return (cur == null ? head : cur.next).vchain.iterator();
    } //end valueIterator

    public Object[] getKeys() { //Get an array of all keys in chain
        Object[] retval = new Object[key_count];
        int idx = 0;
        for(Node cur = head; cur.next != null; cur = cur.next)
            retval[idx++] = cur.next.key;
        return retval;
    } //end getKeys

} //end DictChain
