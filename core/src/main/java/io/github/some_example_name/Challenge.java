package io.github.some_example_name;

public class Challenge {

    private String nameRu;
    private String nameEn;
    private String descriptionRu;
    private String descriptionEn;
    private float targetValue;
    private float currentProgress;
    private boolean completed;
    private int reward;

    public Challenge(String nameRu, String nameEn, String descriptionRu, String descriptionEn, float targetValue, int reward) {
        this.nameRu = nameRu;
        this.nameEn = nameEn;
        this.descriptionRu = descriptionRu;
        this.descriptionEn = descriptionEn;
        this.targetValue = targetValue;
        this.reward = reward;
        this.currentProgress = 0;
        this.completed = false;
    }

    public void updateProgress(float value) {
        if (completed) return;
        this.currentProgress = value;
        if (currentProgress >= targetValue) {
            completed = true;
            currentProgress = targetValue;
        }
    }
    public void resetRun() {
        currentProgress = 0;
    }


    public void reset() {
        currentProgress = 0;
        completed = false;
    }

    public void markCompleted() {
        this.completed = true;
        this.currentProgress = targetValue;
    }

    public boolean isCompleted() { return completed; }
    public float getTargetValue() { return targetValue; }
    public float getCurrentProgress() { return currentProgress; }
    public String getName(boolean isRussian) { return isRussian ? nameRu : nameEn; }
    public String getDescription(boolean isRussian) { return isRussian ? descriptionRu : descriptionEn; }
    public int getReward() { return reward; }
}
