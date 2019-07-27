package grime_server2;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * <p>
 * Title: GRIME - Generic RPG Instant Messenger Environment - Server
 * </p>
 * <p>
 * Description: This is the server side application for GRIME
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: N/A
 * </p>
 *
 * @author Megaspaz
 * @version 2.0
 */

public class grime_server2 {
  boolean packFrame = false;

  // Construct the application.
  public grime_server2() {
    Server_Frame2 frame = new Server_Frame2();
    // Validate frames that have preset sizes.
    // Pack frames that have useful preferred size info, e.g. from their
    // layout
    if (packFrame) {
      frame.pack();
    } else {
      frame.validate();
    }
    // Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
    frame.runServer();
  }

  // Main method
  public static void main(String[] args) {
    String laf = UIManager.getSystemLookAndFeelClassName();
    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          laf = info.getClassName();
          break;
        }
      }
      UIManager.setLookAndFeel(laf);
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    new grime_server2();
  }
}