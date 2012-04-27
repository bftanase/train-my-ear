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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.dao.ScoreDao;
import ro.btanase.chordlearning.dao.SettingsDao;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.ExerciseResult;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.Score;
import ro.btanase.mediaplayer.IMPCallback;
import ro.btanase.mediaplayer.MediaPlayer;
import ro.btanase.utils.ListUtils;
import ro.btanase.utils.ReflectionUtils;

import com.google.inject.Inject;
import javax.swing.JToggleButton;
import javax.swing.border.EtchedBorder;
import javax.swing.SwingConstants;
import java.awt.Toolkit;

public class LessonSSRFrame extends JDialog implements ActionListener {
  private JTextField tfLessonName;
  private JTextField tfProgress;
  private Lesson lesson;
  private Deque<Chord> exerciseStack;
  private Chord activeChord;
  private JButton btnPlay;
  private JPanel jpanelAnswerContainer;

  private Logger log = Logger.getLogger(getClass());
  private JLabel lblAnswerResult;
  private JButton btnNextExercise;
  private List<JButton> answerButtonList = new ArrayList<JButton>();
  
  private final Color BK_COLOR = new Color(33, 98, 120);

  private Score score;
  private List<ExerciseResult> exerciseResultList;
  private boolean exerciseFailed = false;
  private ScoreDao m_scores;

  @Inject
  private SettingsDao settingsDao;

  @Inject
  private MediaPlayer mediaPlayer;
  /**
   * this is set randomly from selected slots; it should
     be reset on exercise start and if a slot button is changed
   */
  private int slotToPlay; 
  
  private JScrollPane scrollPane;
  private JPanel panel_1;
  private JButton btnStop;
  private KeyboardFocusManager manager;
  private MyDispatcher dispatcher;
  private JPanel panel_2;
  private JToggleButton tglSlot1;
  private JToggleButton tglSlot2;
  private JToggleButton tglSlot3;
  private JToggleButton tglSlot4;
  private JToggleButton tglSlot5;
  private JPanel panel_3;
  private JToggleButton tglClueMode;

  private Map<JComponent, Boolean> componentMap;
  private JLabel lblClueMode;
  
