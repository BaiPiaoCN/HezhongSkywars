package com.hezhong.hezhongskywars.game;

import com.hezhong.hezhongskywars.utils.bukkit.CTask;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameTaskManager {
    private CTask countdownTask;
    private CTask eventRunnerTask;
    private CTask checkIfNoPlayer;
    private final Game game;
    public GameTaskManager(Game game) {
        this.game = game;
    }

    public void stopAll() {
        if (countdownTask != null) countdownTask.cancel();
        if (eventRunnerTask != null) eventRunnerTask.cancel();
        if (checkIfNoPlayer != null) checkIfNoPlayer.cancel();
    }
}
