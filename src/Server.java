import java.io.BufferedOutputStream;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Socket;
import java.util.List;
import java.net.InetAddress;
/***/
public class Server
{
    private UserInterface aUserInterface;
    private ArrayList<Client> aClients;
    private ServerSocket aServerSocket;
    private SearchClientThread aSearchClientThread;
    private RunGameThread aRunGameThread;
    private GameEngine aGameEngine;
    /**Constructeur*/
    public Server(final UserInterface pUserInterface)
    {
        this.aClients = new ArrayList<Client>();
        this.aUserInterface = pUserInterface;
        this.aGameEngine = new GameEngine();
    }
    /****/
    public GameEngine getGameEngine(){return this.aGameEngine;}
    /****/
    public ArrayList<Client> getClients(){return this.aClients;}
    /****/
    public ServerSocket getServerSocket(){return this.aServerSocket;}
    /****/
    public void restartGame()
    {
        GameEngine vGameEngine = new GameEngine();
        vGameEngine.copyPlayers(this.aGameEngine.getPlayers());
        this.aGameEngine = vGameEngine;
    }
    /****/
    public void addClient(final Socket pSocket)
    {
        Client vClient = new Client(this, pSocket);
        this.aClients.add(vClient);
        this.aUserInterface.setClientNbr(this.aClients.size());
        this.aGameEngine.addPlayer(pSocket, vClient.getReceived(), vClient.getToSend());
    }
    /****/
    public void removeClient(final Client pClient)
    {
        this.aGameEngine.removePlayer(pClient.getSocket());
        this.aClients.remove(pClient);
        this.aUserInterface.setClientNbr(this.aClients.size());
    }
    /****/
    public void searchClient()
    {
        if(this.aSearchClientThread != null)
            this.aSearchClientThread.interrupt();
        //Thread chargé d'attendre la connexion de clients et de les liers au serveur
        this.aSearchClientThread = new SearchClientThread(this);//Initialise
        this.aSearchClientThread.start();//Démarre
    }
    /****/
    public void unsearchClient()
    {
        if(this.aSearchClientThread != null)
            this.aSearchClientThread.interrupt();
        this.aSearchClientThread = null;
    }
    /**Return true si le serveur est ouvert**/
    public boolean openServer()
    {
        try
        {
            if(this.aServerSocket != null)
                this.aServerSocket.close();

            this.aServerSocket = new ServerSocket(1310);

            int vPort = this.aServerSocket.getLocalPort();
            this.aUserInterface.setConnexionPort(vPort);
            this.aRunGameThread = new RunGameThread(this);
            this.aRunGameThread.start();
            return true;
        }
        catch (java.io.IOException e)
        {e.printStackTrace();return false;}
    }
    /**Return true si le serveur est fermé*/
    public boolean closeServer()
    {
        try
        {
            if(this.aRunGameThread != null)
                this.aRunGameThread.interrupt();
            if(this.aSearchClientThread != null)
                this.aSearchClientThread.interrupt();
            if(this.aServerSocket != null)
                this.aServerSocket.close();
            for(Client vClient: (ArrayList<Client>)this.aClients.clone())
                vClient.close();
            this.aUserInterface.setClientNbr(this.aClients.size());
            return true;
        }
        catch (java.io.IOException|java.lang.NullPointerException e)
        {e.printStackTrace();return false;}
    }
}