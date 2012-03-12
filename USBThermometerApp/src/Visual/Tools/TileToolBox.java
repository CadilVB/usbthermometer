/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.Tools;

import java.awt.Rectangle;
import java.util.Arrays;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;

/**
 *
 * @author pawelkn
 */
public class TileToolBox {

    public static void tile(JDesktopPane desktopPane, int layer) {
        JInternalFrame[] frames = desktopPane.getAllFramesInLayer(layer);
        if (frames.length == 0) {
            return;
        }

        tile(frames, desktopPane.getBounds());
    }

    public static void tileHorizontal(JDesktopPane desktopPane, int layer) {
        JInternalFrame[] frames = desktopPane.getAllFramesInLayer(layer);
        if (frames.length == 0) {
            return;
        }

        tileHorizontal(frames, desktopPane.getBounds());
    }

    public static void tileVertical(JDesktopPane desktopPane, int layer) {
        JInternalFrame[] frames = desktopPane.getAllFramesInLayer(layer);
        if (frames.length == 0) {
            return;
        }

        tileVertical(frames, desktopPane.getBounds());
    }

    public static void tile(JDesktopPane desktopPane) {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) {
            return;
        }

        tile(frames, desktopPane.getBounds());
    }

    private static void tile(JInternalFrame[] frames, Rectangle dBounds) {
        int cols = (int) Math.sqrt(frames.length);
        int rows = (int) (Math.ceil(((double) frames.length) / cols));
        tile(frames, cols, rows, dBounds);
    }

    private static void tileHorizontal(JInternalFrame[] frames, Rectangle dBounds) {
        int cols = 1;
        int rows = (int) (Math.ceil(((double) frames.length) / cols));
        tile(frames, cols, rows, dBounds);
    }

    private static void tileVertical(JInternalFrame[] frames, Rectangle dBounds) {
        int cols = frames.length;
        int rows = (int) (Math.ceil(((double) frames.length) / cols));
        tile(frames, cols, rows, dBounds);
    }

    private static void tile(JInternalFrame[] frames, int cols, int rows, Rectangle dBounds) {
        int lastRow = frames.length - cols * (rows - 1);
        int width;
        int height;
        if (lastRow == 0) {
            rows--;
            height = dBounds.height / rows;
        } else {
            height = dBounds.height / rows;
            if (lastRow < cols) {
                rows--;
                width = dBounds.width / lastRow;
                for (int i = 0; i < lastRow; i++) {
                    frames[cols * rows + i].setBounds(i * width, rows * height, width, height);
                }
            }
        }
        width = dBounds.width / cols;
        for (int j = 0; j < rows; j++) {
            for (int i = 0; i < cols; i++) {
                frames[i + j * cols].setBounds(i * width, j * height, width, height);
            }
        }
    }

    public static void cascade(JDesktopPane desktopPane, int layer) {
        JInternalFrame[] frames = desktopPane.getAllFramesInLayer(layer);
        if (frames.length == 0) {
            return;
        }

        Arrays.sort( frames, new ZOrderComparator( desktopPane ) );

        cascade(frames, desktopPane.getBounds(), 24);
    }

    public static void cascade(JDesktopPane desktopPane) {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) {
            return;
        }

        Arrays.sort( frames, new ZOrderComparator( desktopPane ) );

        cascade(frames, desktopPane.getBounds(), 24);
    }

    private static void cascade(JInternalFrame[] frames, Rectangle dBounds, int separation) {
        int margin = frames.length * separation + separation;
        int width = dBounds.width - margin;
        int height = dBounds.height - margin;
        for (int i = 0; i < frames.length; i++) {
            frames[i].setBounds(separation + dBounds.x + i * separation,
                    separation + dBounds.y + i * separation,
                    width, height);
        }
    }

    static class ZOrderComparator implements java.util.Comparator {

        JLayeredPane desktop;

        public ZOrderComparator(JLayeredPane pane) {
            desktop = pane;
        }

        @Override
        public int compare(Object o1, Object o2) {
            return desktop.getPosition((JInternalFrame) o2) -
                    desktop.getPosition((JInternalFrame) o1);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof ZOrderComparator)) {
                return false;
            }
            ZOrderComparator c = (ZOrderComparator) o;
            return desktop == c.desktop;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + (this.desktop != null ? this.desktop.hashCode() : 0);
            return hash;
        }
    }
}
