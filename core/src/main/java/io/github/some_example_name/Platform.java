package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Platform {
    private float x, y, width, height;
    public static Texture texture;
    public static void load() {
        texture = new Texture("playt.png");
    }

    public static void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public Platform(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public void move(float distance) {
        x -= distance;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}