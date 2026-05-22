package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Warning {
    float time = 0;
    boolean visible = true;
    private float x; // X-координата центра щели между платформами

    public Warning(float x) {
        this.x = x;
    }

    // Двигаемся вместе с миром
    public void move(float distance) {
        x -= distance;
    }

    public void update(float delta) {
        time += delta;
        visible = ((int)(time * 10) % 2 == 0);
    }

    public boolean isFinished() {
        return time > 1.2f;
    }

    public float getX() {
        return x;
    }

    public void render(SpriteBatch batch, Texture tex, Viewport viewport) {
        if (visible) {
            // Рисуем по X щели, снизу экрана
            batch.draw(tex, x - 150, 20, 300, 300);
        }
    }
}
