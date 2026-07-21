package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.config.MapConfig;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ModifyCommand extends HezhongSkywarsCommand {
    public ModifyCommand() {
        super("modify", true, "<mapName> [options] [args]", "修改地图配置");
    }

    @Override
    public void runCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(ColorT.t("&c仅限玩家操作！"));
            return;
        }
        if (args.length < 2) {
            cs.sendMessage(ColorT.t("&c命令参数不足"));
            return;
        }
        Player pp = (Player) cs;
        SwPlayer sp = SwPlayerManager.getPlayer(pp);
        String mapName = args[1];
        if (!ConfigValues.mapConfigs.containsKey(mapName)) {
            pp.sendMessage(ColorT.t("&c查无此图。请先创建地图！"));
            return;
        }
        MapConfig mc = ConfigValues.mapConfigs.get(mapName);
        String world = mc.getCopyWorld();
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitWorld == null) {
            pp.sendMessage(ColorT.t("&c没有世界！"));
            return;
        }
        if (args.length == 2) {
            if (sp.isSettingUpMap()) {
                pp.sendMessage(ColorT.t("&c你需要先退出当前对 &e" + sp.getSetUpMapName() + " &c地图的编辑！"));
                pp.sendMessage(ColorT.t("&e退出：&a /hsw modify " + sp.getSetUpMapName() + " exit"));
                return;
            }
            sp.setSettingUpMap(true);
            sp.setSetUpMapName(mapName);
            sp.getSetupMapStatus().setChests(new LinkedHashMap<>(mc.getChests()));
            sp.getSetupMapStatus().setSpawns(mc.getSpawns());
            pp.sendMessage(ColorT.t("&a正在将你传送到 " + world + "......"));
            pp.teleport(new Location(bukkitWorld, 0, 100, 0));
            return;
        }
        if (args.length >= 3 && sp.isSettingUpMap()) {
            if (!Objects.equals(pp.getWorld().getName(), ConfigValues.mapConfigs.get(sp.getSetUpMapName()).getCopyWorld())) {
                pp.sendMessage(ColorT.t("&c你所在的世界不是你正在修改的世界，你在 " + pp.getWorld().getName() + ", 修改 " + sp.getSetUpMapName()));
                return;
            }
            String opt = args[2];
            if (Objects.equals(opt, "exit")) {
                pp.sendMessage(ColorT.t("&a&l正在保存"));
                sp.setSettingUpMap(false);
                HezhongSkywars.INSTANCE.getConfigManager().setUpMap(sp.getSetUpMapName(), sp.getSetupMapStatus().getSpawns(), new HashMap<>(sp.getSetupMapStatus().getChests()));
                sp.setSetUpMapName("");
                pp.sendMessage(ColorT.t("&a&l保存完成！"));
            } else if (Objects.equals(opt, "editChest")) {
                if (args.length < 4) {
                    pp.sendMessage(ColorT.t("&c参数不足"));
                    return;
                }
                String type = args[3];
                if (sp.getSetupMapStatus().getControllingBlock() != null) {
                    if (sp.getSetupMapStatus().getControllingBlock().getState() instanceof Chest) {
                        Vector pos = sp.getSetupMapStatus().getControllingBlock().getLocation().toVector();
                        sp.getSetupMapStatus().getChests().put(pos, type);
                        pp.sendMessage(ColorT.t("&a设置箱子 X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ()));
                    } else {
                        pp.sendMessage(ColorT.t("&c你选择的方块不是箱子，是 &e" + sp.getSetupMapStatus().getControllingBlock().getType()));
                    }
                }
            } else if (Objects.equals(opt, "rmChest")) {
                if (sp.getSetupMapStatus().getControllingBlock() != null) {
                    if (sp.getSetupMapStatus().getControllingBlock().getState() instanceof Chest) {
                        Vector pos = sp.getSetupMapStatus().getControllingBlock().getLocation().toVector();
                        sp.getSetupMapStatus().getChests().remove(pos);
                        pp.sendMessage(ColorT.t("&a删除箱子 X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ()));
                    } else {
                        pp.sendMessage(ColorT.t("&c你选择的方块不是箱子，是 &e" + sp.getSetupMapStatus().getControllingBlock().getType()));
                    }
                }
            } else if (Objects.equals(opt, "listChest")) {
                pp.sendMessage(ColorT.t("&a&l所有箱子"));
                int id = 1;
                for (Map.Entry<Vector, String> entry : sp.getSetupMapStatus().getChests().entrySet()) {
                    Vector v = entry.getKey();
                    String type = entry.getValue();
                    pp.sendMessage(ColorT.t("&a箱子 #" + id + " &eX=" + v.getX() + " Y="  + v.getY() + " Z=" + v.getZ() + " &a类型 &e" + type));
                    id++;
                }
            } else if (Objects.equals(opt, "addSpawn")) {
                Vector pos = pp.getLocation().toVector();
                sp.getSetupMapStatus().getSpawns().add(pos);
                pp.sendMessage(ColorT.t(String.format("&a设置出生点 X=%.2f Y=%.2f Z=%.2f", pos.getX(), pos.getY(), pos.getZ())));
            } else if  (Objects.equals(opt, "rmSpawn")) {
                if (args.length < 4) {
                    pp.sendMessage(ColorT.t("&c参数不足"));
                    return;
                }
                String idStr = args[3];
                int id = -1;
                try {
                    id = Integer.parseInt(idStr);
                    sp.getSetupMapStatus().getSpawns().remove(id - 1);
                    pp.sendMessage(ColorT.t("&a删除出生点 ID=" + id));
                } catch (NumberFormatException e) {
                    pp.sendMessage(ColorT.t("&cID不是数字！"));
                } catch (Exception e) {
                    pp.sendMessage(ColorT.t("&c发生错误！"));
                    e.printStackTrace();
                }
            } else if (Objects.equals(opt, "listSpawn")) {
                int id = 1;
                for (Vector pos : sp.getSetupMapStatus().getSpawns()) {
                    pp.sendMessage(ColorT.t(String.format("&a出生点 #%s X=%.2f Y=%.2f Z=%.2f", id, pos.getX(), pos.getY(), pos.getZ())));
                    id++;
                }
            }
        }
    }
}
