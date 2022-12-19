import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Font;
/****/
public class TimerCanvas extends JComponent
{
    private double aStartTime;
    private boolean aTimesUp;
    /****/
    public TimerCanvas()
    {
        this.aStartTime = System.currentTimeMillis();
        this.aTimesUp = false;
    }
    /****/
    public void setStartTime(){this.aStartTime = System.currentTimeMillis();}
    /****/
    public boolean isTimesUp(){ return this.aTimesUp;}
    /****/
    @Override public void paint(final Graphics g)
    {
        if(!this.aTimesUp)
        {

            int vElapsedTime = (int)(System.currentTimeMillis() - this.aStartTime)/1000;
            int vTimeLeft = 30 - vElapsedTime;
            this.aTimesUp = (vTimeLeft  <= 0);

            g.setFont(new Font("Poor Richard",Font.BOLD,20));
            g.drawString(""+vTimeLeft,140, 15);
        }
    }
}
