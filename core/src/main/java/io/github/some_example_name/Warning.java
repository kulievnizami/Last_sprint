package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Warning {
    public float x, y;
    private float time = 0;

    public Warning(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        time += delta;
    }

    public boolean isFinished() {
        return time > 1.2f;
    }

    public boolean isVisible() {
        return ((int)(time * 10) % 2 == 0);
    }

    public void render(SpriteBatch batch, Texture tex) {
        if (isVisible()) {
            batch.draw(tex, x - 150, y, 300, 300);
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
