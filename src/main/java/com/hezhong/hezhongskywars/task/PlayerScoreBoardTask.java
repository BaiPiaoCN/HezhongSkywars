package com.hezhong.hezhongskywars.task;

import com.hezhong.hezhongskywars.config.ConfigValues;
import com.hezhong.hezhongskywars.game.Game;
import com.hezhong.hezhongskywars.game.GameEvent;
import com.hezhong.hezhongskywars.game.GameStatus;
import com.hezhong.hezhongskywars.game.SwPlayingGamePlayer;
import com.hezhong.hezhongskywars.manager.SwPlayerManager;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

// 我去强强AI代码
public class PlayerScoreBoardTask extends BukkitRunnable {

    private static final Map<UUID, List<Team>> teamCache = new HashMap<>();

    private static final String[] ENTRIES = {
            "§8§a", "§8§b", "§8§c", "§8§d", "§8§e", "§8§f",
            "§8§0", "§8§1", "§8§2", "§8§3", "§8§4", "§8§5",
            "§8§6", "§8§7", "§8§8", "§8§9"
    };

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            SwPlayer swPlayer = SwPlayerManager.getPlayer(player);
            if (swPlayer == null) continue;

            Game game = swPlayer.getPlayingGame();
            Scoreboard scoreboard = swPlayer.getScoreBoard();
            Objective objective = swPlayer.getScoreBoardObjective();

            objective.setDisplaySlot(DisplaySlot.SIDEBAR); // ?再设置一次

            if (game == null) {
                updateLobbyScoreboard(scoreboard, objective, player);
            } else {
                updateGameScoreboard(scoreboard, objective, player, game);
            }
        }
    }

    private void updateLobbyScoreboard(Scoreboard scoreboard, Objective objective, Player player) {
        objective.setDisplayName(ColorT.t(ConfigValues.serverName));
        SwPlayer sp = SwPlayerManager.getPlayer(player);
        if (sp != null) {

            String[] lines = {
                    "&e/hsw join &a加入游戏",
                    "",
                    "&b经验&7: &f" + sp.getStats().exps,
                    "&e硬币&7: &f" + sp.getStats().coins,
                    "&b等级&7: &f" + sp.getStats().getInGameLevel(),
                    "" + ConfigValues.serverIp,
            };

            setLines(scoreboard, objective, lines, player.getUniqueId());
        }
    }

    // ========== 游戏内计分板 ==========
    private void updateGameScoreboard(Scoreboard scoreboard, Objective objective, Player player, Game game) {
        GameStatus status = game.getGameStatus();
        String[] lines;

        switch (status) {
            case WAITING:
                objective.setDisplayName(ColorT.t("&e&lSkywars &7- &e等待"));
                lines = new String[]{
                        "&a玩家: &f" + game.getAlivePlayers().size() + "&7/&f" + game.getMaxPlayers(),
                        "",
                        "&b地图: &f" + game.getMapName(),
                        "",
                        "&b职业: &f" + game.getPlayerKit(player),
                };
                break;

            case STARTING:
                objective.setDisplayName(ColorT.t("&e&lSkywars &7- &c即将开始"));
                lines = new String[]{
                        "&7&m----------------",
                        "&c倒计时: &f" + game.getCountdownRemaining() + "s",
                        "",
                        "&a玩家: &f" + game.getAlivePlayers().size() + "&7/&f" + game.getMaxPlayers(),
                        "",
                        "&b地图: &f" + game.getMapName(),
                        "",
                        "&b职业: &f" + game.getPlayerKit(player),
                        "&7&m----------------"
                };
                break;

            case PLAYING: {
                SwPlayingGamePlayer swpgp = game.getPlayingPlayer(player.getUniqueId());
                int kills = (swpgp != null) ? swpgp.getKills() : 0;
                int alive = game.getAlivePlayers().size();
                int total = game.getAllPlayers().size();

                GameEvent nextEvent = null;
                if (game.getEventsInFuture() != null && !game.getEventsInFuture().isEmpty()) {
                    nextEvent =  game.getEventsInFuture().get(0);
                }

                objective.setDisplayName(ColorT.t("&c&lSkywars ⚔"));
                lines = new String[]{
                        "&7&m----------------",
                        "&c击杀: &f" + kills,
                        "&a存活: &f" + alive + "&7/&f" + total,
                        "",
                        "&e时间: &f" + formatTime(game.getRunnedTime()),
                        "&e下一事件: &f" + (nextEvent == null ? "无" : nextEvent.getType().getEventName()) + (nextEvent == null ? "" : ("&e" + formatTime(nextEvent.getTime() - game.getRunnedTime()))),
                        "",
                        "&b职业: &f" + game.getPlayerKit(player),
                        "&7&m----------------"
                };
                break;
            }

            case STOPPED:
            case RESETTING:
                objective.setDisplayName(ColorT.t("&6&lSkywars"));
                lines = new String[]{
                        "&7&m----------------",
                        "&e游戏结束",
                        "",
                        "&e胜者 &f" + game.getWinnerName(),
                        "",
                        "&a即将自动返回大厅...",
                        "&7&m----------------"
                };
                break;

            default:
                return;
        }

        setLines(scoreboard, objective, lines, player.getUniqueId());
    }

    private void setLines(Scoreboard scoreboard, Objective objective, String[] lines, UUID uuid) {
        List<Team> teams = teamCache.computeIfAbsent(uuid, k -> new ArrayList<>());

        // 动态注销Team，防止堆积
        // AI Coded™
        while (teams.size() > lines.length) {
            Team team = teams.remove(teams.size() - 1);
            team.unregister();
        }

        while (teams.size() < lines.length) {
            int index = teams.size();
            String entry = ENTRIES[index % ENTRIES.length]; // 取余是为了避免越界
            // 如果行数太长，ENTRIES无法容纳，就会有问题。
            Team team = scoreboard.getTeam("HSW_" + index);
            if (team == null) {
                team = scoreboard.registerNewTeam("HSW_" + index);
            }
            team.addEntry(entry);
            objective.getScore(entry).setScore(lines.length - index);
            teams.add(team);
        }

        for (int i = 0; i < lines.length; i++) {
            Team team = teams.get(i);
            String text = ColorT.t(lines[i]);

            // 避免字符串过长而遭踢出
            // 使用分段策略
            if (text.length() <= 16) {
                team.setPrefix(text);
                team.setSuffix("");
            } else if (text.length() <= 32) {
                team.setPrefix(text.substring(0, 16));
                team.setSuffix(text.substring(16));
            } else {
                team.setPrefix(text.substring(0, 16));
                team.setSuffix(text.substring(16, 32));
            }
        }
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    public static void clearCache() {
        teamCache.clear();
    }
    public static void cleanPlayer(Player pp) {
        teamCache.remove(pp.getUniqueId());
    }
}