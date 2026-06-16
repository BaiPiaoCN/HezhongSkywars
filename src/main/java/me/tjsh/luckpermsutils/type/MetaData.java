package me.tjsh.luckpermsutils.type;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MetaData {
    public Map<String, Integer> prefixes; // 1：prefix / suffix 2：权重
    public Map<String, Integer> suffixes;
    public Map<String, Boolean> permissions; // 1：权限 2：允许 or 禁止
}
