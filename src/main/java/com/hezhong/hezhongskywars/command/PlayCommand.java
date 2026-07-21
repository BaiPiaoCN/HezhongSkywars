package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.game.GameStatus;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayCommand extends HezhongSkywarsCommand {
    public PlayCommand() {
        super("play", false, "<mapName>", "游玩一个地图");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        if (cs instanceof Player) {
            if (args.length < 2) {
                cs.sendMessage(ColorT.t("&c参数不足"));
                return;
            }
            Player p = (Player) cs;
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            Game game = HezhongSkywars.INSTANCE.getGameManager().getGames().get(args[1]);
            if (game == null) {
                cs.sendMessage(ColorT.t("&c地图不存在"));
                return;
            }
            if (game.getGameStatus() == GameStatus.RESETTING) {
                cs.sendMessage(ColorT.t("&c地图正在重置"));
                return;
            }
            sp.joinGame(game);
            cs.sendMessage(ColorT.t("&a把你发送到游戏 " + args[1]));
        } else {
            cs.sendMessage(ColorT.t("&c仅限玩家操作！"));
        }
    }
}
