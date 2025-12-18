package entities;

import world.Map;
import java.util.List;

public class PlayerTank extends Tank {

    private static final int maxHealth = 3;

    public PlayerTank(int x, int y, List<Entity> entities, Map map) {
        super(x, y, entities, map);
        this.health = maxHealth;
    }

    @Override
    public void update(boolean hard) {}

    public void restoreFullHealth() {
        health = maxHealth;
    }

    public void restoreOneHealth() {
        if (health < maxHealth) {
            health++;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
