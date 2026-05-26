package io.github.some_example_name;

import java.util.ArrayList;

public class ChallengeManager {

    private ArrayList<Challenge> challenges;
    private float distanceTraveled;
    private Challenge activeChallengeThisRun;
    private Challenge pendingChallenge;

    public ChallengeManager() {
        challenges = new ArrayList<>();
        initializeChallenges();
        activeChallengeThisRun = null;
        pendingChallenge = null;
        distanceTraveled = 0;
    }

    private void initializeChallenges() {
        challenges.clear();
        challenges.add(new Challenge("Новичок",  "Beginner",  "Пройди 500 м",   "Travel 500 m",   500f,  50));
        challenges.add(new Challenge("Любитель", "Amateur",   "Пройди 1000 м",  "Travel 1000 m",  1000f, 60));
        challenges.add(new Challenge("Опытный",  "Experienced","Пройди 2000 м", "Travel 2000 m",  2000f, 70));
        challenges.add(new Challenge("Мастер",   "Master",    "Пройди 4000 м",  "Travel 4000 m",  4000f, 80));
    }

    public void scheduleChallenge(Challenge challenge) {
        pendingChallenge = challenge;
    }
    public void applyPendingChallenge() {
        if (pendingChallenge != null) {
            activeChallengeThisRun = pendingChallenge;
            activeChallengeThisRun.resetRun();
            pendingChallenge = null;
        } else {
            activeChallengeThisRun = null;
        }
        distanceTraveled = 0;
    }

    public void updateDistance(float score) {
        distanceTraveled = score;

        if (activeChallengeThisRun != null && !activeChallengeThisRun.isCompleted()) {
            activeChallengeThisRun.updateProgress(distanceTraveled);
        }
    }

    public boolean isChallengeActive() {
        return activeChallengeThisRun != null;
    }

    public Challenge getActiveChallengeThisRun() {
        return activeChallengeThisRun;
    }

    public ArrayList<Challenge> getChallenges() {
        return challenges;
    }

    public float getDistanceTraveled() {
        return distanceTraveled;
    }
}
