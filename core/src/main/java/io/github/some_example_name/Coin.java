package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Coin {
    private static Texture coinSpritesheet;
    private static Animation<TextureRegion> coinAnimation;
    private float stateTime; 
    private float x, y;

    public Coin(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.stateTime = 0f;
    }

    public static void load() {
        coinSpritesheet = new Texture("coinanim.png");
        int cols = 20;
        int rows = 1;
        int frameWidth = coinSpritesheet.getWidth() / cols;
        int frameHeight = coinSpritesheet.getHeight() / rows;

        TextureRegion[][] tmpFrames = TextureRegion.split(coinSpritesheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[cols];
        
        for (int i = 0; i < cols; i++) {
            frames[i] = tmpFrames[0][i];
        }
        coinAnimation = new Animation<>(0.1f, frames);
        coinAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }
    public void move(float distance) {
        x -= distance;
    }
    public void updateTime(float deltaTime) {
        stateTime += deltaTime;
    }
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = coinAnimation.getKeyFrame(stateTime);
        batch.draw(currentFrame, x, y, 64f, 64f);
    }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return 64f; }
    public float getHeight() { return 64f; }

    public static void dispose() {
        if (coinSpritesheet != null) {
            coinSpritesheet.dispose();
        }
    }
}