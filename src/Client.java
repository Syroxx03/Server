import java.net.Socket;
import java.util.ArrayList;
import java.net.SocketException;
import java.io.IOException;

/****/
public class Client
{
    private Server aServer;
    //Pseudo du client
    private String aPseudo;
    //Point de connexion au client
    private Socket aSocket;
    //Liste des Objets à envoyer 
    private ArrayList<Object> aToSend;
    //Liste des strings recu et pas encore traitées
    private ArrayList<String> aReceived;
    //Thread chargé de recevoir ce que le client envoie et de les mettre dans aReceived
    private GetInputThread aGetInputThread;
    //Thread chargé d'envoyer le contenue de aToSend au client
    private SendOutputThread aSendOutputThread;
    /****/
    public Client(final Server pServer, final Socket pSocket)
    {
        this.aServer = pServer;

        this.aSocket = pSocket;
        try{this.aSocket.setSoTimeout(3000);}
        catch (SocketException e)
        {e.printStackTrace();}

        this.aToSend = new ArrayList<Object>();
        this.aReceived = new ArrayList<String>();

        this.aGetInputThread = new GetInputThread(this);
        this.aGetInputThread.start();

        this.aSendOutputThread  = new SendOutputThread(this.aSocket, this.aToSend);
        this.aSendOutputThread.start();
    }
    /****/
    public String getPseudo(){return this.aPseudo;}
    /****/
    public Socket getSocket(){return this.aSocket;}
    /****/
    public ArrayList<Object> getToSend(){return this.aToSend;}
    /****/
    public ArrayList<String> getReceived(){return this.aReceived;}
    /****/
    public void close()
    {
        this.aSendOutputThread.interrupt();
        this.aServer.removeClient(this);
        try{this.aSocket.close();}
        catch (IOException e)
        {e.printStackTrace();}
        this.aGetInputThread.interrupt();
    }
}