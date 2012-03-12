/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Visual.Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStreamWriter;
import javax.swing.*;
import javax.swing.table.*;

public class ExcelExporter {

    public static void exportTable(JTable table, File file, String charset) throws IOException {
        TableModel model = table.getModel();

        BufferedWriter out =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),charset));

        for (int i = 0; i < model.getColumnCount(); i++) {
            out.write(model.getColumnName(i) + "\t");
        }
        out.write("\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                out.write(model.getValueAt(i, j).toString() + "\t");
            }
            out.write("\n");
        }

        out.close();
    }
}
