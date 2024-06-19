import javafx.application.Platform;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;

import java.awt.BasicStroke;
import java.awt.Font;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * The Map class inherits the JPanel class which allows for objects and shapes to be drawn onto the screen,
 * listeners are added, and it allows for the user to freely interact with the Panel. This class generates the
 * boroughs off of written data and then presents the information to use the user. It runs on a thread and updates
 * based on Frames Per Second (FPS)
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */
public class Map extends JPanel implements Runnable {

    // Size of the JPanel
    private static final int screenWidth = 620; // Width of GridPane in scenebuilder
    private static final int screenHeight = 400; // Height of GridPane in scenebuilder
    private static final int FPS = 120;


    // Scaling values
    private static final Point centerOffset = new Point(0,0);

    private double polygonScale = 0.75;
    private static final double lowerZoomBound = 0.75;
    private static final double upperZoomBound = 2.5;
    private static final double zoomRange = upperZoomBound - lowerZoomBound;
    private static final double scrollScaleFactor = 0.05;
    private static final int maxFontSize = 30;
    private int currentFontSize;

    // Button inputs
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Button XY velocity change
    private static final int yButtonOffset = 10;
    private static final int xButtonOffset = 10;

    // Bounds for the map (restricts map going out of view)
    private static final double topGradient = 1.3;
    private static final double bottomGradient = 26.2;
    private static final double leftGradient = 3.2;
    private static final double rightGradient = 35.0;


    // Colors
    private static final Color FRAME_BACKGROUND_COLOR = new Color(54,54,54);
    private static final Color MAP_OUTLINE_COLOR = new Color(130,47,47);
    private static final Color SELECTED_COLOR = new Color(0,0,255,100);
    private static final Color FAVOURITED_COLOR = new Color(255,128,0,128);
    private static final Color SELECTED_BORDER_COLOR = Color.BLUE;
    private static final Color FAVOURITED_BORDER_COLOR = new Color(79, 37, 5);
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color TEXT_OUTLINE_COLOR = Color.WHITE;
    private static final Color TEXT_SELECTED_COLOR = Color.WHITE;
    private static final Color FRAME_BORDER_COLOR = Color.DARK_GRAY;
    private static final Color EMPTY_COLOR = Color.GRAY;
    private static final BasicStroke THICK_STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke MEDIUM_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke THIN_STROKE = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private Thread mapThread;

    // User input
    private static final MouseInput mouseInput = new MouseInput();
    private static final MouseWheelInput mouseWheelInput = new MouseWheelInput();
    private static final MouseMotionInput mouseMotionInput = new MouseMotionInput();
    private Borough selectedBorough = null;


    // Size of the user's screen + current mouse position.
    private static final double userScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final double userScreenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final Point currentLocation = new Point(userScreenWidth / 2, userScreenHeight / 2);
    private static final Point previousLocation = new Point(userScreenWidth / 2, userScreenHeight / 2);


    // Borough data
    private Point[] p; // Map points
    private Point[] labels; // boroughLabel points
    private ArrayList<Borough> boroughs;
    private ArrayList<Borough> translatedBoroughs;
    private ArrayList<Borough> scaledBoroughs;
    private HashMap<Borough,Path2D> boroughPaths;


    // Price range data
    private Model model;
    private PriceRangeHandler priceRangeHandler;
    private HashMap<String, Double> brightnessIndexes;


    private MapPanelController controller;


    /**
     * Constructor for Map Class. Takes model and controller as parameters.
     * Creates a map thread, and sets up the JPanel.
     * @param model Model for current price range data
     * @param controller Controller for what panel to open
     */
    public Map(Model model, MapPanelController controller) {
        this.controller = controller;

        setFocusable(true);
        setBackground(FRAME_BORDER_COLOR);
        setDoubleBuffered(true);

        mapThread = new Thread(this);

        addMouseListener(mouseInput);
        addMouseWheelListener(mouseWheelInput);
        addMouseMotionListener(mouseMotionInput);

        this.model = model;
        priceRangeHandler = this.model.getPriceRangeHandler();
        brightnessIndexes = priceRangeHandler.getBoroughPriceRangeIndices();

        startMapThread();
    }

