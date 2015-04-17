import java.util.Iterator;

public interface Dict<K,V> {

    public boolean isEmpty();
      /*
       *  Returns true *iff* there are zero entries in the dictionary.
       */

    public boolean add(K key, V value);
      /*
       *  Add a new, unique entry to the dictionary.
       *  Returns true to indicate successful insertion (or false, if
       *  the given key/value pair was already present and thus rejected).
       */
    public boolean contains(K key);
      /*
       *  Returns true *iff* there is an entry with the given key.
       */

    public V get(K key);
      /*
       *  Retrieve the first value associated with the given key.
       *  Returns null if the given key was not found.
       */

    public Object[] getAll(K key);
      /*
       *  Retrieve all values associated with the given key.
       *  Returns an empty array if the key was not found.
       */

    public V remove(K key);
      /*
       *  Remove the first value associated with the given key.
       *  Returns the removed value (or null, if the key was not found).
       */

    public Object[] removeAll(K key);
      /*
       *  Remove the given key and all associated values.
       *  Returns an array of all removed values (or an empty array, if
       *  the key was not found).
       */

    public Iterator<K> keyIterator();
      /*
       *  Returns an iterator for keys in the dictionary.
       *  The iterator's hasNext() and next() methods will respectively
       *  return false and null if the dictionary is empty.
       */

    public Iterator<V> valueIterator(K key);
      /*
       *  Returns an iterator for values associated with the given key.
       *  The iterator's hasNext() and next() methods will respectively
       *  return false and null if the given key was not found.
       */

} //end Dict
