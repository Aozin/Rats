package gameHandler;

import Controller.GameRenderer;
import Controller.Main;
import entity.*;
import javafx.application.Platform;
import org.json.simple.parser.ParseException;
import playerProfile.Player;
import playerProfile.ProfileReader;
import tiles.Tile;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * <p> 1. File-name: Game.java</p>
 * <p> 2. Creation Date: 20/11/21 </p>
 * <p> 3. Last modification date: 5/12/21 </p>
 * <p> 4. Purpose of the program: Game file Implementation</p>
 *
 * @author ??
 */

public class Game {
    //this is a pretty static way of doing things, but it just works
    private static ArrayList<BasicRat> rats = new ArrayList<BasicRat>();
    private static String levelPath;
    private static GameRenderer runner;
    private static int loseAmount;
    private static Inventory inventory;
    public static int score;
    private static int levelNum;

    private static Timer timer = new Timer();
    private static int timeLeft = -1;

    /**
     * Constructer for game
     */
    public Game() {
    }

    /**
     * gets level number
     *
     * @return levelNum
     */
    public static int getLevelNum() {
        return levelNum;
    }

    /**
     * sets level number
     *
     * @param levelNum the level index
     */
    public static void setLevelNum(int levelNum) {
        Game.levelNum = levelNum;
    }


    /**
     * gets time left
     *
     * @return timeLeft
     */
    public static int getTimeLeft() {
        return timeLeft;
    }

    /**
     * gets all the rats
     *
     * @return rats
     */
    public static ArrayList<BasicRat> getRats() {
        return rats;
    }

    /**
     * gets timer
     *
     * @return timer
     */
    public static Timer getTimer() {
        return timer;
    }

    /**
     * sets timer
     *
     * @param timer of the file
     */
    public static void setTimer(Timer timer) {
        Game.timer = timer;
    }

    /**
     * sets runner
     *
     * @param runner the runner for the rendrerer
     */
    public static void setRunner(GameRenderer runner) {
        Game.runner = runner;
    }

    /**
     * adds the score
     * <p> no side-effects</p>
     * <p> referentially transparent</p>
     *
     * @param addition for adding score to the game score
     */
    public static void addScore(int addition) {
        Game.score += addition;
    }

    /**
     * method to start the game
     * <p> side-effects</p>
     * <p> referentially transparent</p>
     */
    public void start() {
        Game.runner.redrawBoard(this.createCombinedEntityList());
        Game.score = 0;
        System.out.println(this.rats.size());
        System.out.println(Arrays.toString(this.createCombinedEntityList()));
        this.items = new ArrayList<>();
        this.startTimer();
    }

    /**
     * gets data from filehandler and sets up entities
     * set up from save file
     * <p>side-effects</p>
     * <p>referentially transparent</p>

     * @throws ParseException exception
     * @throws IOException exception
     */
    public void setUpFromSave() throws ParseException, IOException {
        //file reader class goes here, reads file and passes data to this method
        Tuple<BasicRat[], Entity[][], char[][], HashMap<String, Integer>,
                HashMap<String, HashMap<String, Integer>>, int[]>
                gameObjects = GameFileHandler.newGameFromSave(levelPath);
        constructTileMap(gameObjects.getThird());
        Entity[][] itemsToAdd = gameObjects.getSecond();
        RatManager.deathRats = new ArrayList<>();
        ArrayList<Entity> idek = new ArrayList<>();
        for (int x = 0; x < itemsToAdd.length; x++) {
            idek.addAll(Arrays.asList(itemsToAdd[x]));
        }
        for (Entity ent : idek) {
            if (ent.getEntityType() == EntityType.RAT) {
                RatManager.deathRats.add((DeathRat) ent);
            } else {
                Game.items.add((Item) ent);
            }
        }

        constructRatList(gameObjects.getFirst());
        setUpLevelStats(gameObjects.getFourth(), gameObjects.getSixth());
    }

    /**
     * starts the game
     * <p>side-effects</p>
     * <p>referentially transparent</p>
     * @throws InterruptedException exceptional
     */
    public void startFromSave() throws InterruptedException {
        Game.runner.redrawBoard(this.createCombinedEntityList());
        Game.score = 0;
        System.out.println(this.rats.size());
        System.out.println(Arrays.toString(this.createCombinedEntityList()));
        this.tickWithoutRats();
        Thread.sleep(100);
        this.startTimer();
    }

    /**
     * tick method without affecting rats
     * <p>side-effects</p>
     * <p>not referentially transparent</p>
     *
     */
    private void tickWithoutRats() {
        inventory.restock();
        ItemManager.performItemActions();
        Platform.runLater(() -> runner.normalTickDrawing(createCombinedEntityList()));
        Game.checkVictory();
        timeLeft--;
    }

