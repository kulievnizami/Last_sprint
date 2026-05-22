package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;


public class Warning {
    float time = 0;
    boolean visible = true;

    public Warning() {
        // пустой конструктор
    }

    public void update(float delta) {
        time += delta;
        visible = ((int)(time * 10) % 2 == 0);
    }

    public boolean isFinished() {
        return time > 1.2f;
    }

    public void render(SpriteBatch batch, Texture tex, Viewport viewport) {
        if (visible) {
            float x = viewport.getWorldWidth() / 2f - 150;
            float y = 20;
            batch.draw(tex, x, y, 300, 300);
        }
    }
}
