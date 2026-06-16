package me.tjsh.luckpermsutils;

import me.tjsh.luckpermsutils.type.MetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.metadata.NodeMetadataKey;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;

import java.util.SortedMap;
import java.util.UUID;

public class LuckPermsDataUtil {
    // 我终于知道为什么他要求写封装了，是挺长的......
    // 慢慢看API docs吧。。
    public static Group getPlayerGroup(String player) {
        if (LuckPermsUtils.provider.getUserManager().getUser(player) == null) {
            return null;
        }
        return LuckPermsUtils.provider.getGroupManager().getGroup(LuckPermsUtils.provider.getUserManager().getUser(player).getPrimaryGroup());
    }
    public static Group getPlayerGroup(UUID player) {
        if (LuckPermsUtils.provider.getUserManager().getUser(player) == null) {
            return null;
        }
        return LuckPermsUtils.provider.getGroupManager().getGroup(LuckPermsUtils.provider.getUserManager().getUser(player).getPrimaryGroup());
    }
    public static MetaData getPlayerMetaData(String player) {
        if (LuckPermsUtils.provider.getUserManager().getUser(player) == null) {
            return null;
        }
        MetaData metaData = new MetaData();
        metaData.setPermissions(LuckPermsUtils.provider.getUserManager().getUser(player).getCachedData().getPermissionData().getPermissionMap());
        // LuckPermsUtils.provider.getUserManager().getUser(playerName).getCachedData().getMetaData().getPrefixes()返回一个SortedMap<Int, String>, 转换成Map<String, Int>
        SortedMap<Integer, String> prefixes = LuckPermsUtils.provider.getUserManager().getUser(player).getCachedData().getMetaData().getPrefixes(); // 将所有prefix存储在一起，然后keySet遍历。
        for (Integer weight : prefixes.keySet()) {
            metaData.prefixes.put(prefixes.get(weight), weight); // 懒得去用Setter了。两种方法都能获取，但还是Getter好罢
        }
        SortedMap<Integer, String> suffixes = LuckPermsUtils.provider.getUserManager().getUser(player).getCachedData().getMetaData().getSuffixes();
        for (Integer weight : suffixes.keySet()) {
            metaData.suffixes.put(suffixes.get(weight), weight);
        }
        return metaData;
    }
    public static MetaData getPlayerMetaData(UUID player) {
        if (LuckPermsUtils.provider.getUserManager().getUser(player) == null) {
            return null;
        }
        MetaData metaData = new MetaData();
        metaData.setPermissions(LuckPermsUtils.provider.getUserManager().getUser(player).getCachedData().getPermissionData().getPermissionMap());
        SortedMap<Integer, String> prefixes = LuckPermsUtils.provider.getUserManager().getUser(player).getCachedData().getMetaData().getPrefixes();
        for (Integer weight : prefixes.keySet()) {
            metaData.prefixes.put(prefixes.get(weight), weight);
        }
        SortedMap<Integer, String> suffixes = LuckPermsUtils.provider.getUserManager().getUser(player).getCachedData().getMetaData().getSuffixes();
        for (Integer weight : suffixes.keySet()) {
            metaData.suffixes.put(suffixes.get(weight), weight);
        }
        return metaData;
    }
    public static MetaData getGroupMetaData(String group) { // 和Player差不多的。。
        if (LuckPermsUtils.provider.getGroupManager().getGroup(group) == null) {
            return null;
        }
        MetaData metaData = new MetaData();
        metaData.setPermissions(LuckPermsUtils.provider.getGroupManager().getGroup(group).getCachedData().getPermissionData().getPermissionMap());
        SortedMap<Integer, String> prefixes = LuckPermsUtils.provider.getGroupManager().getGroup(group).getCachedData().getMetaData().getPrefixes();
        for (Integer weight : prefixes.keySet()) {
            metaData.prefixes.put(prefixes.get(weight), weight);
        }
        SortedMap<Integer, String> suffixes = LuckPermsUtils.provider.getGroupManager().getGroup(group).getCachedData().getMetaData().getSuffixes();
        for (Integer weight : suffixes.keySet()) {
            metaData.suffixes.put(suffixes.get(weight), weight);
        }
        return metaData;
    }

    public static void setPlayerPrefix(UUID player, String prefix, int weight) {
        LuckPermsUtils.provider.getUserManager().modifyUser(player, user -> {
        // 先删掉以前所有权重为weight的Prefix

            for (Node n : user.getNodes()) {
                if (n instanceof PrefixNode) {
                    PrefixNode sn = (PrefixNode) n;
                    if (sn.getPriority() == weight) {
                        user.data().remove(sn);
                    }
                }
            }
            user.data().add(PrefixNode.builder(prefix, weight).build());
        });
    }

    public static void setPlayerSuffix(UUID player, String suffix, int weight) {
        LuckPermsUtils.provider.getUserManager().modifyUser(player, user -> {
            // 先删掉以前所有权重为weight的Suffix

            for (Node n : user.getNodes()) {
                if (n instanceof SuffixNode) {
                    SuffixNode sn = (SuffixNode) n;
                    if (sn.getPriority() == weight) {
                        user.data().remove(sn);
                    }
                }
            }
            user.data().add(SuffixNode.builder(suffix, weight).build());
        });
    }
}
