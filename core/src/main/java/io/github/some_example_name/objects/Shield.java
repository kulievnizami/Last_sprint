package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Shield {
    float x, y;
    float width = 80, height = 80;
    boolean active = true;

    public Shield(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(float distance) {
        x -= distance;
    }

    public void render(SpriteBatch batch, Texture texture) {
        if (active) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
