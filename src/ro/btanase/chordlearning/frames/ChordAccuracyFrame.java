package ro.btanase.chordlearning.frames;

import java.awt.Font;
import java.util.Comparator;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import ro.btanase.chordlearning.dao.ScoreDao;
import ro.btanase.chordlearning.domain.wrappers.ChordAccuracyWrapper;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.EventTableModel;

import com.google.inject.Inject;

public class ChordAccuracyFrame extends JDialog {
  private JTable jtableScore;
  private ScoreDao m_scores;
  private SortedList<ChordAccuracyWrapper> chordAccuracyList;
  private ChartPanel chartPanel;
  

  /**
   * Create the frame.
   */
  @Inject
  public ChordAccuracyFrame(ScoreDao m_scores) {
    setTitle("Chord Identification Score");
    setModal(true);
    this.m_scores = m_scores;
    setBounds(100, 100, 755, 420);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow]"));
    
    JLabel lblChordIdentificationScore = new JLabel("Chord Identification Score");
    lblChordIdentificationScore.setFont(new Font("Tahoma", Font.BOLD, 16));
    getContentPane().add(lblChordIdentificationScore, "cell 0 0,alignx center");
    
    JSplitPane splitPane = new JSplitPane();
    getContentPane().add(splitPane, "cell 0 1,grow");
    
    JPanel panel = new JPanel();
    splitPane.setLeftComponent(panel);
    panel.setLayout(new MigLayout("", "[-82.00][100px:300px,grow]", "[][grow]"));
    
    JScrollPane scrollPane = new JScrollPane();
    panel.add(scrollPane, "cell 1 1,growy");
    
    jtableScore = new JTable();
    scrollPane.setViewportView(jtableScore);
    
    JPanel panel_1 = new JPanel();
    splitPane.setRightComponent(panel_1);
    panel_1.setLayout(new MigLayout("", "[300px,grow]", "[420px,grow]"));
    
    chartPanel = new ChartPanel((JFreeChart) null);
    panel_1.add(chartPanel, "cell 0 0,alignx left,growy");

    initModels();
  }


  private void initModels() {
    
    // set table model stuff
    chordAccuracyList = new SortedList<ChordAccuracyWrapper>(m_scores.getChordAccuracyList(), new Comparator<ChordAccuracyWrapper>() {

      @Override
      public int compare(ChordAccuracyWrapper o1, ChordAccuracyWrapper o2) {
        return (int) (o1.getAccuracy() - o2.getAccuracy());
      }
    });
    
    String[] properties =   {"chordName", "totalCount", "correctCount", "accuracy"};
    String[] labels =       {"Chord", "Total Tests", "Correct", "Accuracy"};
    boolean[] writable = {false, false, false, false};
    
    EventTableModel<ChordAccuracyWrapper> model = new EventTableModel<ChordAccuracyWrapper>(chordAccuracyList,
        properties, labels, writable);
        
    jtableScore.setModel(model);
    
    // set chart stuff
    JFreeChart chart = ChartFactory.createBarChart("Chord Accuracy",
        "Chords",
        "%",
        createDataset(), PlotOrientation.HORIZONTAL, true, true, false);
    
    chartPanel.setChart(chart);
    
  }
  
  private CategoryDataset createDataset(){
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
    for (ChordAccuracyWrapper chordAccuracy : chordAccuracyList) {
      dataset.addValue(chordAccuracy.getAccuracy(), "Chord recognition accuracy", chordAccuracy.getChordName());
    }
    
    return dataset;
    
  }

}
