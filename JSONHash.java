import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * JSON hashes/objects.
 */
public class JSONHash {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The starting size of all new JSONHash objects.
   */
  final int INITIAL_SIZE = 5;
  final double LOAD_FACTOR = 0.5;

  /**
   * The underlying hash table data.
   */
  Object[] buckets;

  /**
   * The current amount of key/value pairs in the hash table.
   */
  int size;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  public JSONHash() {
    this.buckets = new Object[INITIAL_SIZE];
    this.size = 0;
  }

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return "";          // STUB
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    return true;        // STUB
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  @SuppressWarnings("unchecked")
  public int hashCode() {
    // from osera chap 12: hashing
    int result = 831; // random non-zero number.
    
    // sum up each field hashcode
    result = 31 * result + ((ArrayList<KVPair<JSONString, JSONValue>>[]) this.buckets).hashCode();
    result = 31 * result + this.size;

    return result;
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
                        // STUB
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString,JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key.
   */
  @SuppressWarnings("unchecked")
  public JSONValue get(JSONString key) {
    int potentialBucket = find(key);
    ArrayList<KVPair<JSONString, JSONValue>> castedBucket = (ArrayList<KVPair<JSONString, JSONValue>>) this.buckets[potentialBucket];
    for (KVPair<JSONString, JSONValue> pair : castedBucket) {
      if (pair.key().equals(key)) return pair.value();
    }
    // if we haven't found it then it's not here
    throw new IndexOutOfBoundsException();
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString,JSONValue>> iterator() {
    return null;        // STUB
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  @SuppressWarnings("unchecked")
  public void set(JSONString key, JSONValue value) {
    // if too many pigeons for pigeonholes, make more pigeonholes.
    if (this.size() > (this.buckets.length * LOAD_FACTOR)) this.expand();

    int bucketIndex = find(key);
    ArrayList<KVPair<JSONString, JSONValue>> castedBucket = (ArrayList<KVPair<JSONString, JSONValue>>) this.buckets[bucketIndex];
    if (castedBucket == null) {
      this.buckets[bucketIndex] = new ArrayList<>();
    }
    KVPair<JSONString,JSONValue> toAdd = new KVPair<JSONString,JSONValue>(key, value);
    for (int i = 0; i < castedBucket.size(); i++) {
      if (castedBucket.get(i).key().equals(key)) {
        castedBucket.set(i, toAdd);
        return;
      }
    }
    // didn't find the key in this bucket, must add a new one.
    castedBucket.add(toAdd);
    this.size++;
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

  // +---------+---------------------------------------------------------
  // | Helpers |
  // +---------+

  @SuppressWarnings("unchecked")
  void expand() {
    int newSize = 2 * this.buckets.length;
    Object[] oldBuckets = this.buckets;
    this.buckets = new Object[newSize];
    for (Object bucket : oldBuckets) {
      if (bucket == null) return;
      // bucket is otherwise a non empty bucket.
      // cast the bucket to what it is (an arraylist of kvpairs)
      ArrayList<KVPair<JSONString, JSONValue>> castedBucket = (ArrayList<KVPair<JSONString, JSONValue>>) bucket;
      for (KVPair<JSONString, JSONValue> pair : castedBucket) {
        if (pair.key() == null) return;
        // put old pairs into new spots
        this.buckets[ find(pair.key()) ] = pair;
      }
    }
  }

  /**
   * Find the index of the bucket that may contain the given key.
   */
  int find(JSONString key){
    return Math.abs(key.hashCode()) % this.buckets.length;
  }

} // class JSONHash
