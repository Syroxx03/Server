import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

public class PVCanvas extends JComponent
{
    private int aPV;
    private BufferedImage aCoeur;
    private BufferedImage aCoeurVide;
    /****/
    public PVCanvas(final int pPV)
    {
        this.aPV = pPV;
        try{
            this.aCoeur  = ImageIO.read(new File("Tools/Coeur.png"))
            ;}
        catch(Exception e)
        {e.printStackTrace();}
        try{this.aCoeurVide  = ImageIO.read(new File("Tools/CoeurVide.png"));}
        catch(Exception e)
        {e.printStackTrace();}
    }
    /****/
    @Override public void paint(final Graphics g)
    {
        int vWidth = 13;
        int vHeight = 13;
        for(int vHeartNbr = 0 ; vHeartNbr<10 ; vHeartNbr++)
        {
            int vX = 2 + (vWidth-1)*vHeartNbr;
            int vY = 2;
            if(vHeartNbr < this.aPV)
                g.drawImage(this.aCoeur.getScaledInstance(vWidth, vHeight, Image.SCALE_SMOOTH),vX,vY,this);
            else
                g.drawImage(this.aCoeurVide.getScaledInstance(vWidth, vHeight, Image.SCALE_SMOOTH),vX,vY,this);
        }
    }
}