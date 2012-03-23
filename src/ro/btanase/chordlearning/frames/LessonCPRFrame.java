package ro.btanase.chordlearning.frames;

import java.awt.Color;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.dao.ScoreDao;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.ExerciseResult;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.Score;
import ro.btanase.mediaplayer.SequencePlayer;
import ro.btanase.utils.ListUtils;
import ro.btanase.utils.Searchable;
import ca.odell.glazedlists.BasicEventList;

import com.google.inject.Inject;

public class LessonCPRFrame extends JDialog {
  private JTextField tfLessonName;
  private JTextField tfProgress;
  private Lesson lesson;
  // private Deque<Chord> exerciseStack;
  private List<Chord> chordSequence;
  private List<Chord> answeredChordSequence;
  private List<String> playList;
  private int currentExercise = 0;

  private JButton btnPlay;
  private JPanel jpanelAnswerContainer;

  private Logger log = Logger.getLogger(getClass());
  private JLabel lblAnswerResult;
  private JButton btnNextExercise;
  private List<JButton> answerButtonList = new ArrayList<JButton>();

  private Score score;
  private List<ExerciseResult> exerciseResultList;
  private boolean exerciseFailed = false;
  private ScoreDao m_scores;

  @Inject
  private SequencePlayer sequencePlayer;
  private JScrollPane scrollPane;
  private JPanel panel_1;
  private JButton btnStop;
  private JPanel panel_2;
  private JLabel lblChord;
  private JPanel jpanelResult;
  private JScrollPane scrollPane_1;
  private KeyboardFocusManager manager;
  private MyDispatcher dispatcher;

