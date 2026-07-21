package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VersionCommand extends HezhongSkywarsCommand {
    public VersionCommand() {
        super("ver", true, "", "显示插件版本和信息");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        cs.sendMessage(ColorT.t("&b&lHezhong Skywars &eBy Hezhong Technology <== tjshawa"));
        cs.sendMessage(ColorT.t("&bHSW 版本 " + HezhongSkywars.INSTANCE.getPlugin().getDescription().getVersion()));
    }
}
