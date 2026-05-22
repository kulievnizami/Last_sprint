package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FallingLog {

    private float x, y;
    private float speed = 900f;

    public FallingLog(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        y -= speed * delta;
    }

    public void render(SpriteBatch batch, Texture texture) {
        batch.draw(texture, x, y, 220, 80);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return 220;
    }

    public float getHeight() {
        return 80;
    }
}
