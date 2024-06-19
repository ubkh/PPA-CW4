import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

/**
 * This file is part of "Property Hunter"
 * - a London property viewing application.
 *
 * A GraphicsUtil is a local group of static utility methods that can be used
 * to aid in various graphics-related procedures and functionality, that do not
 * belong in other respective classes in which they are invoked.
 *
 * @author Ubayd Khan (k20044237), Mohammed Chowdhury (k21074018), Omar Ahmad (k210524117), Muhammad Beg (k21057641)
 * @version 30.03.2022
 */

public class GraphicsUtil {

    /**
     * Draws an outlined text onto a JPanel.
     * Reference: http://www.java2s.com/example/java-utility-method/draw-outline-index-0.html
     *
     * @param g A given Graphics or Graphics2D object.
     * @param s The string to be displayed.
     * @param x The x-coordinate of the string on the panel.
     * @param y The y-coordinate of the string on the panel.
     * @param c The colour of the text.
     * @param cOutLine The colour of the text outline.
     */
    public static void drawStringOutline(Graphics g, String s, double x, double y, Color c, Color cOutLine) {
        if (c == null)
            c = g.getColor();
        if (cOutLine == null)
            cOutLine = Color.black;
        // outlines can only be created when a Graphics2D is passed in
        // so otherwise draw a simple string
        if (!(g instanceof Graphics2D)) {
            g.drawString(s, (int)x, (int)y);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        Color cb = g2.getColor();

        // makes rendering cleaner
        Object aliasing = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // save current transformation
        AffineTransform t = g2.getTransform();
        try {
            Font f = g2.getFont();
            FontMetrics fm = g2.getFontMetrics(f);
            GlyphVector v = f.createGlyphVector(fm.getFontRenderContext(), s);
            Shape s1 = v.getOutline();
            g2.translate(x, y);
            Stroke st = g2.getStroke();
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
            g.setColor(cOutLine);
            g2.draw(s1);
            g2.setStroke(st);
            g2.setColor(c);
            g2.fill(s1);
        } finally {
            // return to saved transformation
            g2.setTransform(t);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aliasing);
            g2.setColor(cb);
        }
    }
}
