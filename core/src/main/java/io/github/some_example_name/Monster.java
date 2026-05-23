package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;

public class Monster {
    enum State { HOVER, SHOOT, DASH, RETURN }
    private State state = State.HOVER;
    private float stateTimer = 0;

    private float x, y;
    private float width = 300, height = 300;
    private float time;
    private float shootTimer;
    private int shotsFired = 0;
    private int health = 3;
    private int maxHealth = 3;
    private float damageFlashTimer = 0;
    private boolean active = true;

    private static Texture[] textures;
    private static Texture[] projectileTextures;

    public Monster(float startX, float startY) {
        this.x = startX;
        this.y = startY;
    }

    public static void load() {
        textures = new Texture[3];
        textures[0] = new Texture("monster1.png");
        textures[1] = new Texture("monster2.png");
        textures[2] = new Texture("monster3.png");

        projectileTextures = new Texture[3];
        projectileTextures[0] = new Texture("chargemonster1.png");
        projectileTextures[1] = new Texture("chargemonster.png2.png");
        projectileTextures[2] = new Texture("chargemonster.png3.png");
    }

    public void update(float delta, float playerX, float playerY, float viewportWidth, ArrayList<MonsterProjectile> projectiles) {
        time += delta;
        stateTimer += delta;
        if (damageFlashTimer > 0) damageFlashTimer -= delta;

        float targetX, targetY;

        switch (state) {
            case HOVER:
                
                targetX = viewportWidth - 350;
                targetY = 150 + MathUtils.sin(time * 2f) * 100f;
                x += (targetX - x) * 1.5f * delta;
                y += (targetY - y) * 1.5f * delta;

                if (stateTimer > 3f) {
                    state = State.SHOOT;
                    stateTimer = 0;
                    shotsFired = 0;
                }
                break;

            case SHOOT:
                
                shootTimer += delta;
                if (shootTimer > 0.8f && shotsFired < 3) {
                    float heightOffset = (shotsFired == 0) ? 50 : (shotsFired == 1) ? 0 : -50;
                    projectiles.add(new MonsterProjectile(x + width / 4, y + height / 2 + heightOffset, projectileTextures, playerX + 32, playerY + 32));
                    shootTimer = 0;
                    shotsFired++;
                }
                if (shotsFired >= 3 && stateTimer > 3f) {
                    state = State.DASH;
                    stateTimer = 0;
                }
                break;

            case DASH:
                
                targetX = playerX - 50;
                targetY = playerY - 50;
                x += (targetX - x) * 2.5f * delta;
                y += (targetY - y) * 2.5f * delta;

                if (stateTimer > 1.5f) {
                    state = State.RETURN;
                    stateTimer = 0;
                }
                break;

            case RETURN:
                
                targetX = viewportWidth - 350;
                targetY = 300;
                x += (targetX - x) * 1.5f * delta;
                y += (targetY - y) * 1.5f * delta;

                if (stateTimer > 2f) {
                    state = State.HOVER;
                    stateTimer = 0;
                }
                break;
        }
    }

    public void render(SpriteBatch batch) {
        int frame = (int)(time * 5) % 3;
        
        
        if (damageFlashTimer > 0 && (int)(damageFlashTimer * 20) % 2 == 0) {
            batch.setColor(1, 0, 0, 0.8f); 
        }
        batch.draw(textures[frame], x, y, width, height);
        batch.setColor(1, 1, 1, 1); 
    }

    public boolean isHit(Rectangle playerRect) {
        Rectangle monsterRect = new Rectangle(x, y, width, height);
        return monsterRect.overlaps(playerRect);
    }

    public void takeDamage() {
        health--;
        damageFlashTimer = 0.5f;
        if (health <= 0) active = false;
    }

    public int getHealth() { return health; }

    public boolean isActive() { return active; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    
    public static void dispose() {
        for (Texture t : textures) t.dispose();
        for (Texture t : projectileTextures) t.dispose();
    }
}
