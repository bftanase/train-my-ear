package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextPane;

import ro.btanase.chordlearning.ChordLearningApp;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class AboutDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
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
    setModal(true);
    setTitle("About GCET");
    setBounds(100, 100, 460, 319);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
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
        txtpnguitarChordEar.setText("<h3>Guitar Chord Ear Training</h3>\r\n\r\nCopyright: Bogdan Tanase 2011 \r\n <br> " +
              "Contact: bftanase@gmail.com <br/>" +
              "Version: " + ChordLearningApp.VERSION + " freeware \r\n <br/>" + 
              "<p> </p> Thanks to: <br/>" +
              "LBro - for providing the chord samples and valuable feedback <br/>" +
              "and to all the wonderful people from the Java open source community!");
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
            AboutDialog.this.dispose();
          }
        });
        okButton.setActionCommand("Cancel");
        buttonPane.add(okButton);
      }
    }
  }

}