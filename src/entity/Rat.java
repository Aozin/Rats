package entity;

import gameHandler.Game;
import gameHandler.Game.*;
import gameHandler.Pos;
import javafx.scene.image.Image;
import tiles.TileTypes;

import java.util.ArrayList;

/**
 * <p> 1. File-name: Rat.java</p>
 * <p> 2. Creation Date: (N/A) </p>
 * <p> 3. Last modification date:</p>
 * <p> 4. Purpose of the program: Rat Implementation</p>
 *
 * @author Andrew
 */

/**
 * Types of directions rats can move in.
 * North, Eat, South, West
 */

public abstract class Rat extends Entity {
    private static final int BOOST_DURATION = 10;
    protected boolean canMate;
    protected boolean canMove;
    protected RatTypes ratType;
    protected int moveSpeed;
    protected Direction moveDirection;

    /**
     * Method to get mate status
     *
     * @return able to mate
     */

    public boolean getMateStatus() {
        return canMate;
    }

    /**
     * Method to set current mate status
     *
     * @param canMate: mate status
     */

    public void setMateStatus(boolean canMate) {
        this.canMate = canMate;
    }

    /**
     * Method to get movement status
     *
     * @return able to move
     */

    public boolean getMoveStatus() {
        return canMove;
    }

    /**
     * Method to set current move status
     *
     * @param canMove: move status
     */

    public void setMoveStatus(boolean canMove) {
        this.canMove = canMove;
    }

    /**
     * Method to get rat type
     *
     * @return rat type
     */

    public RatTypes getRatType() {
        return ratType;
    }

    /**
     * Method to set current rat type
     *
     * @param type: rat type
     */

    public void setRatType(RatTypes type) {
        this.ratType = type;
    }

    /**
     * Method to get movement speed
     *
     * @return movement speed
     */

    public int getMoveSpeed() {
        return moveSpeed;
    }

    /**
     * Method to set current movement speed
     *
     * @param moveSpeed: movment speed number
     */

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * Method to get movement direction
     *
     * @return direction
     */

    public Direction getMoveDirection() {
        return moveDirection;
    }

    public int boostRemaining = 0;
    public boolean moveBoosted = false;

    /**
     * Method to get amount of boost
     *
     * @return boost amount
     */

    public int getBoostRemaining() {
        return boostRemaining;
    }

    /**
     * Method to set the amount of boost remaining
     *
     * @param boostRemaining: amount of boost
     */

    public void setBoostRemaining(int boostRemaining) {
        this.boostRemaining = boostRemaining;
    }

    /**
     * Method to check if it is boosted
     *
     * @return boosted status
     */

    public boolean isMoveBoosted() {
        return moveBoosted;
    }

    /**
     * Method to set movement boost
     *
     * @param moveBoosted: movement boost
     */

    public void setMoveBoosted(boolean moveBoosted) {
        this.moveBoosted = moveBoosted;
    }

    /**
     * gets inverse move direction
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     *
     * @return inverse move direction
     */
    public Direction getInverseMoveDirection() {
        return Direction.values()[(moveDirection.ordinal() + 2) % 4];
    }

