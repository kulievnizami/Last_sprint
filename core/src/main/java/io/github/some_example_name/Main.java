package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import java.util.Random;
import java.util.ArrayList;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Pixmap;

public class Main extends ApplicationAdapter {

    enum GameState { START, RUNNING, GAME_OVER }
    private GameState state = GameState.START;

    private SpriteBatch batch;
    private Texture background;
    private Texture darkOverlay; 
    private Texture meteoriteTexture; 
    
    private Texture islandWindowTexture;
    private Texture buttonTexture;

    private float gameSpeed;
    private Random random = new Random();
    private Viewport viewport;
    private Sound coinSound;
    private BitmapFont font;

    private int point = 0; 
    private float score = 0; 
    
    private float nextMeteorSpawnScore = 0; 

    private Character player;
    private ArrayList<Coin> coins;
    private ArrayList<Platform> platforms;
    private ArrayList<Meteorite> meteorites; 

    private float bgX = 0;
    private float lastPlatformY = 100f; 

    private Rectangle continueButton;
    private Rectangle exitButton;

    private float windowWidth = 400;
    private float windowHeight = 300;

    @Override
    public void create() {
        Animations.load();
        Platform.load(); 
        meteoriteTexture = new Texture("meteor.png");
        meteoriteTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/coin_play.mp3"));
        font = new BitmapFont();
        font.getData().setScale(2);
        font.setColor(Color.WHITE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        batch = new SpriteBatch();
        background = new Texture("bg_place.png");
        viewport = new ScreenViewport();
        Pixmap pixmapOverlay = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapOverlay.setColor(0, 0, 0, 0.6f);
        pixmapOverlay.fill();
        darkOverlay = new Texture(pixmapOverlay);
        pixmapOverlay.dispose();
        Pixmap pixmapWindow = new Pixmap(400, 300, Pixmap.Format.RGBA8888);
        pixmapWindow.setColor(0.22f, 0.16f, 0.12f, 0.95f);
        pixmapWindow.fillRectangle(0, 0, 400, 300);
        pixmapWindow.setColor(0.45f, 0.35f, 0.25f, 1f);
        pixmapWindow.drawRectangle(0, 0, 400, 300);
        pixmapWindow.drawRectangle(1, 1, 398, 298);
        islandWindowTexture = new Texture(pixmapWindow);
        pixmapWindow.dispose();
        Pixmap pixmapBtn = new Pixmap(220, 50, Pixmap.Format.RGBA8888);
        pixmapBtn.setColor(0.32f, 0.26f, 0.22f, 1f);
        pixmapBtn.fillRectangle(0, 0, 220, 50);
        pixmapBtn.setColor(0.55f, 0.45f, 0.35f, 1f);
        pixmapBtn.drawRectangle(0, 0, 220, 50);
        buttonTexture = new Texture(pixmapBtn);
        pixmapBtn.dispose();
        player = new Character(200, 300);
        coins = new ArrayList<>();
        platforms = new ArrayList<>();
        meteorites = new ArrayList<>();
        initGame();
    }

    private void initGame() {
        point = 0;
        score = 0;
        gameSpeed = 550f; 
        platforms.clear();
        coins.clear();
        meteorites.clear(); 
        
        nextMeteorSpawnScore = 100f + random.nextInt(200);
        float platformHeight = 45f; 
        lastPlatformY = 100f;
        platforms.add(new Platform(0, lastPlatformY, 800, platformHeight));
        player.reset(200, lastPlatformY + platformHeight); 
        for (int i = 0; i < 5; i++) {
            spawnPlatform();
        }
    }

    private void spawnPlatform() {
        float platformHeight = 45f; 
    
        float width = 520f; 
        
        float gap = 350f;   
        
        float yChange = -15 + random.nextInt(30);
        float y = lastPlatformY + yChange;
        if (y < 70) y = 70;
        if (y > 200) y = 200;
        float x = 0;
        if (!platforms.isEmpty()) {
            Platform lastPlatform = platforms.get(platforms.size() - 1);
            x = lastPlatform.getX() + lastPlatform.getWidth() + gap;
        }
        platforms.add(new Platform(x, y, width, platformHeight));
        lastPlatformY = y; 
        if (random.nextInt(100) < 60) {
            float coinX = x + width / 2 - 32;
            float coinY = y + 85f; 
            coins.add(new Coin(coinX, coinY));
        }
    }
    
    private void spawnMeteorite() {
        float startX = viewport.getWorldWidth() + 50; 
        float startY = lastPlatformY + 70 + random.nextInt(150); 
        
        float meteorSpeed = gameSpeed + 150f + random.nextInt(150);
        
        meteorites.add(new Meteorite(startX, startY, meteorSpeed));
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        if (state == GameState.RUNNING) {
            score += 25f * delta; 
            float moveDistance = gameSpeed * delta;

            player.update(delta, platforms);
            bgX -= (moveDistance * 0.4f);
            
            if (bgX <= -viewport.getWorldWidth()) bgX = 0;
            
            if (score >= nextMeteorSpawnScore) {
                spawnMeteorite();
                nextMeteorSpawnScore = score + 100f + random.nextInt(200);
            }

            for (int i = meteorites.size() - 1; i >= 0; i--) {
                Meteorite m = meteorites.get(i);
                m.update(delta);

                if (isColliding(player, m)) {
                    state = GameState.GAME_OVER; 
                    showGameOverMenu();
                } else if (m.getX() + m.getWidth() < -100) {
                    meteorites.remove(i); 
                }
            }

            for (int i = platforms.size() - 1; i >= 0; i--) {
                Platform p = platforms.get(i);
                p.move(moveDistance);
                if (p.getX() + p.getWidth() < 0) {
                    platforms.remove(i);
                    spawnPlatform(); 
                }
            }

            for (int i = coins.size() - 1; i >= 0; i--) {
                Coin coin = coins.get(i);
                coin.move(moveDistance);

                if (isColliding(player, coin)) {
                    coinSound.play();
                    coins.remove(i);
                    point++;
                } else if (coin.getX() + coin.getWidth() < 0) {
                    coins.remove(i);
                }
            }

            if (player.getY() < -player.getHeight()) {
                state = GameState.GAME_OVER;
                showGameOverMenu();
            }
        }

        if (Gdx.input.justTouched()) {
            Vector2 touch = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

            if (state == GameState.START) {
                state = GameState.RUNNING;
            } 
            else if (state == GameState.GAME_OVER) {
                if (continueButton.contains(touch.x, touch.y)) {
                    initGame();
                    state = GameState.START;
                }
                if (exitButton.contains(touch.x, touch.y)) {
                    Gdx.app.exit();
                }
            }
        }

        batch.begin();
        float bgWidth = viewport.getWorldWidth();
        batch.draw(background, bgX, 0, bgWidth, viewport.getWorldHeight());
        batch.draw(background, bgX + bgWidth - 2f, 0, bgWidth, viewport.getWorldHeight());

        for (Platform platform : platforms) platform.render(batch);
        for (Coin coin : coins) coin.render(batch);
        
        for (Meteorite m : meteorites) {
            m.render(batch, meteoriteTexture);
        }
        
        player.render(batch);

        if (state == GameState.RUNNING) {
            font.setColor(Color.WHITE);
            font.draw(batch, "Points: " + point, 30, viewport.getWorldHeight() - 30);
            font.draw(batch, "Score: " + (int) score, 30, viewport.getWorldHeight() - 70);
        } 
        else if (state == GameState.START) {
            batch.draw(darkOverlay, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            
            float wx = (viewport.getWorldWidth() - windowWidth) / 2;
            float wy = (viewport.getWorldHeight() - windowHeight) / 2;
            batch.draw(islandWindowTexture, wx, wy, windowWidth, windowHeight);
            
            font.setColor(Color.GOLD);
            font.draw(batch, "RETRO RUNNER", wx + 95, wy + 200);
            font.setColor(Color.WHITE);
            font.draw(batch, "TAP TO START", wx + 110, wy + 130);
        } 
        else if (state == GameState.GAME_OVER) {
            batch.draw(darkOverlay, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            
            float wx = (viewport.getWorldWidth() - windowWidth) / 2;
            float wy = (viewport.getWorldHeight() - windowHeight) / 2;
            
            batch.draw(islandWindowTexture, wx, wy, windowWidth, windowHeight);
            
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER", wx + 120, wy + 260);
            
            font.setColor(Color.WHITE);
            font.draw(batch, "Points: " + point + "  |  Score: " + (int) score, wx + 60, wy + 210);
            
            batch.draw(buttonTexture, continueButton.x, continueButton.y, continueButton.width, continueButton.height);
            batch.draw(buttonTexture, exitButton.x, exitButton.y, exitButton.width, exitButton.height);
            
            font.draw(batch, "CONTINUE", continueButton.x + 50, continueButton.y + 35);
            font.draw(batch, "EXIT", exitButton.x + 85, exitButton.y + 35);
        }

        batch.end();
    }
    
    private void showGameOverMenu() {
        float wx = (viewport.getWorldWidth() - windowWidth) / 2;
        float wy = (viewport.getWorldHeight() - windowHeight) / 2;
        continueButton = new Rectangle(wx + 90, wy + 110, 220, 50);
        exitButton = new Rectangle(wx + 90, wy + 40, 220, 50);
    }

    private boolean isColliding(Character player, Coin coin) {
        return player.getX() < coin.getX() + coin.getWidth() &&
            player.getX() + player.getWidth() > coin.getX() &&
            player.getY() < coin.getY() + coin.getHeight() &&
            player.getY() + player.getHeight() > coin.getY();
    }
    
    private boolean isColliding(Character player, Meteorite m) {
        float hitShrink = 12f; 
        return player.getX() < m.getX() + m.getWidth() - hitShrink &&
            player.getX() + player.getWidth() > m.getX() + hitShrink &&
            player.getY() < m.getY() + m.getHeight() - hitShrink &&
            player.getY() + player.getHeight() > m.getY() + hitShrink;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        meteoriteTexture.dispose();
        darkOverlay.dispose();
        islandWindowTexture.dispose();
        buttonTexture.dispose();
        Animations.dispose();
        Platform.dispose();
        player.dispose();
        font.dispose();
        coinSound.dispose();
    }
}