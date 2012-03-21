package ro.btanase.chordlearning.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import net.miginfocom.swing.MigLayout;
import ro.btanase.chordlearning.services.UserData;

import com.google.inject.Inject;

public class UserFolderFrame extends JDialog {
  private JTextField tfApplicationData;
  private UserData userData;
  private UserFolderFrame uff;
  private static Logger log = Logger.getLogger(UserFolderFrame.class);


  /**
   * Create the dialog.
   */
  @Inject
  public UserFolderFrame(UserData userData) {
    this.userData = userData;
    setModal(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("Choose application data folder");
    setBounds(100, 100, 596, 177);
    getContentPane().setLayout(new MigLayout("", "[][][grow][]", "[][grow][][]"));
    
    JTextPane textPane = new JTextPane();
    textPane.setBackground(UIManager.getColor("Button.background"));
    textPane.setContentType("text/html");
    textPane.setEditable(false);
    textPane.setText("Please choose where the application should save its data (scores, configuration, media files).<br/>\r\nIf you don't know or don't care just leave the default ;)");
    getContentPane().add(textPane, "cell 0 0 4 1,growx");
    
    JLabel lblSelectedFolder = new JLabel("Selected folder:");
    getContentPane().add(lblSelectedFolder, "cell 1 2,alignx trailing");
    
    tfApplicationData = new JTextField();
    tfApplicationData.setEditable(false);
    getContentPane().add(tfApplicationData, "flowx,cell 2 2,growx");
    tfApplicationData.setColumns(10);
    
    JButton btnBrowse = new JButton("Browse ...");
    btnBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onBtnBrowseActionPerformed();
      }
    });
    getContentPane().add(btnBrowse, "cell 2 2");
    
    JButton btnIDontCare = new JButton("I don't care, use defaults");
    btnIDontCare.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String defaultUserDir = System.getProperty("user.home") + File.separator + ".gcet" + File.separator + "application_data";
        tfApplicationData.setText(defaultUserDir);
        btnContinueActionPerformed();
        UserFolderFrame.this.dispose();
      }
    });
    getContentPane().add(btnIDontCare, "cell 1 3");
    
    JButton btnContinue = new JButton("Continue");
    btnContinue.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        btnContinueActionPerformed();
      }
    });
    getContentPane().add(btnContinue, "cell 2 3,alignx right");

    initModels(); 
  }
  
  private void btnContinueActionPerformed() {
    // check if it's possible to write to the selected directory
    String userDirPath = tfApplicationData.getText();
    try {
      FileUtils.forceMkdir(new File(userDirPath));
      File tmpFile = new File(userDirPath + File.separator + "tst.tmp");

      // test if the directory is writable
      tmpFile.createNewFile(); // try  to create
      tmpFile.delete(); // delete after creation 
      
    } catch (IOException e) {
      log.error("Unable to create direcotry ", e);
      JOptionPane.showMessageDialog(UserFolderFrame.this, "Cannot use the selected directory. Error message: \n"
          + e.getMessage(), "Error selecting folder", JOptionPane.ERROR_MESSAGE);
      return;
    }
    
    userData.setUserFolder(userDirPath);
    
    // check if there is existing data in the (new) location
    if (userData.isUserDirectoryDefined()){
      this.dispose();
      return;
    }else{
      try{
        FileUtils.copyDirectory(new File("preset_data" + File.separator + UserData.MEDIA_FOLDER), 
            new File(userData.getMediaFolder()));
        FileUtils.copyDirectory(new File("preset_data" + File.separator + UserData.CONFIG_FOLDER),
            new File(userData.getConfigFolder()));
      }catch (IOException e) {
        log.fatal("Unable to copy preset data to selected directory ");
        log.fatal("media folder: " + userData.getMediaFolder());
        log.fatal("config folder: " + userData.getConfigFolder());
      }
    }
    
    this.dispose();
    
  }

  private void onBtnBrowseActionPerformed() {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File(tfApplicationData.getText()));
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.showOpenDialog(this);
    
    if (chooser.getSelectedFile() != null){
      tfApplicationData.setText(chooser.getSelectedFile().getPath());
    }
    
  }

  private void initModels(){
    
    log.debug("userFolder: " + userData.getUserFolder());
    if (userData.getUserFolder() == null){
      
      String userHome = System.getProperty("user.home");
      userData.setUserFolder(userHome + File.separator + ".gcet" + File.separator + "application_data");
    }
    
    tfApplicationData.setText(userData.getUserFolder());
  }

}
