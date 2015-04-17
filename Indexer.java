import java.util.Iterator;

public class Indexer<K,V> implements Dict<K,V> {

      /* Data fields */

    private Object[] table; //The hash table: An array of chains
    private int entry_count; //Total number of entries in dictionary
    private float load_factor; //Max ratio of entries to hash buckets
    private int threshold; //Max # of entries as determined by load factor

      /* Constructors */

    public Indexer(int size, float load_factor) {
        if(size <= 0)
            throw new IllegalArgumentException
                    ("Table size must be greater than 0.");
        if(load_factor <= 0)
            throw new IllegalArgumentException
                    ("Load factor must be greater than 0.");
        this.load_factor = load_factor;
        this.entry_count = 0;
        init(size);
    } //end ctor

    public Indexer(int size) {
          /* We use a very high load factor
             because the chains are also dictionaries. */
        this(size, 5.0F);
    } //end ctor

    public Indexer() {
        this(1<<7, 5.0F);
    } //end ctor

      /* Inner classes */

    private class KeyIterator implements Iterator<K> {

          /* Data fields */

        private int idx; //Hash table (array) index
        private DictChain<K,V> chain; //Current chain
        private Iterator<K> it; //Iterator for current chain
        private K peek; //Return value for next()

          /* Constructor */

        @SuppressWarnings("unchecked")
        public KeyIterator() {
            this.idx = 0;
            this.chain = (DictChain<K,V>) table[0];
            this.it = chain.keyIterator();
            this.peek = fetch();
        } //end ctor

          /* Private method */

        @SuppressWarnings("unchecked")
        private K fetch() {
              /* We have to fetch the next key in advance. */
            while(!it.hasNext() && ++idx < table.length) {
                chain = (DictChain<K,V>) table[idx];
                it = chain.keyIterator();
            } //end while
            return it.next();
        } //end peek

          /* Public methods */

        public boolean hasNext() {
            return peek != null;
        } //end hasNext

        public K next() {
            K retval = peek;
            peek = fetch();
            return retval;
        } //end next

        public void remove() {
            throw new UnsupportedOperationException
                    ("This iterator does not support the 'remove' function.");
        } //end remove

    } //end KeyIterator

    private void init(int size) {
          /* Increase the array size to the next-smallest prime number,
             as collisions are less likely to occur in a hash table with
             a prime number of buckets. */
        table = new Object[nextPrime(size)];
        threshold = (int)(load_factor * table.length);
        for(int i = 0; i < table.length; ++i)
            table[i] = new DictChain<K,V>();
    } //end initFields

    private int nextPrime(int n) {
        if(n > 3) { //One, two, and three are all prime.
            OUTER:
            for(n |= 1 ;; n += 2) { //Skip over even numbers
                for(int i = 3; i*i <= n; i += 2) //Check odd factors up to sqrt
                    if(n % i == 0) //Number is not prime
                        continue OUTER; //Continue outer loop
                break; //Prime number found
            } //end for
        } //end if
        return n;
    } //end nextPrime

    private int hashIndex(K key) {
        int retval = key.hashCode() % table.length;
        if(retval < 0)
            retval += table.length;
        return retval;
    } //end hashIndex

    @SuppressWarnings("unchecked")
    private DictChain<K,V> getChain(K key) {
        return (DictChain<K,V>) table[hashIndex(key)];
    } //end getChain

    @SuppressWarnings("unchecked")
    private void rehash() {
        Object[] old_ht = table;
        init(table.length * 2); //Double the table size
        for(int i = 0; i < old_ht.length; ++i) {
            DictChain<K,V> chain = (DictChain<K,V>) old_ht[i];
            if(chain.isEmpty())
                continue;
            Iterator<K> kit = chain.keyIterator();
            while(kit.hasNext()) {
                K key = kit.next();
                Iterator<V> vit = chain.valueIterator(key);
                while(vit.hasNext()) {
                    getChain(key).add(key, vit.next());
                } //end for:while:while
            } //end for:while
        } //end for
    } //end rehash

      /* Public methods */

    public int getCount() {
        return entry_count;
    } //end getCount

    public boolean isEmpty() {
        return entry_count == 0;
    } //end isEmpty

    public boolean add(K key, V value) {
        boolean retval = getChain(key).add(key, value);
        if(retval && ++entry_count > threshold)
            rehash();
        return retval;
    } //end add

    public boolean contains(K key) {
        return getChain(key).contains(key);
    } //end contains

    public V get(K key) {
        return getChain(key).get(key);
    } //end get

    public Object[] getAll(K key) {
        return getChain(key).getAll(key);
    } //end getAll

    public V remove(K key) {
        V retval = getChain(key).remove(key);
        if(retval != null)
            --entry_count;
        return retval;
    } //end remove

    public Object[] removeAll(K key) {
        Object[] retval = getChain(key).removeAll(key);
        entry_count -= retval.length;
        return retval;
    } //end removeAll

    public Iterator<K> keyIterator() {
        return new KeyIterator();
    } //end keyIterator

    public Iterator<V> valueIterator(K key) {
        return getChain(key).valueIterator(key);
    } //end valueIterator

} //end Indexer