    /**
     * Start the map thread
     */
    public void startMapThread() {
        mapThread.start();
    }

    /**
     * Loop that runs while the Map thread is active.
     * Generates all data, then enters the loop at the speed
     * specified by the FPS.
     */
    public void run() {
        generateBoroughCoords();
        generateBoroughLabels();
        generateBoroughs();

        while (mapThread != null) {
            brightnessIndexes = priceRangeHandler.getBoroughPriceRangeIndices();
            repaint();
            userInputUpdate();

            try { Thread.sleep((long)1000/FPS); } catch (Exception e) {}
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        // Transformations of the points based on zoom + translation
        scaledBoroughs = new ArrayList<>();
        scalePoints();
        translatePoints();
        generateBoroughPaths();

        // Drawing each part of the map
        drawBoroughFill(g2);
        drawBoroughOutline(g2);
        drawBoroughLabel(g2);
        drawBoroughMouseUpdates(g2);
        drawFrameOutline(g2);

        g2.dispose();

    }

    /**
     * Draws the filled borough path based on
     * colour returned by the index
     * @param g2 Graphics2D object it is drawn to
     */
    private void drawBoroughFill(Graphics2D g2) {

        for (Borough key : boroughPaths.keySet()) {
            g2.setColor(key.getBoroughColor());
            g2.fill(boroughPaths.get(key));

        }

    }

    /**
     * Draws the outline of each borough using the defined
     * outline color.
     * @param g2 Graphics2D object it is drawn to
     */
    private void drawBoroughOutline(Graphics2D g2) {

        for (Borough key : boroughPaths.keySet()) {
            g2.setColor(MAP_OUTLINE_COLOR);
            g2.setStroke(MEDIUM_STROKE);
            g2.draw(boroughPaths.get(key));
        }

    }

    /**
     * Draws the names of the boroughs on top of the map.
     * @param g2 Graphics2D object it is drawn to
     */
    private void drawBoroughLabel(Graphics2D g2) {

        g2.setFont(getScaledFontSize());

        for (Borough borough : boroughPaths.keySet()) {
            updateLabel(borough);
            drawLines(borough, g2, TEXT_OUTLINE_COLOR, TEXT_COLOR);

        }

    }

    /**
     * Draws the names of the boroughs from Array of String
     * @param borough current borough being drawn
     * @param g2 Graphics2D object it is drawn to
     * @param outline Outline color
     * @param fill Fill color
     */
    private void drawLines(Borough borough, Graphics2D g2, Color outline, Color fill) {

        double yOffset = borough.getBoroughLabelLocation().getPointY();

        for (String line : borough.getBoroughLabel()) {
            GraphicsUtil.drawStringOutline(g2, line,
                    borough.getBoroughLabelLocation().getPointX(),
                    yOffset,
                    outline, fill);

            yOffset += g2.getFontMetrics().getHeight() - 5;

        }

    }

    /**
     * Draws any updates for the map, e.g. Mouse is hovering
     * over a borough (highlighted blue)
     * @param g2 Graphics2D object it is drawn to
     */
    private void drawBoroughMouseUpdates(Graphics2D g2) {

        if (selectedBorough != null) {
            for (Borough key : boroughPaths.keySet()) {
                if (mouseMotionInput.getMousePoint() != null) {
                    if (boroughPaths.get(key).contains(mouseMotionInput.getMousePoint().getPointX(), mouseMotionInput.getMousePoint().getPointY())) {
                        Color selectedColor = controller.getModel().getComparisonHandler().getFavouriteBoroughs().contains(key.getBoroughName()) ? FAVOURITED_COLOR : SELECTED_COLOR;
                        g2.setColor(selectedColor);
                        g2.fill(boroughPaths.get(key));

                        g2.setStroke(THICK_STROKE);
                        Color selectedBorderColor = controller.getModel().getComparisonHandler().getFavouriteBoroughs().contains(key.getBoroughName()) ? FAVOURITED_BORDER_COLOR : SELECTED_BORDER_COLOR;
                        g2.setColor(selectedBorderColor);
                        g2.draw(boroughPaths.get(key));

                        g2.setColor(TEXT_SELECTED_COLOR);
                        g2.setFont(getScaledFontSize());
                        drawLines(key, g2, TEXT_COLOR, TEXT_OUTLINE_COLOR);

                    }
                }
            }
        }

        setSelectedBorough();

        if (!mouseInput.isMouseInside()) {
            selectedBorough = null;
        }

    }

    /**
     * Draws a Border around the JPanel window.
     * @param g2 Graphics2D object it is drawn to
     */
    private void drawFrameOutline(Graphics2D g2) {

        g2.setStroke(THICK_STROKE);
        g2.setColor(FRAME_BORDER_COLOR);
        g2.drawRect(0,0, screenWidth - 1,screenHeight - 1);

    }


    /**
     * Sets the selected borough if the mouse is hovering it.
     * Checks if the mouse is within the borough's path.
     */
    private void setSelectedBorough() {
        for (Borough key : boroughPaths.keySet()) {
            if (mouseMotionInput.getMousePoint() != null) {
                if (boroughPaths.get(key).contains(mouseMotionInput.getMousePoint().getPointX(), mouseMotionInput.getMousePoint().getPointY())) {
                    selectedBorough = key;
                    return;
                }
            }
        }

        selectedBorough = null;
    }

    /**
     * Creates the Path2D objects for each borough from
     * the data provided.
     */
    private void generateBoroughPaths() {
        Path2D path;
        boroughPaths = new HashMap<>();

        for (Borough borough : translatedBoroughs) {
            path = new Path2D.Double();
            path.moveTo(borough.getBoroughPoints()[0].getPointX(), borough.getBoroughPoints()[0].getPointY());

            for (Point point : borough.getBoroughPoints()) {
                path.lineTo(point.getPointX(), point.getPointY());
            }

            path.closePath();
            boroughPaths.put(borough, path);
        }
    }

    /**
     * Calculates the font size of the labels on the map.
     * @return Returns a new Font Object with the calculated font size
     */
    private Font getScaledFontSize() {

        currentFontSize = (int)(Math.round(((polygonScale - lowerZoomBound) / zoomRange) * maxFontSize));

        if (currentFontSize < 11) {
            currentFontSize = 11;
        }

        return new Font("default", Font.BOLD, currentFontSize);
    }

    /**
     * Translates each of the points in every borough by the centerOffset.
     */
    private void translatePoints() {
        translatedBoroughs = new ArrayList<>();

        for (Borough borough : scaledBoroughs) {
            ArrayList<Point> translatedPoints = new ArrayList<>();
            correctCenterOffset();
            Point offset = new Point(centerOffset.getPointX() + 32, centerOffset.getPointY() - 7);
            // 32 and 7 allow for the map to be centered in the beginning

            Point labelOffset = borough.getBoroughLabelLocation().translatePoint(offset);

            for (Point boroughPoint : borough.getBoroughPoints()) {
                translatedPoints.add(boroughPoint.translatePoint(offset));
            }

            translatedBoroughs.add(new Borough(translatedPoints.toArray(new Point[]{}), borough.getBoroughName(), borough.getBoroughColor(), labelOffset, borough.getBoroughLabel()));

        }
    }

    /**
     * Scales each of the points in every borough by the current zoom value.
     */
    private void scalePoints() {
        for (Borough borough : boroughs) {
            Point labelScale = borough.getBoroughLabelLocation().scalePoint(polygonScale);
            ArrayList<Point> translatedPoints = new ArrayList<>();

            for (Point boroughPoint : borough.getBoroughPoints()) {
                translatedPoints.add(boroughPoint.scalePoint(polygonScale));
            }

            scaledBoroughs.add(new Borough(translatedPoints.toArray(new Point[]{}), borough.getBoroughName(), borough.getBoroughColor(), labelScale, borough.getBoroughLabel()));

        }
    }

    /**
     * Corrects the centerOffset value if the X or Y value exceed the limit on each side
     */
    private void correctCenterOffset() {
        double topBound = - (((polygonScale - lowerZoomBound) / scrollScaleFactor) * topGradient);
        double bottomBound = - (((polygonScale - lowerZoomBound) / scrollScaleFactor) * bottomGradient);
        double leftBound =  - (((polygonScale - lowerZoomBound) / scrollScaleFactor) * leftGradient);
        double rightBound =  - (((polygonScale - lowerZoomBound) / scrollScaleFactor) * rightGradient);

        // TOP SIDE
        if (centerOffset.getPointY() > topBound) {
            centerOffset.setPoint(centerOffset.getPointX(), topBound);
        }

        // BOTTOM SIDE
        if (centerOffset.getPointY() < bottomBound) {
            centerOffset.setPoint(centerOffset.getPointX(), bottomBound);
        }

        // LEFT SIDE
        if (centerOffset.getPointX() > leftBound) {
            centerOffset.setPoint(leftBound, centerOffset.getPointY());
        }

        // RIGHT SIDE
        if (centerOffset.getPointX() < rightBound) {
            centerOffset.setPoint(rightBound, centerOffset.getPointY());
        }
    }

    /**
     * If the button on the MapController are pressed, apply the buttonOffset
     * to the centerOffset
     */
    public void updateButtonInput() {
        if (upPressed) {
            centerOffset.applyPointOffset(0,yButtonOffset);
        }

        if (downPressed) {
            centerOffset.applyPointOffset(0,-yButtonOffset);
        }

        if (leftPressed) {
            centerOffset.applyPointOffset(xButtonOffset,0);
        }

        if (rightPressed) {
            centerOffset.applyPointOffset(-xButtonOffset,0);
        }

        correctCenterOffset();
    }

    /**
     * Changes the zoom value from the zoomSlider on MapController
     * @param value Value ranges from 0 to 100.
     */
    public void scrollInput(double value) {
        polygonScale = ((upperZoomBound - lowerZoomBound) * (value / 100)) + lowerZoomBound;

    }

    /**
     * Frame updates based on userInput
     */
    private void userInputUpdate() {
        // Left Mouse Button Clicked
        // Check if borough selected
        if (mouseInput.isLMBPressed()) {
            if (selectedBorough != null) {
                // Borough Clicked ...
                // must use this as running from non-FX thread
                Platform.runLater(
                        () -> {
                            controller.showListingWindow(selectedBorough.getBoroughName());
                        }
                );
                mouseInput.setLMB(false);
            }

        }

        // Right Mouse Button Dragged
        // Map Drag routine
        if (mouseInput.isRMBPressed()) {
            currentLocation.setPoint(MouseInfo.getPointerInfo().getLocation().getX(), MouseInfo.getPointerInfo().getLocation().getY());

            double dx = mouseInput.getNewCenter().getPointX() - currentLocation.getPointX();
            double dy = mouseInput.getNewCenter().getPointY() - currentLocation.getPointY();
            dx = -dx;
            dy = -dy;

            centerOffset.applyPointOffset(dx, dy);
            mouseInput.getNewCenter().setPoint(currentLocation.getPointX(), currentLocation.getPointY());
            mouseMotionInput.setMousePoint(null);

        }


        // Mousewheel scrolled
        // (zoom in/out) routine
        polygonScale += mouseWheelInput.getMouseWheelRotation() * -scrollScaleFactor;

        if (polygonScale < lowerZoomBound) {
            polygonScale = lowerZoomBound;

        } else if (polygonScale > upperZoomBound) {
            polygonScale = upperZoomBound;

        }

        mouseWheelInput.clearMouseWheelRotation();
        updateButtonInput();

    }

    /**
     * Creates XY point for every vertex in the London Borough Map.
     */
    private void generateBoroughCoords() {

        p = new Point[]
                {
                        new Point(74, 111),
                        new Point(137, 128),
                        new Point(214, 95),
                        new Point(306, 55),
                        new Point(322, 31),
                        new Point(358, 26),
                        new Point(435, 36),
                        new Point(438, 78),
                        new Point(460, 90),
                        new Point(465, 102),
                        new Point(508, 128),
                        new Point(558, 109),
                        new Point(622, 99),
                        new Point(664, 170),
                        new Point(678, 168),
                        new Point(699, 206),
                        new Point(635, 218),
                        new Point(617, 270),
                        new Point(600, 317),
                        new Point(564, 376),
                        new Point(555, 459),
                        new Point(523, 489),
                        new Point(514, 524),
                        new Point(468, 513),
                        new Point(446, 470),
                        new Point(419, 472),
                        new Point(363, 524),
                        new Point(334, 488),
                        new Point(298, 455),
                        new Point(288, 460),
                        new Point(264, 415),
                        new Point(217, 475),
                        new Point(203, 479),
                        new Point(219, 419),
                        new Point(209, 406),
                        new Point(179, 384),
                        new Point(161, 383),
                        new Point(153, 370),
                        new Point(102, 336),
                        new Point(97, 324),
                        new Point(65, 308),
                        new Point(85, 191),
                        new Point(71, 164),
                        new Point(156, 196),
                        new Point(192, 196),
                        new Point(201, 164),
                        new Point(243, 141),
                        new Point(257, 159),
                        new Point(251, 176),
                        new Point(272, 180), // 50
                        new Point(286, 199),
                        new Point(327, 172),
                        new Point(336, 125),
                        new Point(342, 97),
                        new Point(412, 129),
                        new Point(399, 167),
                        new Point(435, 196),
                        new Point(462, 190),
                        new Point(488, 184),
                        new Point(501, 204),
                        new Point(543, 188),
                        new Point(559, 135),
                        new Point(564, 173),
                        new Point(591, 183),
                        new Point(567, 248),
                        new Point(587, 270),
                        new Point(546, 241),
                        new Point(550, 288),
                        new Point(524, 295),
                        new Point(511, 349),
                        new Point(484, 350),
                        new Point(447, 368),
                        new Point(387, 358),
                        new Point(349, 380),
                        new Point(353, 403),
                        new Point(359, 456),
                        new Point(272, 402),
                        new Point(258, 348),
                        new Point(233, 363),
                        new Point(213, 351),
                        new Point(204, 303),
                        new Point(193, 321),
                        new Point(152, 330),
                        new Point(165, 344),
                        new Point(227, 288),
                        new Point(253, 297),
                        new Point(264, 280),
                        new Point(252, 263),
                        new Point(205, 272),
                        new Point(132, 273),
                        new Point(150, 239),
                        new Point(126, 212),
                        new Point(226, 230),
                        new Point(246, 223),
                        new Point(258, 227),
                        new Point(275, 228),
                        new Point(288, 225),
                        new Point(301, 222),
                        new Point(325, 218),
                        new Point(347, 247),
                        new Point(359, 244),
                        new Point(361, 233),
                        new Point(337, 176),
                        new Point(370, 174),
                        new Point(385, 203),
                        new Point(379, 229),
                        new Point(390, 225),
                        new Point(390, 255),
                        new Point(419, 255),
                        new Point(426, 270),
                        new Point(431, 280),
                        new Point(449, 275),
                        new Point(445, 257),
                        new Point(442, 237),
                        new Point(435, 205),
                        new Point(459, 255),
                        new Point(472, 264),
                        new Point(504, 265),
                        new Point(523, 245),
                        new Point(457, 300),
                        new Point(437, 300),
                        new Point(416, 325),
                        new Point(406, 279),
                        new Point(378, 303),
                        new Point(372, 321),
                        new Point(363, 255),
                        new Point(346, 369),
                        new Point(313, 355),
                        new Point(309, 341),
                        new Point(264, 343),
                        new Point(253, 317),
                        new Point(280, 303),
                        new Point(307, 309),
                        new Point(313, 291),
                        new Point(339, 307),
                        new Point(348, 284),
                        new Point(335, 287),
                        new Point(329, 266),
                        new Point(313, 267),
                        new Point(285, 267),
                        new Point(260, 251),
                        new Point(276, 285) // 142
                };

    }

    /**
     * Generates the color of each borough depending on the index provided
     * by the index from To + From
     * @param boroughName Name of the borough
     * @return Returns a new scaled colour relative to the most populated borough
     */
    private Color generateBoroughColor(String boroughName) {

        int value;

        if (brightnessIndexes.get(boroughName) != null) {
            // Meaning that the borough has properties
            double brightnessIndex =  0.1 + (brightnessIndexes.get(boroughName) / 1.2); // to prevent map looking too bland
            value = (int)((1 - brightnessIndex) * 255);

        } else {
            // Borough has no properties
            return EMPTY_COLOR;

        }

        // All colours are a shade of red. Some brighter, some darker.
        return new Color(255,value,value);

    }

    /**
     * Creates each borough, the points which make up a borough,
     * the name of a borough, the default colour of a borough, the location of its label
     * and what name the label shows.
     */
    private void generateBoroughs() {

        boroughs = new ArrayList<>();

        boroughs.add(new Borough(new Point[] {p[59],p[60],p[61],p[62],p[63],p[64],p[66],p[118]}, "Barking and Dagenham", generateBoroughColor("Barking and Dagenham"), labels[0], new String[] {"Barking and", " Dagenham"}));
        boroughs.add(new Borough(new Point[] {p[2],p[3],p[53],p[52],p[51],p[50],p[49],p[48],p[47],p[46]}, "Barnet", generateBoroughColor("Barnet"), labels[1]));
        boroughs.add(new Borough(new Point[] {p[17],p[18],p[19],p[69],p[68],p[67],p[66],p[64],p[65]}, "Bexley", generateBoroughColor("Bexley"), labels[2]));
        boroughs.add(new Borough(new Point[] {p[44],p[45],p[46],p[47],p[48],p[49],p[50],p[97],p[96],p[95],p[94],p[93],p[92]}, "Brent", generateBoroughColor("Brent"), labels[3]));
        boroughs.add(new Borough(new Point[] {p[19],p[20],p[21],p[22],p[23],p[24],p[72],p[71],p[70],p[69]}, "Bromley", generateBoroughColor("Bromley"), labels[4]));
        boroughs.add(new Borough(new Point[] {p[50],p[51],p[102],p[101],p[100],p[99],p[98],p[97]}, "Camden", generateBoroughColor("Camden"), labels[5]));
        boroughs.add(new Borough(new Point[] {p[101],p[105],p[106],p[107],p[125],p[100]}, "City of London", generateBoroughColor("City of London"), labels[6], new String[] {"City"}));
        boroughs.add(new Borough(new Point[] {p[24],p[25],p[26],p[27],p[75],p[74],p[73],p[72]}, "Croydon", generateBoroughColor("Croydon"), labels[7]));
        boroughs.add(new Borough(new Point[] {p[43],p[44],p[92],p[93],p[94],p[140],p[87],p[88],p[89],p[90],p[91]}, "Ealing", generateBoroughColor("Ealing"), labels[8]));
        boroughs.add(new Borough(new Point[] {p[3],p[4],p[5],p[6],p[7],p[54],p[52],p[53]}, "Enfield", generateBoroughColor("Enfield"), labels[9]));
        boroughs.add(new Borough(new Point[] {p[66],p[67],p[68],p[69],p[70],p[119],p[120],p[110],p[111],p[112],p[115],p[116],p[117],p[118]}, "Greenwich", generateBoroughColor("Greenwich"), labels[10]));
        boroughs.add(new Borough(new Point[] {p[55],p[56],p[114],p[106],p[105],p[104],p[103]}, "Hackney", generateBoroughColor("Hackney"), labels[11]));
        boroughs.add(new Borough(new Point[] {p[94],p[95],p[139],p[133],p[132],p[131],p[141],p[86],p[87],p[140]}, "Hammersmith and Fulham", generateBoroughColor("Hammersmith and Fulham"), labels[12], new String[] {"H&F"}));
        boroughs.add(new Borough(new Point[] {p[51],p[52],p[54],p[55],p[103],p[102]}, "Haringey", generateBoroughColor("Haringey"), labels[13]));
        boroughs.add(new Borough(new Point[] {p[1],p[2],p[46],p[45],p[44],p[43]}, "Harrow", generateBoroughColor("Harrow"), labels[14]));
        boroughs.add(new Borough(new Point[] {p[11],p[12],p[13],p[14],p[15],p[16],p[17],p[65],p[64],p[63],p[62],p[61]}, "Havering", generateBoroughColor("Havering"), labels[15]));
        boroughs.add(new Borough(new Point[] {p[0],p[1],p[43],p[91],p[90],p[89],p[39],p[40],p[41],p[42]}, "Hillingdon", generateBoroughColor("Hillingdon"), labels[16]));
        boroughs.add(new Borough(new Point[] {p[37],p[38],p[39],p[89],p[88],p[87],p[86],p[85],p[84],p[80],p[81],p[82],p[83]}, "Hounslow", generateBoroughColor("Hounslow"), labels[17]));
        boroughs.add(new Borough(new Point[] {p[101],p[102],p[103],p[104],p[105]}, "Islington", generateBoroughColor("Islington"), labels[18]));
        boroughs.add(new Borough(new Point[] {p[95],p[96],p[138],p[137],p[136],p[133],p[139]}, "Kensington and Chelsea", generateBoroughColor("Kensington and Chelsea"), labels[19], new String[] {"K&C"}));
        boroughs.add(new Borough(new Point[] {p[30],p[31],p[32],p[33],p[34],p[79],p[78],p[77],p[76]}, "Kingston upon Thames", generateBoroughColor("Kingston upon Thames"), labels[20], new String[] {"Kingston", "upon", "Thames"}));
        boroughs.add(new Borough(new Point[] {p[134],p[135],p[125],p[123],p[124],p[72],p[73],p[126]}, "Lambeth", generateBoroughColor("Lambeth"), labels[21]));
        boroughs.add(new Borough(new Point[] {p[109],p[110],p[120],p[119],p[70],p[71],p[72],p[121],p[122]}, "Lewisham", generateBoroughColor("Lewisham"), labels[22]));
        boroughs.add(new Borough(new Point[] {p[73],p[74],p[76],p[77],p[129],p[128],p[127],p[126]}, "Merton", generateBoroughColor("Merton"), labels[23]));
        boroughs.add(new Borough(new Point[] {p[56],p[57],p[58],p[59],p[118],p[117],p[116],p[115],p[113],p[114],}, "Newham", generateBoroughColor("Newham"), labels[24]));
        boroughs.add(new Borough(new Point[] {p[9],p[10],p[11],p[61],p[60],p[59],p[58],p[57]}, "Redbridge", generateBoroughColor("Redbridge"), labels[25]));
        boroughs.add(new Borough(new Point[] {p[34],p[35],p[36],p[37],p[83],p[82],p[81],p[80],p[84],p[85],p[86],p[141],p[131],p[130],p[129],p[77],p[78],p[79]}, "Richmond upon Thames", generateBoroughColor("Richmond upon Thames"), labels[26], new String[] {"Richmond upon", "Thames"}));
        boroughs.add(new Borough(new Point[] {p[125],p[107],p[108],p[109],p[122],p[121],p[72],p[124],p[123]}, "Southwark", generateBoroughColor("Southwark"), labels[27]));
        boroughs.add(new Borough(new Point[] {p[27],p[28],p[29],p[30],p[76],p[74],p[75]}, "Sutton", generateBoroughColor("Sutton"), labels[28]));
        boroughs.add(new Borough(new Point[] {p[106],p[107],p[108],p[109],p[110],p[111],p[112],p[115],p[113],p[114]}, "Tower Hamlets", generateBoroughColor("Tower Hamlets"), labels[29], new String[] {"Tower", "Hamlets"}));
        boroughs.add(new Borough(new Point[] {p[7],p[8],p[9],p[57],p[56],p[55],p[54]}, "Waltham Forest", generateBoroughColor("Waltham Forest"), labels[30], new String[] {"Waltham", " Forest"}));
        boroughs.add(new Borough(new Point[] {p[126],p[127],p[128],p[129],p[130],p[131],p[132],p[133],p[136],p[135],p[134]}, "Wandsworth", generateBoroughColor("Wandsworth"), labels[31]));
        boroughs.add(new Borough(new Point[] {p[96],p[97],p[98],p[99],p[100],p[125],p[135],p[136],p[137],p[138]}, "Westminster", generateBoroughColor("Westminster"), labels[32], new String[] {"West","-minster"}));

    }

    /**
     * Generates the coords of each and every borough's label's position
     */
    private void generateBoroughLabels() {

        labels = new Point[] {
                new Point(507, 208), // Barking and Dagenham
                new Point(269, 129), // Barnet
                new Point(538, 318), // Bexley
                new Point(223, 201), // Brent
                new Point(463, 426), // Bromley
                new Point(296, 212), // Camden
                new Point(362, 246), // City of London
                new Point(367, 454), // Croydon
                new Point(175, 248), // Ealing
                new Point(359, 84),  // Enfield
                new Point(455, 298), // Greenwich
                new Point(375, 208), // Hackney
                new Point(256, 267), // Hammersmith and Fulahm
                new Point(335, 153), // Haringey
                new Point(164, 147), // Harrow
                new Point(604, 193), // Havering
                new Point(77, 244),  // Hillingdon
                new Point(130, 304), // Hounslow
                new Point(333, 193), // Islington
                new Point(297, 280), // Kensington and Chelsea
                new Point(217, 393), // Kingston Upon Thames
                new Point(332, 313), // Lambeth
                new Point(404, 346), // Lewisham
                new Point(282, 381), // Merton
                new Point(450, 234), // Newham
                new Point(472, 156), // Redbridge
                new Point(167, 345), // Richmond upon Thames
                new Point(360, 295), // Southwark
                new Point(293, 437), // Sutton
                new Point(394, 240), // Tower Hamlets
                new Point(412, 125), // Waltham Forest
                new Point(252, 332), // Wandsworth
                new Point(303, 245)  // Westminster
        };

    }

    /**
     * Changes the data of how many properties there are in
     * each borough
     */
    public void updatePriceRanges() {
        priceRangeHandler = model.getPriceRangeHandler();
        brightnessIndexes = priceRangeHandler.getBoroughPriceRangeIndices();
        generateBoroughs();

    }

    /**
     * Changes the data that a label shows based on
     * how much the user has zoomed. Allows for better spacing.
     * @param borough Borough object for name to be changed
     */
    private void updateLabel(Borough borough) {

        String boroughName = borough.getBoroughName();

        if (currentFontSize == 11) {

            switch (boroughName) {
                case "Richmond upon Thames":
                    borough.setBoroughLabel(new String[]{"Richmond", "upon", "Thames"});
                    break;

                case "Kensington and Chelsea":
                    borough.setBoroughLabel(new String[]{"K&C"});
                    break;

                case "Hammersmith and Fulham":
                    borough.setBoroughLabel(new String[]{"H&F"});
                    break;

            }

        } else {

            switch (boroughName) {
                case "Barking and Dagenham":
                    borough.setBoroughLabel(new String[]{"Barking and Dagenham"});
                    break;

                case "Kensington and Chelsea":
                    borough.setBoroughLabel(new String[]{"Kensington &", "Chelsea"});
                    break;

                case "Hammersmith and Fulham":
                    borough.setBoroughLabel(new String[]{"Hammersmith &", "Fulham"});
                    break;

            }

        }

    }

    // Button State Setters

    /**
     * Sets the up button state
     * @param state button pressed (true) / button released (false)
     */
    public void setUpButtonState(boolean state) {
        upPressed = state;

    }

    /**
     * Sets the down button state
     * @param state button pressed (true) / button released (false)
     */
    public void setDownButtonState(boolean state) {
        downPressed = state;

    }

    /**
     * Sets the left button state
     * @param state button pressed (true) / button released (false)
     */
    public void setLeftButtonState(boolean state) {
        leftPressed = state;
    }

    /**
     * Sets the right button state
     * @param state button pressed (true) / button released (false)
     */
    public void setRightButtonState(boolean state) {
        rightPressed = state;
    }
}