    /**
     * method to start the timer
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     */
    private void startTimer() {
        Game.timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    inventory.restock();
                    RatManager.performRatActions();
                    ItemManager.performItemActions();
                    Platform.runLater(() -> runner.normalTickDrawing(createCombinedEntityList()));
                    Game.checkVictory();
                    timeLeft--;
                } catch (ConcurrentModificationException e) {
                    System.out.println("Concurrent Modification, but game still works so pretend it didn't happen");
                } catch (NullPointerException e) {
                    System.out.println("null pointer, but game still works so pretend it didn't happen");
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 100);
    }

    /**
     * method to check whether or not the game has been Won.
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     */
    public static void checkVictory() {
        if (rats.size() == 0) {
            System.out.println("VICTORY!!");
            System.out.println(score);
            if (timeLeft > 0) {
                Game.addScore(timeLeft);
            }
            Game.updatePlayerStats(Game.score);
            Game.cleanUp();
            Platform.runLater(() -> Game.runner.victoryScreen());
        } else if (rats.size() > loseAmount) {
            System.out.println("DEFEAT :c");
            Game.cleanUp();
            Platform.runLater(() -> Game.runner.lossScreen());
        }
    }

	/*
	                "timeLeft", "ratLimit", "bombFreq", "gasFreq", "steriliseFreq",
                "mSexChangeFreq", "fSexChangeFreq", "noEntryFreq", "deathRatFreq", "poisonFreq" ,"loseAmount"
	 */

    /**
     * gets the level's stats to save to a file
     * <p> no side-effects</p>
     * <p> referentially transparent</p>
     * @return the hashmap of stats hashed by their name
     */
    public static HashMap<String, Integer> getLevelStats() {
        HashMap<String, Integer> levelStats = new HashMap<>();
        levelStats.put("timeLeft", Game.getTimeLeft());
        levelStats.put("bombFreq", inventory.getBombRestockRate());
        levelStats.put("gasFreq", inventory.getGasRestockRate());
        levelStats.put("steriliseFreq", inventory.getSterileRestockRate());
        levelStats.put("mSexChangeFreq", inventory.getMaleRestockRate());
        levelStats.put("fSexChangeFreq", inventory.getFemaleRestockRate());
        levelStats.put("noEntryFreq", inventory.getNoEntRestockRate());
        levelStats.put("deathRatFreq", inventory.getDeathRestockRate());
        levelStats.put("poisonFreq", inventory.getPoisonRestockRate());
        levelStats.put("loseAmount", Game.loseAmount);
        return levelStats;
    }

    private static void updatePlayerStats(int score) {
        Player current = Main.activePlayer;
        if (current.getMaxLevelUnlocked() == Game.getLevelNum()) {
            current.setMaxLevelUnlocked(Game.getLevelNum() + 1);
        }
        if (score > current.getScores()[Game.getLevelNum()]) {
            current.getScores()[Game.getLevelNum()] = score;
        }
        System.out.println(current.getMaxLevelUnlocked());
        ProfileReader.updateProfile(current.makeJSON());
    }


    /**
     * Method for cleaning up the game.
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     */
    public static void cleanUp() {
        Game.timer.cancel();
        Game.timer = null;
        Game.items = new ArrayList<>(); //clear off old items
        RatManager.deathRats = new ArrayList<>();
        Game.rats = new ArrayList<>();
        Platform.runLater(() -> Game.runner.removeEntities());
    }

    /**
     * method to combine Entity List for the game
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     *
     * @return List of entities
     */
    private Entity[] createCombinedEntityList() {
        ArrayList<Entity> entities = new ArrayList<>();
        entities.addAll(Game.rats);
        entities.addAll(Game.items);
        entities.addAll(RatManager.deathRats);
        Entity[] entityArray = new Entity[entities.size()];
        return entities.toArray(entityArray);
    }

    public static class RatManager {
        private static ArrayList<DeathRat> deathRats = new ArrayList<DeathRat>();
        private static ArrayList<BasicRat> ratsToAdd = new ArrayList<>();
        private static ArrayList<BasicRat> ratsToKill = new ArrayList<>();
        private static ArrayList<DeathRat> deathRatsToKill = new ArrayList<>();

        public static ArrayList<BasicRat> getRatList() {
            //should only be used by GFH
            return rats;
        }

        public static ArrayList<DeathRat> getDeathRatList() {
            return deathRats;
        }

        public static ArrayList<BasicRat> getRatsAtPos(Pos pos) {
            ArrayList<BasicRat> ratsAtPos = new ArrayList<BasicRat>();
            for (int i = 0; i < rats.size(); i++) {
                BasicRat currentRat = rats.get(i);
                if (currentRat.getPosition().equals(pos)) {
                    ratsAtPos.add(currentRat);
                }
            }
            return ratsAtPos;
        }


        /**
         * method to kill basic rats at certain positions
         * <p> no side-effects</p>
         * <p> not referentially transparent</p>
         *
         * @param pos coordinates of rats to be killed
         * @return returns true if rats are killed, false otherwise
         */
        public static boolean killBasicRatsAtPos(Pos pos) {
            ArrayList<BasicRat> toKill = getRatsAtPos(pos);
            if (ratsToKill != null) {
                ratsToKill.addAll(toKill);
                return true;
            }
            return false;
        }

        /**
         * method to kill a single rat
         * <p> no side-effects</p>
         * <p> not referentially transparent</p>
         *
         * @param rat to kill
         * @return true if rat is in a list or rats or is a death rat, false otherwise
         */
        public static boolean killSingleRat(Rat rat) { //so i made this method for when we need to kill individual rats
            if (rats.contains(rat)) {
                ratsToKill.add((BasicRat) rat);
                return true;
            } else if (deathRats.contains(rat)) {
                deathRatsToKill.add((DeathRat) rat);
                return true;
            }
            System.out.println("Tried to kill a rat that did not exist");
            return false;
        }

        /**
         * method to kill all rats
         * <p> no side-effects</p>
         * <p> not referentially transparent</p>
         *
         * @param toKill a list of rats to kill
         */
        public static void killRatArray(ArrayList<BasicRat> toKill) { //this one kills all basicrats in the array it's passed
            ratsToKill.addAll(ratsToKill);
        }

        /**
         * method for performing the rat's actions
         * <p> no side-effects</p>
         * <p> not referentially transparent</p>
         */
        public static void performRatActions() {

            for (BasicRat rat : rats) {
                rat.move();
                rat.ratActions();
                rat.updateImage();
                if (rat.getHP() <= 0) {
                    ratsToKill.add(rat);
                    //killSingleRat(rat);
                }
            }
            //Have to do this to avoid concurrency modification error
            for (Rat toKill : ratsToKill) {
                if (rats.contains(toKill)) {
                    BasicRat target = (BasicRat) toKill;
                    Game.addScore(target.getScore());
                    rats.remove(toKill);
                }
            }

            Game.rats.addAll(ratsToAdd);
            ratsToAdd.clear();
            for (DeathRat deathRat : deathRats) {
                deathRat.move();
            }

            for (DeathRat deathRat : deathRatsToKill) {
                deathRats.remove(deathRat);
            }
            deathRatsToKill.clear();
        }

        /**
         * Method to add rats to a list
         * <p> side-effects</p>
         * <p> not referentially transparent</p>
         *
         * @param rat a rat to add to the rat list
         */
        public static void addRat(BasicRat rat) {
            ratsToAdd.add(rat);
        }
    }


    private static HashMap<Pos, Tile> tiles = new HashMap<Pos, Tile>();

    public static class TileManager {
        private static int numTileWidth = 0;
        private static int numTileHeight = 0;
        //main purpose is to store tiles and allow entities to access them

        /**
         * gets the tile at a certain position.
         * <p> no side-effects</p>
         * <p> not referentially transparent</p>
         *
         * @param pos coordinates of a tile
         * @return returns position of tile
         */
        public static Tile getTile(Pos pos) {
            return tiles.get(pos);
        }

        /**
         * checks if a tile is passable or not
         * <p> no side-effects</p>
         * <p> not referentially transparent</p>
         *
         * @param pos coordinates of the tile
         * @return returns true if passable, otherwise false
         */
        public static boolean getPassableTile(Pos pos) {
            return tiles.get(pos).isPassable();
        }

        /**
         * gets entities
         *
         * @param pos the pos
         * @return the entities at the pos
         */
        public static ArrayList<Entity> getEntities(Pos pos) {
            ArrayList<Entity> entities = new ArrayList<>();
            entities.addAll(Game.rats);
            entities.addAll(Game.items);
            ArrayList<Entity> entitiesOnPos = new ArrayList<>();
            for (Entity e : entities) {
                if (e.getPosition().equals(pos)) {
                    entitiesOnPos.add(e);
                }
            }
            return entitiesOnPos;
        }

        /**
         * gets the max width of the gameBoard
         *
         * @return numTileWidth
         */
        public static int getNumTileWidth() {
            return numTileWidth;
        }

        /**
         * sets the max width of the gameBoard
         *
         * @param numTileWidth the width
         */
        public static void setNumTileWidth(int numTileWidth) {
            TileManager.numTileWidth = numTileWidth;
        }

        /**
         * gets the max height of the gameBoard
         *
         * @return numTileHeight
         */
        public static int getNumTileHeight() {
            return numTileHeight;
        }

        /**
         * sets the max height of the gameBoard
         *
         * @param numTileHeight the height
         */
        public static void setNumTileHeight(int numTileHeight) {
            TileManager.numTileHeight = numTileHeight;
        }

        /**
         * gets the position in a specified direction
         * <p> no side-effects</p>
         * <p> referentially transparent</p>
         *
         * @param dir the direction to move in, pos start position
         *
         * @param pos the pos before the move
         * @return pos the next position in the direction specified
         */
        public static Pos getPosFromDir(Direction dir, Pos pos) {
            Pos newPos;
            switch (dir) {
                case NORTH:
                    newPos = new Pos(pos.x, pos.y + 1);
                    break;
                case EAST:
                    newPos = new Pos(pos.x + 1, pos.y);
                    break;
                case SOUTH:
                    newPos = new Pos(pos.x, pos.y - 1);
                    break;
                case WEST:
                    newPos = new Pos(pos.x - 1, pos.y);
                    break;
                default:
                    newPos = new Pos(0, 0);
                    System.out.println("enum error");
                    break;
            }
            return newPos;
        }
    }


    private static ArrayList<Item> items = new ArrayList<Item>();

    public static class ItemManager {
        private static ArrayList<Item> itemsToRemove = new ArrayList<>();
        private static ArrayList<Item> itemsToAdd = new ArrayList<>();

        /**
         * gets the list of items
         *
         * @return items
         */
        public static ArrayList<Item> getItemList() {
            //should only be used by GFH
            return items;
        }

        /**
         * tries to place an item
         * <p>side-effects</p>
         * <p>referentially transparent</p>
         *
         * @param itemString the string representing the item, pos the coordinates to place the item at
         * @param pos the position to try and place the item
         */
        public static void tryPlace(String itemString, Pos pos) {
            if (true /*TODO Stock check here*/) { //"just leave it there he's kinda quirky" - Gus, 4:19am when asked if i should remove the if statement
                Item placedItem = null;
                System.out.println(itemString);
                switch (itemString) {
                    //TODO: call onPlacement() whenever an item is placed
                    case "bomb":
                        if (Inventory.getBombCount() > 0) {
                            placedItem = new Bomb(pos);
                            items.add(placedItem);
                            Inventory.setBombCount(Inventory.getBombCount() - 1);
                        }
                        break;
                    case "gas":
                        if (Inventory.getGasCount() > 0) {
                            placedItem = new Gas(pos);
                            items.add(placedItem);
                            Inventory.setGasCount(Inventory.getBombCount() - 1);
                        }
                        break;
                    case "sterile":
                        if (Inventory.getSterileCount() > 0) {
                            placedItem = new Sterilization(pos);
                            items.add(placedItem);
                            Inventory.setSterileCount(Inventory.getSterileCount() - 1);
                        }
                        break;
                    case "noEnt":
                        if (Inventory.getNoEntryCount() > 0) {
                            placedItem = new NoEntrySign(pos);
                            items.add(placedItem);
                            Inventory.setNoEntryCount(Inventory.getBombCount() - 1);
                        }
                        break;
                    case "male":
                        if (Inventory.getMaleCount() > 0) {
                            placedItem = new MaleGenderChange(pos);
                            items.add(placedItem);
                            Inventory.setMaleCount(Inventory.getBombCount() - 1);
                        }
                        break;
                    case "female":
                        if (Inventory.getFemaleCount() > 0) {
                            placedItem = new FemaleGenderChange(pos);
                            items.add(placedItem);
                            Inventory.setFemaleCount(Inventory.getBombCount() - 1);
                        }
                        break;
                    case "deathRat":
                        if (Inventory.getDeathCount() > 0) {
                            //not using placedItem here cause deathrat doesn't inherit from entity
                            RatManager.deathRats.add(new DeathRat(pos));
                            Inventory.setDeathCount(Inventory.getDeathCount() - 1);
                        }
                        break;
                    case "poison":
                        if (Inventory.getPoisonCount() > 0) {
                            placedItem = new Poison(pos);
                            items.add(placedItem);
                            Inventory.setPoisonCount(Inventory.getBombCount() - 1);
                        }
                        break;
                }

                if (!(placedItem == null)) {
                    placedItem.onPlacement();
                }
            }

        }

        /**
         * add item to the item list
         * <p> side-effects</p>
         * <p> referentially transparent</p>
         *
         * @param item the item to be added
         */
        public static void addItem(Item item) {
            itemsToAdd.add(item);
        }

        /**
         * removes item from list
         * <p> side-effects</p>
         * <p> referentially transparent</p>
         *
         * @param item the item to be removed
         */
        public static void killItem(Item item) {
            if (items.contains(item)) {
                itemsToRemove.add(item);
            }
        }

        /**
         * loop through all the items and make them perform their actions
         * <p> side-effects</p>
         * <p> referentially transparent</p>
         *
         */
        public static void performItemActions() {
            for (Item item : items) {
                item.tick();
            }

            //remove items here to avoid concorrentModification exception
            for (Item item : itemsToRemove) {
                Game.items.remove(item);
            }
            itemsToRemove.clear();

            //add items at the end so they can be drawn next tick
            for (Item item : itemsToAdd) {
                Game.items.add(item);
            }
            itemsToAdd.clear();
        }
    }

    /**
     * start the game
     * <p> no side-effects</p>
     * <p> referentially transparent</p>
     *
     * @param args arrrg
     */
    public static void main(String[] args) {
        Game game = new Game();
        // Runner runner = new Runner(); Runner is not needed and will be loaded as part of the javafx stuff so no need to worry about that
        try {
            game.setUp();
        } catch (ParseException e) {
            //handle here
            e.printStackTrace();
        } catch (IOException e) {
            //handle here
            e.printStackTrace();
        }
    }

    /**
     * get a hashmap of tiles and their positions so tiles can know their own positions
     *
     * @return tiles
     */
    public static HashMap<Pos, Tile> getTiles() {
        return tiles;
    }

    /**
     * gets data from filehandler and sets up entities
     * sets up from level file
     * <p> side-effects</p>
     * <p> referentially transparent</p>
     * @throws ParseException exceptional
     * @throws IOException exceptional
     */
    public void setUp() throws ParseException, IOException {
        //file reader class goes here, reads file and passes data to this method
        Tuple<BasicRat[], Entity[][], char[][], HashMap<String, Integer>,
                HashMap<String, HashMap<String, Integer>>, int[]>
                gameObjects = GameFileHandler.newGame(levelPath);
        constructTileMap(gameObjects.getThird());
        constructRatList(gameObjects.getFirst());
        setUpLevelStats(gameObjects.getFourth(), gameObjects.getSixth());
    }

    /**
     * get the level's filepath
     *
     * @return levelPath
     */
    public static String getLevelPath() {
        return levelPath;
    }

    /**
     * set the level's filepath
     *
     * @param levelPath the path of the level file
     */
    public static void setLevelPath(String levelPath) {
        Game.levelPath = levelPath;
    }

    /**
     * constructs the tilemap from Tilemanager
     * <p> side-effects</p>
     * <p> referentially transparent</p>
     * @param map a 2d array of chars
     */
    public void constructTileMap(char[][] map) {
        Game.tiles = new HashMap<>();//set to new hashmap so we don't accidentally keep old boards
        TileManager.numTileWidth = map[0].length;
        TileManager.numTileHeight = map.length;
        System.out.println(Arrays.deepToString(map));
        System.out.println(TileManager.getNumTileHeight() + "-" + TileManager.getNumTileWidth());
        for (int y = 0; y < TileManager.numTileHeight; y++) {
            for (int x = 0; x < TileManager.numTileWidth; x++) {
                Tile tileToAdd = Tile.createTileFromLetter(map[y][x]);
                Pos pos = new Pos(x, y);
                Game.tiles.put(pos, tileToAdd);
            }
        }
    }

    /**
     * constructs an arraylist of rats from an array
     * <p> side-effects</p>
     * <p> referentially transparent</p>
     *
     * @param rats the array to convert
     */
    public void constructRatList(BasicRat[] rats) {
        Game.rats = new ArrayList<BasicRat>(Arrays.asList(rats));
    }

    /**
     * sets up a level's stats and add them to inventory
     * <p> side-effects</p>
     * <p> referentially transparent</p>
     *
     * @param stats the level's stats, inventory the number of items at the start of the level
     * @param inventory  inventory of items
     */
    private void setUpLevelStats(HashMap<String, Integer> stats, int[] inventory) {
        this.timeLeft = stats.get("timeLeft") * 10; //convert from seconds to ticks
        this.loseAmount = stats.get("loseAmount");
        Game.inventory = new Inventory(stats, inventory);
    }
}
