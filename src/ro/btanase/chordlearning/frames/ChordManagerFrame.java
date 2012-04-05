package ro.btanase.chordlearning.frames;

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
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.exceptions.ConstraintException;
import ro.btanase.chordlearning.services.UserData;
import ro.btanase.mediaplayer.IMPCallback;
import ro.btanase.mediaplayer.MediaPlayer;
import ro.btanase.utils.FileUtils;
import ca.odell.glazedlists.swing.EventListModel;

import com.google.inject.Inject;

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

  /**
   * Create the frame.
   */
  @Inject
  public ChordManagerFrame(ChordDao m_chords) {
    setModal(true);
    this.m_chords = m_chords;
    setTitle("Chord Definition");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 459, 434);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[grow][120px:120px:120px,fill]", "[][][][][19.00][][grow]"));

    JLabel lblExistingChords = new JLabel("Existing Chords:");
    contentPane.add(lblExistingChords, "cell 0 0");

    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, "cell 0 1 1 6,grow");

    jlistChords = new JList();
    scrollPane.setViewportView(jlistChords);

    JButton btnNewChord = new JButton("New Chord ...");
    btnNewChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnNewActionPerformed();

      }
    });
    contentPane.add(btnNewChord, "cell 1 1,growx");

    JButton btnEditChord = new JButton("Edit Chord ...");
    btnEditChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnEditChordActionPerformed();
      }
    });
    contentPane.add(btnEditChord, "cell 1 2,growx");

    JButton btnDeleteChord = new JButton("Delete Chord");
    btnDeleteChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnDeleteChordActionPerformed();
      }
    });
    contentPane.add(btnDeleteChord, "cell 1 3,growx");

    btnPlayChord = new JButton("Play Chord");
    btnPlayChord.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnPlayChordActionPerformed();
      }
    });
    contentPane.add(btnPlayChord, "cell 1 5,growx");

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
        try {
          m_chords.deleteChord(chord);
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
      mediaPlayer.playImaFile(chord.getFileName(), new IMPCallback() {

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
      }, 0);
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
  
  
}
