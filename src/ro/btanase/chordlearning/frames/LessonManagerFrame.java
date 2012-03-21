package ro.btanase.chordlearning.frames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import ro.btanase.chordlearning.ChordLearningApp;
import ro.btanase.chordlearning.dao.LessonDao;
import ro.btanase.chordlearning.domain.Lesson;
import ca.odell.glazedlists.swing.EventListModel;

import com.google.inject.Inject;

public class LessonManagerFrame extends JDialog{

  private JPanel contentPane;
  private LessonDao m_lessons;
  private JButton btnNewLesson;
  private JButton btnEditLesson;
  private JButton btnRemoveLesson;
  private JList jlistLessons;


  /**
   * Create the frame.
   */
  @Inject
  public LessonManagerFrame(LessonDao m_lessons) {
    setModal(true);
    this.m_lessons = m_lessons;
    setTitle("Lesson Manager");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 531, 478);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[grow][]", "[][][21.00][][][][22.00][21.00][15.00][][23.00,grow]"));
    
    JLabel lblLessonList = new JLabel("Lesson List:");
    contentPane.add(lblLessonList, "cell 0 0");
    
    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, "cell 0 1 1 10,grow");
    
    jlistLessons = new JList();
    scrollPane.setViewportView(jlistLessons);
    
    btnNewLesson = new JButton("New Lesson");
    btnNewLesson.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnNewLessonActionPerformed();
      }
    });
    contentPane.add(btnNewLesson, "cell 1 3,growx");
    
    btnEditLesson = new JButton("Edit Lesson");
    btnEditLesson.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnEditLessonActionPerformed();
      }
    });
    contentPane.add(btnEditLesson, "cell 1 4,growx");
    
    btnRemoveLesson = new JButton("Remove Lesson");
    btnRemoveLesson.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        onBtnRemoveLessonActionPerformed();
        
      }
    });
    contentPane.add(btnRemoveLesson, "cell 1 5,growx");
    
    initModels();
  }


  private void onBtnRemoveLessonActionPerformed() {
    if (jlistLessons.getSelectedValue() != null){
      Lesson lesson = (Lesson) jlistLessons.getSelectedValue();
      int result = JOptionPane.showConfirmDialog(this, "Are you sure you wish to delete lesson "
          + lesson.getLessonName() + "? ", "Delete lesson?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      
      if (JOptionPane.YES_OPTION == result){
        m_lessons.deleteLesson(lesson);
        jlistLessons.clearSelection();
      }
    }
    
    
  }


  private void onBtnEditLessonActionPerformed() {
    if (jlistLessons.getSelectedValue() != null){
      Lesson lesson = (Lesson) jlistLessons.getSelectedValue();
      AddLessonFrame alf = ChordLearningApp.getInjector().getInstance(AddLessonFrame.class);
      alf.setEditingLesson(lesson);
      alf.setLocationRelativeTo(this);
      alf.setVisible(true);
    }
  }


  private void onBtnNewLessonActionPerformed() {
    AddLessonFrame alf = ChordLearningApp.getInjector().getInstance(AddLessonFrame.class);
    alf.setLocationRelativeTo(this);
    alf.setVisible(true);
  }


  private void initModels(){
    EventListModel<Lesson> listModel = new EventListModel<Lesson>(m_lessons.getAll());
    jlistLessons.setModel(listModel);
  }
  private void addPopup(final Component component, final JPopupMenu popup) {
  }
}
