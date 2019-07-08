package grime_server2;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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

public class Server_Frame2 extends JFrame implements Runnable {
  private static final long serialVersionUID = 1L;
  private JPanel contentPane;
  private JMenuBar jMenuBar1 = new JMenuBar();
  private JMenu jMenuFile = new JMenu();
  private JMenuItem jMenuItemFileExit = new JMenuItem();
  private JMenu jMenuHelp = new JMenu();
  private JMenuItem jMenuItemHelpAbout = new JMenuItem();
  private JMenuItem jMenuItemFileClear = new JMenuItem();
  private JScrollPane jScrollPane_log = new JScrollPane();
  private JTextArea jTextArea_log = new JTextArea();
  // runServer variables
  private ServerSocket server_socket;
  private volatile Thread pThread;
  private Vector<String> players_list = new Vector<String>();
  private Vector<Socket> client_sockets = new Vector<Socket>();
  private boolean run_thread;

  // Construct the frame
  public Server_Frame2() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Component initialization
  private void jbInit() throws Exception {
    contentPane = (JPanel) this.getContentPane();
    setIconImage(Toolkit.getDefaultToolkit().createImage(Server_Frame2.class.getResource("/vj_blood.png")));
    contentPane.setLayout(null);
    this.setJMenuBar(jMenuBar1);
    this.setResizable(false);
    this.setSize(new Dimension(527, 350));
    this.setTitle("GRIME - Generic RPG Instant Messenger Environment - Server 2.0");
    jMenuFile.setText("File");
    jMenuFile.setMnemonic('F');
    jMenuItemFileExit.setText("Exit");
    jMenuItemFileExit.setMnemonic('x');
    jMenuItemFileExit.addActionListener(new Server_Frame2_jMenuItemFileExit_actionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelp.setMnemonic('H');
    jMenuItemHelpAbout.setText("About");
    jMenuItemHelpAbout.setMnemonic('A');
    jMenuItemFileClear.setText("Clear Window");
    jMenuItemFileClear.addActionListener(new Server_Frame2_jMenuItemFileClear_actionAdapter(this));
    jMenuItemFileClear.setMnemonic('C');
    jMenuItemHelpAbout.addActionListener(new Server_Frame2_jMenuItemHelpAbout_actionAdapter(this));
    jMenuBar1.setBorder(null);
    jScrollPane_log.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    jScrollPane_log.setBounds(new Rectangle(2, 1, 520, 303));
    jTextArea_log.setFont(new java.awt.Font("Dialog", 0, 16));
    jTextArea_log.setEditable(false);
    jTextArea_log.setText("");
    jTextArea_log.setLineWrap(true);
    jTextArea_log.setWrapStyleWord(true);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    jMenuFile.add(jMenuItemFileClear);
    jMenuFile.add(jMenuItemFileExit);
    jMenuHelp.add(jMenuItemHelpAbout);
    contentPane.add(jScrollPane_log, null);
    jScrollPane_log.getViewport().add(jTextArea_log, null);
  }

  // Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      stopThread();
      sendKill();
      System.exit(0);
    }
  }

  // File > Exit
  void jMenuItemFileExit_actionPerformed(ActionEvent e) {
    stopThread();
    sendKill();
    System.exit(0);
  }

  // Help > About
  void jMenuItemHelpAbout_actionPerformed(ActionEvent e) {
    frmAbout dlg = new frmAbout(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  /**
   * communication/command type: 1 = player list update - server sends to clients
   * 2 = regular chat - basic communication to all players 3 = dice roll update -
   * all players 4 = player/client leave & disconnect - client to server. unused
   * by server but server will use 1 & 2 to update other players after getting #4
   * command from client 5 = server forces single client to close - verify player
   * alias fails 6 = server forces all clients to close - admin closes server File
   * > Exit
   */
  public void runServer() {
    String stream = "";
    Socket connection;
    DataInputStream input;
    PrintStream output;

    try {
      server_socket = new ServerSocket(5000);

      // add newline to console output
      System.out.println();
      // start the waiting
      updateLog("Waiting For Connections...\n");
      // debug scaffolding
      // System.out.println("updateLog Synchronization Lock Released\n\n");
      while (true) {
        connection = server_socket.accept();
        // add socket to vector
        addClientSocket(connection);
        pThread = new Thread(this);

        input = new DataInputStream(connection.getInputStream());
        output = new PrintStream(connection.getOutputStream());
        // connection established. get the player name to check
        // uniqueness
        sendData("2Initial Connection Granted...", connection, output);
        // client should just send the player alias without any command
        // code
        if ("" != (stream = readData(input))) {
          // send client verifying confirmation
          sendData("2Checking player alias uniqueness...", connection, output);
          if (false == isPlayerUnique(stream)) {
            // send the players list
            String players = getPlayersList();
            sendPlayersList("1" + players, connection, output);
            // send kill signal to recent connection with error
            // message player is already playing
            sendData(
                "5" + stream.toUpperCase() + " already in use. " + "Disconnecting and sending current player list...",
                connection, output);
            // update log
            updateLog("Attempted connection by " + stream + " disconnected. Name already in use.");
            // remove socket from socket vector
            removeClientSocket(connection);
          } else {
            // add the player and connection and send confirmation
            // of permanent connection and current players list
            sendData("2Permanent connection granted to >>> " + stream, connection, output);
            // update the server log
            updateLog("<<< UPDATE >>> " + stream + " has entered the fray!");
            // notify everyone player connected and update
            // everyone's players list
            addPlayerToList(stream);
            for (int i = 0; i < client_sockets.size(); i++) {
              String players = getPlayersList();
              connection = (Socket) client_sockets.elementAt(i);
              // send data
              sendData("2<<< UPDATE >>> " + stream + " has entered the fray!", connection, output);
              // send out player list to everyone
              sendPlayersList("1" + players, connection, output);
            }
            pThread.start();
          }
        }
      }
    } catch (IOException e) {
      updateLog("\n*** IOException occurred in runServer () ***\n" + "*** This may have occurred because you are ***\n"
          + "*** already runnning an instance of the server ***\n");
    } catch (Exception e) {
      updateLog("\n*** Exception occurred in runServer () ***\n" + "*** You may need to shutdown the server ***\n");
    }
  }

  /**
   * communication/command type: 1 = player list update - server sends to clients
   * 2 = regular chat - basic communication to all players 3 = dice roll update -
   * all players 4 = player/client leave & disconnect - client to server. unused
   * by server but server will use 1 & 2 to update other players after getting #4
   * command from client 5 = server forces single client to close - verify player
   * alias fails 6 = server forces all clients to close - admin closes server File
   * > Exit
   */
  public void run() {
    Socket connection_run;
    String client_stream;
    PrintStream data_out = null; // will be initialized in sendData
    DataInputStream data_in;

    try {
      run_thread = true;
      while (true == run_thread) {
        for (int i = 0; i < client_sockets.size(); i++) {
          connection_run = (Socket) client_sockets.elementAt(i);
          data_in = new DataInputStream(connection_run.getInputStream());
          if (0 < data_in.available()) {
            try {
              client_stream = readData(data_in);
              // check if client disconnected, notify others if
              // so, and update all players list
              if ('4' == client_stream.charAt(0)) {
                // update server log
                updateLog("<<< UPDATE >>> " + client_stream.substring(1).toString() + " has bugged out!");
                // remove connection from sockets list
                removeClientSocket(connection_run);
                // remove from player list
                removeFromPlayersList(client_stream.substring(1).toString());
                // set up waiting text
                if (0 == players_list.size()) {
                  updateLog("\nWaiting for connections...\n");
                } else {
                  // notify other players
                  String players = getPlayersList();
                  for (int j = 0; j < client_sockets.size(); j++) {
                    connection_run = (Socket) client_sockets.elementAt(j);
                    sendData("2<<< UPDATE >>> " + client_stream.substring(1).toString() + " has bugged out!",
                        connection_run, data_out);
                    sendPlayersList("1" + players, connection_run, data_out);
                  }
                }
              } else {
                // update server log
                updateLog(client_stream.substring(1).toString());
                // pass everything through to other players
                for (int j = 0; j < client_sockets.size(); j++) {
                  connection_run = (Socket) client_sockets.elementAt(j);
                  sendData(client_stream, connection_run, data_out);
                }
              }
            } catch (Exception e) {
              client_sockets.removeElement(connection_run);
              updateLog("\n*** Exception occurred sending data in Thread.run() ***\n");
            }
          }
        }
        // let other processes (system or otherwise) do stuff
        Thread.sleep(500);
      }
    } catch (Exception e) {
      updateLog("\n*** Exception occurred in Thread.run () ***\n"
          + "*** You you will need to shutdown and restart the server ***\n");
    }
  }

  synchronized protected void sendKill() {
    Socket kill_client;
    PrintStream kill_stream = null;
    for (int i = 0; i < client_sockets.size(); i++) {
      kill_client = (Socket) client_sockets.elementAt(i);
      sendData("6The server is being shutdown by the admin due to routine maintenance or an "
          + "unrecoverable server error.", kill_client, kill_stream);
    }
  }

  synchronized protected void stopThread() {
    run_thread = false;
  }

  synchronized protected String readData(DataInputStream data_input) {
    String datastream = "";
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(data_input));
      datastream = reader.readLine();
      return datastream;
    } catch (IOException e) {
      updateLog("\n*** IOException occurred in readData () ***\n");
      return datastream;
    } catch (Exception e) {
      updateLog("\n*** IOException occurred in readData () ***\n" + "*** You may need to shutdown the server ***\n");
      return datastream;
    }
  }

  synchronized protected void sendData(String datastream, Socket client, PrintStream dataout) {
    try {
      dataout = new PrintStream(client.getOutputStream());
      dataout.println(datastream);
      dataout.flush();
    } catch (IOException e) {
      updateLog("\n*** IOException occurred in sendData () ***\n");
    } catch (Exception e) {
      updateLog("\n*** IOException occurred in sendData () ***\n" + "*** You may need to shutdown the server ***\n");
    }
  }

  synchronized protected String getPlayersList() {
    String players = "";

    for (int i = 0; i < players_list.size(); i++) {
      players = players + players_list.elementAt(i).toString() + "\\";
    }
    // get rid of last "\"
    players = players.substring(0, players.length() - 1);
    return players.toString();
  }

  synchronized protected void sendPlayersList(String playersList, Socket client, PrintStream dataout) {
    try {
      dataout = new PrintStream(client.getOutputStream());
      dataout.println(playersList);
      dataout.flush();
    } catch (IOException e) {
      updateLog("\n*** IOException occurred in sendPlayersList () ***\n");
    } catch (Exception e) {
      updateLog(
          "\n*** IOException occurred in sendPlayersList () ***\n" + "*** You may need to shutdown the server ***\n");
    }
  }

  synchronized protected void removeClientSocket(Socket client) {
    client_sockets.removeElement(client);
  }

  synchronized protected void addClientSocket(Socket client) {
    client_sockets.addElement(client);
  }

  synchronized protected void emptyPlayersList() {
    players_list.clear();
  }

  synchronized protected void addPlayerToList(String alias) {
    players_list.addElement(alias);
  }

  synchronized protected void removeFromPlayersList(String alias) {
    players_list.removeElement(alias);
  }

  synchronized protected boolean isPlayerUnique(String alias) {
    boolean isUnique = true; // assume alias is unique
    // loop over players list
    for (int i = 0; i < players_list.size(); i++) {
      if (0 == alias.compareToIgnoreCase(players_list.elementAt(i).toString())) {
        // player alias is not unique
        isUnique = false;
        break;
      }
    }
    return isUnique;
  }

  synchronized protected void clearLog() {
    jTextArea_log.setText("");
  }

  synchronized protected void updateLog(String log_text) {
    // update graphical text area
    jTextArea_log.append(log_text + "\n");
    // update console output
    System.out.println(log_text);
  }

  void jMenuItemFileClear_actionPerformed(ActionEvent e) {
    clearLog();
  }
}

class Server_Frame2_jMenuItemHelpAbout_actionAdapter implements java.awt.event.ActionListener {
  Server_Frame2 adaptee;

  Server_Frame2_jMenuItemHelpAbout_actionAdapter(Server_Frame2 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemHelpAbout_actionPerformed(e);
  }
}

class Server_Frame2_jMenuItemFileExit_actionAdapter implements java.awt.event.ActionListener {
  Server_Frame2 adaptee;

  Server_Frame2_jMenuItemFileExit_actionAdapter(Server_Frame2 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemFileExit_actionPerformed(e);
  }
}

class Server_Frame2_jMenuItemFileClear_actionAdapter implements java.awt.event.ActionListener {
  Server_Frame2 adaptee;

  Server_Frame2_jMenuItemFileClear_actionAdapter(Server_Frame2 adaptee) {
    this.adaptee = adaptee;
  }

  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemFileClear_actionPerformed(e);
  }
}