  /**
   * Create the dialog.
   */
  public LessonCPRFrame(Lesson lesson) {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        log.debug("Window closing");
        manager.removeKeyEventDispatcher(dispatcher);
        sequencePlayer.stop();
      }
    });
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);
    this.lesson = lesson;
    setTitle("Chord recognition");
    setBounds(100, 100, 774, 375);
    getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][][][119.00,grow][118.00,grow]"));

    JLabel lblLessonName = new JLabel("Lesson name:");
    getContentPane().add(lblLessonName, "cell 0 0,alignx trailing");

    tfLessonName = new JTextField();
    tfLessonName.setEditable(false);
    getContentPane().add(tfLessonName, "cell 1 0");
    // tfLessonName.setColumns(10);

    JLabel lblLessonProgress = new JLabel("Lesson Progress:");
    getContentPane().add(lblLessonProgress, "cell 0 1,alignx trailing");

    tfProgress = new JTextField();
    tfProgress.setText("1/5");
    tfProgress.setEditable(false);
    getContentPane().add(tfProgress, "flowx,cell 1 1,alignx left");

    panel_1 = new JPanel();
    getContentPane().add(panel_1, "cell 0 2 2 1,grow");
    panel_1.setLayout(new MigLayout("", "[58px,grow][grow]", "[26px]"));

    btnPlay = new JButton("Play");
    btnPlay.setMnemonic('p');
    panel_1.add(btnPlay, "cell 0 0,alignx right,aligny top");

    btnStop = new JButton("Stop");
    btnStop.setMnemonic('s');
    btnStop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnStopActionPerformed();
      }
    });
    panel_1.add(btnStop, "cell 1 0,aligny top");
    btnPlay.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        onBtnPlayActionPerformed();

      }
    });

    scrollPane_1 = new JScrollPane();
    getContentPane().add(scrollPane_1, "cell 0 3 2 1,grow");

    panel_2 = new JPanel();
    scrollPane_1.setViewportView(panel_2);
    scrollPane_1.setBorder(BorderFactory.createEmptyBorder());
    panel_2.setBorder(new TitledBorder(null, "Your answer:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    panel_2.setLayout(new MigLayout("", "[grow]", "[grow,center]"));

    jpanelResult = new JPanel();
    panel_2.add(jpanelResult, "cell 0 0,alignx center,aligny center");
    jpanelResult.setLayout(new MigLayout("inset 0, gap 20px", "[][]", "[][center]"));

    scrollPane = new JScrollPane();
    // scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    getContentPane().add(scrollPane, "cell 0 4 2 1,grow");

    JPanel panel = new JPanel();
    scrollPane.setViewportView(panel);
    panel.setBorder(new TitledBorder(null, "Choose Answer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    panel.setLayout(new MigLayout("", "[][grow][]", "[][grow][25px:25px:25px]"));

    jpanelAnswerContainer = new JPanel();
    panel.add(jpanelAnswerContainer, "cell 1 1 2 1,alignx center,aligny center");
    jpanelAnswerContainer.setLayout(new MigLayout("wrap 8", "[]", "[]"));

    lblAnswerResult = new JLabel("");
    lblAnswerResult.setFont(new Font("Tahoma", Font.BOLD, 14));
    panel.add(lblAnswerResult, "flowx,cell 1 2,alignx center");

    btnNextExercise = new JButton("Next Exercise");
    btnNextExercise.setMnemonic('n');
    btnNextExercise.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnNextExerciseActionPerformed();
      }
    });
    panel.add(btnNextExercise, "cell 1 2");
    btnNextExercise.setVisible(false);

    manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    dispatcher = new MyDispatcher();
    manager.addKeyEventDispatcher(dispatcher);
    
    
    initModels();
  }

  private void onBtnStopActionPerformed() {
    sequencePlayer.stop();

  }

  private void onBtnNextExerciseActionPerformed() {
    if (currentExercise != lesson.getNoQuestions()) {
      initModels();
      btnPlay.doClick();
    }

  }

  private void onBtnPlayActionPerformed() {
    if (sequencePlayer.getPlayList() != playList) {
      sequencePlayer.setPlayList(playList, lesson.getChordDelay());
    }

    sequencePlayer.play();

  }

  private void initModels() {
    tfLessonName.setText(lesson.getLessonName());

    chordSequence = lesson.randomSequence();
    answeredChordSequence = new BasicEventList<Chord>();

    playList = new ArrayList<String>();

    for (Chord chord : chordSequence) {
      playList.add(chord.getFileName());
    }

    // sequencePlayer.setPlayList(playList);

    startNextExercise();
    exerciseResultList = new ArrayList<ExerciseResult>();
    btnNextExercise.setText("Next Exercise");

  }

  private void startNextExercise() {
    jpanelAnswerContainer.removeAll();
    jpanelAnswerContainer.revalidate();

    jpanelResult.removeAll();
    jpanelResult.revalidate();

    currentExercise++;

    tfProgress.setText((currentExercise) + "/" + lesson.getNoQuestions());

    generateAnswerButtons();
    lblAnswerResult.setText("");
    btnNextExercise.setVisible(false);
    setEnabledAllbuttons(true);
    exerciseFailed = false;
  }

  private void generateAnswerButtons() {

    // eliminate duplicates
    List<Chord> randomAnswers = new ArrayList<Chord>(lesson.getChordSequence());

    Comparator<Chord> comparator = new Comparator<Chord>() {

      @Override
      public int compare(Chord o1, Chord o2) {
        return o1.getChordName().compareTo(o2.getChordName());
      }

    };

    Collections.sort(randomAnswers, comparator);

    randomAnswers = ListUtils.removeDuplicates(randomAnswers, comparator);

    answerButtonList.clear();

    for (Chord chord : randomAnswers) {
      JButton answerButton = new JButton(chord.getChordName());
      answerButton.setActionCommand(chord.getChordName());
      answerButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          String chordName = e.getActionCommand();

          Chord pressedChord = ListUtils.findInList(chordSequence, chordName, new Searchable<Chord, String>() {

            @Override
            public boolean match(Chord e, String t) {
              return e.getChordName().equals(t);
            }
          });

          answeredChordSequence.add(pressedChord);

          lblChord = new JLabel(chordName);
          jpanelResult.add(lblChord, "cell " + (answeredChordSequence.size() - 1) + " 0,aligny center");
          lblChord.setHorizontalAlignment(SwingConstants.CENTER);
          lblChord.setFont(new Font("Tahoma", Font.PLAIN, 33));

          // if the user pressed the last chord in sequence
          if (answeredChordSequence.size() == lesson.getNoChordsInSequence()) {
            // check if the answer is correct
            if (answeredChordSequence.equals(chordSequence)) {

              // if it's the last exercise display dialog to restart or go back
              // to index
              if (currentExercise == lesson.getNoQuestions()) {
                int result = JOptionPane.showConfirmDialog(LessonCPRFrame.this,
                    "Lesson finished. Would you like to restart?", "Lesson Finished", JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

                if (JOptionPane.YES_OPTION == result) {
                  restart();
                  return;
                } else {
                  LessonCPRFrame.this.dispose();
                  return;
                }
              }

              lblAnswerResult.setForeground(Color.GREEN);
              lblAnswerResult.setText("Correct!");
              btnNextExercise.setVisible(true);
              answeredChordSequence.clear();
              jpanelResult.removeAll();
              jpanelResult.repaint();
              jpanelResult.revalidate();

            } else {
              lblAnswerResult.setForeground(Color.RED);
              lblAnswerResult.setText("Wrong Answer. Try again!");
              answeredChordSequence.clear();
              jpanelResult.removeAll();
              jpanelResult.repaint();
              jpanelResult.revalidate();
            }
          }

          // if (chordName.equals(activeChord.getChordName())) {
          // log.info("Answer correct: " + chordName);
          // lblAnswerResult.setForeground(Color.GREEN);
          // lblAnswerResult.setText("The answer is correct!");
          // btnNextExercise.setVisible(true);
          // setEnabledAllbuttons(false);
          // if (!exerciseFailed) {
          // exerciseResultList.add(new ExerciseResult(activeChord, true));
          // }
          //
          // if (exerciseStack.isEmpty()) {
          // score = new Score(lesson, exerciseResultList);
          // m_scores.addScore(score);
          //
          // btnNextExercise.setText("Finish");
          // }
          //
          // } else {
          // log.info("Incorrect answer: " + chordName);
          // lblAnswerResult.setForeground(Color.RED);
          // lblAnswerResult.setText("The answer is incorrect. Please try again!");
          // btnNextExercise.setVisible(false);
          //
          // if (!exerciseFailed) {
          // exerciseResultList.add(new ExerciseResult(activeChord, false));
          // exerciseFailed = true;
          // }
          // }

        }
      });
      jpanelAnswerContainer.add(answerButton, "gapleft 10");
      answerButtonList.add(answerButton);
    }

  }

  private void setEnabledAllbuttons(boolean enabled) {
    for (JButton button : answerButtonList) {
      button.setEnabled(enabled);
    }
  }

  public void restart() {
    currentExercise = 0;
    initModels();
  }

  @Inject
  public void setM_scores(ScoreDao m_scores) {
    this.m_scores = m_scores;
  }

  /**
   * listener that implements all key events for this form before passing them
   * forward
   * 
   * @author Bogdan
   * 
   */
  private class MyDispatcher implements KeyEventDispatcher {

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
      if (e.getID() == KeyEvent.KEY_PRESSED) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
          log.debug("doClick on Play button");
          btnPlay.doClick();
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
          btnStop.doClick();
        }

        if (e.getKeyCode() == KeyEvent.VK_N && btnNextExercise.isVisible()) {
          btnNextExercise.doClick();
        }

        if (e.getKeyCode() == KeyEvent.VK_1) {
          if (answerButtonList.size() >= 1) {
            answerButtonList.get(0).doClick();
          }
        }

        if (e.getKeyCode() == KeyEvent.VK_2) {
          if (answerButtonList.size() >= 2) {
            answerButtonList.get(1).doClick();
          }
        }

        if (e.getKeyCode() == KeyEvent.VK_3) {
          if (answerButtonList.size() >= 3) {
            answerButtonList.get(2).doClick();
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
          if (answerButtonList.size() >= 4) {
            answerButtonList.get(3).doClick();
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_5) {
          if (answerButtonList.size() >= 5) {
            answerButtonList.get(4).doClick();
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_6) {
          if (answerButtonList.size() >= 6) {
            answerButtonList.get(5).doClick();
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_7) {
          if (answerButtonList.size() >= 7) {
            answerButtonList.get(6).doClick();
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_8) {
          if (answerButtonList.size() >= 8) {
            answerButtonList.get(7).doClick();
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_9) {
          if (answerButtonList.size() >= 9) {
            answerButtonList.get(8).doClick();
          }
        }
        if (e.getKeyCode() == KeyEvent.VK_0) {
          if (answerButtonList.size() >= 10) {
            answerButtonList.get(9).doClick();
          }
        }
      }
      return false;
    }

  }

}
