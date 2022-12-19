import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Socket;
import java.net.URL;
import java.util.ConcurrentModificationException;

/****/
public class GameEngine
{
    private String aStartPath = "Cartes/MainZonev08.txt";

    private boolean aEndPhase;
    private TimerCanvas aTimer;
    private TextCanvas aTextCanvas;
    private HashMap<Socket,Player> aPlayers;
    private HashMap<String,Carte> aCartes;
    /****/
    public GameEngine()
    {
        this.aEndPhase = false;
        this.aTimer = new TimerCanvas();
        this.aTextCanvas = new TextCanvas();
        this.aPlayers = new HashMap<Socket,Player>();
        this.aCartes = new HashMap<String,Carte>();
        this.setCarte(aStartPath);
    }
    /****/
    public HashMap<Socket,Player> getPlayers(){return this.aPlayers;}
    /****/
    public void copyPlayers(final  HashMap<Socket,Player> pPlayers)
    {
        for(HashMap.Entry <Socket , Player> vEntry:pPlayers.entrySet())
        {
            Player vPlayer = vEntry.getValue();
            Socket vSocket = vEntry.getKey();
            this.addPlayer(vSocket , vPlayer.getReceived() , vPlayer.getToSend());
        }
    }
    /****/
    public void updateGame()
    {
        boolean vEndPhase = false;
        for(Player vPlayer: this.aPlayers.values())
        {
            if(vPlayer.getAction().equals("Die"))
                vPlayer.respawn(this.aCartes.get(aStartPath),16,23);
            vPlayer.updateEffects();
            vPlayer.setDefaultImage();
            for(String vText : (ArrayList<String>)vPlayer.getReceived().clone())
            {
                if(vText == null)
                    return;
                if(vText.contains("(KP)"))
                    vPlayer.doAction(Integer.parseInt(vText.split(" ")[1]));
                else if(vText.length() > 0 && vText.charAt(0) == ('/'))
                    this.actionCommand(vPlayer , vText);
                else
                    for(Player vPlayer2 : this.aPlayers.values())
                        vPlayer.getToSend().add(vText);
            }
            vPlayer.updateProjectile();
            this.verifDoors(vPlayer);
            if(vPlayer.verifEndCondition())
                vEndPhase = true;
            vPlayer.getReceived().clear();
        }
        if(vEndPhase && !this.aEndPhase)
            this.startEndingPhase();
        else if(!vEndPhase && this.aEndPhase)
            this.stopEndingPhase();

    }
    /****/
    public void verifDoors(final Player pPlayer)
    {
        if(pPlayer.getCarte().getDoors().containsKey(pPlayer.getPlayerPoint()))
        {
            Door vDoor = pPlayer.getCarte().getDoors().get(pPlayer.getPlayerPoint());
            this.setCarte(vDoor.getExitPath());

            Carte vCurrentCarte = pPlayer.getCarte();
            Carte vNewCarte = this.aCartes.get(vDoor.getExitPath());

            vCurrentCarte.removePlayer(pPlayer);
            vNewCarte.addPlayer(pPlayer);

            pPlayer.setCarte(vNewCarte);
            pPlayer.getTile().setColumn(vDoor.getExitColumn()-1);
            pPlayer.getTile().setRow(vDoor.getExitRow());
        }
    }
    /****/
    public void actionCommand(final Player pPlayer,final String vCommand)
    {
        try{
            switch(vCommand.split(" ")[0])
            {
                case "/Player": pPlayer.setImages(Integer.parseInt(vCommand.split(" ")[1]));break;
                case "/Kill": pPlayer.die();break;
                default:pPlayer.getToSend().add("Cette Commande n'existe pas");break;
            }
        }
        catch(Exception e)
        {e.printStackTrace();}
    }
    public void startEndingPhase()
    {
        this.aEndPhase = true;
        this.aTimer.setStartTime();
        for(Player vPlayer:this.aPlayers.values())
            vPlayer.sendSound("The_end_in_near");
    }
    /****/
    public void stopEndingPhase()
    {
        this.aEndPhase = false;
        for(Player vPlayer:this.aPlayers.values())
            vPlayer.sendSound("PlainesV3");;
    }
    /****/
    public void updateImage()
    {
        try
        {
            for(Player vPlayer : this.aPlayers.values())
            {
                double vColumnShift = vPlayer.getTile().getColumn()+1.5;
                double vRowShift = vPlayer.getTile().getRow();
                Carte vCarte = vPlayer.getCarte();
                BufferedImage vImage = vCarte.getImage(vColumnShift,vRowShift);
                vPlayer.paintPV(vImage);
                vPlayer.getInventory().paintInventory(vImage);
                if(this.aEndPhase)
                    this.aTimer.paint(vImage.createGraphics());
                if(this.aTimer.isTimesUp())
                {
                    if(vPlayer.verifEndCondition())
                        this.aTextCanvas.paintText("Victoire",vImage);
                    else
                        this.aTextCanvas.paintText("Défaite",vImage);
                }
                if(vImage !=null)
                    vPlayer.getToSend().add(vImage);
            }
        }
        catch(ConcurrentModificationException e)
        {e.printStackTrace();}
    }
    /****/
    public void setCarte(final String pCartePath)
    {
        if(!this.aCartes.containsKey(pCartePath))
        {
            Carte vCarte = new Carte(pCartePath);
            this.aCartes.put(pCartePath,vCarte);
        }
    }
    /****/
    public void addPlayer(final Socket pClient, final ArrayList<String> pReceived, final ArrayList<Object> pToSend)
    {
        Carte vCarte = this.aCartes.get(aStartPath);
        System.out.println(vCarte == null);
        Player vPlayer = new Player(vCarte, pReceived, pToSend);
        vCarte.addPlayer(vPlayer);
        this.aPlayers.put(pClient,vPlayer);
        vPlayer.sendSound("PlainesV3");
    }
    /****/
    public void removePlayer(final Socket pClient)
    {
        if(this.aPlayers.containsKey(pClient))
        {
            Player vPlayer = this.aPlayers.get(pClient);
            vPlayer.die();
            this.aPlayers.remove(pClient);
            Carte vCarte = vPlayer.getCarte();
            vCarte.removePlayer(vPlayer);
            this.removePlayer(pClient);
        }
    }
}