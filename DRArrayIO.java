/***************************************************************************
 * @author Danny Ramírez
 *
 * Compilation:   javac DRArrayIO.java
 * Execution:     java DRArrayIO < input.txt
 * Dependencies:  DROut.java
 *                DRIn.java
 *
 *
 * A library for reading in 1D and 2D arrays of integers, doubles
 * and booleans from standard input and printing them out to
 * standard output.
 *
 * % more tinyDouble1D.txt
 * 5
 * 3.0 1.0 2.0 5.0 4.0
 *
 * % more tinyDouble2D.txt
 * 4 3
 * .00  .23  .00
 * .24  .65  .12
 * .22  .33  .44
 * .43  .54  .81
 *
 * % more tinyBoolean2D.txt
 * 4 3
 * 1   1   0
 * 0   0   0
 * 0   1   1
 * 1   0   0
 *
 ***************************************************************************/

/**
 * Standard array IO. This class provides methods for reading
 * in 1D and 2D from standard input and printing out to standard output.
 */
public class DRArrayIO {

    // don't instantiate
    private DRArrayIO() { }

    /*
     * Reads 1 1D array of doubles from standard input and returns it.
     *
     * @return the 1D array of doubles
     */
    public static double[] readDouble1D() {
        int n = DRIn.readInt();
        double[] a = new double[n];

        for (int i = 0; i < n; i++)
            a[i] = DRIn.readDouble();

        return a;
    }

    /**
     * Prints an array of doubles to standard output.
     *
     * @param a the 1D array of doubles
     */
    public static void print(double[] a) {
        int n = a.length;
        DROut.println(n);

        for (int i = 0; i < n; i++)
            DROut.printf("%9.5f ", a[i]);
        DROut.println();
    }

    /**
     * Reads a 2D array of doubles from standard input and returns it.
     *
     * @return the 2D array of doubles
     */
    public static double[][] readDouble2D() {
        int m = DRIn.readInt();
        int n = DRIn.readInt();
        double[][] a = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                a[i][j] = DRIn.readDouble();
        }
        return a;
    }

        /**
     * Prints the 2D array of doubles to standard output.
     *
     * @param a the 2D array of doubles
     */
    public static void print(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        DROut.println(m + " " + n);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                DROut.printf("%9.5f ", a[i][j]);
            DROut.println();

        }
    }

    /**
     * Prints the 2D array of doubles to standard output.
     *
     * @param a the 2D array of doubles
     */
    public static int[] readInt1D() {
        int n = DRIn.readInt();
        int[] a = new int[n];

        for (int i = 0; i < n; i++)
            a[i] = DRIn.readInt();

        return a;
    }

    /**
     * Prints an array of integers to standard output.
     *
     * @param a the 1D array of integers
     */
    public static void print(int[] a) {
        int n = a.length;
        DROut.println(n);

        for (int i = 0; i < n; i++)
            DROut.printf("%9d ", a[i]);
        DROut.println();
    }

    /**
     * Reads a 2D array of integers from standard input and returns it.
     *
     * @return the 2D array of integers
     */
    public static int[][] readInt2D() {
        int m = DRIn.readInt();
        int n = DRIn.readInt();
        int[][] a = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                a[i][j] = DRIn.readInt();
        }
        return a;
    }

    /**
     * Print a 2D array of integers to standard output.
     *
     * @param a the 2D array of integers
     */
    public static void print(int[][] a) {
        int m = a.length;
        int n = a[0].length;
        DROut.println(m + " " + n);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                DROut.printf("%9d ", a[i][j]);
            DROut.println();
        }
    }

    /**
     * Reads a 1D array of booleans from standard input and returns it.
     *
     * @return the 1D array of booleans
     */
    public static boolean[] readBoolean1D() {
        int n = DRIn.readInt();
        boolean[] a = new boolean[n];

        for (int i = 0; i < n; i++)
            a[i] = DRIn.readBoolean();
        return a;
    }

    /**
     * Prints a 1D array of booleans to standard output.
     *
     * @param a the 1D array of booleans
     */
    public static void print(boolean[] a) {
        int n = a.length;
        DROut.println(n);

        for (int i = 0; i < n; i++) {
            if (a[i]) DROut.print("1 ");
            else      DROut.print("0 ");
        }
        DROut.println();
    }

    /**
     * Reads a 2D array of booleans from standard input and returns it.
     *
     * @return the 2D array of booleans
     */
    public static boolean[][] readBoolean2D() {
        int m = DRIn.readInt();
        int n = DRIn.readInt();
        boolean[][] a = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                a[i][j] = DRIn.readBoolean();
        }
        return a;
    }

    /**
     * Prints a 2D array of booleans to standard output.
     *
     * @param a the 2D array of booleans
     */
    public static void print(boolean[][] a) {
        int m = a.length;
        int n = a[0].length;
        DROut.println(m + " " + n);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j]) DROut.print("1 ");
                else         DROut.print("0 ");
            }
            DROut.println();
        }
    }

    /*
     * Unit tests {@code DRArrayIO}
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        // read and prit an array of doubles
        double[] a = DRArrayIO.readDouble1D();
        DRArrayIO.print(a);
        DROut.println();

        // read and print a matrix of doubles
        double[][] b = DRArrayIO.readDouble2D();
        DRArrayIO.print(b);
        DROut.println();

        // read and print a matrix of booleans
        boolean[][] d = DRArrayIO.readBoolean2D();
        DRArrayIO.print(d);
        DROut.println();
    }
}
