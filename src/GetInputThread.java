import java.nio.charset.StandardCharsets;
import java.io.DataInputStream;
import java.io.IOException;
/****/
public class GetInputThread extends Thread
{
    private Client aClient;
    /****/
    public GetInputThread(final Client pClient)
    {
        this.aClient = pClient;
    }
    /****/
    @Override public void run()
    {
        while(!this.isInterrupted())
        {
            String vText = this.receiveText();
            if(vText == null)
                break;
            else if(!vText.equals("(ping)"))
                this.aClient.getReceived().add(vText);
        }
        this.aClient.close();
    }
    /****/
    public String receiveText()
    {
        try
        {
            DataInputStream vDIS = new DataInputStream(this.aClient.getSocket().getInputStream());
            int vBytesLength = vDIS.readInt();
            if(vBytesLength > 0)
            {
                byte[] vBytes = new byte[vBytesLength];
                vDIS.readFully(vBytes);
                String vText = new String(vBytes, StandardCharsets.UTF_8);
                return vText;
            }
        }
        catch (OutOfMemoryError|IOException e){}
        return null;
    }
}