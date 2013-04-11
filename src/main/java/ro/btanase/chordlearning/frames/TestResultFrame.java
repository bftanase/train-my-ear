package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import ro.btanase.chordlearning.domain.ExerciseResult;
import ro.btanase.chordlearning.domain.Score;
import java.awt.Toolkit;

public class TestResultFrame extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField tfAccuracy;
  private JTextField txtCorrect;
  private JTextField txtIncorrect;
  private Score score;
  private JPanel jpanelResultsContainer;
  private LessonSSRFrame ltf;
  private final Color BK_COLOR = new Color(33, 98, 120);

  /**
   * Create the dialog.
   */
  public TestResultFrame(final LessonSSRFrame ltf, Score score) {
    setIconImage(Toolkit.getDefaultToolkit().getImage(TestResultFrame.class.getResource("/res/tme_small.png")));

    contentPanel.setBackground(BK_COLOR);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);
    this.score = score;
    this.ltf = ltf;
    setTitle("Test Result");
    setBounds(100, 100, 285, 371);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[54.00][grow]", "[][][grow]"));
    {
      JLabel lblAccuracy = new JLabel("Accuracy:");
      lblAccuracy.setForeground(new Color(255, 255, 255));
      contentPanel.add(lblAccuracy, "cell 0 0,alignx right");
    }
    {
      tfAccuracy = new JTextField();
      tfAccuracy.setHorizontalAlignment(SwingConstants.TRAILING);
      tfAccuracy.setEditable(false);
      contentPanel.add(tfAccuracy, "cell 1 0,growx");
      tfAccuracy.setColumns(10);
    }
    {
      JLabel lblResults = new JLabel("Results");
      lblResults.setForeground(new Color(255, 255, 255));
      lblResults.setFont(new Font("Tahoma", Font.BOLD, 14));
      lblResults.setHorizontalAlignment(SwingConstants.CENTER);
      contentPanel.add(lblResults, "cell 0 1 2 1,growx");
    }
    {
      JScrollPane scrollPane = new JScrollPane();
      contentPanel.add(scrollPane, "cell 0 2 2 1,grow");
      {
        jpanelResultsContainer = new JPanel();
        jpanelResultsContainer.setBackground(BK_COLOR);
        jpanelResultsContainer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        scrollPane.setViewportView(jpanelResultsContainer);
        jpanelResultsContainer.setLayout(new MigLayout("insets 10 0 10 10", "[54][46px,grow]", "[14px][]"));
        {
          JLabel lblA = new JLabel("A");
          jpanelResultsContainer.add(lblA, "cell 0 0,alignx trailing");
        }
        {
          txtCorrect = new JTextField();
          txtCorrect.setHorizontalAlignment(SwingConstants.TRAILING);
          txtCorrect.setForeground(new Color(60, 179, 113));
          txtCorrect.setText("correct");
          txtCorrect.setEditable(false);
          jpanelResultsContainer.add(txtCorrect, "cell 1 0,growx");
          txtCorrect.setColumns(10);
        }
        {
          JLabel lblDm = new JLabel("Dm");
          jpanelResultsContainer.add(lblDm, "cell 0 1,alignx trailing");
        }
        {
          txtIncorrect = new JTextField();
          txtIncorrect.setHorizontalAlignment(SwingConstants.TRAILING);
          txtIncorrect.setForeground(Color.RED);
          txtIncorrect.setEditable(false);
          txtIncorrect.setText("incorrect");
          jpanelResultsContainer.add(txtIncorrect, "cell 1 1,growx");
          txtIncorrect.setColumns(10);
        }
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setBackground(BK_COLOR);
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      buttonPane.setLayout(new MigLayout("", "[][65px,grow]", "[23px]"));
      {
        JButton btnRestart = new JButton("Restart");
        btnRestart.setMnemonic('r');
        btnRestart.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            onBtnRestartActionPerformed();
          }
        });
        buttonPane.add(btnRestart, "cell 0 0");
      }
      {
        JButton btnBackToLesson = new JButton("Back to lesson index");
        btnBackToLesson.setMnemonic('i');
        btnBackToLesson.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            onBackToLessonActionPerformed();
          }
        });
        buttonPane.add(btnBackToLesson, "cell 1 0,alignx right");
      }
    }
    
    displayStats();
    
  }


  private void onBackToLessonActionPerformed() {
    // REMEMBER: frame.dispose() does NOT trigger WINDOW_CLOSING events
    // we need to send them manually
    WindowEvent closingEvent = new WindowEvent(ltf, WindowEvent.WINDOW_CLOSING);
    ltf.dispatchEvent(closingEvent);

    this.dispose();
    
  }


  private void onBtnRestartActionPerformed() {
    ltf.restart();
    this.dispose();
  }


  private void displayStats() {
    tfAccuracy.setText((int)score.getAccuracy() + " %");
    
    List<ExerciseResult> exerciseResults =  score.getLessonResults();
    jpanelResultsContainer.removeAll();
    jpanelResultsContainer.revalidate();
    
    int i = 0;
    for(ExerciseResult er : exerciseResults){
      JLabel chordNameLabel = new JLabel(er.getChord().getChordName());
      chordNameLabel.setForeground(Color.WHITE);
      JTextField resultTextField = new JTextField();
      if (er.isCorrect()){
        resultTextField.setForeground(new Color(60, 179, 113));
        resultTextField.setText("correct");
        resultTextField.setEditable(false);
      }else{
        resultTextField.setForeground(Color.RED);
        resultTextField.setText("incorrect");
        resultTextField.setEditable(false);
      }
      
      jpanelResultsContainer.add(chordNameLabel, "cell 0 " + i + ",alignx trailing");  
      jpanelResultsContainer.add(resultTextField, "cell 1 " + i + ",growx");

      i++;
    }
  }

}
