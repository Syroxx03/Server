import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.awt.Point;
import java.util.List;
import java.io.File;
import java.net.URL;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/****/
public class Player
{
    private ArrayList<Object> aToSend;
    private ArrayList<String> aReceived;

    private Carte aCurrentCarte;
    private Tile aTile;

    //              Direction         Action          Animation de l'action
    private HashMap<String , HashMap< String , ArrayList<BufferedImage>>> aImages;
    private String aDirection;
    private String aAction;
    private int aImageIndex;

    private Inventory aInventory;
    private ArrayList<Projectile> aProjectiles;
    private ArrayList<Effect> aEffects;
    private boolean aIsVisible;
    private double aSpeed;
    private int aPV;


    /****/
    public Player(final Carte pCarte, final ArrayList<String> pReceived, final ArrayList<Object> pToSend)
    {
        this.aReceived = pReceived;
        this.aToSend = pToSend;
        this.aInventory = new Inventory();

        this.aCurrentCarte = pCarte;

        this.aAction = "Run";
        this.aDirection = "South";
        this.aImageIndex = 0;

        this.setImages(1);
        this.aTile = new Tile("(L3)(W3)(H2)",23,16);
        BufferedImage vImage = this.aImages.get(this.aDirection).get(this.aAction).get(this.aImageIndex);
        this.aTile.setImage(vImage);

        this.aSpeed = 0.3;
        this.aPV = 10;

        this.aEffects = new ArrayList<Effect>();
        this.aProjectiles = new ArrayList<Projectile>();
        this.aIsVisible = true;
    }
    public String getAction()
    {
        return this.aAction;
    }
    /****/
    public int getPV(){return this.aPV;}
    /****/
    public void setPV(final int pPV){this.aPV = pPV;}
    /****/
    public Tile getTile(){return this.aTile;}
    /****/
    public Inventory getInventory() {return this.aInventory;}
    /****/
    public ArrayList<String> getReceived(){return this.aReceived;}
    /****/
    public ArrayList<Object> getToSend(){return this.aToSend;}
    /****/
    public Carte getCarte(){return this.aCurrentCarte;}
    /****/
    public void setCarte(final Carte pCarte){this.aCurrentCarte = pCarte;}
    /****/
    public void setDirection(final String pDirection)
    {
        if(!this.aDirection.equals(pDirection))
            this.aImageIndex = 0;
        this.aDirection = pDirection;
    }
    /****/
    public boolean verifEndCondition()
    {
        if(this.aCurrentCarte.getFilePath().equals("Cartes/FinalTemplev03.txt")&&this.aInventory.verifEndCondition())
            return true;
        return false;
    }
    /****/
    public void hited(final String pDirectionSource , final int pDamage)
    {
        if(this.aAction.equals("Die"))
            return;
        switch(pDirectionSource)
        {
            case "North": this.aDirection = "South";break;
            case "South":this.aDirection = "North";break;
            case "East":this.aDirection = "West";break;
            case "West":this.aDirection = "East";break;
        }
        this.aImageIndex = 0;
        this.aAction = "Hited";
        this.aPV -=pDamage;
        this.sendSound("Kick");
        if(this.aPV<=0)
            this.die();
    }
    /****/
    public void die()
    {
        this.clearEffects();
        Item[] vItems = this.aInventory.getItems();
        for(int vIndex = 0; vIndex<vItems.length; vIndex++)
            this.drop(vIndex);
        this.aAction = "Die";
    }
    /****/
    public void respawn(final Carte pCarte, final int pColumn , final int pRow)
    {
        this.aPV = 10;
        this.aCurrentCarte.removePlayer(this);
        this.aCurrentCarte = pCarte;
        this.aCurrentCarte.addPlayer(this);
        this.aTile.setColumn(pColumn);
        this.aTile.setRow(pRow);
    }
    /****/
    public void setDefaultImage()
    {
        try
        {
            BufferedImage vImage = this.aImages.get(this.aDirection).get("Stop").get(0);
            if(this.aAction.equals("Bow")||this.aAction.equals("Die")||this.aAction.equals("Punch")||this.aAction.equals("Hited")||this.aAction.equals("Attack"))
            {
                if(this.aAction.equals("Hited"))
                {
                    switch(this.aDirection)
                    {
                        case "West":this.moove(this.aTile.getColumn()+0.1, this.aTile.getRow());break;
                        case "East":this.moove(this.aTile.getColumn()-0.1, this.aTile.getRow());break;
                        case "North":this.moove(this.aTile.getColumn(), this.aTile.getRow()+0.1);break;
                        case "South":this.moove(this.aTile.getColumn(), this.aTile.getRow()-0.1);break;
                    }
                }
                if(this.aImageIndex < this.aImages.get(this.aDirection).get(this.aAction).size())
                {

                    vImage = this.aImages.get(this.aDirection).get(this.aAction).get(this.aImageIndex);

                    this.aImageIndex++;
                }
                else
                {
                    if(this.aAction.equals("Punch"))
                        this.aCurrentCarte.hit(this,this.aDirection, this.getDirectionPos(), 1);
                    else if(this.aAction.equals("Attack"))
                        this.aCurrentCarte.hit(this,this.aDirection, this.getDirectionPos(), 3);
                    else if(this.aAction.equals("Bow"))
                    {
                        Projectile addedProjectile = new Projectile(this, aDirection, aCurrentCarte);
                        this.aProjectiles.add(addedProjectile);
                    };
                    this.aAction = "Stop";
                }
            }
            else
                this.aAction = "Stop";
            if(this.aIsVisible)
                this.aTile.setImage(vImage);
        }
        catch(Exception e)
        {e.printStackTrace();this.aAction = "Stop";this.aImageIndex = 0;}
    }
    /****/
    public String getDirection()
    {
        return this.aDirection;
    }
    /****/
    public void doAction(final int pKeyCode)
    {
        if(this.aAction.equals("Bow")||this.aAction.equals("Punch")||this.aAction.equals("Attack")||this.aAction.equals("Die"))
            return;
        switch(pKeyCode)
        {
            // Espace
            case 32:
                this.aInventory.changeMainItem();
                break;
            case 37:
                if(!this.aAction.equals("Stop"))
                    return;
                this.setDirection("West");
                this.aAction=("Run");
                this.updateImage();
                for(double vMove = 0; vMove < this.aSpeed; vMove += 0.1)
                    this.moove(this.aTile.getColumn()-0.1, this.aTile.getRow());
                break;
            case 38:
                if(!this.aAction.equals("Stop"))
                    return;
                this.setDirection("North");
                this.aAction=("Run");
                this.updateImage();
                for(double vMove = 0; vMove < this.aSpeed; vMove += 0.1)
                    this.moove(this.aTile.getColumn(), this.aTile.getRow()-0.1);
                break;
            case 39:
                if(!this.aAction.equals("Stop"))
                    return;
                this.setDirection("East");
                this.aAction=("Run");
                this.updateImage();
                for(double vMove = 0; vMove < this.aSpeed; vMove += 0.1)
                    this.moove(this.aTile.getColumn()+0.1, this.aTile.getRow());
                break;
            case 40:
                if(!this.aAction.equals("Stop"))
                    return;
                this.setDirection("South");
                this.aAction = ("Run");
                this.updateImage();
                for(double vMove = 0; vMove < this.aSpeed; vMove += 0.1)
                    this.moove(this.aTile.getColumn(), this.aTile.getRow()+0.1);
                break;
            case 69:
                this.take();
                break; //touche E
            case 88:
                this.drop(this.aInventory.getMainItemIndex());
                break; //touche X
            case 65:
                Item vHandItem = this.aInventory.getItems()[this.aInventory.getMainItemIndex()];
                if(vHandItem!= null && vHandItem.getTile().getImagePath().contains("Epee"))
                {
                    this.aAction=("Attack");
                    this.sendSound("sword");
                }
                else
                    this.aAction=("Punch");
                this.aImageIndex = 0;
                this.updateImage();
                break;
            case 90:
                this.useItem();
                break;
        }
    }
    /*****/
    private void useItem()
    {
        Item vItem = this.aInventory.getItems()[this.aInventory.getMainItemIndex()];
        if(vItem == null)
            return;
        switch (vItem.getTile().getImagePath()) {
            case "Images/Onegameforall/Objets(L2)/potionDeVie(Item).png" :
                this.aPV = 10;
                this.sendSound("potion");
                this.aInventory.getItems()[this.aInventory.getMainItemIndex()] = null;
                break;
            case "Images/Onegameforall/Decor(L3)/Mushroom(Item).png" :
                if(this.aPV< 7)
                {
                    this.aPV+= 3;
                }
                else{
                    this.aPV = 10;
                    this.sendSound("eat");
                    this.aInventory.getItems()[this.aInventory.getMainItemIndex()] = null;
                }
                break;
            case "Images/Onegameforall/Objets(L2)/PotionInvi(Item).png":
                this.aIsVisible = false;
                this.aTile.setImage(null);
                this.sendSound("potion");
                this.aInventory.getItems()[this.aInventory.getMainItemIndex()] = null;

                Effect vInvi = new Effect(5000,"Invi");
                this.aEffects.add(vInvi);
                break;
            case "Images/Onegameforall/Objets(L2)/PotionVitesse(Item).png":
                this.aSpeed += 0.1;
                this.sendSound("potion");
                this.aInventory.getItems()[this.aInventory.getMainItemIndex()] = null;

                Effect vSpeed = new Effect(5000,"Speed");
                this.aEffects.add(vSpeed);
                break;
            case "Images/Onegameforall/Objets(L2)/Arc(Item).png":
                this.aAction = "Bow";
                this.aImageIndex = 0;
        }
    }
    /****/
    public void clearEffects()
    {
        Iterator<Effect> vIterator = this.aEffects.iterator();
        while(vIterator.hasNext())
        {
            Effect vEffect = vIterator.next();
            switch(vEffect.getType())
            {
                case "Speed":
                    this.aSpeed -= 0.1;
                    break;
                case "Invi":
                    this.aIsVisible = true;
            }
            vIterator.remove();
        }
    }
    /****/
    public void updateEffects()
    {
        Iterator<Effect> vIterator = this.aEffects.iterator();
        while(vIterator.hasNext())
        {
            Effect vEffect = vIterator.next();
            if(vEffect.isFinished())
            {
                switch(vEffect.getType())
                {
                    case "Speed":
                        this.aSpeed -= 0.1;
                        break;
                    case "Invi":
                        this.aIsVisible = true;
                }
                vIterator.remove();
            }
        }
    }
    /*****/
    private void updateImage()
    {
        if(!this.aIsVisible)
            return;
        try
        {
            if(this.aImageIndex >= this.aImages.get(this.aDirection).get(this.aAction).size())
                this.aImageIndex = 0;
            BufferedImage vImage = this.aImages.get(this.aDirection).get(this.aAction).get(this.aImageIndex);
            this.aTile.setImage(vImage);
        }
        catch(Exception e)
        {e.printStackTrace(); this.aAction = "Stop";this.aImageIndex = 0;}
        this.aImageIndex++;
    }
    public void updateProjectile()
    {
        Iterator<Projectile> it = aProjectiles.iterator();
        while(it.hasNext())
        {
            Projectile proj = it.next();
            if(proj.routine())
            {
                proj.deleteTile();
                it.remove();
            }
        }
    }
    /****/
    public void moove(final double pColumn , final double pRow)
    {
        if(!this.containsBox(pColumn, pRow))
        {
            this.aTile.setColumn(pColumn);
            this.aTile.setRow(pRow);
        }
    }
    /****/
    public void take()
    {
        if(!this.aInventory.isFull())
        {
            Item vItem = this.aCurrentCarte.takeItem(this.getDirectionPoint());
            if(vItem != null)
            {
                this.sendSound("take");
                this.aInventory.addItem(vItem);
            }
        }
    }
    /****/
    public void drop(final int pItemIndex)
    {
        Point vDropPoint = this.getDirectionPoint();
        Item vItem = this.aInventory.getItems()[pItemIndex];
        if(vItem!=null)
        {
            int vRayon = 0;
            boolean vIsDropped = false;
            while(!vIsDropped)
            {
                for(int vColumnRayon = -vRayon; vColumnRayon <= vRayon; vColumnRayon++)
                {
                    int vRowRayon = vRayon - Math.abs(vColumnRayon);
                    Point vPoint = new Point(vDropPoint.x+vColumnRayon,vDropPoint.y+vRowRayon);
                    vIsDropped = this.aCurrentCarte.addItem(vItem , vPoint);
                    if(vIsDropped)
                        break;
                    vRowRayon = -vRayon + Math.abs(vColumnRayon);
                    vPoint = new Point(vDropPoint.x+vColumnRayon,vDropPoint.y+vRowRayon);
                    vIsDropped = this.aCurrentCarte.addItem(vItem , vPoint);
                    if(vIsDropped)
                        break;
                }
                vRayon ++;
            }
            this.sendSound("drop");
            this.aInventory.getItems()[pItemIndex] = null;
        }
    }
    /****/
    public Point getPlayerPoint()
    {
        return new Point((int)Math.round(this.aTile.getColumn()+1),(int)Math.round(this.aTile.getRow())+1);
    }
    /****/
    public double[] getPlayerPos()
    {
        return new double[]{this.aTile.getColumn()+1,this.aTile.getRow()+1};
    }
    /****/
    public Point getDirectionPoint()
    {
        double[] vPos = this.getDirectionPos();
        return new Point((int)Math.round(vPos[0]),(int)Math.round(vPos[1]));
    }
    /****/
    public double[] getDirectionPos()
    {
        switch(this.aDirection)
        {
            case "West":
                return new double[]{this.aTile.getColumn(),this.aTile.getRow()+1};
            case "East":
                return new double[]{this.aTile.getColumn()+2,this.aTile.getRow()+1};
            case "North":
                return new double[]{this.aTile.getColumn()+1,this.aTile.getRow()};
            case "South":
                return new double[]{this.aTile.getColumn()+1,this.aTile.getRow()+2};
            default:
                return null;
        }
    }
    /****/
    private boolean containsBox(final double pColumn,final double pRow)
    {
        if(this.aCurrentCarte.containsBox(new Point((int)Math.floor(pColumn+1.2),(int)Math.floor(pRow+1.6))))
            return true;
        if(this.aCurrentCarte.containsBox(new Point((int)Math.floor(pColumn+1.8),(int)Math.floor(pRow+1.4))))
            return true;
        if(this.aCurrentCarte.containsBox(new Point((int)Math.floor(pColumn+1.2),(int)Math.floor(pRow+1.4))))
            return true;
        if(this.aCurrentCarte.containsBox(new Point((int)Math.floor(pColumn+1.8),(int)Math.floor(pRow+1.6))))
            return true;
        return false;
    }
    /****/
    public void setImages(final int pPlayerNbr)
    {
        this.aImages = new HashMap<String , HashMap< String , ArrayList<BufferedImage>>>();

        String vDirection = null;
        String vAction = null;


        try{
            URL vUrl = new File("Player"+pPlayerNbr+"/Images.txt").toURL();
            BufferedReader read = new BufferedReader(
                    new InputStreamReader(vUrl.openStream()));
            String vLine;
            while ((vLine = read.readLine()) != null)
            {
                switch(vLine.split("/")[0])
                {
                    case "Direction":
                        try
                        {
                            vDirection = vLine.split("/")[1];
                            this.aImages.put(vDirection , new HashMap<String ,ArrayList<BufferedImage>>());
                        }
                        catch(Exception e)
                        {e.printStackTrace();}
                        break;
                    case "Action":
                        try
                        {
                            vAction = vLine.split("/")[1];
                            this.aImages.get(vDirection).put(vAction,new ArrayList<BufferedImage>());
                        }
                        catch(Exception e)
                        {e.printStackTrace();}
                        break;
                    default:
                        try
                        {
                            URL vURL = new File("Player"+pPlayerNbr+"/"+vLine).toURL();
                            BufferedImage vImage = ImageIO.read(vURL);
                            this.aImages.get(vDirection).get(vAction).add(vImage);
                        }
                        catch(Exception e)
                        {e.printStackTrace();}
                        break;
                }
            }

            read.close();

        }
        catch(Exception e){}
    }
    /****/
    public void paintPV(final BufferedImage pBackground)
    {
        JComponent vPVComponent = new PVCanvas(this.aPV);
        vPVComponent.paint(pBackground.createGraphics());
    }
    /****/
    public void sendSound(final String pSound)
    {
        try
        {
            URL vURL = this.getClass().getResource("Sounds/"+pSound+".wav");
            AudioInputStream vAIS = AudioSystem.getAudioInputStream(new File("Sounds/" + pSound + ".wav"));
            this.aToSend.add(vAIS);
        }
        catch (Exception e)
        {e.printStackTrace();}
    }
}