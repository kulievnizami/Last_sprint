package io.github.some_example_name;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
public class Character {
    private float velocityY = 0;
    private float gravity = -2000f;
    private boolean onGround = true;
    private boolean canDoubleJump = false;

    public boolean hasShield = false;
    private float shieldTimer = 0;
    private Texture[] idleFrames;

    private static Texture shieldTexture;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private float x, y;
    private float feetOffset = 15f;
    private static ChallengeManager challengeManager;

    public Character(float startX, float startY) {
        idleFrames = new Texture[6];
        TextureRegion[] frames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = new Texture("idle" + (i + 1) + ".png");
            frames[i] = new TextureRegion(idleFrames[i]);
        }

        shieldTexture = new Texture("shit.png");
        idleAnimation = new Animation<>(0.15f, frames);
        reset(startX, startY);
    }

    public static void setChallengeManager(ChallengeManager cm) {
        challengeManager = cm;
    }

    public void reset(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.velocityY = 0;
        this.onGround = true;
        this.canDoubleJump = false;
        this.stateTime = 0f;
        hasShield = false;
        shieldTimer = 0;
    }



    public void update(float deltaTime, ArrayList<Platform> platforms, boolean ignoreTouch) {
        stateTime += deltaTime;
        velocityY += gravity * deltaTime;
        y += velocityY * deltaTime;
        onGround = false;


        if (hasShield) {
            shieldTimer -= deltaTime;

            if (shieldTimer <= 3f) {
                if (((int)(shieldTimer * 10)) % 2 == 0) {
                } else {
                }
            }

            if (hasShield) {
                shieldTimer -= deltaTime;

                if (shieldTimer <= 0) {
                    hasShield = false;
                    shieldTimer = 0;
                    Main.shieldBreakSound.play(Main.volume);
                }
            }
        }
        for (Platform p : platforms) {
            if (velocityY <= 0) {
                float charLeft = x + 15f;
                float charRight = x + getWidth() - 15f;
                if (charRight > p.getX() && charLeft < p.getX() + p.getWidth()) {
                    float platformTop = p.getY() + p.getHeight();
                    if (y <= platformTop && y - velocityY * deltaTime >= platformTop - 40f) {
                        y = platformTop;
                        velocityY = 0;
                        onGround = true;
                        canDoubleJump = true;
                        p.startBreaking();
                        break;
                    }
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            (Gdx.input.justTouched() && !ignoreTouch)) {
            if (onGround) {
                velocityY = 850f;
                onGround = false;
                canDoubleJump = true;
            } else if (canDoubleJump) {
                velocityY = 800f;
                canDoubleJump = false;
            }
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = idleAnimation.getKeyFrame(stateTime, true);
        batch.draw(frame, x, y - feetOffset);

        if (hasShield) {
            if (shieldTimer > 4f || ((int)(shieldTimer * 10)) % 2 == 0) {
                batch.draw(shieldTexture, x + 5, y + 10, getWidth() - 10, getHeight() - 10);
            }
        }
    }



    public void activateShield() {
        hasShield = true;
        shieldTimer = 15f;
    }

    public void breakShield() {
        hasShield = false;
        shieldTimer = 0;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getVelocityY() { return velocityY; }
    public void setVelocityY(float vy) { this.velocityY = vy; }
    public float getWidth() { return idleFrames[0].getWidth(); }
    public float getHeight() { return idleFrames[0].getHeight(); }
    public boolean isOnGround() { return onGround; }

    public void dispose() {
        for (int i = 0; i < 6; i++) {
            if (idleFrames[i] != null) {
                idleFrames[i].dispose();
            }
        }
    }
}
