package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import ro.btanase.btvalidators.BTValidationException;
import ro.btanase.btvalidators.BTValidator;
import ro.btanase.chordlearning.dao.ChordDao;
import ro.btanase.chordlearning.dao.LessonDao;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.LessonType;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;

import com.google.inject.Inject;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import org.hsqldb.lib.tar.RB;
import java.awt.GridLayout;

public class AddLessonFrame extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private JTextField tfLessonName;
  private ChordDao m_chords;
  // private LessonDao m_lessons;
  private Lesson editingLesson;
  private JList jlistChordPool;
  private JButton btnMoveSelected;
  private JButton btnRemoveSelected;
  private JList jlistSelectedChords;
  private LessonDao m_lessons;
  private EventList<Chord> selectedChords;
  private JTextField tfNoQuestions;
  /**
   * @wbp.nonvisual location=432,429
   */
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private JRadioButton rbSSR;
  private JRadioButton rbCPR;
  private JTextField tfNoChordsInProgression;
  private JTextField tfChordDelay;
  private JLabel lblChordDelay;
  private JLabel lblNoChordsInProgression;

  /**
   * Create the dialog.
   */
  @Inject
  public AddLessonFrame(ChordDao m_chords) {
    setModal(true);
    this.m_chords = m_chords;
    // this.m_lessons = m_lessons;

    setTitle("Add/Edit Lesson");
    setBounds(100, 100, 775, 500);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[][100px:n,grow][][100px:n,grow]", "[][][grow][][grow]"));
    {
      JLabel lblLessonName = new JLabel("Lesson Name:");
      contentPanel.add(lblLessonName, "cell 0 0,alignx left");
    }
    {
      tfLessonName = new JTextField();
      contentPanel.add(tfLessonName, "cell 1 0,growx");
      tfLessonName.setColumns(10);
    }
    {
      JLabel lblChordPool = new JLabel("Chord Pool");
      contentPanel.add(lblChordPool, "cell 0 1");
    }
    {
      JLabel lblSelectedChords = new JLabel("Selected Chords");
      contentPanel.add(lblSelectedChords, "cell 3 1");
    }
    {
      JScrollPane scrollPane = new JScrollPane();
      contentPanel.add(scrollPane, "cell 0 2 2 1,grow");
      {
        jlistChordPool = new JList();
        scrollPane.setViewportView(jlistChordPool);
      }
    }
    {
      btnMoveSelected = new JButton("--->");
      btnMoveSelected.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onBtnMoveSelectedActionPerformed();
        }
      });
      btnMoveSelected.setToolTipText("Move Selected items");
      contentPanel.add(btnMoveSelected, "flowy,cell 2 2,growx");
    }
    {
      JScrollPane scrollPane = new JScrollPane();
      contentPanel.add(scrollPane, "cell 3 2,grow");
      {
        jlistSelectedChords = new JList();
        scrollPane.setViewportView(jlistSelectedChords);
      }
    }
    {
      btnRemoveSelected = new JButton("<---");
      btnRemoveSelected.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          onBtnRemoveSelectedActionPerformed();
        }
      });
      contentPanel.add(btnRemoveSelected, "cell 2 2,growx");
    }
    {
      JPanel panel = new JPanel();
      panel.setBorder(new TitledBorder(null, "Lesson Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      contentPanel.add(panel, "cell 0 4 2 1,grow");
      panel.setLayout(new MigLayout("", "[][][grow]", "[][]"));
      {
        JLabel lblNumberOfQuestions = new JLabel("Number of questions in the lesson: ");
        panel.add(lblNumberOfQuestions, "cell 1 1,alignx trailing");
      }
      {
        tfNoQuestions = new JTextField();
        panel.add(tfNoQuestions, "cell 2 1,alignx left");
        tfNoQuestions.setColumns(10);
      }
    }
    {
      JPanel panel = new JPanel();
      panel.setBorder(new TitledBorder(null, "Lesson type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      contentPanel.add(panel, "cell 2 4 2 1,grow");
      panel.setLayout(new MigLayout("", "[grow]", "[][][grow]"));
      {
        rbSSR = new JRadioButton("Single Sound Recognition");
        panel.add(rbSSR, "cell 0 0");
      }
      {
        rbCPR = new JRadioButton("Chord Progression Recognition");
        rbCPR.addChangeListener(new ChangeListener() {
          
          @Override
          public void stateChanged(ChangeEvent e) {
            setCPRFieldsEnabledStatus();
            
          }
        });
        panel.add(rbCPR, "cell 0 1");
      }
      {
        JPanel panel_1 = new JPanel();
        panel.add(panel_1, "cell 0 2,grow");
        panel_1.setLayout(new MigLayout("", "[][grow]", "[][]"));
        {
          lblNoChordsInProgression = new JLabel("No of chords in progression:");
          panel_1.add(lblNoChordsInProgression, "cell 0 0,alignx trailing");
        }
        {
          tfNoChordsInProgression = new JTextField();
          panel_1.add(tfNoChordsInProgression, "cell 1 0,growx");
          tfNoChordsInProgression.setColumns(10);
        }
        {
          lblChordDelay = new JLabel("Delay between chords:");
          panel_1.add(lblChordDelay, "cell 0 1,alignx trailing");
        }
        {
          tfChordDelay = new JTextField();
          panel_1.add(tfChordDelay, "flowx,cell 1 1,growx");
          tfChordDelay.setColumns(10);
        }
        {
          JLabel lblMiliseconds = new JLabel("miliseconds");
          panel_1.add(lblMiliseconds, "cell 1 1");
        }
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            onOkButtonActionPerformed();
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
            AddLessonFrame.this.dispose();
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
    
    buttonGroup.add(rbCPR);
    buttonGroup.add(rbSSR);
    
    
    initModels();
  }

  private void onOkButtonActionPerformed() {
    String lessonName = tfLessonName.getText();
    String noQuestions= tfNoQuestions.getText();
    
    String noChordsInProgression = tfNoChordsInProgression.getText();
    String chordDelay = tfChordDelay.getText();
    
    try {
      BTValidator.input(lessonName).required().validateWithException();
      BTValidator.input(noQuestions).required().integer().validateWithException();

      if (selectedChords == null || selectedChords.isEmpty()) {
        throw new BTValidationException("Lesson is empty!");
      }

      if (rbCPR.isSelected()){
        BTValidator.input(noChordsInProgression).required().integer().validateWithException();
        BTValidator.input(chordDelay).required().integer().validateWithException();
      }
      
      if (editingLesson == null){ //add mode
        Lesson lesson = new Lesson();
        lesson.setChordSequence(selectedChords);
        lesson.setLessonName(lessonName);
        lesson.setNoQuestions(Integer.parseInt(noQuestions));
        
        if (rbCPR.isSelected()){
          lesson.setType(LessonType.PROGRESSION);
          lesson.setNoChordsInSequence(Integer.parseInt(noChordsInProgression));
          lesson.setChordDelay(Integer.parseInt(chordDelay));
        }else{
          lesson.setType(LessonType.SINGLE);
        }
        
        m_lessons.addLesson(lesson);
      }else { //edit mode
        editingLesson.setLessonName(lessonName);
        editingLesson.setChordSequence(selectedChords);
        editingLesson.setNoQuestions(Integer.parseInt(noQuestions));

        if (rbCPR.isSelected()){
          editingLesson.setType(LessonType.PROGRESSION);
          editingLesson.setNoChordsInSequence(Integer.parseInt(noChordsInProgression));
          editingLesson.setChordDelay(Integer.parseInt(chordDelay));
        }else{
          editingLesson.setType(LessonType.SINGLE);
        }

        m_lessons.updateLesson(editingLesson);
      }

      this.dispose();
    } catch (Exception e) {
      if (e instanceof BTValidationException) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
      } else {
        throw new RuntimeException(e);
      }
    }

  }

  private void onBtnRemoveSelectedActionPerformed() {
    int[] selectedIndices = jlistSelectedChords.getSelectedIndices();
    for (int i = 0; i < selectedIndices.length; i++) {
      selectedChords.set(selectedIndices[i], null);
    }

    Iterator<Chord> iterator = selectedChords.iterator();
    while(iterator.hasNext()){
      Chord currentChord = iterator.next();
      if (currentChord == null){
        iterator.remove();
      }
    }

  }

  private void onBtnMoveSelectedActionPerformed() {
    Object[] chordArr = jlistChordPool.getSelectedValues();
    if (chordArr.length > 0) {
      for (int i = 0; i < chordArr.length; i++) {
        if (chordArr[i] instanceof Chord) {
          selectedChords.add((Chord) chordArr[i]);
        }
      }
    }

  }

  private void initModels() {
    EventListModel<Chord> chordListModel = new EventListModel<Chord>(m_chords.getAllChords());
    jlistChordPool.setModel(chordListModel);

    EventListModel<Chord> selectedChordsModel = null;

    selectedChords = new BasicEventList<Chord>();
    selectedChordsModel = new EventListModel<Chord>(selectedChords);

    jlistSelectedChords.setModel(selectedChordsModel);

    setCPRFieldsEnabledStatus();
  }

  @Inject
  public void setM_lessons(LessonDao m_lessons) {
    this.m_lessons = m_lessons;
  }

  public void setEditingLesson(Lesson lesson) {
    this.editingLesson = lesson;
    selectedChords.clear();
    selectedChords.addAll(lesson.getChordSequence());
    jlistSelectedChords.setModel(new EventListModel<Chord>(selectedChords));
    tfLessonName.setText(lesson.getLessonName());
    tfNoQuestions.setText(lesson.getNoQuestions() + "");
    
    if (lesson.getType().equals(LessonType.SINGLE)){
      rbSSR.setSelected(true);
      setCPRFieldsEnabledStatus();
    }else if (lesson.getType().equals(LessonType.PROGRESSION)){
      rbCPR.setSelected(true);
      setCPRFieldsEnabledStatus();
      
      tfNoChordsInProgression.setText(String.valueOf(lesson.getNoChordsInSequence()));
      tfChordDelay.setText(String.valueOf(lesson.getChordDelay()));
    }

  }
  
  private void setCPRFieldsEnabledStatus(){
    
    if (rbCPR.isSelected()){
      tfChordDelay.setEnabled(true);
      tfNoChordsInProgression.setEnabled(true);
      lblChordDelay.setEnabled(true);
      lblNoChordsInProgression.setEnabled(true);
    }else{
      tfChordDelay.setEnabled(false);
      tfNoChordsInProgression.setEnabled(false);
      lblChordDelay.setEnabled(false);
      lblNoChordsInProgression.setEnabled(false);
      
    }
    
  }

}
