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

    private final List<EnemyTank> enemies = new ArrayList<>();
    private final List<Entity> entities = new ArrayList<>();

    private volatile boolean running = true;

    private final int width = 45;
    private final int height = 20;

    private int level = 1;
    private int tankCount = 2;

    public static int kills = 0;

    public static boolean hardMode = true;

    public GameLoop() {
        input = new InputHandler();
        startLevel(true);
    }

    @Override
    public void run() {

        while (running) {

            Renderer.draw(map, entities);

            if (player.getHealth() <= 0) {
                clearScreen();
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

                if (level % 2 == 0) {
                    tankCount++;
                }

                showLevelStats();
                waitForSpace();
                startLevel(false);
            }

            sleepLoop(50);
        }
    }

    private void startLevel(boolean first) {

        int savedHealth = 3;

        if (!first && player != null) {
            savedHealth = player.getHealth();
        }

        destroyLevel();

        map = new Map(width, height);

        player = new PlayerTank(width / 2, height / 2, entities, map);

        if (!first) {
            if (level < 10) {
                savedHealth = 3;
            } else if (level <= 20) {
                savedHealth = Math.min(3, savedHealth + 1);
            }
        }

        player.setHealth(savedHealth);
        entities.add(player);

        Spawner.spawnEnemies(enemies, entities, map, tankCount);
    }

    private void destroyLevel() {

        for (EnemyTank e : enemies) {
            e.stopAI();
        }

        enemies.clear();
        entities.clear();
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

    private void showLevelStats() {

        clearScreen();
        System.out.println("=== LEVEL " + level + " ===");
        System.out.println("Score: " + getScore());
        System.out.println("Kills: " + kills);
        System.out.println("Next enemy tanks: " + tankCount);
        System.out.println("Health: " + player.getHealth());
        System.out.println();
        System.out.println("Press SPACE to continue");
    }

    private int getScore() {
        return kills * 100 + level * 50;
    }

    private void waitForSpace() {

        while (true) {
            if (input.poll() == ' ') {
                break;
            }
            sleepLoop(30);
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
