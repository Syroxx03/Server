/****/
public class RunGameThread extends Thread
{
    private Server aServer;
    /****/
    public RunGameThread(final Server pServer)
    {
        this.aServer = pServer;
    }
    /****/
    @Override public void run()
    {
        while(!this.isInterrupted())
        {
            double vStart = System.currentTimeMillis();
            this.aServer.getGameEngine().updateGame();
            this.aServer.getGameEngine().updateImage();
            double vDurée = System.currentTimeMillis()-vStart;
            int vSleepTime = (int)(60-vDurée);
            if(vSleepTime > 0)
            {
                try{this.sleep(vSleepTime);}
                catch (InterruptedException e){break;}
            }
        }
    }
}