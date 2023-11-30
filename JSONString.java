import java.io.PrintWriter;

/**
 * JSON strings.
 * @author Sam A. Rebelsky
 * @author Albert Okine
 */
public class JSONString implements JSONValue {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The underlying string.
   */
  String value;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Build a new JSON string for a particular string.
   */
  public JSONString(String value) {
    this.value = value;
  } // JSONString(String)

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    // TODO: Ask Rebelsky if check for null makes sense
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
            || ((other instanceof JSONString)
                && (this.value.equals(((JSONString) other).value))));
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    // TODO: Ask Rebelsky if check for null makes sense
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
    pen.print("\"" + this.toString() + "\"");
    pen.flush();
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public String getValue() {
    return this.value;
  } // getValue()

} // class JSONString
