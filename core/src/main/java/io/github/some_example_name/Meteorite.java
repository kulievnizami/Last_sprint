package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Meteorite {
    private float x, y;
    private float width = 80;
    private float height = 80;
    private float rotation = 0f;
    private float speedX;

    public Meteorite(float startX, float startY, float speedX) {
        this.x = startX;
        this.y = startY;
        this.speedX = speedX;
    }
    public void update(float delta) {
        x -= speedX * delta;
        rotation -= 250f * delta; 
    }
    public void render(SpriteBatch batch, Texture texture) {
        TextureRegion region = new TextureRegion(texture);
        batch.draw(
            region, 
            x, y, 
            width / 2f, height / 2f,
            width, height, 
            1f, 1f,
            rotation
        );
    }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}