package ro.btanase.chordlearning.frames;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ro.btanase.chordlearning.ChordLearningApp;
import ro.btanase.chordlearning.dao.ChordDao;
import ro.btanase.chordlearning.dao.SettingsDao;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.exceptions.ConstraintException;
import ro.btanase.chordlearning.services.UserData;
import ro.btanase.mediaplayer.IMPCallback;
import ro.btanase.mediaplayer.MediaPlayer;
import ro.btanase.utils.FileUtils;
import ca.odell.glazedlists.swing.EventListModel;

import com.google.inject.Inject;
import javax.swing.border.TitledBorder;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.border.EtchedBorder;
import java.awt.Toolkit;

public class ChordManagerFrame extends JDialog {

  private JPanel contentPane;
  private ChordDao m_chords;
  private JList jlistChords;
  private JButton btnPlayChord;
  private Logger log = Logger.getLogger(getClass());

  @Inject
  private MediaPlayer mediaPlayer;
  @Inject
  private UserData userData;
  
  @Inject
  private SettingsDao settingsDao;
  
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JRadioButton rbSlot1;
  private JRadioButton rbSlot2;
  private JRadioButton rbSlot3;
  private JRadioButton rbSlot4;
  private JRadioButton rbSlot5;
  
  private final Color BK_COLOR = new Color(33, 98, 120);  

  /**
   * Create the frame.
   */
  @Inject
  public ChordManagerFrame(ChordDao m_chords) {
    setIconImage(Toolkit.getDefaultToolkit().getImage(ChordManagerFrame.class.getResource("/res/tme_small.png")));
    setModal(true);
    this.m_chords = m_chords;
    setTitle("Chord Definition");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 459, 434);
    contentPane = new JPanel();
    contentPane.setBackground(BK_COLOR);
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[200.00,grow][120px:120px:180px,grow,fill]", "[][][][][][grow]"));

