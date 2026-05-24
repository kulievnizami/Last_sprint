package io.github.some_example_name;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class Platform {
    private float x, y, width, height;
    private boolean breakable;
    private boolean breaking = false;
    private boolean falling = false;
    private boolean broken = false;
    private static Texture texture;
    private static Texture breakableTexture;
    private static Sound breakSound;
    private float breakTimer = 0;
    private float fallSpeed = 0;

    public static void load() {
        texture = new Texture("playt.png");
        breakableTexture = new Texture("BreakPlatform.png");
        breakSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/BreakPlat.mp3"));
    }

    public static void dispose() {
        if (texture != null) texture.dispose();
        if (breakableTexture != null) breakableTexture.dispose();
        if (breakSound != null) breakSound.dispose();
    }

    public Platform(float x, float y, float width, float height, boolean breakable) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.breakable = breakable;
    }

    public void startBreaking() {
        if (breakable && !breaking) {
            breaking = true;
            breakTimer = 0.15f;
        }
    }

    public void move(float distance) {
        x -= distance;
    }

    public void update(float delta) {
        if (breaking) {
            if (!falling) {
                breakTimer -= delta;
                if (breakTimer <= 0) {
                    falling = true;
                    if (breakSound != null) breakSound.play(Main.volume);
                }
            } else {
                fallSpeed += 4000f * delta;
                y -= fallSpeed * delta;
                if (y + height < 0) {
                    broken = true;
                }
            }
        }
    }

    public boolean isBroken() { return broken; }
    public boolean isBreakable() { return breakable; }

    public void render(SpriteBatch batch) {
        Texture currentTexture = breakable ? breakableTexture : texture;
        batch.draw(currentTexture, x, y, width, height);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
