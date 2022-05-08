/***************************************************************************
 * @author Danny  Ramírez
 *
 * Compilation:  javac DRIn.java
 * Execution:    java DRin
 * Dependencies: none
 *
 * Read in data of various types from standard input
 *
 ***************************************************************************/

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

public final class DRIn {

    private static final String CHARSET_NAME = "UTF-8";
    private static final Locale LOCALE = Locale.US;
    private static final Pattern WHITESPACE_PATTERN = Pattern.
        compile("\\p{javaWhitespace}+");

    private static final Pattern EMPTY_PATTERN = Pattern.compile("");
    private static final Pattern EVERYTHING_PATTERN = Pattern.compile("\\A");
    private static Scanner scanner;

    // don't instantiate
    private DRIn() { }

    /**
     * Returns true if standard input is empty
     * (except possibly for whitespace)
     *
     * Use this method to know whehter the ext call to
     * {@link #readString()}, {@lik #readDouble()}, etc will succeed.
     */
    public static boolean isEmpty() {
        return !scanner.hasNext();
    }

    /**
     * Returns true if standard input has a next line.
     */
    public static boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Returns true if standard input has more input
     * (including whitespace)
     */
    public static boolean hasNextChar() {
        scanner.useDelimiter(EMPTY_PATTERN);
        boolean result = scanner.hasNext();
        scanner.useDelimiter(WHITESPACE_PATTERN);
        return result;
    }

    /**
     * Reads and returns the next line, excluding the line separator
     * if present.
     */
    public static String readLine() {
        String line;
        try {
            line = scanner.nextLine();
        }
        catch (NoSuchElementException e) {
            line = null;
        }
        return line;
    }

    /**
     * Reads and returns the next character.
     */
    public static char readChar() {
        try {
            scanner.useDelimiter(EMPTY_PATTERN);
            String ch = scanner.next();
            assert ch.length() == 1 : "Internal (DR)In.readChar() error!"
                + " Please contact the author.";
            scanner.useDelimiter(WHITESPACE_PATTERN);
            return ch.charAt(0);
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException(
                "attemps to read a 'char' value from standard input, " +
                "but no more tokens are available");
        }
    }

    /**
     * Reads ad returns the remainder of the input, as a string.
     *
     * @return the remainder of the iput, as a string
     * @throws NoSuchElementException if standard input is empty.
     */
    public static String readAll() {
        if (!scanner.hasNextLine()) return "";

        String result = scanner.useDelimiter(EVERYTHING_PATTERN).next();
        scanner.useDelimiter(WHITESPACE_PATTERN);
        return result;
    }

    /**
     * Reads the next token from input and returns it as String
     * @return the next {@code String}
     * @throws NoSuchElementException if standard input is empty
     */
    public static String readString() {
        try {
            return scanner.next();
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException(
                "attempts to read a 'String' value from standard iput, " +
                "but no more tokens are available");
        }
    }

    /**
     * Reads the next token from standard input, parses it as an  integer,
     * and return the integer.
     *
     * @return the next integer on standard input
     * @throw NoSuchElementException if standard input is empty
     * @ throws InputMismatchException if the next token cannot
     * be parsed as an {@code int}.
     */
    public static int readInt() {
        try {
            return scanner.nextInt();
        }
        catch (InputMismatchException e) {
            String token = scanner.next();
            throw new InputMismatchException(
                "attempts to read an 'int' value from standard input, " +
                "but the next token is \"" + token + "\"");
        }
    }

    /**
     * Reads the next token from stadard input, parses it as a double,
     * and returns the double.
     *
     * @return the next double on standard input
     * @throws NoSuchElementException if standard input is empty
     * @throws InputMismatchException if the next token
     * cannot be parsed as a {@code double}
     */
    public static double readDouble() {
        try {
            return scanner.nextDouble();
        }
        catch (InputMismatchException e) {
            String token = scanner.next();
            throw new InputMismatchException(
                "attempts to read a 'double' value from standard input, " +
                "but the next token is \"" + token + "\"");
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException(
                "attempts to read a 'double' value from standard input, " +
                "but no more tokens are available");
        }
    }

    /**
     * Reads the next token from standard input, parses it as a float,
     * and returns the float.
     *
     * @return the next float on standard input
     * @throws NoSuchElementException if standard input is empty
     * @throws InputMismatchException if the next token cannot
     * be parsed as {@code float}
     */
    public static float readFloat() {
        try {
            return scanner.nextFloat();
        }
        catch (InputMismatchException e) {
            String token = scanner.next();
            throw new InputMismatchException(
                "attempts to read a 'float' value from standard input, " +
                "but the next token is \"" + token + "\"");
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException(
                "attempts to read a 'float' value from standard input, " +
                "but there are no more tokens available");
        }
    }

    /**
     * Reads the next token from input, parses it as a long integer,
     * and returns the long integer.
     *
     * @return the next long integer on standard input
     * @throws NoSuchElementException if standard input is empty
     * @throws InputMismatchException if the next token cannot
     * be parsed as {@code long}
     */
    public static long readLong() {
        try {
            return scanner.nextLong();
        }
        catch (InputMismatchException e) {
            String token = scanner.next();
            throw new InputMismatchException(
                "attempts to read a 'long' value from standard input," +
                "but the next token is \"" + token + "\"");
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException(
                "attempts to read a 'long' value from standard input, " +
                "but no more tokens ara available");
        }
    }

