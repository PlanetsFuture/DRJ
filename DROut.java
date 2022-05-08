/***************************************************************************
 * Danny Ramírez
 *
 * Compilation: javac DROut.java
 * Execution:   java DROut
 * Dependencies: none
 *
 * Writes data of various types to standar output
 *
 *
 **************************************************************************/

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/*
 * This class privides methos for printing strings and numbers to standard
 * output.
 */

public final class DROut {

    // force Unicode UTF-8 encoding
    private static final String CHARSET_NAME = "UTF-8";

    // assume language = English, contry = US
    private static final Locale LOCALE = Locale.US;

    // send output here
    private static PrintWriter out;

    // this is called before invoking any methods
    static {
        try {
            out = new PrintWriter(new OutputStreamWriter(
                                      System.out, CHARSET_NAME), true);
        }
        catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
    }

    // don't instantiate
    private DROut() { }

    /*
     * Terminates the current line by printing the line-separator string.
     */
    public static void println() {
        out.println();
    }

    /*
     * Prints an object to this output stream and then terminates the line.
     */
    public static void println(Object x) {
        out.println(x);
    }

    /*
     * Prints a boolean to standard output and then terminates the line.
     */
    public static void println(boolean x) {
        out.println(x);
    }

    /*
     * Prints a character to standard output and then terminates the line.
     */
    public static void println(char x) {
        out.println(x);
    }

    /*
     * Prints a double to standard output and then terminates the line.
     */
    public static void println(float x) {
        out.println(x);
    }

    /*
     * Prints an integer to standard output and then terminates the line.
     */
    public static void println(int x) {
        out.println(x);
    }

    /*
     * Prints a long to standard output and then terminates the line.
     */
    public static void println(long x) {
        out.println(x);
    }

    /*
     * Prints a short integer to standard output and then terminates the line.
     */
    public static void println(short x) {
        out.println(x);
    }

    /*
     * Prints a byte to standard output and then terminates the line.
     */
    public static void println(byte x) {
        out.println(x);
    }

    /*
     * Flushes standar output.
     */
    public static void print() {
        out.flush();
    }

    /*
     * Prints an object to standard output and flushes standard output.
     */
    public static void print(Object x) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints a boolean to standard output and flushes standard output.
     */
    public static void print(boolean x) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints a character to standard output and flushes standard output.
     */
    public static void print(char x) {
        out.println(x);
        out.flush();
    }

    /*
     * Prints a double to standard output and flushes standard output.
     */
    public static void print(double x) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints a float to standard output and flushes standard output.
     */
    public static void print(float x) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints an integer to standard output and flushes standard output.
     */
    public static void print(int x) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints a long integer to standard output and flushes standard output.
     */
    public static void print(long x ) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints a short integer to standard output ad flushes standard output.
     */
    public static void print(short x) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints a byte to standard output and flushes standard output.
     */
    public static void print(byte x) {
        out.print(x);
        out.flush();
    }

    /*
     * Prints formatted string to standard output, using the specified format
     * string and arguments, and then flushes standard output.
     */
    public static void printf(String format, Object... args) {
        out.printf(LOCALE, format, args);
        out.flush();
    }

    public static void printf(Locale locale, String format, Object... args) {
        out.printf(locale, format, args);
        out.flush();
    }

    /* Unit tests
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        DROut.println("Test");
        DROut.println(17);
        DROut.println(true);
        DROut.printf("%.6f\n", 1.0/7.0);
    }
}
