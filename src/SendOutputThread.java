import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import javax.imageio.ImageIO;
import java.io.OutputStream;
import java.util.ArrayList;
import java.io.IOException;
import java.net.Socket;
/****/
public class SendOutputThread extends Thread
{
    private Socket aSocket;
    private ArrayList<Object> aToSend;
    /****/
    public SendOutputThread(final Socket pSocket , final ArrayList<Object> pToSend)
    {
        this.aSocket = pSocket;
        this.aToSend = pToSend;
    }
    /****/
    @Override public void run()
    {
        while(!this.isInterrupted())
        {
            double vStart = System.currentTimeMillis();
            this.sendText("(ping)");
            for(Object vObject : (ArrayList<Object>)this.aToSend.clone())
                this.sendObject(vObject);
            this.aToSend.clear();
            double vDurée = System.currentTimeMillis()-vStart;
            int vSleepTime = (int)(60-vDurée);
            if(vSleepTime > 0)
            {
                try{this.sleep(vSleepTime);}
                catch (InterruptedException e)
                {break;}
            }
        }
    }
    /****/
    private void sendObject(final Object pObject)
    {
        if(pObject instanceof String)
        {
            this.sendText("(Text)");
            this.sendText((String)pObject);
        }
        else if(pObject instanceof BufferedImage)
        {
            this.sendText("(Image)");
            this.sendImage2((BufferedImage)pObject);
        }
        else if(pObject instanceof AudioInputStream)
        {
            AudioInputStream vAIS = (AudioInputStream)pObject;
            if(this.getDurationInSeconds(vAIS) < 30)
                this.sendText("(Sound)");
            else
                this.sendText("(Music)");
            this.sendSound(vAIS);
        }
    }
    /****/
    public void sendText(final String pText)
    {
        try
        {
            byte[] vBytes = pText.getBytes();
            int vBytesLength = vBytes.length;
            if(vBytes.length > 0 && vBytes.length < 1000)
            {
                OutputStream vOS = this.aSocket.getOutputStream();
                DataOutputStream dOut = new DataOutputStream(vOS);
                dOut.writeInt(vBytes.length);
                dOut.write(vBytes);
                vOS.flush();
            }
        }
        catch (IOException e){}
    }
    /****/
    public void sendImage(final BufferedImage pImage)
    {
        try
        {
            OutputStream vOS = this.aSocket.getOutputStream();
            MemoryCacheImageOutputStream vMCIOS = new MemoryCacheImageOutputStream(vOS);
            ImageIO.write(pImage, "PNG", vMCIOS);
        }
        catch (IOException e)
        {}
    }
    /****/
    public void sendImage2(final BufferedImage pImage)
    {
        try
        {
            ByteArrayOutputStream vBAOS = new ByteArrayOutputStream();
            ImageIO.write(pImage, "PNG", vBAOS);
            byte[] vBytes = vBAOS.toByteArray();
            int vBytesLength = vBytes.length;
            DataOutputStream vDOS = new DataOutputStream(this.aSocket.getOutputStream());
            vDOS.writeInt(vBytesLength);
            vDOS.write(vBytes);
        }
        catch (IOException|NullPointerException e)
        {}
    }
    /****/
    public void sendSound(final AudioInputStream pAIS)
    {
        try
        {
            OutputStream vOS = this.aSocket.getOutputStream();
            AudioSystem.write(pAIS, AudioFileFormat.Type.WAVE, vOS);
        }
        catch (IOException e)
        {e.printStackTrace();}
    }
    /****/
    public double getDurationInSeconds(final AudioInputStream pAIS)
    {
        AudioFormat vAudioFormat = pAIS.getFormat();
        double vDurationInSeconds = pAIS.getFrameLength()/vAudioFormat.getFrameRate();
        return vDurationInSeconds;
    }
}