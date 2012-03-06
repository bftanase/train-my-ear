package ro.btanase.chordlearning;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

public class FreeChartExample extends JFrame {

  private JPanel contentPane;
  private ChartPanel chartPanel;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          FreeChartExample frame = new FreeChartExample();
//          frame.setVisible(true);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });
    System.out.println(System.getProperty("user.home"));
  }

  /**
   * Create the frame.
   */
  public FreeChartExample() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[][grow]", "[][grow]"));
    
    chartPanel = new ChartPanel((JFreeChart) null);
    contentPane.add(chartPanel, "cell 1 1,grow");
    initChart();
  }

  
  private CategoryDataset createDataSet(){
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
    dataset.addValue(100, "Chords", "A");
    dataset.addValue(70, "Chords", "C");
    dataset.addValue(50, "Chords", "D");
    dataset.addValue(60, "Chords", "G");
    dataset.addValue(20, "Chords", "E");
    dataset.addValue(25, "Chords", "Am");
    
    return dataset;
  }
  
  private void initChart(){
    CategoryDataset dataset = createDataSet();
    JFreeChart chart = ChartFactory.createBarChart(
          "Chord status", "Chord", "%", dataset, PlotOrientation.VERTICAL, true, true, false);
    chartPanel.setChart(chart);
    
  }
}
