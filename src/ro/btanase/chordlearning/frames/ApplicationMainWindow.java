package ro.btanase.chordlearning.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import ro.btanase.chordlearning.ChordLearningApp;
import ro.btanase.chordlearning.dao.LessonDao;
import ro.btanase.chordlearning.dao.ScoreDao;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.LessonType;
import ca.odell.glazedlists.swing.EventListModel;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;

public class ApplicationMainWindow extends JFrame {

  private JPanel contentPane;
  private LessonDao m_lessons;
  private JList jlistLessons;
  private JButton btnStartTest;
  private JMenuBar menuBar;
  private JMenu mnStatistics;
  private JMenuItem mntmNewMenuItem;
  private JMenuItem mntmNewMenuItem_1;
  private JMenu mnNewMenu;
  private JMenuItem mntmNewMenuItem_2;
  private JMenuItem mntmNewMenuItem_3;
  private JMenu mnNewMenu_1;
  private JMenuItem mntmNewMenuItem_4;
  private JMenuItem mntmNewMenuItem_5;
  private JTextPane txtpnToStartTraining;
  private static Logger log = Logger.getLogger(ApplicationMainWindow.class);
  private JMenuItem mntmNewMenuItem_6;


  /**
   * Create the frame.
   */
  @Inject
  public ApplicationMainWindow(LessonDao m_lessons) {
    this.m_lessons = m_lessons;
    setTitle("Guitar Chord Ear Training " + "version: " + ChordLearningApp.VERSION);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 501, 480);
    
    menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    
    mnStatistics = new JMenu("Statistics");
    mnStatistics.setIcon(null);
    menuBar.add(mnStatistics);
    
