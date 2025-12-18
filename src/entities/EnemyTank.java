package entities;

import world.Map;
import game.GameLoop;
import java.util.List;
import java.util.Random;

public class EnemyTank extends Tank implements Runnable {

    private final Random r = new Random();
    private final Thread aiThread;

    private volatile boolean running = true;

    private int moveCooldown = 0;
    private int shootCooldown = 0;

    private int steps = 0;
    private int targetSteps = 0;

    public EnemyTank(int x, int y, List<Entity> entities, Map map) {
        super(x, y, entities, map);
        aiThread = new Thread(this, "EnemyAI-" + hashCode());
        aiThread.setDaemon(true);
        aiThread.start();
    }

    @Override
    public void update(boolean hard) {
        if (moveCooldown > 0) moveCooldown--;
        if (shootCooldown > 0) shootCooldown--;
    }

    @Override
    public void run() {
        while (running && !isDestroyed()) {

            if (GameLoop.hardMode) {
                detectPlayerLine();
            } else {
                randomShoot();
            }

            aiMove();

            sleepAI(60);
        }
    }

    private void detectPlayerLine() {
        List<Entity> copy = List.copyOf(entities);

        for (Entity e : copy) {
            if (e instanceof PlayerTank) {
                int px = e.getX();
                int py = e.getY();

                if (px == x) {
                    dx = 0;
                    dy = py < y ? -1 : 1;

                    tryShoot();
                }

                if (py == y) {
                    dx = px < x ? -1 : 1;
                    dy = 0;

                    tryShoot();
                }
            }
        }
    }

    private void randomShoot() {
        if (shootCooldown == 0 && r.nextInt(5) == 0) {
            shoot();
            shootCooldown = 25;
        }
    }

    private void tryShoot() {
        if (shootCooldown == 0) {
            shoot();
            shootCooldown = 25;
        }
    }

    private void aiMove() {
        if (moveCooldown > 0) return;

        if (steps == 0) {
            targetSteps = r.nextInt(5) + 3;
            int d = r.nextInt(4);

            if (d == 0) { dx = 1; dy = 0; }
            if (d == 1) { dx = -1; dy = 0; }
            if (d == 2) { dx = 0; dy = 1; }
            if (d == 3) { dx = 0; dy = -1; }
        }

        int oldX = x;
        int oldY = y;

        move(dx, dy);

        if (x == oldX && y == oldY) {
            steps = 0;
            return;
        }

        steps++;

        if (steps >= targetSteps) {
            steps = 0;
        }

        moveCooldown = 5;
    }

    private void sleepAI(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    @Override
    public boolean isDestroyed() {
        boolean destroyed = super.isDestroyed();
        if (destroyed) running = false;
        return destroyed;
    }
}
