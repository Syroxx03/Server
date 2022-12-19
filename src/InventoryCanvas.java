import javax.swing.JComponent;
import java.util.Comparator;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.BasicStroke;
/****/
public class InventoryCanvas extends JComponent
{
    private Item[] aItems;
    private int aMainItemIndex;
    /****/
    public InventoryCanvas(final Item[] pItems, final int pMainItemIndex)
    {
        this.aItems = pItems;
        this.aMainItemIndex = pMainItemIndex;
    }
    /****/
    @Override public void paint(final Graphics g)
    {
        g.setColor(Color.white);
        int vWidth = 16;
        int vHeight = 16;
        for( int vIndex = 0; vIndex < this.aItems.length ; vIndex++)
        {

            int vX = 200 + vIndex*vWidth;
            int vY = 2;
            Item vItem = this.aItems[vIndex];
            if(vItem != null)
            {
                Image vItemImage = vItem.getTile().getImage();
                g.drawImage(vItemImage,vX , vY , vWidth,vHeight, this);
            }
            g.drawRect(vX , vY , vWidth,vHeight);
        }
        g.setColor(Color.red);
        g.drawRect(200 + this.aMainItemIndex*vWidth , 2 , vWidth,vHeight);
    }
}
