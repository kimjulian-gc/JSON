import java.io.IOException;
import java.io.PrintWriter;

/**
 * JSON constants.
 */
public class JSONConstant implements JSONValue {

  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The true value.
   */
  public static final JSONConstant TRUE = new JSONConstant(Boolean.TRUE);

  /**
   * The false value.
   */
  public static final JSONConstant FALSE = new JSONConstant(Boolean.FALSE);

  /**
   * The null value.
   */
  public static final JSONConstant NULL = new JSONConstant((Object) null);

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The value of the constant.
   */
  Object value;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Build a new constant given a string representation.
   */
  public JSONConstant(String str) throws IOException{
    if (str.equals("null")) {
      this.value = NULL;
    } else if (str.equals("true")) {
      this.value = TRUE;
    } else if (str.equals("false")) {
      this.value = FALSE;
    } else {
      throw new IOException("Invalid object: " + str);
    } // if/else
  } // JSONConstant (String)

  /**
   * Build a new constant.
   */
  public JSONConstant(Object value) {
    this.value = value;
  } // JSONConstant

  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    if (value == null) {
      return "null";
    } else {
      return this.value.toString();
    } // else
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    // TODO: Ask Rebelsky if equals should check other equals value
    return ( ( (other instanceof JSONConstant) 
               && (this.value == ((JSONConstant) other).value) )
             || (this == other) );
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    if (this.value == null)
      return 0;
    else
      return this.value.hashCode();
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.print(this.toString());
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Object getValue() {
    return this.value;
  } // getValue()

} // class JSONConstant