  private Map<JToggleButton, Boolean> toggleButtonsSelectionStatus;
  private List<Integer> lastSelectedSlots = new ArrayList<Integer>();
  
  
  /**
   * Create the dialog.
   */
  public LessonSSRFrame(Lesson lesson) {
    setIconImage(Toolkit.getDefaultToolkit().getImage(LessonSSRFrame.class.getResource("/res/tme_small.png")));
    getContentPane().setBackground(new Color(33, 98, 120));
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        manager.removeKeyEventDispatcher(dispatcher);
      }
    });
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);
    this.lesson = lesson;
    setTitle("Single Sound Recognition");
    setBounds(100, 100, 774, 351);
    getContentPane().setLayout(new MigLayout("", "[][grow][grow][]", "[][][][][grow]"));
    
    panel_2 = new JPanel();
    panel_2.setForeground(new Color(255, 255, 255));
    panel_2.setBackground(BK_COLOR);
    panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Sound sources", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(255, 255, 255)));
    getContentPane().add(panel_2, "cell 0 0 1 2,grow");
    panel_2.setLayout(new MigLayout("", "[][][][][]", "[]"));
    
    tglSlot1 = new JToggleButton("Slot 1");
    
    panel_2.add(tglSlot1, "cell 0 0");
    
    tglSlot2 = new JToggleButton("Slot 2");
    panel_2.add(tglSlot2, "cell 1 0");
    
    tglSlot3 = new JToggleButton("Slot 3");
    panel_2.add(tglSlot3, "cell 2 0");
    
    tglSlot4 = new JToggleButton("Slot 4");
    panel_2.add(tglSlot4, "cell 3 0");
    
    tglSlot5 = new JToggleButton("Slot 5");
    panel_2.add(tglSlot5, "cell 4 0");
    
    tglSlot1.addActionListener(this);
    tglSlot2.addActionListener(this);
    tglSlot3.addActionListener(this);
    tglSlot4.addActionListener(this);
    tglSlot5.addActionListener(this);
    
    panel_3 = new JPanel();
    panel_3.setBackground(BK_COLOR);
    getContentPane().add(panel_3, "cell 1 0 3 2,grow");
    panel_3.setLayout(new MigLayout("", "[][grow,fill]", "[][]"));

    JLabel lblLessonName = new JLabel("Lesson name:");
    lblLessonName.setForeground(new Color(255, 255, 255));
    panel_3.add(lblLessonName, "cell 0 0");

    tfLessonName = new JTextField();
    panel_3.add(tfLessonName, "cell 1 0,growx");
    tfLessonName.setEditable(false);
    // tfLessonName.setColumns(10);

    JLabel lblLessonProgress = new JLabel("Lesson Progress:");
    lblLessonProgress.setForeground(new Color(255, 255, 255));
    panel_3.add(lblLessonProgress, "cell 0 1");

    tfProgress = new JTextField();
    panel_3.add(tfProgress, "flowx,cell 1 1,growx");
    tfProgress.setText("1/5");
    tfProgress.setEditable(false);
    
    tglClueMode = new JToggleButton("Clue Mode");
    tglClueMode.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        disableAllControls(tglClueMode.isSelected());
          
        
      }
    });
    panel_3.add(tglClueMode, "cell 1 1");

    panel_1 = new JPanel();
    panel_1.setBackground(BK_COLOR);
    getContentPane().add(panel_1, "cell 0 2 4 1,grow");
    panel_1.setLayout(new MigLayout("", "[58px,grow][grow]", "[26px]"));

    btnPlay = new JButton("Play");
    btnPlay.setMnemonic(KeyEvent.VK_P);
    panel_1.add(btnPlay, "cell 0 0,alignx right,aligny top");

    btnStop = new JButton("Stop");
    btnStop.setMnemonic(KeyEvent.VK_S);
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
    
    lblClueMode = new JLabel("You are now in \"Clue\" Mode. Press each chord name to listen to it!");
    lblClueMode.setFont(new Font("Tahoma", Font.PLAIN, 16));
    lblClueMode.setForeground(new Color(255, 131, 0));
    lblClueMode.setHorizontalAlignment(SwingConstants.CENTER);
    getContentPane().add(lblClueMode, "cell 0 3 4 1,alignx center");

    scrollPane = new JScrollPane();
    getContentPane().add(scrollPane, "cell 0 4 4 1,grow");

    JPanel panel = new JPanel();
    panel.setBackground(BK_COLOR);
    scrollPane.setViewportView(panel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Choose Answer", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(255, 255, 255)));
    panel.setLayout(new MigLayout("", "[][grow][]", "[][grow][25px:25px:25px]"));

    jpanelAnswerContainer = new JPanel();
    jpanelAnswerContainer.setBackground(BK_COLOR);
    panel.add(jpanelAnswerContainer, "cell 1 1 2 1,alignx center,aligny center");
    jpanelAnswerContainer.setLayout(new MigLayout("wrap 8", "[]", "[]"));

    lblAnswerResult = new JLabel("");
    lblAnswerResult.setFont(new Font("Tahoma", Font.BOLD, 14));
    panel.add(lblAnswerResult, "flowx,cell 1 2,alignx center");

    btnNextExercise = new JButton("Next Exercise");
    btnNextExercise.setMnemonic(KeyEvent.VK_N);
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

    // save to a hasmap the controls that will enable/disable when switching to "Clue Mode"
    componentMap = new HashMap<JComponent, Boolean>();
    componentMap.put(btnNextExercise, btnNextExercise.isEnabled());
    componentMap.put(btnPlay, btnPlay.isEnabled());
    componentMap.put(btnStop, btnStop.isEnabled());
//    componentMap.put(tglSlot1, tglSlot1.isEnabled());
//    componentMap.put(tglSlot2, tglSlot2.isEnabled());
//    componentMap.put(tglSlot3, tglSlot3.isEnabled());
//    componentMap.put(tglSlot4, tglSlot4.isEnabled());
//    componentMap.put(tglSlot5, tglSlot5.isEnabled());
    
    lblClueMode.setVisible(false);

    toggleButtonsSelectionStatus = new HashMap<JToggleButton, Boolean>();
    toggleButtonsSelectionStatus.put(tglSlot1, tglSlot1.isSelected());
    toggleButtonsSelectionStatus.put(tglSlot2, tglSlot2.isSelected());
    toggleButtonsSelectionStatus.put(tglSlot3, tglSlot3.isSelected());
    toggleButtonsSelectionStatus.put(tglSlot4, tglSlot4.isSelected());
    toggleButtonsSelectionStatus.put(tglSlot5, tglSlot5.isSelected());
    
  }

  protected void disableAllControls(boolean selected) {
    Set<JComponent> keys = componentMap.keySet();
    Set<JToggleButton> toggleKeys = toggleButtonsSelectionStatus.keySet();
    
    if (selected == true){
//      tglClueMode.setBackground(Color.RED);
      // save existing state
      for (JComponent jComponent : keys) {
        componentMap.put(jComponent, jComponent.isEnabled());
      }
      
      for (JToggleButton jToggleButton : toggleKeys) {
        toggleButtonsSelectionStatus.put(jToggleButton, jToggleButton.isSelected());
      }
      
      
      // disable all controls
      for (JComponent jComponent : keys) {
        jComponent.setEnabled(false);
      }
      lblClueMode.setVisible(true);
    } else {
      for (JComponent jComponent : keys) {
        jComponent.setEnabled(componentMap.get(jComponent));
      }
      lblClueMode.setVisible(false);
      
      for (JToggleButton jToggleButton : toggleKeys) {
        jToggleButton.setSelected(toggleButtonsSelectionStatus.get(jToggleButton));
      }
      
      
    }
    
  }

  private void onBtnStopActionPerformed() {
    mediaPlayer.stopPlayback();

  }

  private void onBtnNextExerciseActionPerformed() {
    if (exerciseStack.isEmpty()) {
      TestResultFrame trf = new TestResultFrame(this, score);
      trf.setLocationRelativeTo(this);
      trf.setVisible(true);
    } else {
      startNextExercise();
      mediaPlayer.stopPlayback();
      btnPlay.doClick();
    }

  }

  private void onBtnPlayActionPerformed() {
    // if (btnPlay.getText().startsWith("Play")) {
    // mediaPlayer.playImaFile(activeChord.getFileName(), new IMPCallback() {
    //
    // @Override
    // public void onStop() {
    // btnPlay.setText("Play");
    // }
    //
    // @Override
    // public void onPlay() {
    // btnPlay.setText("Stop");
    // }
    // });
    // } else {
    // mediaPlayer.stopPlayback();
    // }

    List<Integer> selectedSlots = getSelectedSlots();

    if (!getSelectedSlots().equals(lastSelectedSlots)){
      randomizeSlot();
      lastSelectedSlots = getSelectedSlots();
    }
    
    
    if (selectedSlots.isEmpty()){
      JOptionPane.showMessageDialog(LessonSSRFrame.this, "You must select at least one sound source",
            "No samples selected", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    mediaPlayer.stopPlayback();

    String methodName = "";
    String fileToPlay = "";
    if (slotToPlay == 0){
      methodName = "getFileName";
    } else {
      methodName = "getFileName" + (slotToPlay + 1);
    }
    try {
      Method method = activeChord.getClass().getMethod(methodName, null); 
      fileToPlay = (String) method.invoke(activeChord, null);
    }catch (Exception e) {
      log.error("Reflection failure", e);
      throw new RuntimeException(e);
    }
    
    mediaPlayer.playImaFile(fileToPlay, new IMPCallback() {

      @Override
      public void onStop() {

      }

      @Override
      public void onPlay() {

      }
    });

  }

  private void initModels() {
    tfLessonName.setText(lesson.getLessonName());
    exerciseStack = new LinkedList<Chord>(lesson.randomize());
    startNextExercise();
    exerciseResultList = new ArrayList<ExerciseResult>();
    btnNextExercise.setText("Next Exercise");

  }

  private void startNextExercise() {
    jpanelAnswerContainer.removeAll();
    jpanelAnswerContainer.revalidate();
    activeChord = exerciseStack.pop();

    tfProgress.setText((lesson.getNoQuestions() - exerciseStack.size()) + "/" + lesson.getNoQuestions());

    generateAnswerButtons();
    lblAnswerResult.setText("");
    btnNextExercise.setVisible(false);
    setEnabledAllbuttons(true);
    exerciseFailed = false;
    randomizeSlot();
  }

  private void generateAnswerButtons() {

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

    for (final Chord chord : randomAnswers) {
      JButton answerButton = new JButton(chord.getChordName());
      answerButton.setActionCommand(chord.getChordName());
      answerButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          String chordName = e.getActionCommand();
          
          if (tglClueMode.isSelected()){
            List<Integer> slotList = getSelectedSlots();
            Collections.shuffle(slotList);
            
            mediaPlayer.stopPlayback();
            mediaPlayer.playImaFile(ReflectionUtils.invokeChordGetFileName(chord, slotList.get(0)), new IMPCallback() {
              
              @Override
              public void onStop() {
                // TODO Auto-generated method stub
                
              }
              
              @Override
              public void onPlay() {
                // TODO Auto-generated method stub
                
              }
            });
            
            
            
          }else {
          
            if (chordName.equals(activeChord.getChordName())) {
              log.info("Answer correct: " + chordName);
              lblAnswerResult.setForeground(Color.GREEN);
              lblAnswerResult.setText("The answer is correct!");
              btnNextExercise.setVisible(true);
              setEnabledAllbuttons(false);
              if (!exerciseFailed) {
                exerciseResultList.add(new ExerciseResult(activeChord, true));
              }
  
              if (exerciseStack.isEmpty()) {
                score = new Score(lesson, exerciseResultList);
                m_scores.addScore(score);
  
                btnNextExercise.setText("Finish");
              }
  
            } else {
              log.info("Incorrect answer: " + chordName);
              lblAnswerResult.setForeground(Color.RED);
              lblAnswerResult.setText("The answer is incorrect. Please try again!");
              btnNextExercise.setVisible(false);
  
              if (!exerciseFailed) {
                exerciseResultList.add(new ExerciseResult(activeChord, false));
                exerciseFailed = true;
              }
            }
          }
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
          log.debug("Play button programmatically clicked");
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

  @Inject
  private void postConstruct(){
    setToggleButtonsNames();
    checkSamplesAvailability();
  }

  private void setToggleButtonsNames() {
    String[] slots = settingsDao.getSlots();
    
    tglSlot1.setText(slots[0]);
    tglSlot2.setText(slots[1]);
    tglSlot3.setText(slots[2]);
    tglSlot4.setText(slots[3]);
    tglSlot5.setText(slots[4]);
  }
  
  private void checkSamplesAvailability(){
    List<Chord> chordList = lesson.getChordSequence();
    
    for (Chord chord : chordList) {
      if (chord.getFileName() == null){
        tglSlot1.setEnabled(false);
      }
      
      if (chord.getFileName2() == null){
        tglSlot2.setEnabled(false);
      }
      if (chord.getFileName3() == null){
        tglSlot3.setEnabled(false);
      }
      if (chord.getFileName4() == null){
        tglSlot4.setEnabled(false);
      }
      if (chord.getFileName5() == null){
        tglSlot5.setEnabled(false);
      }
    }
    
    if (tglSlot1.isEnabled()){
      tglSlot1.setSelected(true);
    }else if (tglSlot2.isEnabled()){
      tglSlot2.setSelected(true);
    }else if (tglSlot3.isEnabled()){
      tglSlot3.setSelected(true);
    }else if (tglSlot4.isEnabled()){
      tglSlot4.setSelected(true);
    }else if (tglSlot5.isEnabled()){
      tglSlot5.setSelected(true);
    }
  }
  
  private List<Integer> getSelectedSlots(){
    List<Integer> selectedSlots = new ArrayList<Integer>();
    
    if (tglSlot1.isSelected()){
      selectedSlots.add(0);
    }
    if (tglSlot2.isSelected()){
      selectedSlots.add(1);
    }
    if (tglSlot3.isSelected()){
      selectedSlots.add(2);
    }
    if (tglSlot4.isSelected()){
      selectedSlots.add(3);
    }
    if (tglSlot5.isSelected()){
      selectedSlots.add(4);
    }
    
    return selectedSlots;
    
  }
  
  private void randomizeSlot(){
    if (!getSelectedSlots().isEmpty()){
      List<Integer> selectedSlots = getSelectedSlots();
      Collections.shuffle(selectedSlots);
      slotToPlay = selectedSlots.get(0);    
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
//    Object source = evt.getSource();
//    
//    // If any button is pressed select a different slot to play
//    if (source == tglSlot1 || source == tglSlot2 || source == tglSlot3 ||
//        source == tglSlot4 || source == tglSlot5){
//      randomizeSlot();
//    }
    
  }
}
