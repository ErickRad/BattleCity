package entities;

import world.Map;
import entities.Tank;
import java.util.List;

public class Bullet extends Entity {

    private int dx, dy;
    private Map map;
    private Tank owner;
    private List<Entity> entities;

    public Bullet(int x, int y, int dx, int dy, Map map, List<Entity> entities, Tank owner){
        super(x, y);
        this.dx = dx;
        this.dy = dy;
        this.map = map;
        this.entities = entities;
        this.owner = owner;
        map.add(this);
    }

    private boolean checkCollision(){
        for (Entity block : List.copyOf(map.get(x, y))){
            if (block == this) continue;

            if (block.blocksBullet()){
                if (block.destroyOnShot()){
                    block.destroy();
                    map.remove(block);
                }
                destroy();
                return true;
            }
        }

        for (Entity e : List.copyOf(entities)){
            if (e == this) continue;
            if (e.getX() != x || e.getY() != y) continue;

            if (e instanceof Tank) {
                if (e == owner) continue;
                ((Tank) e).damage();
                destroy();
                return true;
            }
        }

        return false;
    }

    @Override
    public void update(boolean hard){

        if (checkCollision()) return;

        map.remove(this);

        x += dx;
        y += dy;

        if (x < 0 || y < 0 || x >= map.getWidth() || y >= map.getHeight()){
            destroyed = true;
            return;
        }

        map.add(this);

        checkCollision();
    }

    @Override
    public boolean blocksMovement(){ return false; }
    @Override
    public boolean blocksBullet(){ return false; }
    @Override
    public boolean destroyOnShot(){ return false; }
}
