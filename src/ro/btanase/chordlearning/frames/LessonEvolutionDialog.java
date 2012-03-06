package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventComboBoxModel;

import com.google.inject.Inject;

import ro.btanase.chordlearning.dao.LessonDao;
import ro.btanase.chordlearning.dao.ScoreDao;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.Score;
import ro.btanase.chordlearning.domain.wrappers.LessonEvolutionWrapper;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LessonEvolutionDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JComboBox cbLesson;
  
  private LessonDao m_lessons;
  private ScoreDao m_scores;
  private ChartPanel chartPanel;
  
  private static Logger log = Logger.getLogger(LessonEvolutionDialog.class);

  
  /**
   * Create the dialog.
   */
  @Inject
  public LessonEvolutionDialog(LessonDao m_lessons, ScoreDao m_scores) {
    setTitle("Lesson Evolution Statistics");
    this.m_scores = m_scores;
    this.m_lessons = m_lessons;
    setModal(true);
    setBounds(100, 100, 810, 440);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[][145.00][grow]", "[][][grow]"));
    {
      JLabel lblDisplayStatsFor = new JLabel("Display stats for lesson: ");
      contentPanel.add(lblDisplayStatsFor, "cell 0 1");
    }
    {
      cbLesson = new JComboBox();
      cbLesson.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          initChart();
        }
      });
      contentPanel.add(cbLesson, "cell 1 1,growx");
    }
    {
      chartPanel = new ChartPanel((JFreeChart) null);
      chartPanel.setVisible(false);
      contentPanel.add(chartPanel, "cell 0 2 3 1,grow");
    }
    
    initModels();
  }


  private void initModels() {
    EventComboBoxModel<Lesson> model = new EventComboBoxModel<Lesson>(m_lessons.getAll());
    cbLesson.setModel(model);
    
  }
  
  private XYDataset createDataset(Lesson selectedLesson){
    
    XYSeriesCollection dataset = new XYSeriesCollection();
    
    XYSeries series = new XYSeries(selectedLesson.getLessonName());
    
    EventList<LessonEvolutionWrapper> lewWrapper = m_scores.getEachExerciseLessonAccuracyList(selectedLesson);
    
    for(int i = 0; i < lewWrapper.size(); i++){
      series.add(i+1, lewWrapper.get(i).getAccuracy());
      log.debug("lesson: " + lewWrapper.get(i).getLessonName()
          + " acc: " + lewWrapper.get(i).getAccuracy());
    }
    
    dataset.addSeries(series);
    dataset.setIntervalPositionFactor(1.0);
    return dataset;
  }

  private void initChart(){
    chartPanel.setVisible(true);
    Lesson lesson = (Lesson) cbLesson.getSelectedItem();
    if (lesson == null){
      return;
    }

    // set chart stuff
    JFreeChart chart = ChartFactory.createXYLineChart("Lesson Evolution Accuracy",
        "Lesson session", "Accuracy", createDataset(lesson), PlotOrientation.VERTICAL, true, true, false);
    chart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    chartPanel.setChart(chart);
    
  }
}
