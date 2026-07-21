package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.gui.HezhongSkywarsGUI;
import com.hezhong.hezhongskywars.gui.impl.KitSelectGUI;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand extends HezhongSkywarsCommand {
    public GuiCommand() {
        super("gui", false, "<guiName: kitSelector>", "打开GUI");
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
            HezhongSkywarsGUI hswGUI = null;
            if (gui.equals("kitSelector")) {
                hswGUI = new KitSelectGUI(p);
            }
            if (hswGUI != null) {
                hswGUI.open();
            }
        }
    }
}
