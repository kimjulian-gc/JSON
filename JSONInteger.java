import java.io.PrintWriter;
import java.math.BigInteger;

/**
 * JSON integers.
 * @author Sam A. Rebelsky
 * @author Albert Okine
 */
public class JSONInteger implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying integer.
   */
  BigInteger value;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new integer given the underlying string.
   */
  public JSONInteger(String str) {
    this.value = new BigInteger(str);
  } // JSONInteger(String)

  /**
   * Create a new integer given a BigInteger.
   */
  public JSONInteger(BigInteger value) {
    this.value = value;
  } // JSONInteger(BigInteger)

  /**
   * Create a new integer given an integer or long.
   */
  public JSONInteger(long l) {
    this.value = BigInteger.valueOf(l);
  } // JSONInteger(long)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    // TODO: Ask Rebelsky if checking for null makes sense
    return (this.value == null)
      ? "null"
      : this.value.toString();
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    // TODO: Ask Rebelsky if equals should check other equals value
    return ((this == other)
            || ((other instanceof JSONInteger) 
                && (this.value.equals(((JSONInteger) other).value))));
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    // TODO: Ask Rebelsky if checking for null makes sense
    return (this.value == null)
      ? 0
      : this.value.hashCode();
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.print(this.toString());
    pen.flush();
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public BigInteger getValue() {
    return this.value;
  } // getValue()

} // class JSONInteger
