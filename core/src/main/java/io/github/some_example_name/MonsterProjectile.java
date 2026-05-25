package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MonsterProjectile {
    private float x, y;
    private float speed = 400f;
    private float width = 50, height = 50;
    private float time;
    private Texture[] textures;
    private float dirX, dirY;

    public MonsterProjectile(float x, float y, Texture[] textures, float targetX, float targetY) {
        this.x = x;
        this.y = y;
        this.textures = textures;


        this.dirX = -1;
        this.dirY = 0;
    }

    public void update(float delta) {
        x += dirX * speed * delta;
        y += dirY * speed * delta;
        time += delta;
    }

    public void render(SpriteBatch batch) {
        int frame = (int)(time * 10) % 3;
        batch.draw(textures[frame], x, y, width, height);
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
