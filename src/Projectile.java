import java.awt.Point;
/**
 * Décrivez votre classe Arrow ici.
 *
 * @author (votre nom)
 * @version (un numéro de version ou une date)
 */
public class Projectile
{
    // variables d'instance - remplacez l'exemple qui suit par le vôtre
    private Tile aTile;
    private Carte aCarte;

    private String aDirection;
    private double aSpeed;
    private int aPower;

    private Player aPlayer;

    /**
     * Constructeur d'objets de classe Arrow
     */
    public Projectile(final Player pPlayer,final String pDirection, final Carte pCarte)
    {
        // initialisation des variables d'instance
        aDirection = pDirection;
        aSpeed = 0.7;
        aPower = 3;
        this.aPlayer = pPlayer;


        aCarte = pCarte;
        double vPos[] = aPlayer.getPlayerPos();
        aTile = new Tile("Images/Onegameforall/Objets(L2)/Fleche(Item).png",vPos[0], vPos[1]-0.4);
        this.aCarte.getTiles().add(this.aTile);
        int nbrRotation = 0;
        switch (aDirection)
        {
            case "East":
                nbrRotation = 0;
                break;
            case "South":
                nbrRotation= 1;
                break;
            case "West":
                nbrRotation = 2;
                break;
            case "North":
                nbrRotation = 3;
                break;
        }
        for(int i = 0; i<nbrRotation; i++)
            this.aTile.rotate();
    }


    public boolean routine()
    {
        switch (aDirection)
        {
            case "North":
                this.aTile.setRow(this.aTile.getRow() - this.aSpeed);
                break;
            case "South":
                this.aTile.setRow(this.aTile.getRow() + this.aSpeed);
                break;
            case "East":
                this.aTile.setColumn(this.aTile.getColumn() + this.aSpeed);
                break;
            case "West":
                this.aTile.setColumn(this.aTile.getColumn() - this.aSpeed);
                break;
        }
        if(this.aCarte.containsBox(new Point((int)Math.round(this.aTile.getColumn()), (int)(this.aTile.getRow()+0.5))))
        {return true;}
        return aCarte.hit(aPlayer, aDirection, new double[]{this.aTile.getColumn(), aTile.getRow()}, aPower);

    }

    public void deleteTile()
    {
        this.aCarte.getTiles().remove(aTile);
    }
}