import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * JSON implementation (parsing class)
 * @author Sam A. Rebelsky
 * @author Seunghyeon (Hyeon) Kim
 */
public class JSON {
  // +----------------+----------------------------------------------
  // | Static methods |
  // +----------------+

  /**
   * Parse a string into JSON.
   */
  public static JSONValue parse(String source) throws ParseException, IOException {
    return parse(new StringReader(source));
  } // parse(String)

  /**
   * Parse a file into JSON.
   */
  public static JSONValue parseFile(String filename) throws ParseException, IOException {
    FileReader reader = new FileReader(filename);
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws ParseException, IOException {
    JSONUtil utilClass = new JSONUtil();
    JSONValue result = utilClass.parseKernel(source);
    if (-1 != utilClass.skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", utilClass.getPos());
    } // if
    return result;
  } // parse(Reader)
} // class JSON
