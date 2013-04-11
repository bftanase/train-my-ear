package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.dao.SettingsDao;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.mediaplayer.IMPCallback;
import ro.btanase.mediaplayer.MediaPlayer;
import ro.btanase.utils.ListUtils;
import ro.btanase.utils.ReflectionUtils;

import com.google.inject.Inject;

public class ClueDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private final Color BK_COLOR = new Color(33, 98, 120);

  @Inject
  private SettingsDao settingsDao;
  private JToggleButton tglSlot1;
  private JToggleButton tglSlot2;
  private JToggleButton tglSlot3;
  private JToggleButton tglSlot4;
  private JToggleButton tglSlot5;

  @Inject 
  private MediaPlayer mediaPlayer;
  
  private Lesson lesson;

  private Logger log = Logger.getLogger(getClass());
  private JPanel jpanelSamplesContainer;

  private JDialog parent;
  /**
   * Create the dialog.
   */
  @Inject
  public ClueDialog(final JDialog dialog, Lesson lesson) {
    super(dialog);
    setModal(false);
    this.parent = dialog;
    this.lesson = lesson;
    setTitle("Play samples");
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        ((IClue) dialog).clueClosed();
      }
    });
    this.setSize(774, 298);
    
    tryToDockToParent();
    
    // int parentY = dialog.getLocation().y;
    // int parentX = dialog.getLocation().x;
    //
    // setLocation(parentX + dialog.getWidth(), parentY);

    // setBounds(100, 100, 450, 300);

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    getContentPane().setBackground(BK_COLOR);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBackground(BK_COLOR);
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[grow,center]", "[][grow]"));

    JPanel panel = new JPanel();
    panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED,
        null, null), "Sample Source", TitledBorder.LEADING, TitledBorder.TOP,
        null, new Color(255, 255, 255)));
    panel.setForeground(Color.WHITE);
    panel.setBackground(new Color(33, 98, 120));
    contentPanel.add(panel, "cell 0 0,grow");
    panel.setLayout(new MigLayout("", "[][][][][]", "[]"));

    tglSlot1 = new JToggleButton("Slot 1");
    panel.add(tglSlot1, "cell 0 0");

    tglSlot2 = new JToggleButton("Slot 2");
    panel.add(tglSlot2, "cell 1 0");

    tglSlot3 = new JToggleButton("Slot 2");
    panel.add(tglSlot3, "cell 2 0");

    tglSlot4 = new JToggleButton("Slot 2");
    panel.add(tglSlot4, "cell 3 0");

    tglSlot5 = new JToggleButton("Slot 2");
    panel.add(tglSlot5, "cell 4 0");

    ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add(tglSlot1);
    buttonGroup.add(tglSlot2);
    buttonGroup.add(tglSlot3);
    buttonGroup.add(tglSlot4);
    buttonGroup.add(tglSlot5);

    jpanelSamplesContainer = new JPanel();
    jpanelSamplesContainer.setBackground(BK_COLOR);
    contentPanel.add(jpanelSamplesContainer, "cell 0 1,alignx center,aligny center");
    jpanelSamplesContainer.setLayout(new MigLayout("wrap 8", "[]", "[]"));
  }

  private void tryToDockToParent() {

    Dimension activeDisplayScreenSize = getActiveDisplayScreenSize(parent);

    log.debug("active display screen size: " + activeDisplayScreenSize);
    
    int topParent = parent.getLocation().y;
    int leftParent = parent.getLocation().x;
    int heightParent = parent.getHeight();
    
    if (topParent + heightParent + this.getHeight() <= activeDisplayScreenSize.getHeight()){
      this.setLocation(leftParent, topParent + heightParent);
    }else if (topParent - this.getHeight() >= 0){
      this.setLocation(leftParent, topParent - this.getHeight());
    } else{
      setLocationRelativeTo(parent);      
    }
    
  }

  private Dimension getActiveDisplayScreenSize(Window wnd){
    Dimension result = new Dimension();
    GraphicsConfiguration graphConfig = parent.getGraphicsConfiguration();
    GraphicsDevice myScreen = graphConfig.getDevice();

    DisplayMode mode = myScreen.getDisplayMode();
    
    result.setSize(mode.getWidth(), mode.getHeight());
    
    return result;
  }
  
  private void setToggleButtonsNames() {
    String[] slots = settingsDao.getSlots();

    tglSlot1.setText(slots[0]);
    tglSlot2.setText(slots[1]);
    tglSlot3.setText(slots[2]);
    tglSlot4.setText(slots[3]);
    tglSlot5.setText(slots[4]);
  }

  private void checkSamplesAvailability() {
    List<Chord> chordList = lesson.getChordSequence();

    for (Chord chord : chordList) {
      if (chord.getFileName() == null) {
        tglSlot1.setEnabled(false);
      }

      if (chord.getFileName2() == null) {
        tglSlot2.setEnabled(false);
      }
      if (chord.getFileName3() == null) {
        tglSlot3.setEnabled(false);
      }
      if (chord.getFileName4() == null) {
        tglSlot4.setEnabled(false);
      }
      if (chord.getFileName5() == null) {
        tglSlot5.setEnabled(false);
      }
    }

    if (tglSlot1.isEnabled()) {
      tglSlot1.setSelected(true);
    } else if (tglSlot2.isEnabled()) {
      tglSlot2.setSelected(true);
    } else if (tglSlot3.isEnabled()) {
      tglSlot3.setSelected(true);
    } else if (tglSlot4.isEnabled()) {
      tglSlot4.setSelected(true);
    } else if (tglSlot5.isEnabled()) {
      tglSlot5.setSelected(true);
    }
  }

  @Inject
  private void postConstruct() {
    log.debug("Entering post construct");
    setToggleButtonsNames();
    checkSamplesAvailability();
    createAnswerButtons();
  }

  public void createAnswerButtons() {
    List<Chord> randomAnswers = new ArrayList<Chord>(lesson.getChordSequence());

    Comparator<Chord> comparator = new Comparator<Chord>() {

      @Override
      public int compare(Chord o1, Chord o2) {
        return o1.getChordName().compareTo(o2.getChordName());
      }

    };

    Collections.sort(randomAnswers, comparator);

    // since the lesson might be configured with the same chord multiple times
    // duplicates must be removed
    randomAnswers = ListUtils.removeDuplicates(randomAnswers, comparator);

    for (final Chord chord : randomAnswers) {
      JButton answerButton = new JButton(chord.getChordName());
      answerButton.setActionCommand(chord.getChordName());
      answerButton.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
//          String chordName = e.getActionCommand();

          List<Integer> slotList = getSelectedSlots();

          if (slotList.isEmpty()) {
            JOptionPane.showMessageDialog(ClueDialog.this,
                "You must select at least one sample source",
                "No sample source selected", JOptionPane.WARNING_MESSAGE);
            return;
          }

          mediaPlayer.stopPlayback();
          mediaPlayer.playImaFile(
              ReflectionUtils.invokeChordGetFileName(chord, slotList.get(0)),
              new IMPCallback() {

                @Override
                public void onStop() {
                  // TODO Auto-generated method stub

                }

                @Override
                public void onPlay() {
                  // TODO Auto-generated method stub

                }
              });
        }
      });

      jpanelSamplesContainer.add(answerButton, "gapleft 10");
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
  
}