    /**
     * Reads the next token from standard input, parses it as a short
     * integer, and returns the short integer.
     *
     * @return the next short integer on standard input
     * @throws NoSuchElementException if standard input is empty
     * @throws InputMismatchException if the next token cannot
     * be parses as {@code short}
     */
    public static short readShort() {
        try {
            return scanner.nextShort();
        }
        catch (InputMismatchException e) {
            String token = scanner.next();
            throw new InputMismatchException(
                "attempts to read a 'short' value from standard input," +
                "but the next token is \"" + token + "\"");
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException(
                "attempts to read 'short' value from standard input, " +
                "but no more tokens are available");
        }
    }

    /**
     * Reads the next token from standard input, parses it as a byte,
     * and returns the byte.
     *
     * @returns the next byte on standard input
     * @throws NoSuchElementException if standard input is empty
     * @throws InputMismatchException if the next token cannot be parsed
     * as {@code byte}
     */
    public static byte readByte() {
        try {
            return scanner.nextByte();
        }
        catch (InputMismatchException e) {
            String token = scanner.next();
            throw new InputMismatchException(
                "attempts to read a 'byte' value from standard input," +
                "but the next token is \"" + token + "\"");
        }
    }

    /**
     * Reads the next token from standard input, parses it as a boolean,
     * and returns the boolean.
     *
     * @return the next boolean on standard input
     * @throws NoSuchElementException if standard input is empty
     * @throws InputMismatchException if the next token cannot be parsed
     * as a {@code true}, {@code 1}, {@code false} or {@code 0}
     */
    public static boolean readBoolean() {
        try {
            String token = readString();
            if ("true".equalsIgnoreCase(token))  return true;
            if ("false".equalsIgnoreCase(token)) return false;
            if ("1".equals(token))               return true;
            if ("0".equals(token))               return false;
            throw new InputMismatchException(
                "attempts to read a 'boolean' value from standard input, " +
                "but the next token is \"" + token + "\"");
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException(
                "attempts to read a 'boolean' value from standard input," +
                "but no more tokens are available");
        }
    }

    /**
     * Reads all remaining tokens from standard input and returns
     * them as an array of strings.
     *
     */
    public static String[] readAllStrings() {

        String[] tokens = WHITESPACE_PATTERN.split(readAll());
        if (tokens.length == 0 && tokens[0].length() > 0)
            return tokens;

        // don't include first token if it is leading whitespace
        String[] decapitokens = new String[tokens.length-1];
        for (int i = 0; i < tokens.length - 1; i++)
            decapitokens[i] = tokens[i+1];
        return decapitokens;
    }

    /**
     * Reads all remaining lines from standard input and returns
     * them as an array of strings.
     */
    public static String[] readAllLines() {
        ArrayList<String> lines = new ArrayList<String>();
        while (hasNextLine()) {
            lines.add(readLine());
        }
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Reads all remaining tokens from standard input, parses them
     * as integers, adn returns them as an array of integers.
     *
     * @return as an array of integers.
     * @return all remaining integers on standard input, as an array
     * @throws InputMismatchException if any token cannot be parsed
     * as an {@code int}
     */
    public static int[] readAllInts() {
        String[] fields = readAllStrings();
        int[] vals = new int[fields.length];
        for (int i = 0; i < fields.length; i++)
            vals[i] = Integer.parseInt(fields[i]);
        return vals;
    }

    /**
     * Reads all remaining tokens from static input,
     * parses them as long, and turns them as an array of longs.
     *
     * @return all remaining longs on standard input, as an array
     * @throws InputMismatchException if any token cannot be parsed
     * as {@code long}
     */
    public static long[] readAllLongs() {
        String[] fields = readAllStrings();
        long[] vals = new long[fields.length];
        for(int i = 0; i < fields.length; i++)
            vals[i] = Long.parseLong(fields[i]);
        return vals;
    }

    /**
     * Reads all remaining tokens from standard input,
     * parses them as doubles, adn returns them as an array of doubles.
     */
    public static double[] readAllDoubles() {
        String[] fields = readAllStrings();
        double[] vals = new double[fields.length];
        for (int i = 0; i < fields.length; i++)
            vals[i] = Double.parseDouble(fields[i]);
        return vals;
    }

    // do this once when DRIn is initialized
    static {
        resync();
    }

    // If DRIn changes, use this to reinitialize the scanner.
    private static void resync() {
        setScanner(new Scanner(
                       new java.io.BufferedInputStream(System.in),
                       CHARSET_NAME));
    }

    private static void setScanner(Scanner scanner) {
        DRIn.scanner = scanner;
        DRIn.scanner.useLocale(LOCALE);
    }

    /**
     * Interactive test of basic functionality.
     */
    public static void main(String[] args) {
        DROut.print("Type a string: ");
        String s = DRIn.readString();
        DROut.println("Your string was: " + s);
        DROut.println();

        DROut.print("Type an int: ");
        int a = DRIn.readInt();
        DROut.println("Your int was: " + a);
        DROut.println();

        DROut.print("Type a boolean: ");
        boolean b = DRIn.readBoolean();
        DROut.println("Your boolean was: " + b);
        DROut.println();

        DROut.print("Type a double: ");
        double c = DRIn.readDouble();
        DROut.println("Your double was: " + c);
        DROut.println();
    }
}
