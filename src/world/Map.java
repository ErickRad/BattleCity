package world;

import entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {

    private List<Entity>[][] grid;
    private int width;
    private int height;

    @SuppressWarnings("unchecked")
    
    public Map(int width, int height){
        this.width = width;
        this.height = height;

        grid = new ArrayList[height][width];

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                grid[y][x] = new ArrayList<>();

        generate();
    }

    private void generate() {
        Random r = new Random();

        for (int x = 0; x < width; x++) {
            add(new Steel(x, 0));
            add(new Steel(x, height - 1));
        }
        for (int y = 0; y < height; y++) {
            add(new Steel(0, y));
            add(new Steel(width - 1, y));
        }

        for (int i = 0; i < 18; i++) {
            int x = r.nextInt(width - 4) + 1;
            int y = r.nextInt(height - 4) + 1;

            int type = r.nextInt(3);

            switch (type) {
                case 0 -> placeHorizontalWall(x, y);
                case 1 -> placeVerticalWall(x, y);
                case 2 -> placeCornerWall(x, y);
            }
        }

        for (int i = 0; i < 4; i++) {
            int x = r.nextInt(width - 3) + 1;
            int y = r.nextInt(height - 3) + 1;
            placeWaterBlock(x, y);
        }

        for (int i = 0; i < 12; i++) {
            int x = r.nextInt(width - 2) + 1;
            int y = r.nextInt(height - 2) + 1;
            if (isFree(x, y))
                add(new Bush(x, y));
        }
    }

    private boolean isFree(int x, int y) {
        return grid[y][x].isEmpty();
    }

    private void placeHorizontalWall(int x, int y) {
        for (int i = 0; i < 4; i++) {
            if (isFree(x + i, y))
                add(new Brick(x + i, y));
        }
    }

    private void placeVerticalWall(int x, int y) {
        for (int i = 0; i < 4; i++) {
            if (isFree(x, y + i))
                add(new Brick(x, y + i));
        }
    }

    private void placeCornerWall(int x, int y) {

        for (int i = 0; i < 3; i++) {
            if (isFree(x, y + i))
                add(new Brick(x, y + i));
        }

        for (int i = 0; i < 3; i++) {
            if (isFree(x + i, y))
                add(new Brick(x + i, y));
        }
    }

    private void placeWaterBlock(int x, int y) {
        for (int dy = 0; dy < 2; dy++) {
            for (int dx = 0; dx < 2; dx++) {
                if (isFree(x + dx, y + dy))
                    add(new Water(x + dx, y + dy));
            }
        }
    }

    public void add(Entity e){
        grid[e.getY()][e.getX()].add(e);
    }

    public List<Entity> get(int x, int y){
        return grid[y][x];
    }

    public void remove(Entity e){
        grid[e.getY()][e.getX()].remove(e);
    }

    public int getWidth(){ 
        return width; 
    }
    public int getHeight(){ 
        return height; 
    }
}
