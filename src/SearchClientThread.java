import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;

public class SearchClientThread extends Thread
{
    private Server aServer;
    /****/
    public SearchClientThread(final Server pServer)
    {
        this.aServer=pServer;
    }
    /****/
    @Override public void run()
    {
        try
        {
            while(!this.isInterrupted())
            {
                ServerSocket vServerSocket = this.aServer.getServerSocket();

                Socket vSocket = vServerSocket.accept();

                this.aServer.addClient(vSocket);
            }
        }
        catch (IOException e)
        {}
        this.aServer.unsearchClient();
    }
}