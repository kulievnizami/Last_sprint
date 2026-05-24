package io.github.some_example_name;

import java.util.ArrayList;

public class ChallengeManager {
    private ArrayList<Challenge> challenges;
    private float distanceTraveled;
    private boolean coinsCollectedThisRun;
    private boolean doubleJumpUsedThisRun;
    private Challenge activeChallengeThisRun;

    public ChallengeManager() {
        challenges = new ArrayList<>();
        initializeChallenges();
        resetProgress();
    }

    private void initializeChallenges() {
        challenges.add(new Challenge(
            "БЕЗ МОНЕТ",
            "Пройти 50м не собирая монеты",
            Challenge.ChallengeType.NO_COINS,
            50f,
            100
        ));

        challenges.add(new Challenge(
            "БЕЗ ДВОЙНОГО ПРЫЖКА",
            "Пройти 100м без двойного прыжка",
            Challenge.ChallengeType.NO_DOUBLE_JUMP,
            100f,
            150
        ));

        challenges.add(new Challenge(
            "СПРИНТ",
            "Пройти 150м за время",
            Challenge.ChallengeType.SPEED_RUN,
            150f,
            200
        ));
    }

    public void resetProgress() {
        distanceTraveled = 0;
        coinsCollectedThisRun = false;
        doubleJumpUsedThisRun = false;
        activeChallengeThisRun = null;
    }

    public void startChallenge(Challenge challenge) {
        activeChallengeThisRun = challenge;
        challenge.reset();
        resetProgress();
    }

    public void updateDistance(float score) {
        distanceTraveled = score / 10f;

        if (activeChallengeThisRun != null && !activeChallengeThisRun.isCompleted()) {
            if (activeChallengeThisRun.getType() == Challenge.ChallengeType.NO_COINS) {
                if (!coinsCollectedThisRun) {
                    activeChallengeThisRun.updateProgress(distanceTraveled);
                }
            } else if (activeChallengeThisRun.getType() == Challenge.ChallengeType.NO_DOUBLE_JUMP) {
                if (!doubleJumpUsedThisRun) {
                    activeChallengeThisRun.updateProgress(distanceTraveled);
                }
            } else if (activeChallengeThisRun.getType() == Challenge.ChallengeType.SPEED_RUN) {
                activeChallengeThisRun.updateProgress(distanceTraveled);
            }
        }
    }

    public void onCoinCollected() {
        coinsCollectedThisRun = true;
        Challenge noCoinChallenge = challenges.get(0);
        if (activeChallengeThisRun == noCoinChallenge && !noCoinChallenge.isCompleted()) {
            noCoinChallenge.reset();
        }
    }

    public void onDoubleJumpUsed() {
        doubleJumpUsedThisRun = true;
        Challenge noDoubleJumpChallenge = challenges.get(1);
        if (activeChallengeThisRun == noDoubleJumpChallenge && !noDoubleJumpChallenge.isCompleted()) {
            noDoubleJumpChallenge.reset();
        }
    }

    public boolean isChallengeActive() {
        return activeChallengeThisRun != null;
    }

    public Challenge getActiveChallengeThisRun() {
        return activeChallengeThisRun;
    }

    public ArrayList<Challenge> getChallenges() { return challenges; }
    public float getDistanceTraveled() { return distanceTraveled; }
}
