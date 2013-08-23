/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package featureextractor.plot;

import featureextractor.extractor.db.DbExtractor;
import featureextractor.model.Batch;
import featureextractor.model.DataTime;
import featureextractor.model.SingleCoordinateSet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author ark0n3
 */
public class Plot extends javax.swing.JFrame {

    public final static int time_divisor = 10000000;
    private long[] marker = new long[2];
    private List<IntervalMarker> preexisting_markers=new ArrayList<IntervalMarker>();
    private int marker_idx = 0;
    private final DbExtractor db_extractor;
    private final Batch batch;
    private JFreeChart chart;
    private long last_marker=0;

    private void addPlot() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(1024, 768));
        chart = ChartFactory.createXYLineChart(
                batch.getTitle(),
                "Timestamp",
                "m/s^2",
                this.createDataset(batch),
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
        chart.addProgressListener(new ChartProgressListener() {
            @Override
            public void chartProgress(ChartProgressEvent e) {
                long value=(long)e.getChart().getXYPlot().getDomainCrosshairValue();
                if (e.getType() == 2 && e.getPercent()==100 && value>0) {
                    last_marker=value;
                    if (marker_idx>1) {
                        marker_idx=0;
                        marker=new long[2]; // reset marker range
                    }
                    marker[marker_idx]=value;
                    Plot.this.txtSelected.setText("Setting "+(marker_idx==0?"first":"last")+" marker point @ "+value);
                    System.out.println(Plot.this.txtSelected.getText());
                    if (marker_idx==1) {                        
                        batch.getMarkers().add(new IntervalMarker(Math.min(marker[0], marker[1]), Math.max(marker[0], marker[1])));
                        e.getChart().getXYPlot().addDomainMarker(batch.getMarkers().get(batch.getMarkers().size()-1));
                    }
                    marker_idx++;
                }
            }
        });
        
        XYPlot xyplot = chart.getXYPlot();
        xyplot.setRangeCrosshairVisible(true);
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setDomainCrosshairLockedOnData(false);
        xyplot.setBackgroundPaint(Color.white);
        xyplot.setRangePannable(true);
        xyplot.setDomainPannable(true);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
        for(IntervalMarker marker: batch.getMarkers()) {
            System.out.println("Adding pre-existing marker @trunk "+batch.getTrunk()+": "+(long)marker.getStartValue()+" - "+(long)marker.getEndValue());
            xyplot.addDomainMarker(marker);
        }
        this.mainPanel.add(chartPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }
    
    /**
     * Creates new form Plot
     */
    public Plot(Batch batch, DbExtractor db_extractor) {
        this.db_extractor=db_extractor;
        this.batch=batch;
        initComponents();
        addPlot();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JLabel getTxtSelected() {
        return txtSelected;
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtSelected = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnDeleteLastMarker = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(txtSelected, java.awt.BorderLayout.PAGE_START);

        mainPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        btnDeleteLastMarker.setText("Delete last marker");
        btnDeleteLastMarker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLastMarkerActionPerformed(evt);
            }
        });
        jPanel2.add(btnDeleteLastMarker, java.awt.BorderLayout.CENTER);

        jButton2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButton2.setText("SAVE STAIRS");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2, java.awt.BorderLayout.PAGE_END);

        mainPanel.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jPanel1.add(mainPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            if (batch.getMarkers().isEmpty()) throw new Exception("No marker set yet");
            if (batch.getTrunk()==0) throw new Exception("This batch is not a trunk");
            db_extractor.setSteps(batch.getMarkers(), batch.getTrunk());
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnDeleteLastMarkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLastMarkerActionPerformed
        if (batch.getMarkers().isEmpty()==false) {
            chart.getXYPlot().removeDomainMarker(batch.getMarkers().get(batch.getMarkers().size()-1));
            batch.getMarkers().remove(batch.getMarkers().size()-1);
        } else JOptionPane.showMessageDialog(this, "No marker set yet", "ERROR", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_btnDeleteLastMarkerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteLastMarker;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel txtSelected;
    // End of variables declaration//GEN-END:variables
}
