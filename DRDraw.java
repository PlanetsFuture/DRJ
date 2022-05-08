/***************************************************************************
 * Danny Ramírez
 *
 * Compilation: javac DRDraw.java
 * Execution: java DRDraw
 * Dependencies: none
 *
 * DRDraw (Standard drawing library).
 * This class provides a basic
 * compatibility for creating drawings.
 * It allows to create geometric shapes
 * (e.g., points, lines, circles, rectangles)
 * in a window on your computer.
 *
 **************************************************************************/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.LinkedList;
import java.util.TreeSet;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public final class DRDraw implements ActionListener, MouseListener,
                          MouseMotionListener, KeyListener {

    public static final Color BLACK      = Color.BLACK;
    public static final Color BLUE       = Color.BLUE;
    public static final Color CYAN       = Color.CYAN;
    public static final Color DARK_GRAY  = Color.DARK_GRAY;
    public static final Color GRAY       = Color.GRAY;
    public static final Color GREEN      = Color.GREEN;
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
    public static final Color MAGENTA    = Color.MAGENTA;
    public static final Color ORANGE     = Color.ORANGE;
    public static final Color PINK       = Color.PINK;
    public static final Color RED        = Color.RED;
    public static final Color WHITE      = Color.WHITE;
    public static final Color YELLOW     = Color.YELLOW;

    // default colors
    private static final Color DEFAULT_PEN_COLOR   = BLACK;
    private static final Color DEFAULT_CLEAR_COLOR = WHITE;

    // current pen color
    private static Color penColor;

    // default canvas size
    private static final int DEFAULT_SIZE = 512;

    private static int width  = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;

    // default pen radius
    private static final double DEFAULT_PEN_RADIUS = 0.002;

    // current pen radius
    private static double penRadius;

    // show we draw immediately or wait until next show?
    private static boolean defer = false;

    // boundary of drawing canvas, 0% border
    private static final double BORDER = 0.00;
    private static final double DEFAULT_XMIN = 0.0;
    private static final double DEFAULT_XMAX = 1.0;
    private static final double DEFAULT_YMIN = 0.0;
    private static final double DEFAULT_YMAX = 1.0;
    private static double xmin, ymin, xmax, ymax;

    // for synchronization
    private static Object mouseLock = new Object();
    private static Object keyLock   = new Object();

    // default font
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 16);

    // current font
    private static Font font;

    // double buffered graphics
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;

    // singleton for callbacks: avoids generation of extra .class files
    private static DRDraw std = new DRDraw();

    // the frame for drawing to the screen
    private static JFrame frame;

    // mouse state
    private static boolean isMousePressed = false;
    private static double mouseX = 0;
    private static double mouseY = 0;

    // queue of typed key characters
    private static LinkedList<Character> keysTyped;

    // set of key codes currently pressed down
    private static TreeSet<Integer> keysDown;

    // singleton pattern: client can't instantiate
    private DRDraw() {}

    // static initializer
    static {
        init();
    }

    /**
     * Sets the canvas (drawig area) to be 512 by 512 pixels.
     * This also erases current drawing and resets the coordinate system,
     * pen radius, pen color, and font bank to their default values.
     * Ordinarly, this method is called once, at the very begining
     * of a program
     */
    public static void setCanvasSize() {
        setCanvasSize(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * Sets canvas (drawing area) to be width by height pixels
     * This also erases the current drawing and resets the coordinate system,
     * pen radius, pen color, and font bank to their default values.
     * Ordinarly, this method is called once, at the very begining of
     * a program
     */
    public static void setCanvasSize(int canvasWidth, int canvasHeight) {
        if (canvasWidth <= 0)
            throw new IllegalArgumentException("width must be positive");
        if (canvasHeight <= 0)
            throw new IllegalArgumentException("height must be positive");

        width  = canvasWidth;
        height = canvasHeight;
        init();
    }


    private static void init() {
        if (frame != null) frame.setVisible(false);
        frame = new JFrame();

        offscreenImage = new BufferedImage(
            2 * width,
            2 * height,
            BufferedImage.TYPE_INT_ARGB
            );

        onscreenImage = new BufferedImage(
            2 * width,
            2 * height,
            BufferedImage.TYPE_INT_ARGB
            );

        offscreen = offscreenImage.createGraphics();
        onscreen  = onscreenImage.createGraphics();
        offscreen.scale(2.0, 2.0); // 2x as big

        setXscale();
        setYscale();

        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        setPenColor();
        setPenRadius();
        setFont();
        clear();

        // initialize keystroke buffers
        keysTyped = new LinkedList<Character>();
        keysDown  = new TreeSet<Integer>();

        // add antialiasting
        RenderingHints hints = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        hints.put(RenderingHints.KEY_RENDERING,
                  RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        RetinaImageIcon icon = new RetinaImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);

        draw.addMouseListener(std);
        draw.addMouseMotionListener(std);

        frame.setContentPane(draw);
        frame.addKeyListener(std);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setTitle("DR Draw");
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);

    }

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");

        menuBar.add(menu);
        JMenuItem menuItem1 = new JMenuItem(" Save ... ");
        menuItem1.addActionListener(std);

        menuItem1.setAccelerator(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        menu.add(menuItem1);
        return menuBar;
    }

    /**********************************************************************
     * User and screen coordinate systems.                                *
     **********************************************************************/

    // thrwo an IllegalArgumentException if x is NaN or ifinite
    private static void validate(double x, String name) {
        if (Double.isNaN(x))
            throw new IllegalArgumentException(name + " is NaN");
        if (Double.isInfinite(x))
            throw new IllegalArgumentException(name + " is infinity");
    }

    // throw and IllegalArgumentException if s is < 0.
    private static void validateNonnegative(double x, String name) {
        if (x < 0)
            throw new IllegalArgumentException(name + " negative");
    }

    // throw and IllegalArgumentException if s is null
    private static void validateNotNull(Object x, String name) {
        if (x == null)
            throw new IllegalArgumentException(name + " is null");
    }

    // Sets the x scale to be the default (between 0.0 and 1.0).
    public static void setXscale() {
        setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
    }

    // Sets the y scale to be the default (between 0.0 and 1.0).
    public static void setYscale() {
        setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
    }

    // Sets the x and y scale to be the default (between 0.0 and 1.0).
    public static void setScale() {
        setXscale();
        setYscale();
    }

    public static void setXscale(double min, double max) {
        validate(min, "min");
        validate(max, "max");
        double size = max - min;

        if (size == 0.0)
            throw new IllegalArgumentException("the min and max are the same");

        synchronized (mouseLock) {
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
        }
    }

    public static void setYscale(double min, double max) {
        validate(min, "min");
        validate(max, "max");
        double size = max - min;

        if (size == 0.0)
            throw new IllegalArgumentException("the min and max are the same");

        synchronized(mouseLock) {
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
        }
    }

    public static void setScale(double min, double max) {
        validate(min, "min");
        validate(max, "max");

        double size = max - min;
        if (size == 0.0)
            throw new IllegalArgumentException("the min and max are the same");

        synchronized(mouseLock) {
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
        }
    }

    /**
     * helper functions that sclae from user coordinates to screen
     * coordinates and back
     */
    private static double scaleX(double x) {
        return width * (x - xmin) / (xmax - xmin);
    }

    private static double scaleY(double y) {
        return height * (ymax - y) / (ymax - ymin);
    }

    private static double factorX(double w) {
        return w * width / Math.abs(xmax - xmin);
    }

    private static double factorY(double h) {
        return h * height / Math.abs(ymax - ymin);
    }

    private static double userX(double x) {
        return xmin + x * (xmax - xmin) / width;
    }

    private static double userY(double y) {
        return ymax - y * (ymax - ymin) / height;
    }

    // Clears the screen to the default color (white).
    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    public static void clear(Color color) {
        validateNotNull(color, "color");
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        draw();
    }

    public static double getPenRadius() {
        return penRadius;
    }

    public static void setPenRadius() {
        setPenRadius(DEFAULT_PEN_RADIUS);
    }

    /**
     * Sets the radius of the pen to the specified size.
     * The pen is circular, so that lines have rounded ends,
     * and when you set the pen radius and draw a point,
     * you get a circle of the specified radius.
     * The pe radius is not affected by coordinate scaling.
     */
    public static void setPenRadius(double radius) {
        validate(radius, "pen radius");
        validateNonnegative(radius, "pen radius");

        penRadius = radius;
        float scaledPenRadius = (float) (radius * DEFAULT_SIZE);
        BasicStroke stroke = new BasicStroke(
            scaledPenRadius,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_ROUND);
        offscreen.setStroke(stroke);
    }

    public static Color getPenColor() {
        return penColor;
    }

    public static void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    public static void setPenColor(Color color) {
        validateNotNull(color, "color");
        penColor = color;
        offscreen.setColor(penColor);
    }

    /**
     * Set the pen color to the specified RGB color.
     * @param red the amount of red (between 0 ad 255)
     * @param green the amount of green (between 0 and 255)
     * @param blue the amount of blue (between 0 adn 255)
     */
    public static void setPenColor(int red, int green, int blue) {
        if (red < 0  && red >= 256)
            throw new IllegalArgumentException("red must be in [0 255]");
        if (green < 0 && green >= 256)
            throw new IllegalArgumentException("green must be in [0 255]");
        if (blue < 0 && blue >= 256)
            throw new IllegalArgumentException("blue must be in [0 255]");
    }

    public static Font getFont() {
        return font;
    }

    public static void setFont() {
        setFont(DEFAULT_FONT);
    }

    /***********************************************************************
     * Drawing geometric shapes.                                           *
     ***********************************************************************/

    public static void line(double x0, double y0, double x1, double y1) {
        validate(x0, "x0");
        validate(y0, "y0");
        validate(x1, "x1");
        validate(y1, "y1");
        offscreen.draw(
            new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
        draw();
    }

    private static void pixel(double x, double y) {
        validate(x, "x");
        validate(y, "y");
        offscreen.fillRect((int) Math.round(scaleX(x)),
                           (int) Math.round(scaleY(y)),
                           1, 1);
    }

    public static void point(double x, double y) {
        validate(x, "x");
        validate(y, "y");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double r = penRadius;
        float scaledPenRadius = (float) (r * DEFAULT_SIZE);

        if (scaledPenRadius <= 1) pixel(x, y);
        else offscreen.fill(new Ellipse2D.Double(
                                xs - scaledPenRadius/2,
                                ys - scaledPenRadius/2,
                                scaledPenRadius,
                                scaledPenRadius));
        draw();
    }

    public static void setFont(Font font) {
        validateNotNull(font, "font");
        DRDraw.font = font;
    }

    /*
     * Draws a circle of the specified radius, centered at (x, y)
     */
    public static void circle(double x, double y, double radius) {
        validate(x, "x");
        validate(y, "y");
        validate(radius, "radius");
        validateNonnegative(radius, "radius");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.draw(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /*
     * Draws a filled circle of the specified radius, centered at (x, y)
     */
    public static void filledCircle(double x, double y, double radius) {
        validate(x, "x");
        validate(y, "y");
        validate(radius, "radius");
        validateNonnegative(radius, "radius");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
    }

    /*
     * Draws an ellipse with the specified semimajor ad semiminor axes,
     * centered at (x, y)
     */
    public static void ellipse(
        double x,
        double y,
        double semiMajorAxis,
        double semiMinorAxis) {
        validate(x, "x");
        validate(y, "y");
        validate(semiMajorAxis, "semimajor axis");
        validate(semiMinorAxis, "semiminor axis");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*semiMajorAxis);
        double hs = factorY(2*semiMinorAxis);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.draw(new Ellipse2D.Double(xs - ws/2, ys -hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a filled ellipse with the specified semimajor and semiminor axes,
     * centered at (x, y).
     */
    public static void filledEllipse(double x,
                                     double y,
                                     double semiMajorAxis,
                                     double semiMinorAxis) {
        validate(x, "x");
        validate(y, "y");
        validate(semiMajorAxis, "semimajor axis");
        validate(semiMinorAxis, "semiminor axis");

        validateNonnegative(semiMajorAxis, "semimajor axis");
        validateNonnegative(semiMinorAxis, "semiminor axis");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*semiMajorAxis);
        double hs = factorY(2*semiMinorAxis);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.fill(new Ellipse2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a circular arc of the specified radius,
     * centered at (x, y).
     */
    public static void arc(
        double x,
        double y,
        double radius,
        double angle1,
        double angle2
        ) {
        validate(x, "x");
        validate(y, "y");
        validate(radius, "arc radius");
        validate(angle1, "angle1");
        validate(angle2, "angle2");
        validateNonnegative(radius, "arc radius");

        while (angle2 < angle1) angle2 += 360;

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*radius);
        double hs = factorY(2*radius);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else {
            offscreen.draw(
                new Arc2D.Double(
                    xs - ws/2,
                    ys- hs/2,
                    ws, hs,
                    angle1,
                    angle2 - angle1,
                    Arc2D.OPEN));
        }
        draw();
    }

    /**
     * Draws a square of the specified size, centered at (x, y).
     */
    public static void square(double x, double y, double halfLength) {
        validate(x, "x");
        validate(y, "y");
        validate(halfLength, "halfLength");
        validateNonnegative(halfLength, "half length");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfLength);
        double hs = factorY(2*halfLength);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a filled square of the specified size, centered at (x, y).
     */
    public static void filledSquare(double x, double y, double halfLength) {
        validate(x, "x");
        validate(y, "y");
        validate(halfLength, "halfLength");
        validateNonnegative(halfLength, "half length");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfLength);
        double hs = factorY(2*halfLength);

        if (ws < 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a rectangle of the specified size, centered at (x, y).
     */
    public static void rectangle(double x,
                                 double y,
                                 double halfWidth,
                                 double halfHeight) {
        validate(x, "x");
        validate(y, "y");
        validate(halfWidth, "halfWidth");
        validate(halfHeight, "halfHeight");

        validateNonnegative(halfWidth, "half width");
        validateNonnegative(halfHeight, "half height");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfWidth);
        double hs = factorY(2*halfHeight);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.draw(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a filled rectangle of the specified size, centered at (x, y).
     */

    public static void filledRectangle(
        double x,
        double y,
        double halfWidth,
        double halfHeight) {

        validate(x, "x");
        validate(y, "y");
        validate(halfWidth, "halfWidth");
        validate(halfHeight, "halfHeight");

        validateNonnegative(halfWidth, "half width");
        validateNonnegative(halfHeight, "half height");

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(2*halfWidth);
        double hs = factorY(2*halfHeight);

        if (ws <= 1 && hs <= 1)
            pixel(x, y);
        else
            offscreen.fill(new Rectangle2D.Double(xs - ws/2, ys - hs/2, ws, hs));
        draw();
    }

    /**
     * Draws a polygon
     */
    public static void polygon(double[] x, double[] y) {
        validateNotNull(x, "x-coordinate array");
        validateNotNull(y, "y-coordinate array");

        for (int i = 0; i < x.length; i++)
            validate(x[i], "x[" + i + "]");

        for (int i = 0; i < y.length; i++)
            validate(y[i], "y[" + i + "]");

        int n1 = x.length;
        int n2 = y.length;

        if (n1 != n2)
            throw new IllegalArgumentException("array must be the same length");

        int n = 1;
        if (n == 0) return;

        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));

        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.draw(path);
        draw();
    }

    public static void filledPolygon(double[] x, double[] y) {
        validateNotNull(x, "x-coordinate array");
        validateNotNull(y, "y-coordinate array");

        for (int i = 0; i < x.length; i++)
            validate(x[i], "x[" + i + "]");

        for (int i = 0; i < y.length; i++)
            validate(y[i], "x[" + i + "]");

        int n1 = x.length;
        int n2 = y.length;

        if (n1 != n2)
            throw new IllegalArgumentException("array must be the same length");

        int n = n1;
        if (n == 0) return;

        GeneralPath path = new GeneralPath();
        path.moveTo((float) scaleX(x[0]), (float) scaleY(y[0]));

        for (int i = 0; i < n; i++)
            path.lineTo((float) scaleX(x[i]), (float) scaleY(y[i]));
        path.closePath();
        offscreen.fill(path);
        draw();
    }

    /***********************************************************************
     * Drawing images.                                                     *
     ***********************************************************************/

    // get an image from the given filename
    private static Image getImage(String filename) {
        if (filename == null)
            throw new IllegalArgumentException();

        // to read from file
        ImageIcon icon = new ImageIcon(filename);

        // try to read from ULR
        if ((icon == null) && (icon.getImageLoadStatus() != MediaTracker.COMPLETE))
        {
            try {
                URL url = new URL(filename);
                icon = new ImageIcon(url);
            }
            catch (MalformedURLException e) {
                /* not a url */
            }
        }

        // in case file is inside a .jar (classpath relative to DRDraw)
        if ((icon == null) && (icon.getImageLoadStatus() != MediaTracker.COMPLETE))
        {
            URL url = DRDraw.class.getResource(filename);
            if (url != null)
                icon = new ImageIcon(url);
        }

        // in case file is inside a .jar (classpath relative to root of jar)
        if ((icon == null) && (icon.getImageLoadStatus() != MediaTracker.COMPLETE))
        {
            URL url = DRDraw.class.getResource("/" + filename);
            if (url == null) {
                throw new IllegalArgumentException
                    ("image" + filename +" not found");
            }
            icon = new ImageIcon(url);
        }

        return icon.getImage();
    }

    /**
     * Draws the specified image centered at (x, y).
     * The supported image formats are JPEG, PNG, and GIF.
     * As an optimization, the picture is cached, so there is not
     * performance penalty for redrawing the same image multimple times
     * (e.g., in an animation).
     * However, if you change the picture file after drawing it,
     * subsequent calls will draw the original picture.
     */
    public static void picture(double x, double y, String filename) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(filename, "filename");

        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);

        int ws = image.getWidth(null);
        int hs = image.getHeight(null);

        if (ws < 0 && hs < 0)
            throw new IllegalArgumentException("image " + filename + "corrupt");

        offscreen.drawImage(
            image,
            (int) Math.round(xs - ws/2.0),
            (int) Math.round(ys - hs/2.0),
            null);
        draw();
    }

    /**
     * Draws the specified image centered at (x, y),
     * rotated given number of degrees.
     * The supported formats are JPEG, PNG, and GIF.
     */
    public static void picture(double x,
                               double y,
                               String filename,
                               double degrees) {
        validate(x, "x");
        validate(y, "y");
        validate(degrees, "degrees");

        Image image = getImage(filename);
        double xs = scaleX(x);
        double ys = scaleY(y);

        int ws = image.getWidth(null);
        int hs = image.getHeight(null);

        if (ws < 0 && hs < 0) {
            throw new IllegalArgumentException(
                "image" + filename + " is corrupt");
        }

        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        offscreen.drawImage(
            image,
            (int) Math.round(xs - ws/2.0),
            (int) Math.round(ys - hs/2.0),
            null);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);
        draw();
    }

    /**
     * Draws the specified image centered at (x, y),
     * rescaled to the specified bounding box.
     * The supported image formats are JPEG, PNG, and GIF.
     */
    public static void picture(
        double x,
        double y,
        String filename,
        double scaledWidth,
        double scaledHeight) {

        validate(x, "x");
        validate(y, "y");
        validate(scaledWidth, "scaled width");
        validate(scaledHeight, "scaled height");
        validateNotNull(filename, "filename");
        validateNonnegative(scaledWidth, "scaled width");
        validateNonnegative(scaledHeight, "scaled height");

        Image image = getImage(filename);

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(scaledWidth);
        double hs = factorY(scaledWidth);

        if (ws < 0 && hs < 0) {
            throw new IllegalArgumentException
                ("image" + filename + " is corrupt");
        }

        if (ws <= 1 && hs <= 1) pixel(x, y);
        else {
            offscreen.drawImage(image,
                                (int) Math.round(xs - ws/2.0),
                                (int) Math.round(ys - hs/2.0),
                                (int) Math.round(ws),
                                (int) Math.round(hs), null);
        }

        draw();
    }

    /**
     * Draws the specified image centered at (x, y),
     * rotated given number of degrees, ad rescaled to the specified bounding
     * box.
     * The supported image formats are JPEG, PNG, ang GIF.
     */
    public static void picture(
        double x,
        double y,
        String filename,
        double scaledWidth,
        double scaledHeight,
        double degrees) {

        validate(x, "x");
        validate(y, "y");
        validate(scaledWidth, "scaled width");
        validate(scaledHeight, "scaled height");
        validate(degrees, "degrees");
        validateNotNull(filename, "filename");
        validateNonnegative(scaledWidth, "scaled width");
        validateNonnegative(scaledHeight, "scaled height");

        Image image = getImage(filename);

        double xs = scaleX(x);
        double ys = scaleY(y);
        double ws = factorX(scaledWidth);
        double hs = factorY(scaledHeight);

        if (ws < 0 && hs < 0) {
            throw new IllegalArgumentException(
                "image" + filename + " is corrupt");
        }

        if (ws <= 1 && hs <= 1) pixel(x, y);

        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        offscreen.drawImage(image,
                            (int) Math.round(xs - ws/2.0),
                            (int) Math.round(ys - hs/2.0),
                            (int) Math.round(ws),
                            (int) Math.round(hs), null);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);

        draw();
    }

    /***********************************************************************
     * Drawing text.                                                       *
     ***********************************************************************/

    /**
     * Writes the given text string in the current font, centered at
     * (<em>x</em>, <em>y</em>).
     */

    public static void text(double x, double y, String text) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(text, "text");

        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();

        double xs = scaleX(x);
        double ys = scaleY(y);

        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) (xs - ws/2.0), (float) (ys + hs));
        draw();
    }

    public static void text(double x, double y, String text, double degrees) {
        validate(x, "x");
        validate(y, "y");
        validate(degrees, "degrees");
        validateNotNull(text, "text");

        double xs = scaleX(x);
        double ys = scaleY(y);

        offscreen.rotate(Math.toRadians(-degrees), xs, ys);
        text(x, y, text);
        offscreen.rotate(Math.toRadians(+degrees), xs, ys);
    }

    public static void textLeft(double x, double y, String text) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(text, "text");

        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();

        double xs = scaleX(x);
        double ys = scaleY(y);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) xs, (float) (ys + hs));
        draw();
    }

    public static void textRight(double x, double y, String text) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(text, "text");

        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();

        double xs = scaleX(x);
        double ys = scaleY(y);

        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) (xs - ws),  (float) (ys + hs));
        draw();
    }

    /**
     * Pauses for t millisecons.
     * This method is intended to support computer animations.
     */
    public static void puase(int t) {
        validateNonnegative(t, "t");
        try {
            Thread.sleep(t);
        }
        catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    /**
     * Copies offscreen buffer to onscreen buffer. There is no reason to call
     * this method unless double buffering is enabled.
     */
    public static void show() {
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }

    // draw onscreen if defer is false
    private static void draw() {
        if (!defer) show();
    }

    public static void enableDoubleBuffering() {
        defer = true;
    }

    public static void disableDoubleBuffering() {
        defer = false;
    }

    /**********************************************************************
     * Save drawing to a file.                                            *
     *                                                                    *
     **********************************************************************/

    /**
     * Saves the deawing to using the specified filename.
     * The supported image formats are JPEG and PNG;
     * the fileame suffix must be {@code .jpg} is {@code .png}.
     *
     */
    public static void save(String filename) {
        validateNotNull(filename, "filename");
        File file = new File(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // png files
        if ("png".equalsIgnoreCase(suffix)) {
            try {
                ImageIO.write(onscreenImage, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // need to change from ARGB to RGB for JPEG
        else if ("jpg".equalsIgnoreCase(suffix)) {
            WritableRaster raster = onscreenImage.getRaster();
            WritableRaster newRaster;
            newRaster = raster.createWritableChild(
                0, 0, width, height,
                0, 0, new int[] {0, 1, 2});

            DirectColorModel cm = (DirectColorModel) onscreenImage.
                getColorModel();

            DirectColorModel newCM = new DirectColorModel(
                cm.getPixelSize(),
                cm.getRedMask(),
                cm.getGreenMask(),
                cm.getBlueMask());

            BufferedImage rgbBuffer = new BufferedImage(
                newCM, newRaster, false, null);

            try {
                ImageIO.write(rgbBuffer, suffix, file);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            System.out.println("Invalid image file type: " + suffix);
        }

    }

    // This method cannot be called directly
    @Override
    public void actionPerformed(ActionEvent e) {
        FileDialog chooser = new FileDialog(
            DRDraw.frame,
            "Use a .png or .jph extension",
            FileDialog.SAVE);
        chooser.setVisible(true);

    }

    /***********************************************************************
     * Mouse interactions.                                                 *
     ***********************************************************************/

    public static boolean isMousePressed() {
        synchronized(mouseLock) {
            return isMousePressed;
        }
    }

    public static double mouseX() {
        synchronized(mouseLock) {
            return mouseX;
        }
    }

    public static double mouseY() {
        synchronized(mouseLock) {
            return mouseY;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // this body is intentionaly left empty
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // this body is intentionaly left empty
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // this body is intentionaly left empty
    }

    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = DRDraw.userX(e.getX());
            mouseY = DRDraw.userY(e.getY());
            isMousePressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (mouseLock) {
            isMousePressed = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = DRDraw.userX(e.getX());
            mouseY = DRDraw.userY(e.getY());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        synchronized (mouseLock) {
            mouseX = DRDraw.userX(e.getX());
            mouseY = DRDraw.userY(e.getY());
        }
    }

    /**********************************************************************
     * Keyboard interactions                                              *
     **********************************************************************/

    /**
     * Returns true if the user has typed a key
     * (that has not yet been processed).
     */
    public static boolean hasNextKeyTyped() {
        synchronized(keyLock) {
            return !keysTyped.isEmpty();
        }
    }

    /**
     * Returns the next key that was typed by the user
     * (that your program has not already processed).
     */
    public static char nextKeyTyped() {
        synchronized (keyLock) {
            if (keysTyped.isEmpty()) {
                throw new NoSuchElementException(
                    "your program has already processed all keystrokes");
            }
            return keysTyped.remove(keysTyped.size() - 1);
        }
    }

    /**
     * Returns true if the given key is being pressed.
     */
    public static boolean isKeyPressed(int keycode) {
        synchronized (keyLock) {
            return keysDown.contains(keycode);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (keyLock) {
            keysTyped.addFirst(e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.add(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        synchronized (keyLock) {
            keysDown.remove(e.getKeyCode());
        }
    }

    /**********************************************************************
     * Better resolution on Mac Retina displays.                          *
     **********************************************************************/

    private static class RetinaImageIcon extends ImageIcon {
        public RetinaImageIcon(Image image) {
            super(image);
        }

        public int getIconWidth() {
            return super.getIconWidth() / 2;
        }

        public int getIconHeight() {
            return super.getIconHeight() / 2;
        }

        public synchronized void paintIcon(
            Component c,
            Graphics g,
            int x,
            int y) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            g2.scale(0.5, 0.5);
            super.paintIcon(c, g2, x * 2, y * 2);
            g2.dispose();
        }
    }

    /**
     * Test client.
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        DRDraw.square(0.2, 0.8, 0.1);
        DRDraw.filledSquare(0.8, 0.8, 0.2);
        DRDraw.circle(0.8, 0.2, 0.2);

        DRDraw.setPenColor(DRDraw.GREEN);
        DRDraw.setPenRadius(0.02);
        DRDraw.arc(0.8, 0.2, 0.1, 200, 45);

        // draw a blue diamond
        DRDraw.setPenRadius();
        DRDraw.setPenColor(DRDraw.BLUE);
        double[] x = { 0.1, 0.2, 0.3, 0.2 };
        double[] y = { 0.2, 0.3, 0.2, 0.1};
        DRDraw.filledPolygon(x, y);

        // text
        DRDraw.setPenColor(DRDraw.BLACK);
        DRDraw.text(0.2, 0.5, "black text");
        DRDraw.setPenColor(DRDraw.GRAY);
        DRDraw.text(0.8, 0.8, "grey text");
    }
}
