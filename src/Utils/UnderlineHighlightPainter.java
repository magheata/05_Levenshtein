/**
 * AUTHORS: Rafael Adrián Gil Cañestro
 * Miruna Andreea Gheata
 */package Utils;

import javax.swing.text.*;
import java.awt.*;

public class UnderlineHighlightPainter extends LayeredHighlighter.LayerPainter {

    public UnderlineHighlightPainter(Color c) {
        color = c;
    }

    public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
        // Do nothing: this method will never be called
    }

    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
        g.setColor(color == null ? c.getSelectionColor() : color);

        Rectangle alloc = null;
        if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
            if (bounds instanceof Rectangle) {
                alloc = (Rectangle) bounds;
            } else {
                alloc = bounds.getBounds();

            }
        } else {
            try {
                Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
                alloc = (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();
            } catch (BadLocationException e) {
                return null;
            }
        }

        FontMetrics fm = c.getFontMetrics(c.getFont());
        int baseline = alloc.y + alloc.height - fm.getDescent() + 1;
        Graphics2D g2 = (Graphics2D) g;
        Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2, 2}, 0);
        g2.setStroke(stroke);
        g2.drawLine(alloc.x, baseline, alloc.x + alloc.width, baseline);
        g2.drawLine(alloc.x - 2, baseline + 1, alloc.x + alloc.width, baseline + 1);

        return alloc;
    }

    protected Color color; // The color for the underline
}
