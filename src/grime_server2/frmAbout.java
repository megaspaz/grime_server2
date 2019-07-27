package grime_server2;

/**
 * <p>Title: GRIME - Generic RPG Instant Messenger Envirnoment - Server</p>
 * <p>Description: This is the server application for the GRIME suite</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: N/A</p>
 * @author Megaspaz
 * @version 1.5
 */

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * <p>
 * Title: GRIME - Generic RPG Instant Messenger Environment - Server
 * </p>
 * <p>
 * Description: Client Interface For GRIME
 * </p>
 * <p>
 * Package/Class: grime_client.frmAbout
 * </p>
 * <p>
 * Package/Class Description: The About form with general application
 * information
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

public class frmAbout extends JDialog implements ActionListener {
  private static final long serialVersionUID = 1L;
  private JPanel panel1 = new JPanel();
  private JPanel panel2 = new JPanel();
  private JPanel insetsPanel1 = new JPanel();
  private JPanel insetsPanel2 = new JPanel();
  private JPanel insetsPanel3 = new JPanel();
  private JLabel imageLabel = new JLabel();
  private JLabel label1 = new JLabel();
  private JLabel label2 = new JLabel();
  private JLabel label3 = new JLabel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private BorderLayout borderLayout2 = new BorderLayout();
  private FlowLayout flowLayout1 = new FlowLayout();
  private GridLayout gridLayout1 = new GridLayout();
  private JLabel jLabel1 = new JLabel();

  public frmAbout(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Component initialization
  private void jbInit() throws Exception {
    imageLabel.setBorder(BorderFactory.createLineBorder(Color.black));
    imageLabel.setIcon(new ImageIcon(frmAbout.class.getResource("/goku.gif")));
    this.getContentPane().setBackground(Color.white);
    this.setTitle("");
    this.setUndecorated(true);
    this.addMouseListener(new frmAbout_this_mouseAdapter(this));
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBackground(Color.white);
    insetsPanel2.setBorder(null);
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setBackground(Color.white);
    label1.setFont(new java.awt.Font("Dialog", 1, 10));
    label1.setForeground(Color.blue);
    label1.setHorizontalAlignment(SwingConstants.CENTER);
    label1.setHorizontalTextPosition(SwingConstants.CENTER);
    label1.setText("GRIME - Generic RPG Instant Messenger Environment");
    label2.setBackground(Color.white);
    label2.setFont(new java.awt.Font("Dialog", 1, 10));
    label2.setForeground(Color.blue);
    label2.setHorizontalAlignment(SwingConstants.CENTER);
    label2.setHorizontalTextPosition(SwingConstants.CENTER);
    label2.setText("Server - Version 2.0");
    label3.setBackground(Color.white);
    label3.setFont(new java.awt.Font("Dialog", 1, 10));
    label3.setForeground(Color.blue);
    label3.setHorizontalAlignment(SwingConstants.CENTER);
    label3.setText("Written By Megaspaz");
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBackground(Color.white);
    insetsPanel3.setBorder(null);
    panel1.setBackground(Color.white);
    panel1.setBorder(BorderFactory.createLineBorder(Color.black));
    panel1.setMaximumSize(new Dimension(450, 140));
    panel1.setMinimumSize(new Dimension(450, 140));
    panel1.setPreferredSize(new Dimension(450, 140));
    insetsPanel1.setBackground(Color.white);
    jLabel1.setFont(new java.awt.Font("Monospaced", 1, 10));
    jLabel1.setForeground(Color.blue);
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
    jLabel1.setText("Copyright (c) 2003");
    insetsPanel2.add(imageLabel, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    this.getContentPane().add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(jLabel1, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
    setResizable(false);
  }

  // Overridden so we can exit when window is closed
  @Override
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  // Close the dialog
  void cancel() {
    dispose();
  }

  // Close the dialog on a button event
  @Override
  public void actionPerformed(ActionEvent e) {
    // do nothing. let "this" close frame on mouse click.
    // if (e.getSource() == button1)
    // {
    // cancel();
    // }
  }

  void this_mouseReleased(MouseEvent e) {
    cancel();
  }
}

class frmAbout_this_mouseAdapter extends java.awt.event.MouseAdapter {
  frmAbout adaptee;

  frmAbout_this_mouseAdapter(frmAbout adaptee) {
    this.adaptee = adaptee;
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    adaptee.this_mouseReleased(e);
  }
}
