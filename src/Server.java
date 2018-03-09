import java.io.DataInputStream;
import java.io.EOFException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/*
 * A chat server that delivers public and private messages.
 */
public class Server {

    // The server socket.
    private static ServerSocket serverSocket = null;
    // The client socket.
    private static Socket clientSocket = null;

    // This chat server can accept up to maxClientsCount clients' connections.
    private static final int maxClientsCount = 10;
    private static final clientThread[] threads = new clientThread[maxClientsCount];

    public static void main(String args[]) {

        // The default port number.
        int portNumber = 2222;
        if (args.length < 1) {
            System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
                    + "Now using port number=" + portNumber);
        } else {
            portNumber = Integer.valueOf(args[0]).intValue();
        }

        /*
         * Open a server socket on the portNumber (default 2222). Note that we can
         * not choose a port less than 1023 if we are not privileged users (root).
         */
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e);
        }

        /*
         * Create a client socket for each connection and pass it to a new client
         * thread.
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientsCount; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
                if (i == maxClientsCount) {
                    PrintStream os = new PrintStream(clientSocket.getOutputStream());
                    os.println("Server too busy. Try later.");
                    os.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}

/*
 * The chat client thread. This client thread opens the input and the output
 * streams for a particular client, ask the client's name, informs all the
 * clients connected to the server about the fact that a new client has joined
 * the chat room, and as long as it receive data, echos that data back to all
 * other clients. The thread broadcast the incoming messages to all clients and
 * routes the private message to the particular client. When a client leaves the
 * chat room this thread informs also all the clients about that and terminates.
 */
class clientThread extends Thread {

    private String clientName = null;
    private String pow = null;
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
            /*
             * Create input and output streams for this client.
             */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());
            String id="";
            boolean flag=false;
            while (!flag) {
                id = is.readLine();
                String pass = is.readLine();
                String mode = is.readLine();

                //System.out.println(id+" "+pass+" "+mode);

                RandomAccessFile db = new RandomAccessFile("db.txt", "rw");

                String tempid;
                String temppass;
                String ret="Errore";
                boolean EOF;
                EOF = false;

                while(!EOF) {
                    try {
                        tempid = db.readUTF();
                        temppass = db.readUTF();

                        if (tempid.equals(id)) {
                            if (mode.equals("sub")) {
                                ret = "Nome utente esistente!";
                            } else if (temppass.equals(pass)) {
                                ret = db.readUTF();
                                pow = ret;
                                flag=true;
                            } else {
                                ret = "no";
                            }
                            EOF = true;
                        } else {
                            db.readUTF();
                        }
                    } catch (EOFException e) {
                        EOF = true;
                        if (mode.equals("login")) {
                            ret = "no";
                        }
                        if (mode.equals("sub")) {
                            ret = "Registrazione effettuata con successo!";
                            db.seek(db.length());
                            db.writeUTF(id);
                            db.writeUTF(pass);
                            db.writeUTF("standard");
                            pow = "standard";
                            flag=true;
                        }
                    }
                }
                //System.out.println(ret);
                os.println(ret);
                os.flush();

            }


            synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] == this) {
                        clientName = id;
                        threads[i].os.println(getUsersList());
                        break;
                    }
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null && threads[i] != this && threads[i].clientName != null) {
                        threads[i].os.println("*** " + id
                                + " e' entrato in stanza!!! ***");
                        threads[i].os.println(getUsersList());
                    }
                }
            }
            String color;
            if(pow.equals("standard"))
                color = "#1d21f7";
            else
                color = "#419b4a";
            /* Start the conversation. */
            while (true) {
                String com = is.readLine();

                /* If the message is private sent it to the given client. */
                if (com.equals("1")) {
                    String mex = is.readLine();
                    String user = is.readLine();
                    String mitt = is.readLine();

                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName.equals(user)) {

                                threads[i].os.println("1");
                                os.flush();
                                mex="<span color=\"b600ff\">"+this.clientName+": "+mex+"</span>";
                                threads[i].os.println(mex);
                                os.flush();
                                /*
                                 * Echo this message to let the client know the private
                                 * message was sent.
                                 */
                                break;
                            }
                            else if(threads[i].clientName.equals(mitt) && threads[i]!= null)
                            {
                                mex="<span color=\""+color+"\">"+this.clientName+" > "+user+": <span color=\"b600ff\">"+mex+"</span></span>";
                                threads[i].os.println("1");
                                os.flush();
                                threads[i].os.println(mex);
                                os.flush();

                            }
                        }
                    }

                } else {
                    String line = is.readLine();

                    synchronized (this) {
                        for (int i = 0; i < maxClientsCount; i++) {
                            if (threads[i] != null && threads[i].clientName != null) {
                                threads[i].os.println("0");
                                os.flush();
                                threads[i].os.println("<span color=\"" + color + "\">" + id + ":</span> "+line);
                                os.flush();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
    }
    public String getUsersList() {
        String activeUsers = "&^";
        for (int j = 0; j < maxClientsCount; j++) {
            if (threads[j] != null) {
                activeUsers = activeUsers + threads[j].clientName + ":";
            }
        }
        activeUsers = activeUsers.concat("^&");
        System.out.println(activeUsers);
        return activeUsers;
    }

}