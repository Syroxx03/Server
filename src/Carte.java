import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.awt.Image;

/****/
public class Carte
{
    private String aFilePath;
    private ArrayList<Tile> aTiles;
    private ArrayList<Point> aBoxes;
    private ArrayList<Player> aPlayers;
    private HashMap<Point,Item> aItems;
    private HashMap<Point,Door> aDoors;
    /****/
    public Carte(final String pPath)
    {
        this.aFilePath = pPath;

        this.aPlayers = new ArrayList<Player>();
        this.aItems = new HashMap<Point,Item>();
        this.setDoorAndTiles();
        this.setBoxes();
    }
    /****/
    public String getFilePath(){return this.aFilePath;}
    public ArrayList<Tile> getTiles(){return this.aTiles;}
    /****/
    public HashMap<Point,Door> getDoors(){return this.aDoors;}
    /****/
    public BufferedImage getImage(final double pColumnShift, final double pRowShift)
    {
        BufferedImage vCarteImage =  new BufferedImage(300,200,BufferedImage.TYPE_INT_ARGB);
        int vCaseSize = 16;
        int vXShift = (int)(vCarteImage.getWidth()/2 - pColumnShift*vCaseSize);
        int vYShift = (int)(vCarteImage.getHeight()/2 -  pRowShift*vCaseSize);
        JComponent vCarteComponent = new CarteCanvas(this.aTiles , vCaseSize, vXShift , vYShift);
        vCarteComponent.paint(vCarteImage.createGraphics());
        return vCarteImage;
    }
    /****/
    public boolean  containsBox(final Point pPoint)
    {
        if(this.aBoxes.contains(pPoint))
            return true;
        return false;
    }
    /****/
    public void addPlayer(final Player pPlayer)
    {
        this.aTiles.add(pPlayer.getTile());
        this.aPlayers.add(pPlayer);
    }
    /****/
    public void removePlayer(final Player pPlayer)
    {
        this.aTiles.remove(pPlayer.getTile());
        this.aPlayers.remove(pPlayer);
    }
    /****/
    public Item takeItem(final Point pPos)
    {
        if(this.aItems.containsKey(pPos))
        {
            Item vItem = this.aItems.get(pPos);
            this.aItems.remove(pPos);
            this.aTiles.remove(vItem.getTile());
            return vItem;
        }
        return null;
    }
    /**return true si la case est libre*/
    public boolean addItem(final Item pItem, final Point pPos)
    {
        if(!this.aBoxes.contains(pPos) && !this.aItems.containsKey(pPos))
        {
            this.aItems.put(pPos , pItem);
            Tile vTile = pItem.getTile();
            vTile.setColumn(pPos.x);
            vTile.setRow(pPos.y);
            this.aTiles.add(pItem.getTile());
            return true;
        }
        return false;
    }
    /****/
    public boolean hit(final Player pPlayer,final String pDirection, final double[] pHitPos, final int pDamage)
    {
        boolean vConnect = false;
        for(Player vPlayer:this.aPlayers)
        {
            double vRange = 0.7;
            double[] vPlayerPos = vPlayer.getPlayerPos();
            if(vPlayer != pPlayer && Math.abs(vPlayerPos[0]-pHitPos[0]) < vRange && Math.abs(vPlayerPos[1]-pHitPos[1]) < vRange)
            {vPlayer.hited(pDirection , pDamage);
                vConnect = true;}
        }
        return vConnect;
    }
    /****/
    private void setDoorAndTiles()
    {
        this.aTiles = new ArrayList<Tile>();
        this.aDoors = new HashMap<Point,Door>();
        String vTileImagePath = null;
        BufferedImage vImg = null;
        try
        {
            URL vUrl = new File(this.aFilePath).toURL();
            BufferedReader read = new BufferedReader(
                    new InputStreamReader(vUrl.openStream()));
            String vLine;
            while ((vLine = read.readLine()) != null)
            {
                String vValues[] = vLine.split("/");
                try
                {
                    switch(vValues[0])
                    {
                        case "Cartes":this.addDoor(vLine);break;
                        case "Images":
                            vTileImagePath = vLine;
                            URL vURL2 = new File(vLine).toURL();
                            vImg = ImageIO.read(vURL2);
                            break;
                        default:
                            this.addTile(vImg , vTileImagePath , vValues);break;
                    }
                }
                catch(Exception e){e.printStackTrace();}
            }
            read.close();
        }
        catch(Exception e)
        {e.printStackTrace();}
    }
    /****/
    private void addDoor(final String vLine)
    {
        String[] vValues = vLine.split(" ");

        String vExitPath = vValues[0];

        int vEntryColumn = Integer.parseInt(vValues[1].split("/")[0]);
        int vEntryRow = Integer.parseInt(vValues[1].split("/")[1]);

        int vExitColumn = Integer.parseInt(vValues[2].split("/")[0]);
        int vExitRow = Integer.parseInt(vValues[2].split("/")[1]);

        Door vDoor = new Door(vExitPath, vExitColumn, vExitRow);
        Point vEntryPos = new Point(vEntryColumn , vEntryRow);

        this.aDoors.put(vEntryPos , vDoor);
    }
    // /****/ 
    // private void addTile(final BufferedImage pImg, final String pImgPth, final String[] pValues)
    // {
    // int vColumn = Integer.parseInt(pValues[0]);
    // int vRow = Integer.parseInt(pValues[1]);
    // Tile vTile = new Tile(pImg, pImgPth,vColumn,vRow);
    // this.aTiles.add(vTile);
    // if (pImgPth.contains("(Item)"))
    // this.aItems.put(new Point(vColumn, vRow),getItem(pImgPth,vTile));
    // }
    /****/
    private void addTile(final BufferedImage pImg ,final String pImagePath, final String[] pValues)
    {
        int vColumn = Integer.parseInt(pValues[0]);
        int vRow = Integer.parseInt(pValues[1]);
        Tile vTile = new Tile(pImg, pImagePath,vColumn,vRow);

        int vFinalRotation = Integer.parseInt(pValues[2]);
        for(int vActualRotation = 0 ; vActualRotation < vFinalRotation ; vActualRotation++)
            vTile.rotate();

        boolean vVerticalFlip = pValues[3].equals("1");
        if(vVerticalFlip)
            vTile.verticalFlip();

        boolean vHorizontalFlip = pValues[4].equals("1");
        if(vHorizontalFlip)
            vTile.horizontalFlip();

        this.aTiles.add(vTile);
        if (pImagePath.contains("(Item)"))
            this.aItems.put(new Point(vColumn, vRow),getItem(pImagePath,vTile));
    }
    /****/
    private Item getItem(final String pPath, final Tile pTile)
    {
        Item vItem = new Item(pTile);
        return vItem;
    }
    /****/
    private void setBoxes()
    {
        this.aBoxes = new ArrayList<Point>();
        for(Tile vTile:this.aTiles)
        {
            int[][] vBoxes=vTile.getBoxes();
            if(vBoxes!=null)
            {
                for(int vRelativeColumn = 0 ; vRelativeColumn < vBoxes.length; vRelativeColumn++){
                    for(int vRelativeRow = 0;vRelativeRow < vBoxes[vRelativeColumn].length; vRelativeRow++){
                        if(vBoxes[vRelativeColumn][vRelativeRow] == 1)
                        {
                            int vColumn = (int)vTile.getColumn()+vRelativeColumn;
                            int vRow = (int)vTile.getRow()+vRelativeRow;
                            Point vPoint = new Point(vColumn,vRow);
                            this.aBoxes.add(vPoint);
                        }
                    }
                }
            }
        }
    }
}