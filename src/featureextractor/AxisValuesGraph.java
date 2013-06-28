/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import de.erichseifert.gral.util.Orientation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author po
 */
public class AxisValuesGraph extends JFrame {

    public AxisValuesGraph(ArrayList<ArrayList<CoupleTimeData>> data) {
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(800, 600));
        
        DataTable graphData = new DataTable(Double.class, Double.class, Double.class, Double.class);
        
        for (int i = 0; i < data.get(0).size(); i++) {
            
            graphData.add(data.get(0).get(i).time, data.get(0).get(i).value,
                    data.get(1).get(i).value, data.get(2).get(i).value);
        }
        
        DataSeries seriesX = new DataSeries("X Axis", graphData, 0, 1);
        DataSeries seriesY = new DataSeries("Y Axis", graphData, 0, 2);
        DataSeries seriesZ = new DataSeries("Z axis", graphData, 0, 3);
        
        XYPlot plot = new XYPlot(seriesX, seriesY, seriesZ);
        
        double insetsTop = 10.0, insetsLeft = 70.0, insetsBottom = 60.0, insetsRight = 20.0;
        
        plot.setInsets(new Insets2D.Double(insetsTop, insetsLeft, insetsBottom, insetsRight));
        
        plot.setSetting(BarPlot.TITLE, "Valori registrati");
        plot.setSetting(Plot.LEGEND, true);
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.LABEL, "Tempo (ms)");
        plot.getAxisRenderer(XYPlot.AXIS_Y).setSetting(AxisRenderer.LABEL, "Accelerazione (m/s*2)");
        
        PointRenderer pointsX = new DefaultPointRenderer2D();
        pointsX.setSetting(PointRenderer.SHAPE, new Ellipse2D.Double());
        LineRenderer lineX = new DefaultLineRenderer2D();
        lineX.setSetting(LineRenderer.COLOR, new Color(1.0f, 0.0f, 0.0f));
        plot.setPointRenderer(seriesX, pointsX);
        plot.setLineRenderer(seriesX, lineX);
        
        PointRenderer pointsY = new DefaultPointRenderer2D();
        pointsY.setSetting(PointRenderer.SHAPE, new Ellipse2D.Double());
        LineRenderer lineY = new DefaultLineRenderer2D();
        lineY.setSetting(LineRenderer.COLOR, new Color(0.0f, 1.0f, 0.0f));
        plot.setPointRenderer(seriesY, pointsY);
        plot.setLineRenderer(seriesY, lineY);
        
        PointRenderer pointsZ = new DefaultPointRenderer2D();
        pointsZ.setSetting(PointRenderer.SHAPE, new Ellipse2D.Double());
        LineRenderer lineZ = new DefaultLineRenderer2D();
        lineZ.setSetting(LineRenderer.COLOR, new Color(0.0f, 0.0f, 1.0f));
        plot.setPointRenderer(seriesZ, pointsZ);
        plot.setLineRenderer(seriesZ, lineZ);
        
        plot.getLegend().setSetting(Legend.ORIENTATION, Orientation.VERTICAL);
        
        getContentPane().add(new InteractivePanel(plot), BorderLayout.CENTER);
    }
    
    
}
