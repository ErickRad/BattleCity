package entities;

import world.Map;
import java.util.List;

public abstract class Tank extends Entity {

    protected int health;
    protected Map map;
    protected List<Entity> entities;
    protected int dx = 0;
    protected int dy = -1;

    public Tank(int x, int y, List<Entity> entities, Map map){
        super(x, y);
        this.entities = entities;
        this.map = map;
    }

    public int getHealth(){ return health; }

    public void damage(){
        health--;
        if (health <= 0) destroy();
    }

    @Override
    public void update(boolean hard){}

    @Override
    public boolean blocksMovement(){ 
        return true; 
    }

    @Override
    public boolean blocksBullet(){ return true; }

    @Override
    public boolean destroyOnShot(){ return true; }

    public void move(int mx, int my){
        dx = mx;
        dy = my;

        int nx = x + mx;
        int ny = y + my;

        for (Entity e : map.get(nx, ny)) {
            if (e.blocksMovement()) return;
        }

        x = nx;
        y = ny;
    }

    public void shoot(){
        entities.add(new Bullet(x + dx, y + dy, dx, dy, map, entities, this));
    }
}
