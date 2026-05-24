package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import java.util.Random;
import com.badlogic.gdx.Input;
import java.util.ArrayList;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Main extends ApplicationAdapter {

    enum GameState { MENU, RUNNING, GAME_OVER, RECORDS, SETTINGS, PAUSED, SHOP, AUTHORS, CHALLENGES }
    private GameState state = GameState.MENU;

    private boolean isRussian;
    public static float volume;
    private int totalCoins;
    private int lastScore;
    private int highScore = 0;
    private int selectedBgInShop = 0;
    private int activeBgIndex = 0;
    private boolean[] unlockedBgs = {true, false, false};
    private int[] bgPrices = {0, 10, 50};

    private SpriteBatch batch;
    private Texture[] bgTextures;
    private Texture backgroundMenu;
    private Music bgMusic;
    private Music gameMusic;

    private Texture darkOverlay;
    private Texture meteoriteTexture;

    private ArrayList<FallingLog> logs;
    private Texture logTexture;

    private Texture islandWindowTexture;
    private Texture buttonTexture;

    private float gameSpeed;

    private Random random = new Random();
    private Viewport viewport;
    private Sound coinSound;

    private BitmapFont font;
    private GlyphLayout glyphLayout = new GlyphLayout();

    private int point = 0;
    private float score = 0;
    private float nextMeteorSpawnScore = 0;
    private float nextLogSpawnScore = 0;
    private float nextMonsterSpawnScore = 500f;

    private Preferences prefs;

    private Character player;
    private ArrayList<Coin> coins;
    private ArrayList<Platform> platforms;
    private ArrayList<Meteorite> meteorites;
    private ArrayList<Warning> warnings;
    private ArrayList<Monster> monsters;
    private ArrayList<MonsterProjectile> monsterProjectiles;
    private Texture warningTexture;
    private Texture heartFull, heartEmpty;
    private int health = 3;
    private float invincibilityTimer = 0;
    private boolean isInvincible = false;

    private ChallengeManager challengeManager;
    private int selectedChallengeIndex = 0;

    private float bgX = 0;
    private float lastPlatformY = 100f;

    private Rectangle menuPlayButton;
    private Rectangle menuRecordsButton;
    private Rectangle menuShopButton;
    private Rectangle menuSettingsButton;
    private Rectangle menuChallengesButton;
    private Rectangle menuExitButton;
    private Rectangle menuAuthorsButton;

    private Rectangle settingsVolumeButton;
    private Rectangle settingsLangButton;
    private Rectangle settingsBackButton;

    private Rectangle gameOverContinueButton;
    private Rectangle gameOverExitButton;

    private Rectangle recordsBackButton;
    private Rectangle recordsClearButton;

    private Rectangle shopNextBgButton;
    private Rectangle shopBuySelectButton;
    private Rectangle shopBackButton;

    private Rectangle authorsBackButton;

    private Rectangle challengesBackButton;
    private Rectangle challengesLeftButton;
    private Rectangle challengesRightButton;
    private Rectangle challengesStartButton;

    private Rectangle pauseButton;
    private Rectangle pauseContinueButton;
    private Rectangle pauseExitButton;

    private Animation<TextureRegion> coinAnimation;
    private float menuStateTime;

    private float windowWidth = 400;
    private float windowHeight = 420;

    @Override
    public void create() {
        Animations.load();
        Platform.load();
        Coin.load();
        Monster.load();

        challengeManager = new ChallengeManager();
        Character.setChallengeManager(challengeManager);

        prefs = Gdx.app.getPreferences("RetroRunnerSettings");
        lastScore = prefs.getInteger("lastScore", 0);
        highScore = prefs.getInteger("highScore", 0);
        totalCoins = prefs.getInteger("totalCoins", 0);
        activeBgIndex = prefs.getInteger("activeBgIndex", 0);
        isRussian = prefs.getBoolean("isRussian", true);
        volume = prefs.getFloat("volume", 1.0f);
        unlockedBgs = new boolean[]{
            true,
            prefs.getBoolean("bgUnlocked1", false),
            prefs.getBoolean("bgUnlocked2", false)
        };

        Texture coinAnimSheet = new Texture("coinanim.png");
        TextureRegion[][] tmp = TextureRegion.split(coinAnimSheet, coinAnimSheet.getWidth() / 20, coinAnimSheet.getHeight());
        TextureRegion[] coinFrames = new TextureRegion[20];
        for (int i = 0; i < 20; i++) coinFrames[i] = tmp[0][i];
        coinAnimation = new Animation<>(0.05f, coinFrames);

        meteoriteTexture = new Texture("meteor.png");
        warnings = new ArrayList<>();
        warningTexture = new Texture("Warning.png");
        logs = new ArrayList<>();
        logTexture = new Texture("log.png");
        meteoriteTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        coinSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/coin_play.mp3"));

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/bg_music.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(volume);

        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds/game_music.mp3"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(volume);

        bgMusic.play();

        loadFont();

        batch = new SpriteBatch();

        bgTextures = new Texture[3];
        bgTextures[0] = new Texture("bg_place2.png");
        bgTextures[1] = new Texture("bg_place3.png");
        bgTextures[2] = new Texture("bg_place4.png");
        backgroundMenu = new Texture("bg_menu.png");
        heartFull = new Texture("heart1.png");
        heartEmpty = new Texture("heart2.png");

        viewport = new ScreenViewport();

        Pixmap pixmapOverlay = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapOverlay.setColor(0, 0, 0, 0.6f);
        pixmapOverlay.fill();
        darkOverlay = new Texture(pixmapOverlay);
        pixmapOverlay.dispose();

        Pixmap pixmapWindow = new Pixmap(400, 450, Pixmap.Format.RGBA8888);
        pixmapWindow.setColor(0.22f, 0.16f, 0.12f, 0.95f);
        pixmapWindow.fillRectangle(0, 0, 400, 450);
        pixmapWindow.setColor(0.45f, 0.35f, 0.25f, 1f);
        pixmapWindow.drawRectangle(0, 0, 400, 450);
        pixmapWindow.drawRectangle(1, 1, 398, 448);
        islandWindowTexture = new Texture(pixmapWindow);
        pixmapWindow.dispose();

        Pixmap pixmapBtn = new Pixmap(250, 50, Pixmap.Format.RGBA8888);
        pixmapBtn.setColor(0.32f, 0.26f, 0.22f, 1f);
        pixmapBtn.fillRectangle(0, 0, 250, 50);
        pixmapBtn.setColor(0.55f, 0.45f, 0.35f, 1f);
        pixmapBtn.drawRectangle(0, 0, 250, 50);
        buttonTexture = new Texture(pixmapBtn);
        pixmapBtn.dispose();

        player = new Character(200, 300);
        coins = new ArrayList<>();
        platforms = new ArrayList<>();
        meteorites = new ArrayList<>();
        monsters = new ArrayList<>();
        monsterProjectiles = new ArrayList<>();

        initGame();
    }

    private void loadFont() {
        if (font != null) font.dispose();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(isRussian ? "Robot-russian.ttf" : "Robot_anglich.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 28;
        parameter.color = Color.WHITE;
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюяABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789:|-.,! %";
        font = generator.generateFont(parameter);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        generator.dispose();
    }

    private void initGame() {
        point = 0; score = 0; health = 3; invincibilityTimer = 0; isInvincible = false;
        gameSpeed = 550f;
        platforms.clear(); coins.clear(); meteorites.clear(); monsters.clear();
        monsterProjectiles.clear(); logs.clear(); warnings.clear();
        nextMeteorSpawnScore = 100f + random.nextInt(200);
        nextLogSpawnScore = 200f; nextMonsterSpawnScore = 500f;
        float platformHeight = 45f; lastPlatformY = 100f;
        platforms.add(new Platform(0, lastPlatformY, 800, platformHeight, false));
        player.reset(200, lastPlatformY + platformHeight);
        for (int i = 0; i < 5; i++) spawnPlatform();
        challengeManager.startNewRun();
    }

    private void spawnPlatform() {
        float platformHeight = 45f, width = 520f, gap = 350f;
        float yChange = -40 + random.nextInt(80);
        float y = lastPlatformY + yChange;
        if (y < 60) y = 60; if (y > 250) y = 250;
        float x = 0;
        if (!platforms.isEmpty()) {
            Platform lastPlatform = platforms.get(platforms.size() - 1);
            x = lastPlatform.getX() + lastPlatform.getWidth() + gap;
        }
        boolean breakable = random.nextFloat() < 0.3f;
        platforms.add(new Platform(x, y, width, platformHeight, breakable));
        lastPlatformY = y;
        if (random.nextInt(100) < 70) warnings.add(new Warning(x - gap / 2f));
        if (random.nextInt(100) < 60) coins.add(new Coin(x + width / 2 - 32, y + 85f));
    }

    private void spawnMeteorite() {
        meteorites.add(new Meteorite(viewport.getWorldWidth() + 50, 320 + random.nextInt(80), gameSpeed + 150f + random.nextInt(150)));
    }

    private void checkAndSaveRecords() {
        lastScore = (int) score; prefs.putInteger("lastScore", lastScore);
        totalCoins += point; prefs.putInteger("totalCoins", totalCoins);
        if ((int)score > highScore) { highScore = (int)score; prefs.putInteger("highScore", highScore); }
        prefs.flush();
    }

    public static float getVolume() {
        Preferences p = Gdx.app.getPreferences("RetroRunnerSettings");
        return p.getFloat("volume", 1.0f);
    }

    private void takeDamage() {
        health--;
        if (health <= 0) { state = GameState.GAME_OVER; checkAndSaveRecords(); }
        else { isInvincible = true; invincibilityTimer = 3f; }
    }

    private void drawCenteredText(String text, float x, float y, float width, float height) {
        glyphLayout.setText(font, text);
        font.draw(batch, text, x + (width - glyphLayout.width) / 2, y + (height + glyphLayout.height) / 2);
    }

    private void drawProgressBar(float x, float y, float width, float height, float progress) {
        Pixmap pix = new Pixmap((int)width, (int)height, Pixmap.Format.RGBA8888);
        pix.setColor(0.2f, 0.2f, 0.2f, 1f);
        pix.fill();
        pix.setColor(0.2f, 0.8f, 0.2f, 1f);
        pix.fillRectangle(0, 0, (int)(width * progress), (int)height);
        Texture barTex = new Texture(pix);
        pix.dispose();
        batch.draw(barTex, x, y, width, height);
        barTex.dispose();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        float delta = Gdx.graphics.getDeltaTime();
        boolean holdTime = Gdx.input.isKeyPressed(Input.Keys.R) && player.isOnGround();
        viewport.apply(); batch.setProjectionMatrix(viewport.getCamera().combined);

        float curWinHeight = (state == GameState.MENU) ? 450 : 350;
        float wx = (viewport.getWorldWidth() - 400) / 2;
        float wy = (viewport.getWorldHeight() - curWinHeight) / 2;

        menuPlayButton = new Rectangle(wx + 75, wy + 340, 250, 50);
        menuRecordsButton = new Rectangle(wx + 75, wy + 280, 250, 50);
        menuShopButton = new Rectangle(wx + 75, wy + 220, 250, 50);
        menuChallengesButton = new Rectangle(wx + 75, wy + 160, 250, 50);
        menuSettingsButton = new Rectangle(wx + 75, wy + 100, 250, 50);
        menuExitButton = new Rectangle(wx + 75, wy + 40, 250, 50);
        menuAuthorsButton = new Rectangle(wx + 20, wy + 20, 40, 40);

        settingsLangButton = new Rectangle(wx + 75, wy + 180, 250, 50);
        settingsVolumeButton = new Rectangle(wx + 75, wy + 120, 250, 50);
        settingsBackButton = new Rectangle(wx + 75, wy + 60, 250, 50);

        recordsBackButton = new Rectangle(wx + 75, wy + 100, 120, 50);
        recordsClearButton = new Rectangle(wx + 205, wy + 100, 120, 50);

        shopNextBgButton = new Rectangle(wx + 75, wy + 140, 250, 50);
        shopBuySelectButton = new Rectangle(wx + 75, wy + 80, 250, 50);
        shopBackButton = new Rectangle(wx + 75, wy + 20, 250, 50);

        authorsBackButton = new Rectangle(wx + 75, wy + 40, 250, 50);

        challengesBackButton = new Rectangle(wx + 75, wy + 20, 250, 50);
        challengesLeftButton = new Rectangle(wx + 75, wy + 140, 50, 50);
        challengesRightButton = new Rectangle(wx + 275, wy + 140, 50, 50);
        challengesStartButton = new Rectangle(wx + 75, wy + 60, 250, 50);

        gameOverContinueButton = new Rectangle(wx + 75, wy + 110, 250, 50);
        gameOverExitButton = new Rectangle(wx + 75, wy + 40, 250, 50);

        pauseButton = new Rectangle((viewport.getWorldWidth() - 60) / 2, viewport.getWorldHeight() - 70, 60, 60);
        pauseContinueButton = new Rectangle(wx + 75, wy + 160, 250, 50);
        pauseExitButton = new Rectangle(wx + 75, wy + 90, 250, 50);

        if (state == GameState.RUNNING && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) state = GameState.PAUSED;
        else if (state == GameState.PAUSED && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) state = GameState.RUNNING;

        if (state == GameState.RUNNING) {
            bgMusic.stop(); gameMusic.play();
            gameSpeed += 3f * delta; if (!holdTime) score += gameSpeed * delta * 0.05f;
            float moveDistance = gameSpeed * delta;
            if (isInvincible) { invincibilityTimer -= delta; if (invincibilityTimer <= 0) isInvincible = false; }
            player.update(delta, platforms);
            challengeManager.updateDistance(score);
            if (!holdTime) {
                bgX -= (moveDistance * 0.4f);
                if (bgX <= -viewport.getWorldWidth()) bgX = 0;
            }
            boolean monsterActive = !monsters.isEmpty();
            if (!monsterActive) {
                if (score >= nextMeteorSpawnScore) { spawnMeteorite(); nextMeteorSpawnScore = score + 100f + random.nextInt(200); }
                if (score >= nextLogSpawnScore) { warnings.add(new Warning(viewport.getWorldWidth() - 100f)); nextLogSpawnScore += 200f; }
            }
            if (score >= nextMonsterSpawnScore && !monsterActive) { monsters.add(new Monster(viewport.getWorldWidth() + 200, 300)); nextMonsterSpawnScore += 500f; meteorites.clear(); logs.clear(); warnings.clear(); }
            for (int i = monsters.size() - 1; i >= 0; i--) {
                Monster m = monsters.get(i); m.update(delta, player.getX(), player.getY(), viewport.getWorldWidth(), monsterProjectiles);
                Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
                Rectangle monsterRect = new Rectangle(m.getX(), m.getY(), m.getWidth(), m.getHeight());
                if (!m.isActive()) monsters.remove(i);
                else if (playerRect.overlaps(monsterRect) && player.getVelocityY() < 0 && player.getY() > m.getY() + m.getHeight() * 0.5f) { m.takeDamage(); player.setVelocityY(600f); }
            }
            for (int i = monsterProjectiles.size() - 1; i >= 0; i--) {
                MonsterProjectile mp = monsterProjectiles.get(i); mp.update(delta);
                if (mp.isOffScreen()) monsterProjectiles.remove(i);
                else if (!isInvincible && mp.getBounds().overlaps(new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight()))) { monsterProjectiles.remove(i); takeDamage(); }
            }
            for (int i = meteorites.size() - 1; i >= 0; i--) {
                Meteorite m = meteorites.get(i); m.update(delta);
                if (!isInvincible && isColliding(player, m)) { meteorites.remove(i); takeDamage(); }
                else if (m.getX() + m.getWidth() < -100) meteorites.remove(i);
            }
            for (int i = logs.size() - 1; i >= 0; i--) {
                FallingLog log = logs.get(i); log.update(delta); if (!holdTime) log.move(moveDistance);
                if (!isInvincible && isColliding(player, log)) { logs.remove(i); takeDamage(); }
                else if (log.getY() + log.getHeight() < 0) logs.remove(i);
            }
            for (int i = warnings.size() - 1; i >= 0; i--) {
                Warning w = warnings.get(i); w.update(delta);
                if (w.isFinished()) { logs.add(new FallingLog(w.getX() - 110f, viewport.getWorldHeight() + 100));  warnings.remove(i); }
            }
            if (!holdTime) {
                for (int i = platforms.size() - 1; i >= 0; i--) {
                    Platform p = platforms.get(i);
                    p.move(moveDistance);
                    if (p.getX() + p.getWidth() < 0 || p.isBroken()) { platforms.remove(i); spawnPlatform(); }
                }
                for (Warning w : warnings) w.move(moveDistance);
            } else {
                for (int i = platforms.size() - 1; i >= 0; i--) {
                    if (platforms.get(i).isBroken()) { platforms.remove(i); spawnPlatform(); }
                }
            }
            for (Platform p : platforms) p.update(delta);
            for (int i = coins.size() - 1; i >= 0; i--) {
                Coin coin = coins.get(i); if (!holdTime) coin.move(moveDistance); coin.updateTime(delta);
                if (isColliding(player, coin)) { coinSound.play(volume); coins.remove(i); point++; challengeManager.onCoinCollected(); }
                else if (coin.getX() + coin.getWidth() < 0) coins.remove(i);
            }
            if (player.getY() < -player.getHeight()) { state = GameState.GAME_OVER; checkAndSaveRecords(); }
        }

        if (Gdx.input.justTouched()) {
            Vector2 touch = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            if (state == GameState.MENU) {
                gameMusic.stop(); bgMusic.play();
                if (menuPlayButton.contains(touch.x, touch.y)) { initGame(); state = GameState.RUNNING; }
                else if (menuRecordsButton.contains(touch.x, touch.y)) state = GameState.RECORDS;
                else if (menuShopButton.contains(touch.x, touch.y)) state = GameState.SHOP;
                else if (menuChallengesButton.contains(touch.x, touch.y)) { state = GameState.CHALLENGES; selectedChallengeIndex = 0; }
                else if (menuSettingsButton.contains(touch.x, touch.y)) state = GameState.SETTINGS;
                else if (menuExitButton.contains(touch.x, touch.y)) Gdx.app.exit();
                else if (menuAuthorsButton.contains(touch.x, touch.y)) state = GameState.AUTHORS;
            }
            else if (state == GameState.RUNNING && pauseButton.contains(touch.x, touch.y)) state = GameState.PAUSED;
            else if (state == GameState.PAUSED) {
                if (pauseContinueButton.contains(touch.x, touch.y)) state = GameState.RUNNING;
                else if (pauseExitButton.contains(touch.x, touch.y)) { checkAndSaveRecords(); state = GameState.MENU; }
            }
            else if (state == GameState.RECORDS) {
                if (recordsBackButton.contains(touch.x, touch.y)) state = GameState.MENU;
                else if (recordsClearButton.contains(touch.x, touch.y)) { highScore = 0; lastScore = 0; prefs.putInteger("highScore", 0); prefs.putInteger("lastScore", 0); prefs.flush(); }
            }
            else if (state == GameState.AUTHORS) {
                if (authorsBackButton.contains(touch.x, touch.y)) state = GameState.MENU;
            }
            else if (state == GameState.CHALLENGES) {
                if (challengesBackButton.contains(touch.x, touch.y)) state = GameState.MENU;
                else if (challengesLeftButton.contains(touch.x, touch.y)) selectedChallengeIndex = (selectedChallengeIndex - 1 + challengeManager.getChallenges().size()) % challengeManager.getChallenges().size();
                else if (challengesRightButton.contains(touch.x, touch.y)) selectedChallengeIndex = (selectedChallengeIndex + 1) % challengeManager.getChallenges().size();
            }
            else if (state == GameState.SHOP) {
                if (shopBackButton.contains(touch.x, touch.y)) state = GameState.MENU;
                else if (shopNextBgButton.contains(touch.x, touch.y)) selectedBgInShop = (selectedBgInShop + 1) % bgTextures.length;
                else if (shopBuySelectButton.contains(touch.x, touch.y)) {
                    if (unlockedBgs[selectedBgInShop]) { activeBgIndex = selectedBgInShop; prefs.putInteger("activeBgIndex", activeBgIndex); prefs.flush(); }
                    else if (totalCoins >= bgPrices[selectedBgInShop]) { totalCoins -= bgPrices[selectedBgInShop]; unlockedBgs[selectedBgInShop] = true; activeBgIndex = selectedBgInShop; prefs.putInteger("totalCoins", totalCoins); prefs.putBoolean("bgUnlocked" + selectedBgInShop, true); prefs.putInteger("activeBgIndex", activeBgIndex); prefs.flush(); }
                }
            }
            else if (state == GameState.SETTINGS) {
                if (settingsLangButton.contains(touch.x, touch.y)) { isRussian = !isRussian; prefs.putBoolean("isRussian", isRussian); prefs.flush(); loadFont(); }
                else if (settingsVolumeButton.contains(touch.x, touch.y)) { volume -= 0.25f; if (volume < 0f) volume = 1.0f; prefs.putFloat("volume", volume); prefs.flush(); bgMusic.setVolume(volume); gameMusic.setVolume(volume); }
                else if (settingsBackButton.contains(touch.x, touch.y)) state = GameState.MENU;
            }
            else if (state == GameState.GAME_OVER) {
                if (gameOverContinueButton.contains(touch.x, touch.y)) { initGame(); state = GameState.RUNNING; }
                else if (gameOverExitButton.contains(touch.x, touch.y)) state = GameState.MENU;
            }
        }

        batch.begin();
        float bgWidth = viewport.getWorldWidth();
        menuStateTime += delta;

        if (state == GameState.RUNNING || state == GameState.GAME_OVER || state == GameState.PAUSED) {
            batch.draw(bgTextures[activeBgIndex], bgX, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(bgTextures[activeBgIndex], bgX + bgWidth - 2f, 0, bgWidth, viewport.getWorldHeight());
            for (Warning w : warnings) w.render(batch, warningTexture, viewport);
            for (Platform p : platforms) p.render(batch);
            for (Coin c : coins) c.render(batch);
            for (Meteorite m : meteorites) m.render(batch, meteoriteTexture);
            for (FallingLog l : logs) l.render(batch, logTexture);
            for (Monster m : monsters) m.render(batch);
            for (MonsterProjectile mp : monsterProjectiles) mp.render(batch);
            if (!isInvincible || (int)(invincibilityTimer * 10) % 2 == 0) player.render(batch);
            for (int i = 0; i < 3; i++) batch.draw((i < health) ? heartFull : heartEmpty, viewport.getWorldWidth() - 150 + (i * 45), viewport.getWorldHeight() - 60, 40, 40);
            if (state == GameState.RUNNING) {
                font.draw(batch, (isRussian ? "Очки: " : "Points: ") + point, 30, viewport.getWorldHeight() - 30);
                font.draw(batch, (isRussian ? "Счет: " : "Score: ") + (int) score, 30, viewport.getWorldHeight() - 70);
                font.draw(batch, "||", pauseButton.x + 20, pauseButton.y + 40);
            } else if (state == GameState.PAUSED) {
                batch.draw(darkOverlay, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
                batch.draw(islandWindowTexture, wx, wy, 400, 350);
                font.setColor(Color.GOLD); drawCenteredText(isRussian ? "ПАУЗА" : "PAUSE", wx, wy + 260, 400, 50); font.setColor(Color.WHITE);
                batch.draw(buttonTexture, pauseContinueButton.x, pauseContinueButton.y, pauseContinueButton.width, pauseContinueButton.height);
                batch.draw(buttonTexture, pauseExitButton.x, pauseExitButton.y, pauseExitButton.width, pauseExitButton.height);
                drawCenteredText(isRussian ? "ПРОДОЛЖИТЬ" : "CONTINUE", pauseContinueButton.x, pauseContinueButton.y, pauseContinueButton.width, pauseContinueButton.height);
                drawCenteredText(isRussian ? "МЕНЮ" : "MENU", pauseExitButton.x, pauseExitButton.y, pauseExitButton.width, pauseExitButton.height);
            } else {
                batch.draw(darkOverlay, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
                batch.draw(islandWindowTexture, wx, wy, 400, 350);
                font.setColor(Color.RED); drawCenteredText(isRussian ? "ИГРА ОКОНЧЕНА" : "GAME OVER", wx, wy + 280, 400, 50); font.setColor(Color.WHITE);
                drawCenteredText((isRussian ? "Очки: " : "Points: ") + point + (isRussian ? " | Счет: " : " | Score: ") + (int) score, wx, wy + 210, 400, 50);
                batch.draw(buttonTexture, gameOverContinueButton.x, gameOverContinueButton.y, 250, 50);
                batch.draw(buttonTexture, gameOverExitButton.x, gameOverExitButton.y, 250, 50);
                drawCenteredText(isRussian ? "ЗАНОВО" : "RESTART", gameOverContinueButton.x, gameOverContinueButton.y, 250, 50);
                drawCenteredText(isRussian ? "МЕНЮ" : "MENU", gameOverExitButton.x, gameOverExitButton.y, 250, 50);
            }
        }
        else if (state == GameState.MENU) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, 400, 450);
            font.setColor(Color.GOLD); drawCenteredText(isRussian ? "ГЛАВНОЕ МЕНЮ" : "MAIN MENU", wx, wy + 380, 400, 50); font.setColor(Color.WHITE);
            String coinText = "" + totalCoins;
            font.draw(batch, coinText, 30, viewport.getWorldHeight() - 30);
            TextureRegion curCoin = coinAnimation.getKeyFrame(menuStateTime, true);
            glyphLayout.setText(font, coinText);
            batch.draw(curCoin, 40 + glyphLayout.width, viewport.getWorldHeight() - 58, 30, 30);

            batch.draw(buttonTexture, menuAuthorsButton.x, menuAuthorsButton.y, menuAuthorsButton.width, menuAuthorsButton.height);
            drawCenteredText("A", menuAuthorsButton.x, menuAuthorsButton.y, menuAuthorsButton.width, menuAuthorsButton.height);

            Rectangle[] btns = {menuPlayButton, menuRecordsButton, menuShopButton, menuChallengesButton, menuSettingsButton, menuExitButton};
            String[] texts = {isRussian?"ИГРАТЬ":"PLAY", isRussian?"РЕКОРДЫ":"RECORDS", isRussian?"МАГАЗИН":"SHOP", isRussian?"ЧЕЛЛЕНДЖИ":"CHALLENGES", isRussian?"НАСТРОЙКИ":"SETTINGS", isRussian?"ВЫХОД":"EXIT"};
            for(int i=0; i<6; i++) { batch.draw(buttonTexture, btns[i].x, btns[i].y, 250, 50); drawCenteredText(texts[i], btns[i].x, btns[i].y, 250, 50); }
        }
        else if (state == GameState.AUTHORS) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, 400, 350);
            font.setColor(Color.GOLD); drawCenteredText(isRussian ? "АВТОРЫ ИГРЫ" : "GAME AUTHORS", wx, wy + 280, 400, 50);
            font.setColor(Color.WHITE);
            drawCenteredText("Антонов Иван", wx, wy + 210, 400, 50);
            drawCenteredText("Кулиев Низами", wx, wy + 160, 400, 50);
            batch.draw(buttonTexture, authorsBackButton.x, authorsBackButton.y, 250, 50);
            drawCenteredText(isRussian ? "НАЗАД" : "BACK", authorsBackButton.x, authorsBackButton.y, 250, 50);
        }
        else if (state == GameState.RECORDS) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, 400, 350);
            font.setColor(Color.GOLD); drawCenteredText(isRussian ? "РЕКОРДЫ" : "RECORDS", wx, wy + 280, 400, 50); font.setColor(Color.WHITE);
            drawCenteredText((isRussian ? "Посл. результат: " : "Last Score: ") + lastScore, wx, wy + 210, 400, 50);
            drawCenteredText((isRussian ? "Макс. результат: " : "Best Score: ") + highScore, wx, wy + 160, 400, 50);
            batch.draw(buttonTexture, recordsBackButton.x, recordsBackButton.y, 120, 50);
            batch.draw(buttonTexture, recordsClearButton.x, recordsClearButton.y, 120, 50);
            drawCenteredText(isRussian ? "НАЗАД" : "BACK", recordsBackButton.x, recordsBackButton.y, 120, 50);
            drawCenteredText(isRussian ? "СБРОС" : "CLEAR", recordsClearButton.x, recordsClearButton.y, 120, 50);
        }
        else if (state == GameState.CHALLENGES) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, 400, 350);
            font.setColor(Color.GOLD); drawCenteredText(isRussian ? "ЧЕЛЛЕНДЖИ" : "CHALLENGES", wx, wy + 280, 400, 50); font.setColor(Color.WHITE);

            Challenge ch = challengeManager.getChallenges().get(selectedChallengeIndex);
            drawCenteredText(ch.getName(), wx, wy + 220, 400, 50);
            drawCenteredText(ch.getDescription(), wx, wy + 180, 400, 40);

            font.setColor(Color.YELLOW);
            drawCenteredText((int)ch.getCurrentProgress() + " / " + (int)ch.getTargetValue(), wx, wy + 130, 400, 40);
            font.setColor(Color.WHITE);

            drawProgressBar(wx + 50, wy + 110, 300, 15, ch.getProgressPercentage());

            if (ch.isCompleted()) {
                font.setColor(Color.GREEN);
                drawCenteredText(isRussian ? "ВЫПОЛНЕНО!" : "COMPLETED!", wx, wy + 85, 400, 30);
                font.setColor(Color.WHITE);
            }

            batch.draw(buttonTexture, challengesLeftButton.x, challengesLeftButton.y, challengesLeftButton.width, challengesLeftButton.height);
            batch.draw(buttonTexture, challengesRightButton.x, challengesRightButton.y, challengesRightButton.width, challengesRightButton.height);
            drawCenteredText("<", challengesLeftButton.x, challengesLeftButton.y, challengesLeftButton.width, challengesLeftButton.height);
            drawCenteredText(">", challengesRightButton.x, challengesRightButton.y, challengesRightButton.width, challengesRightButton.height);

            batch.draw(buttonTexture, challengesBackButton.x, challengesBackButton.y, 250, 50);
            drawCenteredText(isRussian ? "НАЗАД" : "BACK", challengesBackButton.x, challengesBackButton.y, 250, 50);
        }
        else if (state == GameState.SHOP) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, 400, 450);
            font.setColor(Color.GOLD); drawCenteredText(isRussian ? "МАГАЗИН" : "SHOP", wx, wy + 380, 400, 50); font.setColor(Color.WHITE);
            String coinText = "" + totalCoins;
            font.draw(batch, coinText, 30, viewport.getWorldHeight() - 30);
            glyphLayout.setText(font, coinText);
            batch.draw(coinAnimation.getKeyFrame(menuStateTime, true), 40 + glyphLayout.width, viewport.getWorldHeight() - 58, 30, 30);
            batch.draw(bgTextures[selectedBgInShop], wx + 100, wy + 210, 200, 100);
            batch.draw(buttonTexture, shopNextBgButton.x, shopNextBgButton.y, 250, 50);
            drawCenteredText(isRussian ? "СЛЕДУЮЩИЙ" : "NEXT", shopNextBgButton.x, shopNextBgButton.y, 250, 50);
            batch.draw(buttonTexture, shopBuySelectButton.x, shopBuySelectButton.y, 250, 50);
            String bText = unlockedBgs[selectedBgInShop] ? (selectedBgInShop == activeBgIndex ? (isRussian?"ВЫБРАНО":"SELECTED") : (isRussian?"ВЫБРАТЬ":"SELECT")) : (isRussian?"КУПИТЬ: ":"BUY: ") + bgPrices[selectedBgInShop];
            drawCenteredText(bText, shopBuySelectButton.x, shopBuySelectButton.y, 250, 50);
            batch.draw(buttonTexture, shopBackButton.x, shopBackButton.y, 250, 50);
            drawCenteredText(isRussian ? "НАЗАД" : "BACK", shopBackButton.x, shopBackButton.y, 250, 50);
        }
        else if (state == GameState.SETTINGS) {
            batch.draw(backgroundMenu, 0, 0, bgWidth, viewport.getWorldHeight());
            batch.draw(islandWindowTexture, wx, wy, 400, 350);
            font.setColor(Color.GOLD); drawCenteredText(isRussian ? "НАСТРОЙКИ" : "SETTINGS", wx, wy + 280, 400, 50); font.setColor(Color.WHITE);
            batch.draw(buttonTexture, settingsLangButton.x, settingsLangButton.y, 250, 50);
            batch.draw(buttonTexture, settingsVolumeButton.x, settingsVolumeButton.y, 250, 50);
            batch.draw(buttonTexture, settingsBackButton.x, settingsBackButton.y, 250, 50);
            drawCenteredText(isRussian ? "ЯЗЫК: РУС" : "LANG: ENG", settingsLangButton.x, settingsLangButton.y, 250, 50);
            drawCenteredText((isRussian ? "ЗВУК: " : "VOL: ") + (int)(volume * 100) + "%", settingsVolumeButton.x, settingsVolumeButton.y, 250, 50);
            drawCenteredText(isRussian ? "НАЗАД" : "BACK", settingsBackButton.x, settingsBackButton.y, 250, 50);
        }
        batch.end();
    }

    private boolean isColliding(Character player, Coin coin) {
        return player.getX() < coin.getX() + coin.getWidth() && player.getX() + player.getWidth() > coin.getX() && player.getY() < coin.getY() + coin.getHeight() && player.getY() + player.getHeight() > coin.getY();
    }
    private boolean isColliding(Character player, Meteorite m) {
        float s = 12f; return player.getX() < m.getX() + m.getWidth() - s && player.getX() + player.getWidth() > m.getX() + s && player.getY() < m.getY() + m.getHeight() - s && player.getY() + player.getHeight() > m.getY() + s;
    }
    private boolean isColliding(Character player, FallingLog log) {
        float s = 12f; return player.getX() < log.getX() + log.getWidth() - s && player.getX() + player.getWidth() > log.getX() + s && player.getY() < log.getY() + log.getHeight() - s && player.getY() + player.getHeight() > log.getY() + s;
    }
    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void dispose() {
        batch.dispose(); for (Texture t : bgTextures) t.dispose(); backgroundMenu.dispose(); meteoriteTexture.dispose();
        darkOverlay.dispose(); islandWindowTexture.dispose(); buttonTexture.dispose(); Animations.dispose();
        Platform.dispose(); Monster.dispose(); player.dispose(); font.dispose(); coinSound.dispose(); Coin.dispose();
    }
}
