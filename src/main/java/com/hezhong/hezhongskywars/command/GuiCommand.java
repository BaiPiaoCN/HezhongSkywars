package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.gui.HezhongSkywarsGUI;
import com.hezhong.hezhongskywars.gui.impl.HubGUI;
import com.hezhong.hezhongskywars.gui.impl.KitSelectGUI;
import com.hezhong.hezhongskywars.gui.impl.SelectMapGUI;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand extends HezhongSkywarsCommand {
    public GuiCommand() {
        super("gui", false, "<guiName:kitSelector,mapSelector,main>", "打开GUI");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        if (args.length < 2) {
            cs.sendMessage(ColorT.t("&c命令参数不足"));
            return;
        }
        String gui = args[1];
        if (cs instanceof Player) {
            Player p = (Player) cs;
            SwPlayer sp = SwPlayerManager.getPlayer(p);
            if (sp != null) {
                HezhongSkywarsGUI hswGUI = null;
                if (gui.equalsIgnoreCase("kitSelector")) {
                    hswGUI = new KitSelectGUI(p, sp);
                }
                if (gui.equalsIgnoreCase("main")) {
                    hswGUI = new HubGUI(p, sp);
                }
                if (gui.equalsIgnoreCase("mapSelector")) {
                    hswGUI = new SelectMapGUI(p, sp);
                }
                if (hswGUI != null) {
                    hswGUI.open();
                }
            }
        }
    }
}
