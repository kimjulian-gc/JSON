import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 * @author Seunghyeon (Hyeon) Kim
 */

public class JSONUtil {
  /**
   * Current character
   */
  int ch;

  /**
   * Current position of the character
   */
  int pos;

  public JSONUtil() {
    pos = 0;
  } // JSONUtil()

  /**
   * Retrieves the position of the current pointer in the source
   * @return
   */
  int getPos() {
    return pos;
  } // getPos()

  /**
   * It parses the given source and returns the JSONValue associated
   * @param source
   * @param curPos
   * @throws ParseException
   * @throws IOException
   */
   JSONValue parseKernel(Reader source) throws ParseException, IOException {
    // consume the first character
    ch = skipWhitespace(source);
    return parseKernelHelper(source);
  } // parseKernel (Reader)

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
   JSONValue parseKernelHelper(Reader source) throws ParseException, IOException {
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } // if
    // character is otherwise valid

    if (ch == '{') {
      JSONHash temp = new JSONHash();
      extractHash(temp, source);
      return temp;
    } else if (ch == '\"') {
      return extractString("", source);
    } else if (ch == '[') {
      JSONArray tempArr = new JSONArray();
      extractArray(tempArr, source);
      return tempArr;
    } else if (Character.isDigit(ch) || (ch == '-')) {
      return extractNumber("", source, 0);
    } else if (Character.isLetter(ch)) {
      return extractConstant("", source);
    } // if/else
    throw new ParseException("Error! Invalid formatting! Encountered " + (char) ch, pos);
  } // parseKernel (Reader)

  /**
   * Get the next character from source, skipping over whitespace.
   */
   int skipWhitespace(Reader source) throws IOException {
    int ch;
    do {
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch)); // do/while
    return ch;
  } // skipWhitespace(Reader)

  /**
   * Determine if a character is JSON whitespace (newline, carriage return,
   * space, or tab).
   */
   boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

  // +--------------------+------------------------------------------
  // | Extracting Methods |
  // +--------------------+

  /**
   * extracts hash from the source and adds it to the temp. Helper function for
   * extractHash
   * @param temp
   * @param source
   * @throws ParseException
   * @throws IOException
   */
   void extractHash(JSONHash temp, Reader source) throws ParseException, IOException {
    ch = skipWhitespace(source);
    pushHash(temp, source);
    if (ch == '}') {
      ch = skipWhitespace(source);
      return;
    } else if (ch == ',') {
      extractHash(temp, source);
    } else {
      throw new ParseException("Invalid hash structure", pos);
    } // if/else
  } // extractHash(JSONHash, Reader)

  /**
   * Extracts the JSONArray from the source and adds it to the tempArr. Helper
   * function for extractArray
   * @param tempArr
   * @param source
   * @throws IOException
   * @throws ParseException
   */
   void extractArray(JSONArray tempArr, Reader source) throws IOException, ParseException {
    ch = skipWhitespace(source);
    tempArr.add(parseKernelHelper(source));
    if (ch == ']') {
      ch = skipWhitespace(source);
      return;
    } else if (ch == ',') {
      extractArray(tempArr, source);
    } else {
      throw new ParseException("Invalid array structure", pos);
    }
  } // extractArray(JSONArray, Reader)

  /**
   * Extracts the String from the source and concatenates it to temp.
   * @param temp
   * @param source
   * @throws IOException
   * @throws ParseException
   */
   JSONString extractString(String temp, Reader source) throws IOException, ParseException {
    ch = source.read();
    if (ch == '\"') {
      ch = skipWhitespace(source);
      return new JSONString(temp);
    } else if (ch == -1) {
      throw new ParseException("Unexpected end of file", pos);
    } else {
      temp += (char) ch;
      return extractString(temp, source);
    } // if/else
  } // extractString(int, Reader)

  /**
   * Extracts numbers from of the source and concatenates it to temp. Depending
   * on the type, it returns a JSONReal or JSONInteger
   * @param temp
   * @param source
   * @param type
   * @throws ParseException
   * @throws IOException
   */
   JSONValue extractNumber(String temp, Reader source, int type) throws ParseException, IOException {
    if (ch == '.') {
      type = 1;
    } // if
    if (Character.isDigit(ch) || (ch == '.') || (ch == '-')) {
      temp += (char) ch;
      ch = skipWhitespace(source);
      return extractNumber(temp, source, type);
    } else {
      if (type == 1) {
        return new JSONReal(temp);
      } // if
      return new JSONInteger(temp);
    } // if/else
  } // extractNumber(Reader)

  /**
   * Extracts constants out of the source.
   * 
   * @param source
   * @return
   * @throws ParseException
   * @throws IOException
   */
   JSONConstant extractConstant(String temp, Reader source) throws ParseException, IOException {
    if (Character.isLetter(ch)) {
      temp += (char) ch;
      ch = skipWhitespace(source);
      return extractConstant(temp, source);
    } else {
      return new JSONConstant(temp);
    } // if/else
  } // extractConstant(Reader)

  /**
   * Helper function for extractHash, it pushes the hash block into temp
   * from the source
   * @param temp
   * @param source
   * @throws ParseException
   * @throws IOException
   */
  private  void pushHash(JSONHash temp, Reader source) throws ParseException, IOException {
    JSONValue tempKey = parseKernelHelper(source);
    if (!(tempKey instanceof JSONString)) {
      throw new ParseException("Error! Your file formatting is wrong. Invalid Key:", pos);
    } // if
    // if the key exists, throw an exception saying duplicate keys.
    if (containsKey(temp, (JSONString) tempKey)) {
      throw new ParseException("Duplicate keys", pos);
    } // if
    // if colon, skip the colon and read in the value
    if (ch == ':') {
      ch = skipWhitespace(source);
      JSONValue tempValue = parseKernelHelper(source);
      temp.set((JSONString) tempKey, tempValue);
      // if colon not found, the structure is invalid.
    } else {
      throw new ParseException("Invalid hash (does not gave a value)", pos);
    } // if/else
  } // pushHash(JSONHash, Reader)

  private  boolean containsKey(JSONHash temp, JSONString key) {
    try {
      temp.get(key);
      return true;
    } catch (Exception e) {
      return false;
    } // try/catch
  } // containsKey(JSONHash, JSONString)
} // class JSONUtil
