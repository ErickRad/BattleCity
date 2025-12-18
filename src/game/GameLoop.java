package game;

import world.Map;
import entities.PlayerTank;
import entities.EnemyTank;
import entities.Entity;
import logic.Spawner;
import logic.Collision;
import utils.Renderer;

import java.util.ArrayList;
import java.util.List;

public class GameLoop extends Thread {

    private Map map;
    private PlayerTank player;
    private InputHandler input;

    private List<EnemyTank> enemies = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();

    private volatile boolean running = true;

    private int width = 45;
    private int height = 20;

    private int level = 1;
    private int tankCount = 3;

    public static int kills = 0;
    public static boolean hardMode = false;

    public GameLoop() {
        input = new InputHandler();
        startLevel(true);
    }

    @Override
    public void run() {
        while (running) {

            Renderer.draw(map, entities);

            if (player.getHealth() <= 0) {
                System.out.println("=== GAME OVER ===");
                System.out.println("Level reached: " + level);
                System.out.println("Final score: " + getScore());
                break;
            }

            handlePlayer(input.poll());

            updateEntities();
            Collision.process(entities, map);

            if (enemies.isEmpty()) {
                level++;
                if (level % 2 == 0) tankCount++;
                showLevelStats();
                waitForSpace();
                startLevel(false);
            }

            sleepLoop(50);
        }
    }

    private void startLevel(boolean first) {
        map = new Map(width, height);

        enemies.clear();
        entities.clear();

        if (first) {
            player = new PlayerTank(width / 2, height / 2, entities, map);
        } else {
            restorePlayerHealth();
            player.setPosition(width / 2, height / 2);
        }

        entities.add(player);
        Spawner.spawnEnemies(enemies, entities, map, tankCount);
    }

    private void restorePlayerHealth() {
        if (level < 10) {
            player.restoreFullHealth();
            return;
        }

        if (level <= 20) {
            player.restoreOneHealth();
        }
    }

    private void showLevelStats() {
        clearScreen();
        System.out.println("=== LEVEL " + level + " ===");
        System.out.println("Score: " + getScore());
        System.out.println("Kills: " + kills);
        System.out.println("Enemy tanks: " + tankCount);
        System.out.println("Health: " + player.getHealth());
        System.out.println();
        System.out.println("Press SPACE to continue");
    }

    private int getScore() {
        return kills * 100 + level * 50;
    }

    private void waitForSpace() {
        while (true) {
            if (input.poll() == ' ') break;
            sleepLoop(30);
        }
    }

    private void handlePlayer(char input) {
        switch (input) {
            case 'w' -> player.move(0, -1);
            case 's' -> player.move(0, 1);
            case 'a' -> player.move(-1, 0);
            case 'd' -> player.move(1, 0);
            case ' ' -> player.shoot();
            case 'q' -> running = false;
        }
    }

    private void updateEntities() {
        List<Entity> snapshot = new ArrayList<>(entities);

        for (Entity e : snapshot) {
            e.update(hardMode);
            if (e.isDestroyed()) {
                entities.remove(e);
                if (e instanceof EnemyTank) {
                    enemies.remove(e);
                    kills++;
                }
            }
        }
    }

    private void sleepLoop(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
