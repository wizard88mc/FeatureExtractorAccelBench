/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.ui;

import featureextractor.model.CoupleTimeData;
import java.util.ArrayList;
import javax.swing.JFrame;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author po
 */
public class DeltaTimesGraph extends JFrame {
    
    public DeltaTimesGraph(ArrayList<CoupleTimeData> data) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setSize(800, 600);
        this.setMinimumSize(new Dimension(800, 600));
        
        DataTable dataTable = new DataTable(Integer.class, Double.class);
        dataTable.add(0, data.get(0).time);
        for (int i = 1; i < data.size(); i++) {
            dataTable.add(i, data.get(i).time - data.get(i-1).time);
        }
        
        XYPlot plot = new XYPlot(dataTable);
        
        getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);
        
        LineRenderer renderer = new DefaultLineRenderer2D();
        plot.setLineRenderer(dataTable, renderer);
        Color color = new Color(0.0f, 0.0f, 1.0f);
        plot.getPointRenderer(dataTable).setSetting(PointRenderer.COLOR, color);
        plot.getPointRenderer(dataTable).setSetting(PointRenderer.SHAPE, new Ellipse2D.Double());
        plot.getLineRenderer(dataTable).setSetting(LineRenderer.COLOR, color);
        
        double insetsTop = 10.0, insetsLeft = 70.0, insetsBottom = 60.0, insetsRight = 10.0;
        
        plot.setInsets(new Insets2D.Double(insetsTop, insetsLeft, insetsBottom, insetsRight));
        
        plot.setSetting(BarPlot.TITLE, "Delta T tra un campionamento ed il successivo");
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.LABEL, "Campionamento");
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.LABEL, "Distanza da precedente (ms)");
        
        
    }
    
}
