/****/
public class Door
{
    private String aExitCarte;
    private int aExitColumn;
    private int aExitRow;
    /****/
    public Door(final String pExitCarte, final int pExitColumn, final int pExitRow)
    {
        this.aExitCarte = pExitCarte;
        this.aExitColumn = pExitColumn;
        this.aExitRow = pExitRow;
    }
    /****/
    public int getExitRow(){return this.aExitRow;}
    /****/
    public int getExitColumn(){return this.aExitColumn;}
    /****/
    public String getExitPath(){return this.aExitCarte;}
}