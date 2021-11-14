import javafx.scene.image.Image;
package enum CollisionType {
    RAT, ITEM, TILE, NONE
}
public abstract class Entity {
    protected Image image;
    protected CollisionType collisionGroup; //what this entity is
    protected CollisionType[] collisionTargets; //what this entity will collide with. Eg, explosionPart will collide with items and rats
    //but gas will only collide with rats, and rats will only collide with other rats (then we check sex of rat after)
    //protected pos;?


    public Entity(Image image, CollisionType collisionGroup, CollisionType[] collisionTargets) {
        this.image = image;
        this.collisionGroup = collisionGroup;
        this.collisionTargets = collisionTargets;
    }

    public CollisionType getCollisionGroup() {
        return collisionGroup;
    }

    public CollisionType[] getCollisionTargets() {
        return collisionTargets;
    }

    abstract onCollision(Entity entity)

    public draw() {
        //draw the entity
        //or don't idm
    }


}
