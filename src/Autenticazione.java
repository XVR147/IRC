
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.StyledEditorKit;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Autenticazione extends JFrame{

    Container c = getContentPane();
    JPanel p = new JPanel();

    JPanel Ptitolo = new JPanel();
    JLabel titolo = new JLabel("Accesso");

    JPanel nord = new JPanel();
    JLabel id = new JLabel("ID");
    JPanel idpanel = new JPanel();
    JPanel idtextpanel = new JPanel();
    JTextField idtext = new JTextField(20);

    JPanel centro = new JPanel();
    JLabel pass = new JLabel("Password");
    JPanel passpanel = new JPanel();
    JPanel passtextpanel = new JPanel();
    JPasswordField passtext = new JPasswordField(20);

    JPanel sud = new JPanel();
    JButton login = new JButton("Login");
    JButton registrati = new JButton("Registrati");

    public Autenticazione()
    {
        super("Accesso");
        c.add(p);
        ImageIcon img = new ImageIcon("../iRC/src/iRC/icon.png");
        setIconImage(img.getImage());
        p.setLayout(new GridLayout(4,1));
        p.add(Ptitolo);
        p.add(nord);
        p.add(centro);
        p.add(sud);

        titolo.setFont(new Font(titolo.getFont().getName(), Font. BOLD, 20));
        Ptitolo.add(titolo);

        nord.setLayout(new BorderLayout());
        nord.add(idpanel, BorderLayout.WEST);
        nord.add(idtextpanel, BorderLayout.CENTER);

        idpanel.setPreferredSize(new Dimension(100,60));
        idpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        idpanel.add(id);
        idtextpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        idtextpanel.add(idtext);

        centro.setLayout(new BorderLayout());
        centro.add(passpanel, BorderLayout.WEST);
        centro.add(passtextpanel, BorderLayout.CENTER);

        passpanel.setPreferredSize(new Dimension(100,60));
        passpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        passpanel.add(pass);
        passtextpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        passtextpanel.add(passtext);

        Bottone x = new Bottone();
        login.addActionListener(new Bottone());
        registrati.addActionListener(new Bottone());
        sud.add(login);
        sud.add(registrati);
        login.setPreferredSize(new Dimension(100,30));
        registrati.setPreferredSize(new Dimension(100,30));
        login.setBackground(new Color(242, 249, 92));
        registrati.setBackground(new Color(252, 170, 63));
        passpanel.setBackground(Color.WHITE);
        passtextpanel.setBackground(Color.WHITE);
        idpanel.setBackground(Color.WHITE);
        idtextpanel.setBackground(Color.WHITE);
        sud.setBackground(Color.WHITE);
        Ptitolo.setBackground(Color.WHITE);
        setSize(435,250);
        setVisible(true);
        setResizable(false);
    }

    public boolean login()
    {

        String user= idtext.getText();
        char[] pw = passtext.getPassword();
        String psw = new String(pw);
        String mode = "login";
        try{

            //-----------------invia credenziali-------------------

            Main.os.println(user);
            Main.os.flush();
            Main.os.println(psw);
            Main.os.flush();
            Main.os.println(mode);
            Main.os.flush();
            //connessione.close();
            //-------ricezione risultato dal server----------------
            //connessione = new Socket(ip,porta);
            String ret = null;
            ret = Main.is.readLine();
            //System.out.println(ret);
            if(ret.equals("no"))
                JOptionPane.showMessageDialog(null, "ID o password errati!\n");
            //--------------------------------------
            if(ret.equals("standard")|| ret.equals("admin")){
                JOptionPane.showMessageDialog(null, "Login effettuato con successo!\n");
                Client.ret=ret;
                Client.u=user;
                Client.flag=false;

                dispose();
            }

        }
        catch(IOException e){
            JOptionPane.showMessageDialog(null, "Server Scollegato. Impossibile connettersi.");
        }
        return false;
    }

    public boolean registrazione()
    {

        String user= idtext.getText();
        char[] pw = passtext.getPassword();
        String psw = new String(pw);
        String mode = "sub";
        try{
            //-----------------invia credenziali-------------------

            Main.os.println(user);
            Main.os.flush();
            Main.os.println(psw);
            Main.os.flush();
            Main.os.println(mode);
            //connessione.close();
            //-------ricezione risultato dal server----------------
            //connessione = new Socket(ip,porta);

            String ret = null;
            ret = Main.is.readLine();
            //connessione.close();
            JOptionPane.showMessageDialog(null, ret+"\n");
            //--------------------------------------
            if(ret.equals("Registrazione effettuata con successo!")){
                Client.ret="standard";
                Client.u=user;
                Client.flag=false;
                dispose();
            }

        }
        catch(IOException e){
            JOptionPane.showMessageDialog(null, "Server Scollegato. Impossibile connettersi.");
        }
        return false;
    }
    public class Bottone implements ActionListener{

        public void actionPerformed(ActionEvent e)
        {
            if(((JButton)(e.getSource())).getText().equals("Login"))
            {
                if(login())
                    dispose();

            }
            if(((JButton)(e.getSource())).getText().equals("Registrati"))
            {
                if(registrazione())
                    dispose();
            }
        }
    }
}
