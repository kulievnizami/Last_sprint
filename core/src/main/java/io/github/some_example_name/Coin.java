package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Coin {

    public float getX() { return x; }
    public float getY() { return y; }

    public float getWidth() { return 32; }
    public float getHeight() { return 32; }

    float x;
    float y;

    public Coin(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
    }

    public void render(SpriteBatch batch) {
        batch.draw(Animations.coinTexture, x, y, 64, 64);
    }
}
