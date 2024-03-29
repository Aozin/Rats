package entity;

import gameHandler.Game;
import gameHandler.Pos;
import javafx.scene.image.Image;

/**
 * <p> 1. File-name: MaleGenderChange.java</p>
 * <p> 2. Creation Date: 1/12/2021 </p>
 * <p> 3. Last modification date: 5/12/2021</p>
 * <p> 4. Purpose of the program: Male gender change implementation</p>
 *
 * @author Andrew
 */

public class MaleGenderChange extends Item {

    /**
     * 1st Constructor.
     * <p> side-effects</p>
     * <p> not referentially transparent</p>
     *
     * @param position takes coordinates of male gender change to be placed.
     */
    public MaleGenderChange(Pos position) {
        super(ItemType.MALE, new Image("file:resources/male.png"), position);

    }

    /**
     * Check if rat has touched change gender item
     * <p> no side-effects</p>
     * <p> not referentially transparent</p>
     *
     * @param target takes the spesific rat in to change its gender
     *              if all conditions are met.
     */

    public void ratCollision(Rat target) {
        if (target.getRatType() != RatTypes.DEATH && target.getRatType() != RatTypes.BABY) {
            BasicRat targ = (BasicRat) target;
            targ.setGender(RatTypes.MALE);
        }
        Game.ItemManager.killItem(this);
    }

    /**
     * Check if change gender item has touched a rat
     *
     * @param target takes the change gender item in to collide with rat
     */

    public void itemCollision(Item target) {
    }

    public void onPlacement() {
    }

    public void tick() {

    }
}