package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand extends HezhongSkywarsCommand {
    public HubCommand() {
        super("hub", false, "", "返回大厅");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(ColorT.t("&c仅限玩家操作！"));
            return;
        }
        Player pp = (Player) cs;
        SwPlayer sp = SwPlayerManager.getPlayer(pp);
        pp.teleport(HezhongSkywars.INSTANCE.getLobbySpawnLocation());
        sp.setPlayingGame(null);
        pp.sendMessage(ColorT.t("&a传送到大厅......"));
    }
}
