/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.plot;

import featureextractor.model.Batch;
import featureextractor.model.DataTime;
import featureextractor.model.SingleCoordinateSet;
import static featureextractor.plot.GralPlot.time_divisor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author ark0n3
 */
public class Plot extends JFrame {

    public final static int time_divisor = 10000000;

    public Plot(Batch batch) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(1024, 768));
        JFreeChart chart = ChartFactory.createXYLineChart(
                batch.getTitle(),
                "Timestamp",
                "m/s^2",
                this.createDataset(batch),
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
        chart.addProgressListener(new ChartProgressListener() {
            @Override
            public void chartProgress(ChartProgressEvent e) {
			if (e.getType() != 2)
				return;
                XYPlot xyplot = (XYPlot)e.getChart().getXYPlot();
                System.out.println((long)xyplot.getDomainCrosshairValue());
            }
        });
        XYPlot xyplot=chart.getXYPlot();
        xyplot.setDomainZeroBaselinePaint(Color.black);
//        xyplot.setDomainCrosshairVisible(true);
        xyplot.setRangeCrosshairVisible(true);
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setDomainCrosshairLockedOnData(false);
        xyplot.setBackgroundPaint(Color.white);               
        ChartPanel chartPanel = new ChartPanel(chart);
        getContentPane().add(chartPanel, BorderLayout.CENTER);
    }

    private XYDataset createDataset(Batch batch) {
        java.util.List<SingleCoordinateSet> axes = batch.getValues();
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int axis = 0; axis < axes.size(); axis++) {
            XYSeries series = new XYSeries(axes.get(axis).getTitle());
            for (DataTime dt : axes.get(axis).getValues()) {
                series.add(dt.getTime() / time_divisor, dt.getValue());
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

}
