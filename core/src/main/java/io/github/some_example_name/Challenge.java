package io.github.some_example_name;

public class Challenge {
    public enum ChallengeType { NO_COINS, NO_DOUBLE_JUMP, SPEED_RUN }
    
    private String name;
    private String description;
    private ChallengeType type;
    private float targetValue;
    private float currentProgress;
    private boolean completed;
    private int reward;

    public Challenge(String name, String description, ChallengeType type, float targetValue, int reward) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetValue = targetValue;
        this.reward = reward;
        this.currentProgress = 0;
        this.completed = false;
    }

    public void updateProgress(float value) {
        this.currentProgress = value;
        if (currentProgress >= targetValue) {
            completed = true;
        }
    }

    public void reset() {
        currentProgress = 0;
        completed = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ChallengeType getType() { return type; }
    public float getTargetValue() { return targetValue; }
    public float getCurrentProgress() { return currentProgress; }
    public boolean isCompleted() { return completed; }
    public int getReward() { return reward; }
    public float getProgressPercentage() { return Math.min(currentProgress / targetValue, 1f); }
}