    JLabel lblExistingChords = new JLabel("Existing Chords:");
    lblExistingChords.setForeground(new Color(255, 255, 255));
    contentPane.add(lblExistingChords, "cell 0 0");

    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, "cell 0 1 1 5,grow");

    jlistChords = new JList();
    scrollPane.setViewportView(jlistChords);

    JButton btnNewChord = new JButton("New Chord ...");
    btnNewChord.setMnemonic('n');
    btnNewChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnNewActionPerformed();

      }
    });
    contentPane.add(btnNewChord, "cell 1 1,growx");

    JButton btnEditChord = new JButton("Edit Chord ...");
    btnEditChord.setMnemonic('e');
    btnEditChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnEditChordActionPerformed();
      }
    });
    contentPane.add(btnEditChord, "cell 1 2,growx");

    JButton btnDeleteChord = new JButton("Delete Chord");
    btnDeleteChord.setMnemonic('d');
    btnDeleteChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnDeleteChordActionPerformed();
      }
    });
    contentPane.add(btnDeleteChord, "cell 1 3,growx");

    JPanel panel = new JPanel();
    panel.setForeground(new Color(255, 255, 255));
    panel.setBackground(BK_COLOR);
    panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Test Samples", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(255, 255, 255)));
    contentPane.add(panel, "cell 1 4,grow");
    panel.setLayout(new MigLayout("", "[grow,fill]", "[][][][][][]"));

    rbSlot1 = new JRadioButton("Slot 1");
    rbSlot1.setForeground(new Color(255, 255, 255));
    rbSlot1.setBackground(BK_COLOR);
    rbSlot1.setSelected(true);
    buttonGroup.add(rbSlot1);
    panel.add(rbSlot1, "cell 0 0,growx");

    rbSlot2 = new JRadioButton("Slot 2");
    rbSlot2.setForeground(new Color(255, 255, 255));
    rbSlot2.setBackground(BK_COLOR);
    buttonGroup.add(rbSlot2);
    panel.add(rbSlot2, "cell 0 1");

    rbSlot3 = new JRadioButton("Slot 3");
    rbSlot3.setForeground(new Color(255, 255, 255));
    rbSlot3.setBackground(BK_COLOR);
    buttonGroup.add(rbSlot3);
    panel.add(rbSlot3, "cell 0 2");

    rbSlot4 = new JRadioButton("Slot 4");
    rbSlot4.setForeground(new Color(255, 255, 255));
    rbSlot4.setBackground(BK_COLOR);
    buttonGroup.add(rbSlot4);
    panel.add(rbSlot4, "cell 0 3");

    rbSlot5 = new JRadioButton("Slot 5");
    rbSlot5.setForeground(new Color(255, 255, 255));
    rbSlot5.setBackground(BK_COLOR);
    buttonGroup.add(rbSlot5);
    panel.add(rbSlot5, "cell 0 4");

    btnPlayChord = new JButton("Play Chord");
    btnPlayChord.setMnemonic('p');
    panel.add(btnPlayChord, "cell 0 5,growx");
    btnPlayChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnPlayChordActionPerformed();
      }
    });

    initModels();
  }

  private void onBtnEditChordActionPerformed() {
    Chord selectedChord = (Chord) jlistChords.getSelectedValue();

    if (selectedChord == null) {
      return;
    }

    // empty Recording buffer before going to edit mode
    for (int i = 0; i < MediaPlayer.NO_SLOTS; i++) {
      mediaPlayer.emptyRecordBuffer(i);
    }

    AddChordDialog chordDialog = new AddChordDialog(selectedChord, new IDialog<Chord>() {

      @Override
      public void onSubmit(Chord chord) {
        if (checkForNonEmptyBuffers()) {

          mediaPlayer.stopPlayback();

          for (int i = 0; i < MediaPlayer.NO_SLOTS; i++) {
            if (!mediaPlayer.isBufferEmpty(i)) {
              try {
                saveChord(chord, i);
              } catch (Exception e) {
                log.error("reflection failed", e);
                throw new RuntimeException(e);
              }
            }
          }

        }

        m_chords.updateChord(chord);
      }

      @Override
      public void onCancel(Chord object) {
        // TODO Auto-generated method stub

      }
    });
    ChordLearningApp.getInjector().injectMembers(chordDialog);

    chordDialog.setLocationRelativeTo(this);

    chordDialog.setVisible(true);
  }

  private void onBtnDeleteChordActionPerformed() {
    Chord chord = (Chord) jlistChords.getSelectedValue();
    if (chord != null) {
      int result = JOptionPane.showConfirmDialog(this, "Delete this chord?", "Cord deletion",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

      if (JOptionPane.YES_OPTION == result) {
        int oldSelection = jlistChords.getSelectedIndex();
        
        try {
          m_chords.deleteChord(chord);
          
          if (oldSelection > 0){
            oldSelection--;
            jlistChords.setSelectedIndex(oldSelection);
          }
        } catch (ConstraintException e) {
          JOptionPane.showMessageDialog(this, "Cannot delete this chord. It is used in other lessons",
              "Error deleting chord", JOptionPane.WARNING_MESSAGE);
        }
      }
    }

  }

  private void onBtnPlayChordActionPerformed() {
    if (jlistChords.getSelectedValue() == null) {
      return;
    }

    Chord chord = (Chord) jlistChords.getSelectedValue();
    log.debug("Entering play event...");
    if (btnPlayChord.getText().startsWith("Play")) {
      log.debug("Play path chosen - button name is Play...");

      String fileName = invokeGetFileNameForSlot(chord, getActiveSlot());

      if (fileName == null){
        JOptionPane.showMessageDialog(ChordManagerFrame.this, "This slot is empty. Nothing to play",
            "Slot empty",
            JOptionPane.ERROR_MESSAGE);
        return;
      }
      
      mediaPlayer.playImaFile(fileName, new IMPCallback() {

        @Override
        public void onStop() {
          EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
              btnPlayChord.setText("Play Chord");
            }
          });
        }

        @Override
        public void onPlay() {
          EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
              btnPlayChord.setText("Stop");
            }
          });
        }
      });
    } else {
      System.out.println("stopping ...");
      mediaPlayer.stopPlayback();
    }
  }

  private void onBtnNewActionPerformed() {
    AddChordDialog addChordDialog = new AddChordDialog(new IDialog<Chord>() {

      @Override
      public void onSubmit(Chord chord) {
        if (checkForNonEmptyBuffers()) {
          mediaPlayer.stopPlayback();

          for (int i = 0; i < MediaPlayer.NO_SLOTS; i++) {
            if (!mediaPlayer.isBufferEmpty(i)) {
              try {
                saveChord(chord, i);
              } catch (Exception e) {
                log.error("reflection failed", e);
                throw new RuntimeException(e);
              }
            }
          }
          m_chords.addChord(chord);
        }
      }

      @Override
      public void onCancel(Chord object) {
        // TODO Auto-generated method stub

      }

    });

    ChordLearningApp.getInjector().injectMembers(addChordDialog);
    addChordDialog.setLocationRelativeTo(this);
    addChordDialog.setVisible(true);

  }

  private void initModels() {
    EventListModel<Chord> chordListModel = new EventListModel<Chord>(m_chords.getAllChords());
    jlistChords.setModel(chordListModel);

  }

  /**
   * returns true if at least one buffer is not empty
   * 
   * @return
   */
  private boolean checkForNonEmptyBuffers() {
    boolean result = false;

    for (int i = 0; i < MediaPlayer.NO_SLOTS; i++) {
      if (!mediaPlayer.isBufferEmpty(i)) {
        result = true;
        break;
      }
    }

    return result;
  }

  private void saveChord(Chord chord, int slot) throws Exception {
    // chord.setFileName(FileUtils.safeName(chord.getChordName()) + ".ima");
    String fileName = FileUtils.safeName(chord.getChordName()) + "_slot_" + slot + "_" + ".ima";

    // kinda of a dirty hack. We'll use reflection to invoke the method
    // corresponding to the passed slot
    String setFileNameMethodName = "";
    if (slot == 0) {
      setFileNameMethodName = "setFileName";
    } else {
      setFileNameMethodName = "setFileName" + (slot + 1);
    }

    Method setFileNameMethod = chord.getClass().getMethod(setFileNameMethodName, String.class);

    String filePath = userData.getMediaFolder() + File.separator + fileName;

    File file = new File(filePath);

    int i = 1;
    log.debug("Checking for file: " + file.getAbsolutePath());
    while (file.exists()) {

      String baseName = FilenameUtils.getBaseName(fileName);
      String extension = FilenameUtils.getExtension(fileName);

      fileName = baseName + i + "." + extension;
      file = new File(userData.getMediaFolder() + File.separator + fileName);
      i++;
    }

    mediaPlayer.saveToFile(userData.getMediaFolder() + File.separator + fileName, slot);

    setFileNameMethod.invoke(chord, fileName); // this is equivalent to
                                               // chord.setFileName#(fileName)
  }

  private int getActiveSlot() {
    if (rbSlot1.isSelected()) {
      return 0;
    } else if (rbSlot2.isSelected()) {
      return 1;
    } else if (rbSlot3.isSelected()) {
      return 2;
    } else if (rbSlot4.isSelected()) {
      return 3;
    } else if (rbSlot5.isSelected()) {
      return 4;
    } else {
      throw new IllegalStateException("Invalid selection on slot radio buttons");
    }
  }

  private String invokeGetFileNameForSlot(Chord chord, int slot) {
    
    String result = "";
    String getFileNameMethodName = "";
    if (slot == 0) {
      getFileNameMethodName = "getFileName";
    } else {
      getFileNameMethodName = "getFileName" + (slot + 1);
    }
    try {
      
      log.debug("attempting to bulid Chord method: " + getFileNameMethodName);
      Method getFileNameMethod = chord.getClass().getMethod(getFileNameMethodName);

      result = (String) getFileNameMethod.invoke(chord); 
    } catch (Exception e) {
      log.error("reflection failure", e);
      throw new RuntimeException(e);
    }
    
    return result;
  }

  @Inject // == @PostConstruct
  private void postConstruct(){
    setSlotNames(settingsDao.getSlots());
  }

  private void setSlotNames(String[] slots) {
    rbSlot1.setText(slots[0]);
    rbSlot2.setText(slots[1]);
    rbSlot3.setText(slots[2]);
    rbSlot4.setText(slots[3]);
    rbSlot5.setText(slots[4]);
  }
}
