package ro.btanase.chordlearning.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import javax.swing.border.BevelBorder;

public class TimerProgressDialog extends JDialog {

  private final JPanel contentPanel = new JPanel();
  private int counter = 5;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    TimerProgressDialog dialog = new TimerProgressDialog();
  }

  /**
   * Create the dialog.
   */
  public TimerProgressDialog() {
    setUndecorated(true);
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setModal(true);
    setBounds(100, 100, 231, 177);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
    final JLabel label = new JLabel(counter + "");
    label.setForeground(Color.RED);
    label.setFont(new Font("Tahoma", Font.PLAIN, 99));
    label.setHorizontalAlignment(SwingConstants.CENTER);
    contentPanel.add(label, "cell 0 0,alignx center");
    setLocationRelativeTo(null);
    Thread thread = new Thread(new Runnable() {
      
      @Override
      public void run() {
        while(counter > 0){
          try {
            Thread.sleep(1000);
            label.setText(--counter + "");
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
        TimerProgressDialog.this.dispose();
      }
    });
    thread.start();
    
    setVisible(true);
    
    
  }

}
