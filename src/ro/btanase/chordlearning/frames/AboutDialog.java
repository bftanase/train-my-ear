package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import ro.btanase.chordlearning.ChordLearningApp;
import java.awt.Toolkit;
import javax.swing.UIManager;

public class AboutDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private final Color BK_COLOR = new Color(33, 98, 120);

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
    } catch (Throwable e) {
      e.printStackTrace();
    }
    try {
      AboutDialog dialog = new AboutDialog();
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   */
  public AboutDialog() {
    setIconImage(Toolkit.getDefaultToolkit().getImage(AboutDialog.class.getResource("/res/tme_small.png")));
    setModal(true);
    setTitle("About Train My Ear");
    setBounds(100, 100, 460, 357);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().setBackground(BK_COLOR);
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPanel.setBackground(BK_COLOR);
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
    {
      JScrollPane scrollPane = new JScrollPane();
      contentPanel.add(scrollPane, "cell 0 0,grow");
      {
        JTextPane txtpnguitarChordEar = new JTextPane();
        scrollPane.setViewportView(txtpnguitarChordEar);
        txtpnguitarChordEar.setContentType("text/html");
        txtpnguitarChordEar.setEditable(false);
        txtpnguitarChordEar.setText("<h3>Train My Ear</h3>\r\n\r\nCopyright: Bogdan Tanase 2012 \r\n <br>" +
        		" Contact: bftanase@gmail.com <br/>" +
        		"Version: " + ChordLearningApp.VERSION + " freeware \r\n <br/>" +
        				"<p> </p> Thanks to: <br/>" +
        				"LBro - for providing the chord samples and valuable feedback <br/>" +
        				"Bhaz - for providing the electric samples <br/>" +
        				"Matan - great feedback!<br/>" +
        				"and to all the wonderful people from the Java open source community!");
      }
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setBackground(BK_COLOR);
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            AboutDialog.this.dispose();
          }
        });
        okButton.setActionCommand("Cancel");
        buttonPane.add(okButton);
      }
    }
  }

}
