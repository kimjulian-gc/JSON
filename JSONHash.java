import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * JSON hashes/objects.
 */
public class JSONHash implements JSONValue {

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
    String result = "{ ";

    Iterator<KVPair<JSONString, JSONValue>> pairs = this.iterator();
    if (pairs.hasNext())
      result += pairs.next();
    while (pairs.hasNext()) {
      result += ", " + pairs.next();
    }

    result += " }";
    return result;
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    if (!(other instanceof JSONHash))
      return false;
    // other is otherwise a JSONHash
    JSONHash castedOther = (JSONHash) other;
    return Arrays.equals(castedOther.buckets, this.buckets) && castedOther.size == this.size;
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    // from osera chap 12: hashing
    int result = 831; // random non-zero number.

    // sum up each field hashcode
    result = 31 * result + Arrays.deepHashCode(this.buckets);
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
    pen.println(this.toString());
    pen.flush();
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<KVPair<JSONString, JSONValue>> getValue() {
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
    ArrayList<KVPair<JSONString, JSONValue>> castedBucket =
        (ArrayList<KVPair<JSONString, JSONValue>>) this.buckets[potentialBucket];
    for (KVPair<JSONString, JSONValue> pair : castedBucket) {
      if (pair.key().equals(key))
        return pair.value();
    }
    // if we haven't found it then it's not here
    throw new IndexOutOfBoundsException();
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString, JSONValue>> iterator() {
    return new Iterator<KVPair<JSONString, JSONValue>>() {
      int currBucket = 0;
      int currValue = 0;

      @Override
      public boolean hasNext() {
        if (currBucket >= JSONHash.this.buckets.length)
          return false;

        ArrayList<KVPair<JSONString, JSONValue>> castedBucket =
            castBucket(JSONHash.this.buckets[currBucket]);
        if (castedBucket == null) {
          currBucket++;
          return hasNext();
        }
        if (currValue == castedBucket.size()) {
          currValue = 0;
          currBucket++;
          return hasNext();
        }

        return true;
      }

      @Override
      public KVPair<JSONString, JSONValue> next() {
        if (!hasNext())
          throw new NoSuchElementException();

        KVPair<JSONString, JSONValue> result = null;

        for (; currBucket < JSONHash.this.buckets.length; currBucket++) {
          ArrayList<KVPair<JSONString, JSONValue>> bucket =
              castBucket(JSONHash.this.buckets[currBucket]);
          if (bucket == null)
            continue;

          // otherwise bucket exists
          // return the current value
          result = bucket.get(currValue++);
          break;
        }

        return result;
      }

    };
  }

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
    // if too many pigeons for pigeonholes, make more pigeonholes.
    if (this.size() > (this.buckets.length * LOAD_FACTOR))
      this.expand();

    int bucketIndex = find(key);
    ArrayList<KVPair<JSONString, JSONValue>> castedBucket = castBucket(this.buckets[bucketIndex]);
    if (castedBucket == null) {
      this.buckets[bucketIndex] = new ArrayList<>();
      castedBucket = castBucket(this.buckets[bucketIndex]);
    }
    KVPair<JSONString, JSONValue> toAdd = new KVPair<JSONString, JSONValue>(key, value);
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
  // TODO: fix expand
  void expand() {
    int newSize = 2 * this.buckets.length;
    Object[] oldBuckets = this.buckets;
    this.buckets = new Object[newSize];
    for (Object bucket : oldBuckets) {
      if (bucket == null)
        continue;
      // bucket is otherwise a non empty bucket.
      // cast the bucket to what it is (an arraylist of kvpairs)
      ArrayList<KVPair<JSONString, JSONValue>> castedBucket = castBucket(bucket);
      for (KVPair<JSONString, JSONValue> pair : castedBucket) {
        if (pair.key() == null)
          continue;
        // put old pairs into new spots
        this.buckets[find(pair.key())] = pair;
      }
    }
  }

  /**
   * Find the index of the bucket that may contain the given key.
   */
  int find(JSONString key) {
    return Math.abs(key.hashCode()) % this.buckets.length;
  }

  @SuppressWarnings("unchecked")
  ArrayList<KVPair<JSONString, JSONValue>> castBucket(Object bucket) {
    return (ArrayList<KVPair<JSONString, JSONValue>>) bucket;
  }

} // class JSONHash
