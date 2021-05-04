package gg.solarmc.kitpvp.kill.result;

/**
 * Result used for text display. None of these values are to be relied on for accuracy, but should be accurate due to being simply cached versions of latest values.
 */
public class KillResult {

    private final boolean isLevelUp;
    private final int killedKillstreak;
    private final int killerKillstreak;


    private final int rewardAmount;
    private final int assistAmount;

    public KillResult(boolean isLevelUp, int killedKillstreak, int killerKillstreak, int rewardAmount, int assistAmount) {
        this.isLevelUp = isLevelUp;
        this.killedKillstreak = killedKillstreak;
        this.killerKillstreak = killerKillstreak;
        this.rewardAmount = rewardAmount;
        this.assistAmount = assistAmount;
    }

    public boolean isLevelUp() {
        return isLevelUp;
    }

    public int getKillerKillstreak() {
        return killerKillstreak;
    }

    public int getKilledKillstreak() {
        return killedKillstreak;
    }

    public boolean isEndedKillstreak() {
        return killedKillstreak > 10;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public int getAssistAmount() {
        return assistAmount;
    }

}
