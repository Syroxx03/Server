import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Font;
/****/
public class TextCanvas extends JComponent
{
    private String aText;
    /****/
    public TextCanvas()
    {
        this.aText = "";
    }
    /****/
    public void paintText(final String pText,final BufferedImage pImage)
    {
        this.aText = pText;
        this.paint(pImage.createGraphics());
    }
    /****/
    @Override public void paint(final Graphics g)
    {
        g.setFont(new Font("Poor Richard",Font.BOLD,40));
        g.drawString(this.aText,80, 100);
    }
}