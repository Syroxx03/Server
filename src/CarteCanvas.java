import javax.swing.JComponent;
import java.util.Comparator;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
public class CarteCanvas extends JComponent
{
    private ArrayList<Tile> aTiles;
    private int aCaseSize;
    private int aColumnShift;
    private int aRowShift;
    /****/
    public CarteCanvas(final ArrayList<Tile> pTiles,final int pCaseSize,final int pColumnShift, final int pRowShift)
    {
        this.aTiles = pTiles;
        this.aCaseSize = pCaseSize;
        this.aColumnShift = pColumnShift;
        this.aRowShift = pRowShift;
    }
    /****/
    @Override public void paint(final Graphics g)
    {
        double vStart = System.currentTimeMillis();
        this.aTiles.sort(Comparator.comparing(Tile::getLayer).thenComparing(Tile::getLastRow));
        try
        {
            for(Tile vTile:this.aTiles)
            {
                int vX = (int)(vTile.getColumn()*this.aCaseSize)+this.aColumnShift;
                int vY = (int)(vTile.getRow()*this.aCaseSize)+this.aRowShift;
                int vWidth = vTile.getWidth()*this.aCaseSize;
                int vHeight = vTile.getHeight()*this.aCaseSize;
                Image vImage = vTile.getImage();
                g.drawImage(vImage,vX,vY,vWidth, vHeight,this);
            }
        }
        catch(ConcurrentModificationException e)
        {}
        // System.out.println(System.currentTimeMillis() - vStart);
    }
}