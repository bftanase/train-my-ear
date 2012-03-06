package ro.btanase.chordlearning.services;

import javax.swing.JOptionPane;

public class MessengerServiceSwingImpl implements MessengerService {

  @Override
  public void displayMessage(String message) {
    JOptionPane.showMessageDialog(null, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
  }

}
