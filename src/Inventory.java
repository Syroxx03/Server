import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.util.ArrayList;
/****/
public class Inventory
{
    private int aMainItemIndex;
    private Item[] aItems;
    /****/
    public Inventory()
    {
        this.aItems = new Item[6];
        this.aMainItemIndex = 0;
    }
    /****/
    public Item[] getItems(){return this.aItems;}
    /****/
    public int getMainItemIndex(){return this.aMainItemIndex;}
    /****/
    public void paintInventory(final BufferedImage pBackground)
    {
        JComponent vInventoryComponent = new InventoryCanvas(this.aItems , this.aMainItemIndex);
        vInventoryComponent.paint(pBackground.createGraphics());
    }
    /**Return true si l'item à pût être ajouté**/
    public void addItem(Item pItem)
    {
        for(int vIndex = 0; vIndex <this.aItems.length; vIndex++)
            if(this.aItems[vIndex] == null)
            {
                this.aItems[vIndex] = pItem;
                return;
            }
    }
    /****/
    public boolean verifEndCondition()
    {
        int vJoyauxNbr = 0;
        for(int vIndex = 0; vIndex <this.aItems.length; vIndex++)
            if(this.aItems[vIndex]!=null && this.aItems[vIndex].getTile().getImagePath().contains("Joyaux"))
                vJoyauxNbr++;
        if(vJoyauxNbr == 3)
            return true;
        return false;
    }
    /****/
    public boolean isFull()
    {
        for(int vIndex = 0; vIndex <this.aItems.length; vIndex++)
            if(this.aItems[vIndex] == null)
                return false;
        return true;
    }
    /****/
    public void changeMainItem()
    {
        this.aMainItemIndex++;
        if(this.aMainItemIndex >= this.aItems.length)
            this.aMainItemIndex = 0;
    }
}