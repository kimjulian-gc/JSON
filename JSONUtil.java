import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 * 
 * @author Seunghyeon (Hyeon) Kim
 */

public class JSONUtil {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Current character
   */
  int ch;

  /**
   * Current position of the character
   */
  int pos;

  // +-------------+-------------------------------------------------
  // | Constructor |
  // +-------------+

  /**
   * When the constructor is called, initialize pos to 0.
   */
  public JSONUtil() {
    pos = 0;
  } // JSONUtil()

  // +--------------+------------------------------------------------
  // | Main Methods |
  // +--------------+

  /**
   * Retrieves the position of the current pointer in the source
   * 
   * @return
   */
  int getPos() {
    return pos;
  } // getPos()

  /**
   * It parses the given source and returns the JSONValue associated
   * 
   * @param source
   * @param curPos
   * @throws ParseException
   * @throws IOException
   */
  JSONValue parseKernel(Reader source)
      throws ParseException, IOException {
    // consume the first character
    ch = skipWhitespace(source);
    return parseKernelHelper(source);
  } // parseKernel (Reader)

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  JSONValue parseKernelHelper(Reader source)
      throws ParseException, IOException {
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
   * extracts hash from the source and adds it to the temp.
   * 
   * @param temp
   * @param source
   * @throws ParseException
   * @throws IOException
   */
  void extractHash(JSONHash temp, Reader source)
      throws ParseException, IOException {
    ch = skipWhitespace(source);
    // Check if the hash is empty
    if (ch == '}') {
      ch = skipWhitespace(source);
      return;
    } // if
    pushHash(temp, source);
    // When it reaches the end of hash, stop the process
    if (ch == '}') {
      ch = skipWhitespace(source);
      return;
      // When ",", repeat the step.
    } else if (ch == ',') {
      extractHash(temp, source);
      // When it encounters invalid character that is not related to hash, but within
      // hash string,
      // throw an error
    } else {
      throw new ParseException("Invalid hash structure", pos);
    } // if/else
  } // extractHash(JSONHash, Reader)

  /**
   * Extracts the JSONArray from the source and adds it to the tempArr.
   * 
   * @param tempArr
   * @param source
   * @throws IOException
   * @throws ParseException
   */
  void extractArray(JSONArray tempArr, Reader source)
      throws IOException, ParseException {
    ch = skipWhitespace(source);
    // Check if it is an empty array
    if (ch == ']') {
      ch = skipWhitespace(source);
      return;
    } // if
    // If it is not empty, add elements
    tempArr.add(parseKernelHelper(source));
    // When it reaches the end after adding, stop the process
    if (ch == ']') {
      // Skip the end of array indicator
      ch = skipWhitespace(source);
      return;
      // When there is a ",", add elements recursively
    } else if (ch == ',') {
      extractArray(tempArr, source);
    } else {
      throw new ParseException("Invalid array structure", pos);
    } // if/else
  } // extractArray(JSONArray, Reader)

  /**
   * Extracts the String from the source and concatenates it to temp.
   * 
   * @param temp   initially empty string.
   * @param source
   * @throws IOException
   * @throws ParseException
   */
  JSONString extractString(String temp, Reader source)
      throws IOException, ParseException {
    ch = source.read();
    // When it reaches the end of the string, return the JSONString
    if (ch == '\"') {
      ch = skipWhitespace(source);
      return new JSONString(temp);
      // When it encounters end of file without ending the string, throw an error
    } else if (ch == -1) {
      throw new ParseException("Unexpected end of file", pos);
      // Otherwise, concatenate ch to temp and repeat until it reaches the end of the
      // string.
    } else {
      temp += (char) ch;
      return extractString(temp, source);
    } // if/else
  } // extractString(int, Reader)

  /**
   * Extracts numbers from of the source and concatenates it to temp. Depending
   * on the type, it returns a JSONReal or JSONInteger
   * 
   * @param temp
   * @param source
   * @param type   0: integer (no decimal), 1: real number (decimal)
   * @throws ParseException
   * @throws IOException
   */
  JSONValue extractNumber(String temp, Reader source, int type)
      throws ParseException, IOException {
    // When . is found any time, it means it is a decimal, so change the type to 1
    if (ch == '.') {
      // if type is already 1, throw an exception
      if (type == 1) {
        throw new ParseException("Invalid decimal, there are 2 or more decimal points in the data.", pos);
      } // if
      type = 1;
    } // if
    // When given decimal is digit, add it on to the temp and repeat until not digit
    // or negative sign or decimal point.
    if (Character.isDigit(ch) || (ch == '.') || (ch == '-')) {
      temp += (char) ch;
      ch = skipWhitespace(source);
      return extractNumber(temp, source, type);
      // When the number expression ends, return the respective types with the temp.
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
   * @param temp   valid strings are null, true, false.
   * @param source
   * @throws ParseException
   * @throws IOException
   */
  JSONConstant extractConstant(String temp, Reader source)
      throws ParseException, IOException {
    // if the given character is letter, add it on to temp and repeat the steps
    // until it is not a letter
    if (Character.isLetter(ch)) {
      temp += (char) ch;
      ch = skipWhitespace(source);
      return extractConstant(temp, source);
      // When the given character finishes reading in the letters, return the
      // constant. Note that
      // if the character is invalid, it will throw an error automatically.
    } else {
      return new JSONConstant(temp);
    } // if/else
  } // extractConstant(Reader)

  /**
   * Helper function for extractHash, it pushes the hash block into temp
   * from the source
   * 
   * @param temp
   * @param source
   * @throws ParseException
   * @throws IOException
   */
  private void pushHash(JSONHash temp, Reader source)
      throws ParseException, IOException {
    // read in the key assuming it is a JSONValue
    JSONValue tempKey = parseKernelHelper(source);
    // check whether it is a JSONString. If not, throw an error since the key is
    // wrong.
    if (!(tempKey instanceof JSONString)) {
      throw new ParseException("Error! Your file formatting is wrong. Invalid Key: " + tempKey, pos);
    } // if
    // if the key exists, throw an exception saying duplicate keys.
    if (containsKey(temp, (JSONString) tempKey)) {
      throw new ParseException("Duplicate keys", pos);
    } // if
    // if colon, skip the colon and read in the value
    if (ch == ':') {
      // skip a character that expresses the colon.
      ch = skipWhitespace(source);
      JSONValue tempValue = parseKernelHelper(source);
      temp.set((JSONString) tempKey, tempValue);
      // When expected colon is not found, the structure must be wrong, so throw
      // an error
    } else {
      throw new ParseException("Invalid hash (does not give a value)", pos);
    } // if/else
  } // pushHash(JSONHash, Reader)

  /**
   * Returns the boolean telling whether the key is inside the JSONHash
   * 
   * @param temp
   * @param key
   */
  private boolean containsKey(JSONHash temp, JSONString key) {
    try {
      // attempt to retrieve a key from the JSONHash
      temp.get(key);
      // if succeeded, the key is inside the JSONHash, so return true.
      return true;
    } catch (Exception e) {
      // if fails, the key does not exist, so return false.
      return false;
    } // try/catch
  } // containsKey(JSONHash, JSONString)
} // class JSONUtil