    mntmNewMenuItem = new JMenuItem("Chord Accuracy ...");
    mntmNewMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showChordAccuracy();
      }
    });
    mntmNewMenuItem.setIcon(null);
    mnStatistics.add(mntmNewMenuItem);
    
    mntmNewMenuItem_1 = new JMenuItem("Lesson Evolution Chart ...");
    mntmNewMenuItem_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showEvolutionChart();
      }
    });
    mntmNewMenuItem_1.setIcon(null);
    mnStatistics.add(mntmNewMenuItem_1);
    
    mnNewMenu = new JMenu("Configuration");
    menuBar.add(mnNewMenu);
    
    mntmNewMenuItem_2 = new JMenuItem("Manage Chord Samples ...");
    mntmNewMenuItem_2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showChordManager();
      }
    });
    mnNewMenu.add(mntmNewMenuItem_2);
    
    mntmNewMenuItem_3 = new JMenuItem("Manage Lessons ...");
    mntmNewMenuItem_3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showLessonManager();
      }
    });
    mnNewMenu.add(mntmNewMenuItem_3);
    
    mntmNewMenuItem_6 = new JMenuItem("Reset Scores!");
    mntmNewMenuItem_6.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        resetScores();
      }
    });
    mnNewMenu.add(mntmNewMenuItem_6);
    
    mnNewMenu_1 = new JMenu("Help");
    menuBar.add(mnNewMenu_1);
    
    mntmNewMenuItem_4 = new JMenuItem("Online Help (opens browser)");
    mntmNewMenuItem_4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try{
          URI uri = new URI("http://chords.btanase.ro/index.php/page/documentation");
          Desktop.getDesktop().browse(uri);
        }catch(Exception ex){
          log.error("Can't launch user browser ", ex);
          JOptionPane.showMessageDialog(ApplicationMainWindow.this, "Web browser cannot be launched automatically. \n " +
                "Please go to chords.btanase.ro to read the documentation ", "Error launching browser", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    mnNewMenu_1.add(mntmNewMenuItem_4);
    
    mntmNewMenuItem_5 = new JMenuItem("About ...");
    mntmNewMenuItem_5.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutWindow();
      }
    });
    mnNewMenu_1.add(mntmNewMenuItem_5);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(new MigLayout("", "[280:355.00][grow,left]", "[][][23.00,grow]"));
    
    txtpnToStartTraining = new JTextPane();
    txtpnToStartTraining.setEditable(false);
    txtpnToStartTraining.setText("To start training select a lesson below and click Go\r\nDon't forget that you can (and I highly recommend to) create your own lessons!\r\nJust go to the menu above: Configuration -> Manage Lessons");
    contentPane.add(txtpnToStartTraining, "cell 0 0,grow");
    
    btnStartTest = new JButton("Go!");
    btnStartTest.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnStartTestActionPerformed();
      }
    });
    contentPane.add(btnStartTest, "cell 1 0,alignx left,aligny bottom");
    
    JScrollPane scrollPane = new JScrollPane();
    contentPane.add(scrollPane, "cell 0 1 2 2,grow");
    
    jlistLessons = new JList();
    scrollPane.setViewportView(jlistLessons);
    
    
    jlistLessons.addKeyListener(new KeyListener() {
      
      @Override
      public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
          btnStartTest.doClick();
        }
        
      }
    });
    initModels();
  }


  protected void resetScores() {
    int result = JOptionPane.showConfirmDialog(this, "Are you sure you wish to RESET all scores? \n " +
        "You will loose all progress!", "Reset scores", JOptionPane.YES_NO_OPTION);
    
    if (result == JOptionPane.YES_OPTION){
      ScoreDao scoreDao = ChordLearningApp.getInjector().getInstance(ScoreDao.class);
      scoreDao.resetScores();
    }
  }


  private void showLessonManager() {
    Injector injector = ChordLearningApp.getInjector();
    
    LessonManagerFrame frame = injector.getInstance(LessonManagerFrame.class);
    frame.setLocationRelativeTo(this);
    frame.setVisible(true);
    
  }


  private void showEvolutionChart() {
    Injector injector = ChordLearningApp.getInjector();
    LessonEvolutionDialog led = injector.getInstance(LessonEvolutionDialog.class);
    led.setLocationRelativeTo(this);
    led.setVisible(true);
    
  }


  private void showChordAccuracy() {
    ChordAccuracyFrame caFrame = ChordLearningApp.getInjector().getInstance(ChordAccuracyFrame.class);
    caFrame.setLocationRelativeTo(this);
    caFrame.setVisible(true);
    
  }


  private void showChordManager() {
    ChordManagerFrame cdf = ChordLearningApp.getInjector().getInstance(ChordManagerFrame.class);
    cdf.setLocationRelativeTo(this);
    cdf.setVisible(true);
    
  }


  private void onBtnStartTestActionPerformed() {
    Lesson lesson = (Lesson) jlistLessons.getSelectedValue();
    
    if (lesson == null){
      JOptionPane.showMessageDialog(this, "You must select a lesson!", "No Selection",
          JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    
    if (lesson.getType().equals(LessonType.SINGLE)){
      LessonSSRFrame ltf = new LessonSSRFrame(lesson);
      ChordLearningApp.getInjector().injectMembers(ltf);
      ltf.setLocationRelativeTo(this);
      ltf.setVisible(true);
    }else if (lesson.getType().equals(LessonType.PROGRESSION)){
      
      LessonCPRFrame lcp = new LessonCPRFrame(lesson);
      ChordLearningApp.getInjector().injectMembers(lcp);
      lcp.setLocationRelativeTo(this);
      lcp.setVisible(true);
    }
    
    
  }


  private void initModels(){
    EventListModel<Lesson> listModel = new EventListModel<Lesson>(m_lessons.getAll());
    jlistLessons.setModel(listModel);
  }
  private void addPopup(final Component component, final JPopupMenu popup) {
  }


  private void showAboutWindow() {
    AboutDialog aboutDialog = ChordLearningApp.getInjector().getInstance(AboutDialog.class);
    aboutDialog.setLocationRelativeTo(this);
    aboutDialog.setVisible(true);
  }
}
