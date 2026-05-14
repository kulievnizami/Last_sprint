package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Character {
    private Texture spriteSheet;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private float x;
    private float y;
    private float[] offsetX;

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
        offsetX = new float[6];
        offsetX[0] = 0f;  
        offsetX[1] = 0f;  
        offsetX[2] = 0f;  
        offsetX[3] = 0f;  
        offsetX[4] = 0f;
        offsetX[5] = 0f;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        int frameIndex = idleAnimation.getKeyFrameIndex(stateTime);
        float drawX = x - (currentFrame.getRegionWidth() / 2f) + offsetX[frameIndex];
        float drawY = y; 
        batch.draw(currentFrame, drawX, drawY);
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}