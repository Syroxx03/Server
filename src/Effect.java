/****/
public class Effect
{
    private double aStartTime;
    private int aDurationMs;
    private String aType;
    /****/
    public Effect(final int pDuration , final String pType )
    {
        this.aType = pType;
        this.aDurationMs = pDuration;
        this.aStartTime = System.currentTimeMillis();
    }
    /****/
    public String getType(){return this.aType;}
    /****/
    public boolean isFinished()
    {
        double vElapsedTimeMs = (System.currentTimeMillis() - this.aStartTime);
        return vElapsedTimeMs  > this.aDurationMs ;
    }
}