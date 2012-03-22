package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ro.btanase.btvalidators.BTValidator;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.mediaplayer.IMPCallback;
import ro.btanase.mediaplayer.MediaPlayer;
import ro.btanase.utils.FileUtils;

import com.google.inject.Inject;

public class AddChordDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField tfChord;
  private JButton btnRecord;
  private JButton btnPlay;
  private JCheckBox chckbxDelayedRecording;
  private IDialog<Chord> dialogCallback;
  private Chord editingChord;
  private JButton btnImportFromFile;
  
  @Inject 
  private MediaPlayer mediaPlayer;
  private static String fileChooserPath ="./";
  
  
  private Logger log = Logger.getLogger(getClass());
  private JLabel lblSelectedFile;
  
  /**
   * This constructor should be used when editing chords
   * 
   * @param chord
   * @param dialogCallback
   */
  public AddChordDialog(Chord chord, IDialog<Chord> dialogCallback) {
    this(dialogCallback);
    this.editingChord = chord;
    tfChord.setText(chord.getChordName());
    
  }

  /**
   * Create the dialog.
   * 
   * @wbp.parser.constructor
   */
  public AddChordDialog(IDialog<Chord> dialogCallback) {
    setModal(true);
    this.dialogCallback = dialogCallback;
    setTitle("Add new chord");
    setBounds(100, 100, 447, 197);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[][][grow][]", "[][][][]"));
    {
      JLabel lblChordName = new JLabel("Chord name:");
      contentPanel.add(lblChordName, "cell 1 1,alignx trailing");
    }
    {
      tfChord = new JTextField();
      tfChord.setToolTipText("");
      contentPanel.add(tfChord, "cell 2 1 2 1,growx");
      tfChord.setColumns(10);
    }
    {
      btnRecord = new JButton("Record");
      btnRecord.setMnemonic('r');
      btnRecord.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onBtnRecordActionPerformed();
        }
      });
      contentPanel.add(btnRecord, "cell 1 2");
    }
    {
      btnPlay = new JButton("Play");
      btnPlay.setMnemonic('p');
      btnPlay.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onBtnPlayActionPerformed();
        }
      });
      contentPanel.add(btnPlay, "flowx,cell 2 2");
    }
    {
      btnImportFromFile = new JButton("Import from file (wav) ...");
      btnImportFromFile.setToolTipText("Wave must be PCM 44100/16 bit/stereo!");
      btnImportFromFile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onBtnImportActionPerformed();
        }
      });
      contentPanel.add(btnImportFromFile, "cell 3 2");
    }
    {
      chckbxDelayedRecording = new JCheckBox("Delayed Recording (5 sec)");
      contentPanel.add(chckbxDelayedRecording, "cell 1 3 2 1");
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            onBtnOkActionPerformed();

          }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            onBtnCancelActionPerformed();
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }

    btnPlay.setEnabled(false);
    {
      lblSelectedFile = new JLabel("");
      contentPanel.add(lblSelectedFile, "cell 3 3,alignx center");
    }
  }

  private void onBtnCancelActionPerformed() {
    this.dispose();

  }

  private void onBtnPlayActionPerformed() {

    if (btnPlay.getText().startsWith("Play")) {
      mediaPlayer.playFromMemory(new IMPCallback() {

        @Override
        public void onStop() {
          btnPlay.setText("Play");
          btnRecord.setEnabled(true);
        }

        @Override
        public void onPlay() {
          btnPlay.setText("Stop");
          btnRecord.setEnabled(false);
        }
      });
    } else {
      mediaPlayer.stopPlayback();
    }

  }

  private void onBtnRecordActionPerformed() {
    if (btnRecord.getText().startsWith("Record") && chckbxDelayedRecording.isSelected()) {
      new TimerProgressDialog();
    }

    if (btnRecord.getText().startsWith("Record")) {
      mediaPlayer.recordToMemoryBuffer(new IMPCallback() {

        @Override
        public void onStop() {
          btnRecord.setText("Record");
          btnPlay.setEnabled(true);
        }

        @Override
        public void onPlay() {
          btnRecord.setText("Stop");
          btnPlay.setEnabled(false);
        }
      });

    } else {
      mediaPlayer.stopRecording();
    }

  }

  private void onBtnOkActionPerformed() {
    String chordName = tfChord.getText();

    try {
      // BTValidator.input(chordName).required().checkRegex("[^/\\|\":*?<>]*").validateWithException();
      BTValidator.input(chordName).required().validateWithException();
      String fileName = FileUtils.safeName(chordName) + ".ima";
      if (editingChord == null) {
        Chord chord = new Chord(chordName, fileName);
        dialogCallback.onSubmit(chord);
      } else {
        editingChord.setChordName(chordName);
        dialogCallback.onSubmit(editingChord);
      }
      this.dispose();
    } catch (Exception e) {
      log.debug("Error during form submit", e);
      JOptionPane.showMessageDialog(AddChordDialog.this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

  }

  public JTextField getTfChord() {
    return tfChord;
  }

  private void onBtnImportActionPerformed() {
    JFileChooser fileChooser = new JFileChooser(fileChooserPath);
    
    fileChooser.showOpenDialog(AddChordDialog.this);
    if (fileChooser.getSelectedFile()!=null){
      File file = fileChooser.getSelectedFile();
      log.debug("selected file: " + file.getPath());
      fileChooserPath = file.getParent();
      lblSelectedFile.setText(file.getName());
      log.debug("fileChooserPath: " + fileChooserPath);
      mediaPlayer.setMemoryAudioStream(file);
      btnPlay.setEnabled(true);
    }
  }

}
