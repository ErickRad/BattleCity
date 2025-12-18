package entities;

public abstract class Entity {

    protected int x;
    protected int y;

    protected boolean destroyed = false;

    public Entity(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){ 
        return x; 
    }

    public int getY(){ 
        return y; 
    }

    public boolean isDestroyed(){ 
        return destroyed; 
    }

    public void destroy(){ 
        destroyed = true; 
    }

    public abstract void update(boolean hard);

    public abstract boolean blocksMovement();
    public abstract boolean blocksBullet();
    public abstract boolean destroyOnShot();
}
