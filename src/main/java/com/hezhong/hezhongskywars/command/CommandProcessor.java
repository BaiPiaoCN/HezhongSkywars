package com.hezhong.hezhongskywars.command;

import com.hezhong.hezhongskywars.HezhongSkywars;
import com.hezhong.hezhongskywars.config.ConfigManager;
import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.config.MapConfig;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class CommandProcessor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command command, String label, String[] args) {
        if (!cs.hasPermission("hsw.command")) {
            return false;
        }
        if (args.length < 1) {
            cs.sendMessage(ColorT.t("&c命令参数不足"));
            return true;
        }
        if (Objects.equals(args[0], "help")) {
            processHelp(cs);
        }
        if (Objects.equals(args[0], "ver")) {
            processVersion(cs);
        }
        if (Objects.equals(args[0], "create")) {
            processCreate(cs, args);
        }
        if (Objects.equals(args[0], "modify")) {
            processModify(cs, args);
        }
        if (Objects.equals(args[0], "play")) {
            processPlay(cs, args);
        }
        if (Objects.equals(args[0], "start")) {
            processStart(cs);
        }

        return true;
    }

    private void processHelp(CommandSender cs) {
        cs.sendMessage(ColorT.t("&b&l Hezhong Skywars 命令帮助"));
        cs.sendMessage(ColorT.t("&c&l <>为必选参数，[]为可选参数"));
        cs.sendMessage(ColorT.t("&e /hsw help &a显示帮助"));
        cs.sendMessage(ColorT.t("&e /hsw ver &a显示插件版本和信息"));
        cs.sendMessage(ColorT.t("&e /hsw create <mapName> <originalWorldName> <copyWorldName> &a新建新地图"));
        cs.sendMessage(ColorT.t("&e /hsw modify <mapName> [options] [args] &a修改地图配置"));
        cs.sendMessage(ColorT.t("&e /hsw play <mapName> &a游玩一个地图"));
        cs.sendMessage(ColorT.t("&e /hsw start &a启动你正在游玩的地图"));
        if (cs instanceof Player) {
            Player pp = (Player) cs;
            SwPlayer sp = SwPlayerManager.getPlayer(pp);
            if (sp != null) {
                if (sp.isSettingUpMap()) {
                    cs.sendMessage(ColorT.t("&a&b正在修改地图 " + sp.getSetUpMapName()));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " &a帮助"));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " exit &a推出修改模式&a&l并保存"));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " editChest <chestType> &a修改或添加箱子"));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " rmChest &a删除箱子"));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " listChest &a列出箱子"));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " addSpawn &a增加出生点"));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " rmSpawn <id> &a删除出生点"));
                    cs.sendMessage(ColorT.t("&e /hsw modify " + sp.getSetUpMapName() + " listSpawn &a列出出生点"));
                }
            }
        }
    }
    private void processVersion(CommandSender cs) {
        cs.sendMessage(ColorT.t("&b&lHezhong Skywars &eBy Hezhong Technology <== tjshawa"));
        cs.sendMessage(ColorT.t("&b版本 " + HezhongSkywars.INSTANCE.getPlugin().getDescription().getVersion()));
    }
    private void processCreate(CommandSender cs, String[] args) {
        if (args.length < 4) {
            cs.sendMessage(ColorT.t("&c命令参数不足"));
            return;
        }
        final String mapName = args[1];
        final String original = args[2];
        final String copy = args[3];
        HezhongSkywars.INSTANCE.getConfigManager().createMap(mapName, original, copy);
        cs.sendMessage(ColorT.t("&a地图 " + mapName + "已在配置文件中创建。正在创建世界......"));
        Bukkit.getScheduler().runTaskAsynchronously(HezhongSkywars.INSTANCE.getPlugin(), () -> {
            try {
                HezhongSkywars.INSTANCE.getGameManager().resetGame(mapName);
                // 我靠，一定记得是mapName，不是copy！
                // Deepseek找Bug神力来了
                cs.sendMessage(ColorT.t("&a&l世界创建成功，游戏已加载！"));
            } catch (Exception e) {
                cs.sendMessage(ColorT.t("&c&l地图创建失败，请查看内部报错"));
                HezhongSkywars.INSTANCE.getLogger().warning("HSW Failed to reset game");
                e.printStackTrace();
            }
        });

    }
    private void processModify(CommandSender cs, String[] args) {
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
            // 必须初始化，因为重写是完全覆盖的！
            sp.getSetupMapStatus().setChests(new LinkedHashMap<>(mc.getChests()));
            sp.getSetupMapStatus().setSpawns(mc.getSpawns());
            pp.sendMessage(ColorT.t("&a正在将你传送到 " + world + "......"));
            pp.teleport(new Location(bukkitWorld, 0, 100, 0));
            return;
        }
        if (args.length >= 3) {
            if (!Objects.equals(pp.getWorld().getName(), ConfigValues.mapConfigs.get(sp.getSetUpMapName()).getCopyWorld())) {
                pp.sendMessage(ColorT.t("&c你所在的世界不是你正在修改的世界，你在 " + pp.getWorld().getName() + ", 修改 " + sp.getSetUpMapName()));
                return;
            }
            String opt = args[2];
            // 说来话长
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
                if (sp.getSetupMapStatus().getControllingBlock() instanceof Chest) {
                    // Bukkit chest
                    // 设置一下
                    Vector pos = sp.getSetupMapStatus().getControllingBlock().getLocation().toVector();
                    sp.getSetupMapStatus().getChests().put(pos, type);
                    pp.sendMessage(ColorT.t("&a设置箱子 X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ()));
                }

            } else if (Objects.equals(opt, "rmChest")) {
                if (sp.getSetupMapStatus().getControllingBlock() instanceof Chest) {
                    // Bukkit chest
                    // 设置一下
                    Vector pos = sp.getSetupMapStatus().getControllingBlock().getLocation().toVector();
                    sp.getSetupMapStatus().getChests().remove(pos);
                    pp.sendMessage(ColorT.t("&a删除箱子 X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ()));
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
                } catch (NumberFormatException e) {
                    pp.sendMessage(ColorT.t("&cID不是数字！"));
                    return;
                } catch (Exception e) {
                    pp.sendMessage(ColorT.t("&c发生错误！"));
                    e.printStackTrace();
                    return;
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
    private void processPlay(CommandSender cs, String[] args) {}
    private void processStart(CommandSender cs) {}
}
