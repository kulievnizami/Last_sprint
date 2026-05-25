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
        System.out.println(distanceTraveled);


        if (activeChallengeThisRun != null && !activeChallengeThisRun.isCompleted()) {

            switch (activeChallengeThisRun.getType()) {

                case NO_COINS:
                    if (coinsCollectedThisRun) {
                        activeChallengeThisRun = null;
                    } else {
                        activeChallengeThisRun.updateProgress(distanceTraveled);
                    }
                    break;

                case NO_DOUBLE_JUMP:
                    if (doubleJumpUsedThisRun) {
                        activeChallengeThisRun = null;
                    } else {
                        activeChallengeThisRun.updateProgress(distanceTraveled);
                    }
                    break;

                case SPEED_RUN:
                    activeChallengeThisRun.updateProgress(distanceTraveled);
                    break;
            }
        }
    }

    public void onCoinCollected() {
        coinsCollectedThisRun = true;

        if (activeChallengeThisRun != null &&
            activeChallengeThisRun.getType() == Challenge.ChallengeType.NO_COINS) {

            activeChallengeThisRun = null;
        }
    }


    public void onDoubleJumpUsed() {
        doubleJumpUsedThisRun = true;

        if (activeChallengeThisRun != null &&
            activeChallengeThisRun.getType() == Challenge.ChallengeType.NO_DOUBLE_JUMP) {

            activeChallengeThisRun = null;
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
