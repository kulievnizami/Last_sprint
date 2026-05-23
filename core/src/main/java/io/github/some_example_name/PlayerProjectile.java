package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PlayerProjectile {
    private float x, y;
    private float speed = 800f;
    private float width = 30, height = 15;
    private static Texture texture;

    public PlayerProjectile(float x, float y) {
        this.x = x;
        this.y = y;
        if (texture == null) {
            
            
            
        }
    }

    public void update(float delta) {
        x += speed * delta;
    }

    public void render(SpriteBatch batch, Texture tex) {
        batch.draw(tex, x, y, width, height);
    }

    public boolean isOffScreen(float viewportWidth) {
        return x > viewportWidth;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
