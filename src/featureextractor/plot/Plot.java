/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.plot;

import featureextractor.model.DataTime;
import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.BarPlot;
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
import featureextractor.model.Batch;
import featureextractor.model.SingleCoordinateSet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author Nicola Beghin
 */
public class Plot extends JFrame {

    public Plot(Batch batch) {

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(800, 600));

        DataTable graphData = new DataTable(Long.class, Double.class, Double.class, Double.class);

        List<SingleCoordinateSet> axes = batch.getValues();
        System.out.println("Samples: "+axes.get(0).getValues().size());
        for (int i = 0; i < axes.get(0).getValues().size(); i++) {
            graphData.add((long)(axes.get(0).getValues().get(i).getTime()/1000000),
                    axes.get(0).getValues().get(i).getValue(),
                    axes.get(1).getValues().get(i).getValue(),
                    axes.get(2).getValues().get(i).getValue()
             );
        }
        DataSeries seriesX = new DataSeries(axes.get(0).getTitle(), graphData, 0, 1);
        DataSeries seriesY = new DataSeries(axes.get(1).getTitle(), graphData, 0, 2);
        DataSeries seriesZ = new DataSeries(axes.get(2).getTitle(), graphData, 0, 3);
//        DataSeries seriesV = new DataSeries("|V|", graphData, 0, 4);

        XYPlot plot = new XYPlot(seriesX, seriesY, seriesZ);

        double insetsTop = 10.0, insetsLeft = 70.0, insetsBottom = 60.0, insetsRight = 20.0;

        plot.setInsets(new Insets2D.Double(insetsTop, insetsLeft, insetsBottom, insetsRight));

        plot.setSetting(BarPlot.TITLE, "Valori registrati");
        plot.setSetting(de.erichseifert.gral.plots.Plot.LEGEND, true);
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
