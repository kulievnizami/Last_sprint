package io.github.some_example_name;

import java.util.ArrayList;

public class ChallengeManager {
    private ArrayList<Challenge> challenges;
    private float distanceTraveled;
    private boolean coinsCollectedThisRun;
    private boolean doubleJumpUsedThisRun;

    public ChallengeManager() {
        challenges = new ArrayList<>();
        initializeChallenges();
        resetProgress();
    }

    private void initializeChallenges() {
        challenges.add(new Challenge(
            "Без монет",
            "Пройти 500 м не взяв ни одной монеты",
            Challenge.ChallengeType.NO_COINS,
            50f,
            50
        ));

        challenges.add(new Challenge(
            "Без двойного прыжка",
            "Доберись до 2000 м не используя двойной прыжок",
            Challenge.ChallengeType.NO_DOUBLE_JUMP,
            2000f,
            100
        ));

        challenges.add(new Challenge(
            "Спринт",
            "Пройди 1000 м за 60 секунд",
            Challenge.ChallengeType.SPEED_RUN,
            1000f,
            75
        ));
    }

    public void resetProgress() {
        distanceTraveled = 0;
        coinsCollectedThisRun = false;
        doubleJumpUsedThisRun = false;
    }

    public void updateDistance(float score) {
        distanceTraveled = score / 10f;

        for (Challenge c : challenges) {
            if (c.getType() == Challenge.ChallengeType.NO_COINS) {
                if (!coinsCollectedThisRun) {
                    c.updateProgress(distanceTraveled);
                }
            } else if (c.getType() == Challenge.ChallengeType.NO_DOUBLE_JUMP) {
                if (!doubleJumpUsedThisRun) {
                    c.updateProgress(distanceTraveled);
                }
            } else if (c.getType() == Challenge.ChallengeType.SPEED_RUN) {
                c.updateProgress(distanceTraveled);
            }
        }
    }

    public void onCoinCollected() {
        coinsCollectedThisRun = true;
        Challenge noCoinChallenge = challenges.get(0);
        if (!noCoinChallenge.isCompleted()) {
            noCoinChallenge.reset();
        }
    }

    public void onDoubleJumpUsed() {
        doubleJumpUsedThisRun = true;
        Challenge noDoubleJumpChallenge = challenges.get(1);
        if (!noDoubleJumpChallenge.isCompleted()) {
            noDoubleJumpChallenge.reset();
        }
    }

    public void startNewRun() {
        resetProgress();
        for (Challenge c : challenges) {
            if (!c.isCompleted()) {
                c.reset();
            }
        }
    }

    public ArrayList<Challenge> getChallenges() { return challenges; }
    public float getDistanceTraveled() { return distanceTraveled; }
}
