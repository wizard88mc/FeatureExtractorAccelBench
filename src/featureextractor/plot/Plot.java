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
import java.awt.event.ItemEvent;
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
import org.jfree.chart.plot.Marker;
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
    private boolean linear;
    private long[] marker = new long[2];
    private List<IntervalMarker> preexisting_markers = new ArrayList<IntervalMarker>();
    private int marker_idx = 0;
    private final DbExtractor db_extractor;
    private final Batch batch;
    private XYSeriesCollection dataset;
    private List<XYSeries> series_container = new ArrayList<XYSeries>();
    private JFreeChart chart;
    private long last_marker = 0;
    private boolean alreadyPrintedGravity = false;

    private void addPlot(boolean accelerometer, boolean accelerometerNoGravity, 
            boolean linear, boolean rotation) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(1024, 768));
        chart = ChartFactory.createXYLineChart(
                defineCorrectTitle(batch, accelerometer, accelerometerNoGravity, linear, rotation),
                "Timestamp",
                "m/s^2",
                this.createDataset(batch, accelerometer, accelerometerNoGravity, linear, rotation),
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
        chart.addProgressListener(new ChartProgressListener() {
            @Override
            public void chartProgress(ChartProgressEvent e) {
                long value = (long) e.getChart().getXYPlot().getDomainCrosshairValue();
                if (e.getType() == 2 && e.getPercent() == 100 && value > 0 && value != last_marker) {
                    last_marker = value;
                    if (marker_idx > 1) {
                        marker_idx = 0;
                        marker = new long[2]; // reset marker range
                    }
                    marker[marker_idx] = value;
                    Plot.this.txtSelected.setText("Setting " + (marker_idx == 0 ? "first" : "last") + " marker point @ " + value);
                    System.out.println(Plot.this.txtSelected.getText());
                    if (marker_idx == 1) {
                        batch.getMarkers().add(new IntervalMarker(Math.min(marker[0], marker[1]), Math.max(marker[0], marker[1])));
                        e.getChart().getXYPlot().addDomainMarker(batch.getMarkers().get(batch.getMarkers().size() - 1));
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

        for (IntervalMarker marker : batch.getMarkers()) {
            System.out.println("Adding pre-existing marker @trunk " + batch.getTrunk() + ": " + (long) marker.getStartValue() + " - " + (long) marker.getEndValue());
            xyplot.addDomainMarker(marker);
        }
        this.mainPanel.add(chartPanel, BorderLayout.CENTER);
        this.setVisible(true);
        
    }
    
    private String defineCorrectTitle(Batch batch, boolean accelerometer, 
            boolean accelerometerNoGravity, boolean linear, boolean rotation) {
        
        String baseString = batch.getTitle()+(batch.getMode()!=null?" ("+batch.getMode()+")":"");
        
        if (accelerometer && !rotation) {
            baseString += " SOLO ACCELEROMETRO";
        }
        else if (accelerometer && rotation) {
            baseString += " ACCELEROMETRO + ROTATION";
        }
        else if (accelerometerNoGravity && !rotation) {
            baseString += " ACCELEROMETRO - GRAVITY";
        }
        else if (accelerometerNoGravity && rotation) {
            baseString += " ACCELEROMETRO - GRAVITY + ROTATION";
        }
        else if (linear && !rotation) {
            baseString += " LINEAR";
        }
        else if (linear && rotation) {
            baseString += " LINEAR + ROTATION";
        }
        
        return baseString;
        
    }

    /**
     * 
     * @param batch
     * @param db_extractor
     * @param accelerometer
     * @param accelerometerNoGravity
     * @param linear
     * @param rotation 
     */
    public Plot(Batch batch, DbExtractor db_extractor, boolean accelerometer, 
            boolean accelerometerNoGravity, boolean linear, boolean rotation) {
        this.db_extractor = db_extractor;
        this.batch = batch;
        this.linear = linear;
        batch.getMarkers().clear();
        try {
            batch.getMarkers().addAll(db_extractor.getMarkersForTrunk(batch.getTrunk(), linear));
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        initComponents();
        addPlot(accelerometer, accelerometerNoGravity, linear, rotation);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JLabel getTxtSelected() {
        return txtSelected;
    }

    private XYDataset createDataset(Batch batch, boolean accelerometer, boolean accelerometerNoGravity,
        boolean linear, boolean rotation) {
        java.util.List<SingleCoordinateSet> axes = batch.getValues();
        if (accelerometer && rotation) {
            axes = batch.getValuesRotated();
        }
        else if (accelerometerNoGravity && !rotation) {
            axes = batch.getValuesWithoutGravity();
        }
        else if (accelerometerNoGravity && rotation) {
            axes = batch.getValuesWithoutGravityRotated();
        }
        else if (linear && !rotation) {
            axes = batch.getLinearValues();
        }
        else if (linear && rotation) {
            axes = batch.getLinearValuesRotated();
        }
        
        dataset = new XYSeriesCollection();
        for (int axis = 0; axis < axes.size(); axis++) {
            XYSeries series = new XYSeries(axes.get(axis).getTitle());
            for (DataTime dt : axes.get(axis).getValues()) {
                series.add(dt.getTime() / time_divisor, dt.getValue());
            }
            series_container.add(series);
            dataset.addSeries(series);
        }
        return dataset;
    }

    private void addSeries(int series_idx) {
        dataset.addSeries(series_container.get(series_idx));
    }

    private void removeSeries(int series_idx) {
        dataset.removeSeries(series_container.get(series_idx));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        txtSelected = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        checkX = new javax.swing.JCheckBox();
        checkY = new javax.swing.JCheckBox();
        checkZ = new javax.swing.JCheckBox();
        checkV = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        btnDeleteTrunk = new javax.swing.JButton();
        btnDeleteLastMarker = new javax.swing.JButton();
        btnSetInTasca = new javax.swing.JButton();
        btnSetInMano = new javax.swing.JButton();
        btnDeleteAllSteps = new javax.swing.JButton();
        btnSaveStairs = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());
        jPanel1.add(txtSelected, java.awt.BorderLayout.PAGE_START);

        mainPanel.setLayout(new java.awt.BorderLayout());
        mainPanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        checkX.setSelected(true);
        checkX.setText("X");
        checkX.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkXItemStateChanged(evt);
            }
        });
        jPanel3.add(checkX);

        checkY.setSelected(true);
        checkY.setText("Y");
        checkY.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkYItemStateChanged(evt);
            }
        });
        jPanel3.add(checkY);

        checkZ.setSelected(true);
        checkZ.setText("Z");
        checkZ.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkZItemStateChanged(evt);
            }
        });
        jPanel3.add(checkZ);

        checkV.setSelected(true);
        checkV.setText("|V|");
        checkV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                checkVStateChanged(evt);
            }
        });
        checkV.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkVItemStateChanged(evt);
            }
        });
        jPanel3.add(checkV);

        jPanel2.add(jPanel3, java.awt.BorderLayout.LINE_START);

        mainPanel.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        btnDeleteTrunk.setText("Delete trunk");
        btnDeleteTrunk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteTrunkActionPerformed(evt);
            }
        });
        jPanel4.add(btnDeleteTrunk);

        btnDeleteLastMarker.setText("Delete last marker");
        btnDeleteLastMarker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLastMarkerActionPerformed(evt);
            }
        });
        jPanel4.add(btnDeleteLastMarker);

        btnSetInTasca.setText("In tasca");
        btnSetInTasca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetInTascaActionPerformed(evt);
            }
        });
        jPanel4.add(btnSetInTasca);

        btnSetInMano.setText("In mano");
        btnSetInMano.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetInManoActionPerformed(evt);
            }
        });
        jPanel4.add(btnSetInMano);

        btnDeleteAllSteps.setText("Delete all steps");
        btnDeleteAllSteps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteAllStepsActionPerformed(evt);
            }
        });
        jPanel4.add(btnDeleteAllSteps);

        btnSaveStairs.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        btnSaveStairs.setText("SAVE STAIRS");
        btnSaveStairs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveStairsActionPerformed(evt);
            }
        });
        jPanel4.add(btnSaveStairs);

        mainPanel.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel1.add(mainPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.META_MASK));
        jMenuItem1.setText("Close");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveStairsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveStairsActionPerformed
        try {
            if (batch.getMarkers().isEmpty()) {
                throw new Exception("No marker set yet");
            }
            if (batch.getTrunk() == 0) {
                throw new Exception("This batch is not a trunk");
            }
            db_extractor.setSteps(batch.getMarkers(), batch.getTrunk(), this.linear);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveStairsActionPerformed

    private void btnDeleteLastMarkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLastMarkerActionPerformed
        if (batch.getMarkers().isEmpty() == false) {
            chart.getXYPlot().removeDomainMarker(batch.getMarkers().get(batch.getMarkers().size() - 1));
            batch.getMarkers().remove(batch.getMarkers().size() - 1);
        } else {
            JOptionPane.showMessageDialog(this, "No marker set yet", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteLastMarkerActionPerformed

    private void btnDeleteTrunkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteTrunkActionPerformed
        try {
            int choice = JOptionPane.showConfirmDialog(this, "Do you want to delete trunk " + batch.getTrunk(), "CONFIRM", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                db_extractor.deleteTrunk(batch.getTrunk(), this.linear);
                this.dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteTrunkActionPerformed

    private void checkVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_checkVStateChanged
    }//GEN-LAST:event_checkVStateChanged

    private void checkVItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkVItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            addSeries(3);
        } else {
            removeSeries(3);
        }
    }//GEN-LAST:event_checkVItemStateChanged

    private void checkZItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkZItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            addSeries(2);
        } else {
            removeSeries(2);
        }
    }//GEN-LAST:event_checkZItemStateChanged

    private void checkYItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkYItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            addSeries(1);
        } else {
            removeSeries(1);
        }
    }//GEN-LAST:event_checkYItemStateChanged

    private void checkXItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkXItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            addSeries(0);
        } else {
            removeSeries(0);
        }
    }//GEN-LAST:event_checkXItemStateChanged

    private void btnDeleteAllStepsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteAllStepsActionPerformed
        try {
            db_extractor.deleteAllSteps(batch.getTrunk(), this.linear);
            for (Marker marker : batch.getMarkers()) {
                chart.getXYPlot().removeDomainMarker(marker);
            }
            batch.getMarkers().clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnDeleteAllStepsActionPerformed

    private void btnSetInTascaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetInTascaActionPerformed
        try {
            System.out.println("Setting as in tasca");
            db_extractor.setTrunkAsInTasca(batch.getTrunk(), this.linear);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSetInTascaActionPerformed

    private void btnSetInManoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetInManoActionPerformed
        try {
            System.out.println("Setting as in mano");
            db_extractor.setTrunkAsInMano(batch.getTrunk(), this.linear);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSetInManoActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteAllSteps;
    private javax.swing.JButton btnDeleteLastMarker;
    private javax.swing.JButton btnDeleteTrunk;
    private javax.swing.JButton btnSaveStairs;
    private javax.swing.JButton btnSetInMano;
    private javax.swing.JButton btnSetInTasca;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox checkV;
    private javax.swing.JCheckBox checkX;
    private javax.swing.JCheckBox checkY;
    private javax.swing.JCheckBox checkZ;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel txtSelected;
    // End of variables declaration//GEN-END:variables
}
