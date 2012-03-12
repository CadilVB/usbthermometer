/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Visual.Tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author pawelkn
 */
public class BetterTable extends JTable {

    private static final Color EVEN_ROW_COLOR = new Color(241, 245, 250);
    private static final Color ODD_ROW_COLOR = Color.WHITE;
    private static final Color SELECTED_ROW_COLOR = new Color(0x0099cc);
    private static final Color TABLE_GRID_COLOR = new Color(0xd9d9d9);
    
    private static final CellRendererPane CELL_RENDER_PANE = new CellRendererPane();

    private boolean mouseIn = false;

    public BetterTable(TableModel dm) {
        super(dm);
        init();

        addMouseListener( new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent arg0) {
                if( arg0.getButton() == MouseEvent.BUTTON3 ) {
                    clearSelection();
                }
            }
            @Override
            public void mouseEntered(MouseEvent arg0) {
                mouseIn = true;
            }
            @Override
            public void mouseExited(MouseEvent arg0) {
                mouseIn = false;
            }
        });
    }

    public boolean isMouseIn() {
        return mouseIn;
    }

    private void init() {
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setTableHeader(createTableHeader());
        getTableHeader().setReorderingAllowed(false);
        setOpaque(false);
        setGridColor(TABLE_GRID_COLOR);
        setIntercellSpacing(new Dimension(0, 0));
        // turn off grid painting as we'll handle this manually in order to paint
        // grid lines over the entire viewport.
        setShowGrid(false);
    }

    /**
     * Creates a JTableHeader that paints the table header background to the right
     * of the right-most column if neccesasry.
     */
    private JTableHeader createTableHeader() {
        return new JTableHeader(getColumnModel()) {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // if this JTableHEader is parented in a JViewport, then paint the
                // table header background to the right of the last column if
                // neccessary.
                JViewport viewport = (JViewport) table.getParent();
                if (viewport != null && table.getWidth() < viewport.getWidth()) {
                    int x = table.getWidth();
                    int width = viewport.getWidth() - table.getWidth();
                    paintHeader(g, getTable(), x, width);
                }
            }
        };
    }

    /**
     * Paints the given JTable's table default header background at given
     * x for the given width.
     */
    private static void paintHeader(Graphics g, JTable table, int x, int width) {
        TableCellRenderer renderer = table.getTableHeader().getDefaultRenderer();
        Component component = renderer.getTableCellRendererComponent(
                table, "", false, false, -1, 2);

        component.setBounds(0, 0, width, table.getTableHeader().getHeight());

        ((JComponent) component).setOpaque(false);
        CELL_RENDER_PANE.paintComponent(g, component, null, x, 0,
                width, table.getTableHeader().getHeight(), true);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        // if the rendere is a JComponent and the given row isn't part of a
        // selection, make the renderer non-opaque so that striped rows show
        // through.
        if (component instanceof JComponent) {
            JComponent jComponent = ((JComponent) component);
            if (getSelectionModel().isSelectedIndex(row)) {
                jComponent.setBackground(SELECTED_ROW_COLOR);
                jComponent.setForeground(Color.WHITE);
            } else {
                jComponent.setBackground(getRowColor(row));
                jComponent.setForeground(Color.BLACK);
            }
        }
        return component;
    }

    private Color getRowColor(int row) {
        return row % 2 == 0 ? EVEN_ROW_COLOR : ODD_ROW_COLOR;
    }
}
