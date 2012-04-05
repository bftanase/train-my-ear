package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import ro.btanase.btvalidators.BTValidator;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.mediaplayer.IMPCallback;
import ro.btanase.mediaplayer.MediaPlayer;
import ro.btanase.utils.FileUtils;

import com.google.inject.Inject;
import javax.swing.JToggleButton;

public class AddChordDialog extends JDialog implements ActionListener{

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
  private JPanel panel;
  private JToggleButton tglSlot1;
  private JToggleButton tglSlot2;
  private JToggleButton tglSlot3;
  private JToggleButton tglSlot4;
  private JToggleButton tglSlot5;
  private JLabel lblNewLabel;
  private JTextField tfPackName;
  
  private ButtonGroup buttonGroupToggle;
  
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
    setBounds(100, 100, 611, 234);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[][][grow][]", "[][][][][]"));
    {
      panel = new JPanel();
      contentPanel.add(panel, "cell 0 0 4 1,grow");
      panel.setLayout(new MigLayout("", "[120][120][120][120][120]", "[46.00,fill]"));
      {
        tglSlot1 = new JToggleButton("Acoustic by LBro");
        panel.add(tglSlot1, "cell 0 0,growx");
      }
      {
        tglSlot2 = new JToggleButton("Free Slot");
        panel.add(tglSlot2, "cell 1 0,growx");
      }
      {
        tglSlot3 = new JToggleButton("Free Slot");
        panel.add(tglSlot3, "cell 2 0,growx");
      }
      {
        tglSlot4 = new JToggleButton("Free Slot");
        panel.add(tglSlot4, "cell 3 0,growx");
      }
      {
        tglSlot5 = new JToggleButton("Free Slot");
        panel.add(tglSlot5, "cell 4 0,growx");
      }
    }
    {
      lblNewLabel = new JLabel("Sample pack name:");
      contentPanel.add(lblNewLabel, "cell 0 1");
    }
    {
      tfPackName = new JTextField();
      contentPanel.add(tfPackName, "cell 2 1,growx");
      tfPackName.setColumns(10);
    }
    {
      JLabel lblChordName = new JLabel("Chord name:");
      contentPanel.add(lblChordName, "cell 0 2 2 1,alignx left");
    }
    {
      tfChord = new JTextField();
      tfChord.setToolTipText("");
      contentPanel.add(tfChord, "cell 2 2,growx");
      tfChord.setColumns(10);
    }
    {
      {
        btnRecord = new JButton("Record");
        btnRecord.setMnemonic('r');
        btnRecord.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            onBtnRecordActionPerformed();
          }
        });
        contentPanel.add(btnRecord, "cell 0 3,alignx right");
      }
    }
    {
      btnImportFromFile = new JButton("Import from file (wav) ...");
      btnImportFromFile.setToolTipText("Wave must be PCM 44100/16 bit/stereo!");
      btnImportFromFile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onBtnImportActionPerformed();
        }
      });
      btnPlay = new JButton("Play");
      btnPlay.setMnemonic('p');
      btnPlay.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onBtnPlayActionPerformed();
        }
      });
      contentPanel.add(btnPlay, "flowx,cell 1 3");
      
          btnPlay.setEnabled(false);
      contentPanel.add(btnImportFromFile, "cell 3 3");
    }
    {
      chckbxDelayedRecording = new JCheckBox("Delayed Recording (5 sec)");
      contentPanel.add(chckbxDelayedRecording, "cell 1 4 2 1");
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
    {
      lblSelectedFile = new JLabel("");
      contentPanel.add(lblSelectedFile, "cell 3 4,alignx center");
    }
    
    // set slot buttons behaviour
    buttonGroupToggle = new ButtonGroup();
    buttonGroupToggle.add(tglSlot1);
    buttonGroupToggle.add(tglSlot2);
    buttonGroupToggle.add(tglSlot3);
    buttonGroupToggle.add(tglSlot4);
    buttonGroupToggle.add(tglSlot5);
    
    tglSlot1.addActionListener(this);
    tglSlot2.addActionListener(this);
    tglSlot3.addActionListener(this);
    tglSlot4.addActionListener(this);
    tglSlot5.addActionListener(this);
    
    // start with first slot selected
    tglSlot1.doClick();
    
    tfPackName.getDocument().addDocumentListener(new DocumentListener() {
      
      @Override
      public void removeUpdate(DocumentEvent e) {
        update();
      }
      
      @Override
      public void insertUpdate(DocumentEvent e) {
        update();
      }
      
      @Override
      public void changedUpdate(DocumentEvent e) {
        update();
      }
      
      private void update(){
        if (tglSlot1.isSelected()){
          tglSlot1.setText(tfPackName.getText());
        } else if (tglSlot2.isSelected()){
          tglSlot2.setText(tfPackName.getText());
        } else if (tglSlot3.isSelected()){
          tglSlot3.setText(tfPackName.getText());
        } else if (tglSlot4.isSelected()){
          tglSlot4.setText(tfPackName.getText());
        } else if (tglSlot5.isSelected()){
          tglSlot5.setText(tfPackName.getText());
        }
      }
    });
    
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
      }, getActiveSlot());
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
      }, getActiveSlot());

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
      mediaPlayer.setMemoryAudioStream(file, getActiveSlot());
      btnPlay.setEnabled(true);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == tglSlot1){
      tfPackName.setText(tglSlot1.getText());
    } else if(e.getSource() == tglSlot2){
      tfPackName.setText(tglSlot2.getText());
    } else if(e.getSource() == tglSlot3){
      tfPackName.setText(tglSlot3.getText());
    } else if(e.getSource() == tglSlot4){
      tfPackName.setText(tglSlot4.getText());
    } else if(e.getSource() == tglSlot5){
      tfPackName.setText(tglSlot5.getText());
    }
  }
  
  private int getActiveSlot(){
    if (tglSlot1.isSelected()){
      return 0;
    } else if (tglSlot2.isSelected()){
      return 1;
    } else if (tglSlot3.isSelected()){
      return 2;
    } else if (tglSlot4.isSelected()){
      return 3;
    } else if (tglSlot5.isSelected()){
      return 4;
    } else {
      throw new IllegalStateException("At least one button must be pressed.");
    }
  }

}