    /**
     * move function for the rat
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     * @return a boolean of whether the rat moved
     */
    public boolean move() {
        if (Game.getTiles().get(this.pos).getType() == TileTypes.SPEEDTILE) {
            this.setMoveBoosted(true);
            this.boostRemaining = BOOST_DURATION;
        }

        //   if moveDirection = null, pick a direction from directions enum at random
        if (moveDirection.equals(null)) {
            moveDirection = Direction.values()[((int) (Math.random() * 4))];
        }
        int actingMoveSpeed = this.moveSpeed;
        if (this.moveBoosted) {
            actingMoveSpeed = actingMoveSpeed * 2;
            boostRemaining--;
        }
        if (this.boostRemaining <= 0) {
            this.moveBoosted = false;
        }
        if (Game.getTimeLeft() % (int) (10 / actingMoveSpeed) == 0) {
            //      ANDREW'S MOVEMENT CODE (partially stolen from gus)
            // check in front, left and right
            Direction leftDir = Direction.values()[((moveDirection.ordinal() + 3) % 4)];
            Direction rightDir = Direction.values()[((moveDirection.ordinal() + 1) % 4)];
            ArrayList<Direction> availablePaths = new ArrayList<Direction>();
            //add directions to a list if they are passable
            if (TileManager.getPassableTile(getPosFromDir(moveDirection, this.pos))) {
                availablePaths.add(moveDirection);
            }

            if (TileManager.getPassableTile(getPosFromDir(leftDir, this.pos))) {
                availablePaths.add(leftDir);
            }
            if (TileManager.getPassableTile(getPosFromDir(rightDir, this.pos))) {
                availablePaths.add(rightDir);
            }
            Direction chosenDirection = this.moveDirection;
            if (availablePaths.size() == 0) {
                //if no paths ahead, turn around
                if (TileManager.getPassableTile(getPosFromDir(getInverseMoveDirection(), this.pos))) {
                    chosenDirection = getInverseMoveDirection();
                } else {
                    System.out.println("A rat got stuck and can't move");
                }
            } else if (availablePaths.size() == 1) {
                //if only one path is available, take it
                chosenDirection = availablePaths.get(0);
            } else {
                //randomly chose a direction from the options available
                chosenDirection = availablePaths.get((int) (Math.random() * (availablePaths.size())));
            }
            moveDirection = chosenDirection;
            setPosition(getPosFromDir(moveDirection, pos));
            //    move one square
            /**   rats with a higher movespeed (baby rats or rats under the influence of speedtiles will loop and
             *    go through this process again so they do not end up inside walls) */

        }
        checkCurrentTile();
        return true;
    }

    /**
     * Gets the new position as a result of the current positon and direction
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     *
     * @param dir takes the direction to be looking towards
     * @param pos takes the current coordinates
     * @return returns the new coordinates
     */
    public Pos getPosFromDir(Direction dir, Pos pos) {
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

    /**
     * Used by other entities to move rats in a different way.
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     *
     * @param movesLeft takes how many moves are needed for moving
     * @param direction takes direction to move in
     * @return boolean if true for force move
     */
    public boolean forceMove(int movesLeft, Direction direction) {
        //forces the entity to move when it is not the rat's turn to move
        //used by noEntrySign
        moveDirection = direction;
        while (movesLeft > 0) {
            Pos northTile = new Pos(this.pos.x, this.pos.y - 1);
            Pos eastTile = new Pos(pos.x + 1, pos.y);
            Pos southTile = new Pos(pos.x, pos.y + 1);
            Pos westTile = new Pos(pos.x - 1, pos.y);
            Pos[] directionTiles = {northTile, eastTile, southTile, westTile};
            setPosition(directionTiles[moveDirection.ordinal()]);
            movesLeft--;
        }
        return true; //Idk i just want to compile
    }

    /**
     * abtstract method not to be used, no implementation needed
     */
    public abstract void checkCurrentTile();

    /**
     * 1st Constructor, creates an instance of a rat
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     *
     * @param type takes in the type of rat
     * @param pos  takes in the position of the rat
     */
    public Rat(RatTypes type, Pos pos) {
        super(new Image("File:resources/male.png"), EntityType.RAT, pos);
        setRatType(type);
        setMoveStatus(true);
        moveDirection = Direction.NORTH;
        switch (type) {
            case BABY:
                setMateStatus(false);
                setMoveSpeed(4);
                this.image = new Image("File:resources/babyRat.png");
                break;
            case DEATH:
                setMateStatus(false);
                setMoveSpeed(3);
                this.image = new Image("File:resources/deathRat.png");
                break;
            case MALE:
                setMateStatus(true);
                setMoveSpeed(2);
                this.image = new Image("File:resources/maleRat.png");
                break;
            case FEMALE:
                setMateStatus(true);
                setMoveSpeed(2);
                this.image = new Image("File:resources/femaleRat.png");
                break;
            default:
                setMateStatus(true);
                setMoveSpeed(2);
                this.image = new Image("File:resources/male.png");
                break;
        }
    }

}

