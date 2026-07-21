package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectKitCommand extends HezhongSkywarsCommand {
    public SelectKitCommand() {
        super("selectKit", false, "<kitName>", "选择职业");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        if (args.length < 2) {
            cs.sendMessage(ColorT.t("&c命令参数不足"));
            return;
        }
        String kitName = args[1];
        if (cs instanceof Player) {
            Player p = (Player) cs;
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            if (sp.getPlayingGame() != null) {
                Game playing = sp.getPlayingGame();
                if (ConfigValues.kitConfigs.containsKey(kitName)) {
                    playing.setPlayerKit(p, kitName);
                    p.sendMessage(ColorT.t("&a&l你选择了职业：" + kitName));
                }
            }
        }
    }
}
