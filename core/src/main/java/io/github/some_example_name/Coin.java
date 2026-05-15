package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Coin {

    private float x;
    private float y;

    private float speed;

    public Coin(float x, float y, float speed) {

        this.x = x;
        this.y = y;

        this.speed = speed;
    }

    public void update(float delta) {

        x -= speed * delta;
    }

    public void render(SpriteBatch batch) {

        batch.draw(
            Animations.coinTexture,
            x,
            y,
            64,
            64
        );
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return 64;
    }

    public float getHeight() {
        return 64;
    }
}
