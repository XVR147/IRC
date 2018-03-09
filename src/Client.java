import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends JFrame implements Runnable {

    // The client socket


    public static boolean flag=true;
    public static String ret;
    private static Container c;
    private static JPanel chat = new JPanel();
    private static JPanel text = new JPanel();
    private static JScrollPane chatScroll = new JScrollPane(chat);
    public static String u;
    private static String p;
    // SEZIONE UTENTI
    private static JPanel users = new JPanel();
    private static String[] utenti = new String[25];

    private static JList usersList;
    private static DefaultListModel usersModel;
    private static JScrollPane usersScroll = new JScrollPane(usersList);

    private static JButton invia = new JButton("Invia");
    private static JTextField msg = new JTextField(20);
    private static JLabel chatText = new JLabel();
    private static JLabel L_Utenti = new JLabel("   Utenti attivi   ");




    // The default port.


    public Client(){

        super("iRC");
        new Autenticazione();
        while(flag)
        {
            System.out.print("");
        }
        p=ret;
        c = this.getContentPane();
        c.setLayout(new BorderLayout());
        chatText.setText("<html></html>");
        //chat
        chat.setBackground(Color.WHITE);
        chat.setBorder(new JTextField().getBorder());
        chat.setLayout(new FlowLayout(FlowLayout.LEFT));
        c.add(chatScroll, BorderLayout.CENTER);

        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        usersModel = new DefaultListModel();
        for (int j = 0; j < 25; j++) {
            usersModel.add(j, null);
        }
        usersList = new JList(usersModel);
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setVisibleRowCount(150);
        usersList.addListSelectionListener(new ListSelectionListener() {
                                               @Override
                                               public void valueChanged(ListSelectionEvent e) {
                                                   String x = (String) usersList.getSelectedValue();
                                                   msg.setText("/w [" + x + "] ");
                                               }
                                           }
        );


        usersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        usersScroll.setLayout(new ScrollPaneLayout());
        usersScroll.setViewportView(usersList);


        usersList.setLayoutOrientation(JList.VERTICAL);
        chatText.setVerticalAlignment(JLabel.TOP);
        chatText.setVerticalTextPosition(JLabel.TOP);
        chatText.setHorizontalAlignment(JLabel.LEFT);
        chatText.setHorizontalTextPosition(JLabel.LEFT);
        //chatText.setBorder(new EmptyBorder(10,10,0,0));
        chat.add(chatText);


        //utenti
        users.setBackground(Color.WHITE);
        users.setBorder(new JTextField().getBorder());
        c.add(users, BorderLayout.EAST);
        users.setLayout(new BorderLayout());

        //LABEL UTENTI ATTIVI
        L_Utenti.setFont(new Font(L_Utenti.getFont().getName(), Font.BOLD, 20));
        users.add(L_Utenti, BorderLayout.NORTH);
        // LISTA UTENTI ATTIVI

        users.add(usersScroll, BorderLayout.CENTER);
        //barra di testo
        text.setBackground(Color.WHITE);
        text.setBorder(new JTextField().getBorder());
        c.add(text, BorderLayout.SOUTH);

        //barra di testo effettiva
        text.setLayout(new FlowLayout());
        msg.setPreferredSize(new Dimension(440, 30));
        text.add(msg);

        L_Utenti.setHorizontalAlignment(SwingConstants.CENTER);
        msg.setAlignmentY(L_Utenti.getAlignmentY());
        msg.setBorder(new JTextField().getBorder());
        msg.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    invia.doClick();
                    msg.setText("");
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    msg.setText("");
                }
            }

            @Override
            public void keyTyped(KeyEvent arg0) {

            }
        });

        //pulsante invia
        invia.setBackground(new Color(131, 247, 152));
        invia.setPreferredSize(new Dimension(60, 30));
        invia.setFocusable(false);
        invia.addActionListener(new ListenerSend());
        text.add(invia);


        setSize(550, 400);
        setVisible(true);



    }

    /*
     * Create a thread to read from the server. (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        /*
         * Keep on reading from the socket till we receive "Bye" from the
         * server. Once we received that then we want to break.
         */
        String responseLine;


        try {
            while ((responseLine = Main.is.readLine()) != null) {

                input(responseLine);

            }
            Main.closed = true;
        } catch (Exception x) {
            System.err.println("Exception:  " + x);
        }

    }

    public void input(String x){
        if(x.equals("0"))
        {
            String m="Errore";
            try
            {m = Main.is.readLine();}
            catch(Exception d)
            {

            }
            String actual = chatText.getText().substring(0, chatText.getText().length()-7);
            chatText.setText(actual+m+"<br></html>");
        }
        else if(x.equals("1"))
        {
            String m="Errore";
            try
            {
                m=Main.is.readLine();
            }
            catch(Exception d)
            {

            }
            String actual = chatText.getText().substring(0, chatText.getText().length()-7);
            chatText.setText(actual+m+"<br></html>");
        } else if(x.startsWith("&^") && x.endsWith("^&"))
        {
            updateUsersList(x);
        }
        return;
    }

    static public void output(){
        String send = msg.getText();
        msg.setText("");
        Main.os.println("0");
        Main.os.flush();
        Main.os.println(send);
        Main.os.flush();

    }
    static public void updateUsersList(String x){
        String temp = x.substring(2,(x.length())-3);
        utenti = temp.split(":");
        System.out.println(java.util.Arrays.toString(utenti));

        usersModel.clear();
        for(int i = 0; i < utenti.length;i++){
            usersModel.add(i,utenti[i]);
        }
        usersList = new JList(usersModel);

    }
    private class ListenerSend implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String color = "";
            if (p.equals("admin")) {
                color = "#419b4a";
            } else {
                color = "#1d21f7";
            }
            String mex2 = "";
            String user = "";
            String mex = msg.getText();
            if(!mex.equals(""))
            {
                Boolean cmd = false;
                Boolean err = false;
                if (mex.length() > 3) {
                    if (mex.substring(0, 2).equals("/w")) {
                        cmd = true;
                        mex = mex.substring(3);
                        if (mex.charAt(0) == '[') {
                            mex2 = mex.substring(1);
                            int i = mex.indexOf("]");
                            if (i != -1) {
                                user = mex2.substring(0, i - 1);
                                mex2 = mex2.substring(i + 1);
                            } else {
                                err = true;
                                mex = "Comando errato.";
                            }
                        } else {
                            err = true;
                            mex = "Comando errato.";
                        }
                    }
                }
                //if (!cmd)
                //chatText.setText(chatText.getText().substring(0, chatText.getText().length() - 7) + "<span color=\"" + color + "\">" + u + ":</span> " + "</html>");
                //else if (!err) {
                //chatText.setText(chatText.getText().substring(0, chatText.getText().length() - 7) + "<span color=\"" + color + "\">" + u + ":</span> " + "<span color=\"#b600ff\">" + mex + "</span><br/></html>");
                //send(mex2, user);
                // } else if (err)
                //   chatText.setText(chatText.getText().substring(0, chatText.getText().length() - 7) + "<span color=\"#ff0000\">" + mex + "</span><br/></html>");
                chatScroll.getViewport().setViewPosition(new Point(0, chatText.getHeight()));
                if(!cmd)
                    output();
                else if(!err)
                    send(mex2,user);
                msg.setText("");
            }
        }

        public void send(String mex, String user) {

            Main.os.println("1");
            Main.os.flush();
            Main.os.println(mex);
            Main.os.flush();
            Main.os.println(user);
            Main.os.flush();
            Main.os.println(u);
            Main.os.flush();
        }
    }
}