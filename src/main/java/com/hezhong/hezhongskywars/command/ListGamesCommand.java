package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.game.GameStatus;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class ListGamesCommand extends HezhongSkywarsCommand {
    public ListGamesCommand() {
        super("listGames", false, "", "列出游戏");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        cs.sendMessage(ColorT.t("&a游戏"));
        for (Map.Entry<String, Game> entry : HezhongSkywars.INSTANCE.getGameManager().getGames().entrySet()) {
            String mapName = entry.getKey();
            Game g = entry.getValue();
            GameStatus status = g.getGameStatus();
            cs.sendMessage(ColorT.t(String.format("&b游戏 &7%s &b状态 &7%s&b 人数 &7%s/%s", mapName, status, g.getAlivePlayers().size(), g.getMaxPlayers())));
        }
    }
}
