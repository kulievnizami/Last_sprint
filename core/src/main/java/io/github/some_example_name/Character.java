package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Character {

    private float velocityY = 0;
    private float gravity = -2000f;
    private boolean onGround = true;
    private Texture spriteSheet;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;



    private float x;
    private float y;


    public Character(float startX, float startY) {
        this.x = startX;
        this.y = startY;

        spriteSheet = new Texture("idle.png");

        int cols = 6;
        int rows = 2;

        int frameWidth = spriteSheet.getWidth() / cols;
        int frameHeight = spriteSheet.getHeight() / rows;

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        TextureRegion[] frames = new TextureRegion[cols];

        for (int i = 0; i < cols; i++) {
            frames[i] = tmp[0][i];
        }

        idleAnimation = new Animation<>(0.4f, frames);
        stateTime = 0f;
    }


    public void update(float deltaTime) {

        stateTime += deltaTime;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            velocityY = 800f;
            onGround = false;
        }

        velocityY += gravity * deltaTime;

        y += velocityY * deltaTime;

        if (y <= 100) {
            y = 100;
            velocityY = 0;
            onGround = true;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = idleAnimation.getKeyFrame(stateTime, true);

        batch.draw(frame, x, y);
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public float getWidth() {
        return spriteSheet.getWidth() / 9f;
    }

    public float getHeight() {
        return spriteSheet.getHeight() / 2f;
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
