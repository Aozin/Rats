package gameHandler;

import java.util.HashMap;

public class Inventory {

    private static int bombCount;
    private static int gasCount;
    private static int sterileCount;
    private static int noEntryCount;
    private static int maleCount;
    private static int femaleCount;
    private static int deathCount;
    private static int poisonCount;

    private int bombRestockRate;
    private int gasRestockRate;
    private int sterileRestockRate;
    private int noEntRestockRate;
    private int maleRestockRate;
    private int femaleRestockRate;
    private int deathRestockRate;
    private int poisonRestockRate;

    public Inventory(HashMap<String, Integer> stats, int[] inventory) {
        this.bombRestockRate = stats.get("bombFreq");
        this.gasRestockRate = stats.get("gasFreq");
        this.sterileRestockRate = stats.get("steriliseFreq");
        this.noEntRestockRate = stats.get("noEntryFreq");
        this.maleRestockRate = stats.get("mSexChangeFreq");
        this.femaleRestockRate = stats.get("fSexChangeFreq");
        this.deathRestockRate = stats.get("deathRatFreq");
        this.poisonRestockRate = stats.get("poisonFreq");
        bombCount = inventory[0];
        gasCount = inventory[1];
        sterileCount = inventory[2];
        noEntryCount = inventory[3];
        maleCount = inventory[4];
        femaleCount = inventory[5];
        deathCount = inventory[6];
        poisonCount = inventory[7];
    }

    public static int getPoisonCount() {
        return poisonCount;
    }

    public static void setPoisonCount(int poisonCount) {
        Inventory.poisonCount = poisonCount;
    }

    // TODO:
    public static int getBombCount() {
        return bombCount;
    }

    public static int getGasCount() {
        return gasCount;
    }

    public static int getSterileCount() {
        return sterileCount;
    }

    public static int getNoEntryCount() {
        return noEntryCount;
    }

    public static int getMaleCount() {
        return maleCount;
    }

    public static int getFemaleCount() {
        return femaleCount;
    }

    public static int getDeathCount() {
        return deathCount;
    }

    public static void setBombCount(int bombCount) {
        Inventory.bombCount = bombCount;
    }

    public static void setGasCount(int gasCount) {
        Inventory.gasCount = gasCount;
    }

    public static void setSterileCount(int sterileCount) {
        Inventory.sterileCount = sterileCount;
    }

    public static void setNoEntryCount(int noEntryCount) {
        Inventory.noEntryCount = noEntryCount;
    }

    public static void setMaleCount(int maleCount) {
        Inventory.maleCount = maleCount;
    }

    public static void setFemaleCount(int femaleCount) {
        Inventory.femaleCount = femaleCount;
    }

    public static void setDeathCount(int deathCount) {
        Inventory.deathCount = deathCount;
    }

    public void restock() {
        if (Game.getTimeLeft() % bombRestockRate == 0) {
            bombCount += 1;
        }

        if (Game.getTimeLeft() % gasRestockRate == 0) {
            gasCount += 1;
        }

        if (Game.getTimeLeft() % sterileRestockRate == 0) {
            sterileCount += 1;
        }

        if (Game.getTimeLeft() % noEntRestockRate == 0) {
            noEntryCount += 1;
        }

        if (Game.getTimeLeft() % maleRestockRate == 0) {
            maleCount += 1;
        }

        if (Game.getTimeLeft() % femaleRestockRate == 0) {
            femaleCount += 1;
        }
        if (Game.getTimeLeft() % deathRestockRate == 0) {
            deathCount += 1;
        }

        if (Game.getTimeLeft() % poisonRestockRate == 0) {
            poisonCount += 1;
        }
    }

    /*
    public String toString() {
        String result = "Remaining Items:\n";
        //gets information from the "ItemsLeft" Attribute
        for (int i = 0; i< itemsLeft.length + 1; i++) {
        	result += itemNames[i] + " "; 
        	result += itemsLeft[i] + "\n";
        }  
        return result;
    }

    
    public void placeItem(int itemArrayPos, int[] itemBoardPos) {
        //where itemPos is the id of the item's type, and also it's position in the array
    	if (itemsLeft[itemArrayPos] > 0) {
    		itemsLeft[itemArrayPos]--;
    		//send request to place item - probably needs access to board/game to set items or smth
    		//like a Board.SetItem(itemBoadPos, itemName[itemArrayPos]) type thing
    	} else {
    		System.out.println("Ran out of uses of " + itemNames[itemArrayPos]);
    		//not sure what javafx uses to print text but use that instead
    	}   	
    }
    */
}
	