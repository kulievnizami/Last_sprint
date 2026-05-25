package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Clock {

    private float x, y;
    private float width =100, height = 100;
    private float speed = 0;
    private static Texture texture;

    public static void load() {
        texture = new Texture("time.png"); // твой PNG
    }

    public Clock(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(float moveX) {
        x -= moveX;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public static void dispose() {
        texture.dispose();
    }
}
