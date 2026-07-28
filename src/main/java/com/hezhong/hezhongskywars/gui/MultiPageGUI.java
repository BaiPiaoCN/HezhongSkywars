package com.hezhong.hezhongskywars.gui;

import com.cryptomorin.xseries.XMaterial;
import com.hezhong.hezhongskywars.player.SwPlayer;
import com.hezhong.hezhongskywars.utils.ColorT;
import com.hezhong.hezhongskywars.utils.SimpleMath;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class MultiPageGUI extends HezhongSkywarsGUI {

    // Vibe Coding ™

    protected final Map<Integer, ItemStack> multiPageInventory = new LinkedHashMap<>();
    protected final ItemStack nextPageItem;
    protected final ItemStack prePageItem;
    protected int currentPage = 0;
    private final int rows;

    public MultiPageGUI(Player owner, SwPlayer ownerSp, String title, int rows, ItemStack nextPageItem, ItemStack prePageItem) {
        super(owner, ownerSp, title, rows);
        if (rows < 2) throw new IllegalArgumentException("MultiPageGUI needs 2 rows at least.");
        this.rows = rows;
        this.nextPageItem = nextPageItem;
        this.prePageItem = prePageItem;
    }

    protected static ItemStack createPageItem(String name) {
        ItemStack item = XMaterial.ARROW.parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorT.t(name));
        item.setItemMeta(meta);
        return item;
    }

    /**
    多行显示的核心组件，计算页码，从multiPageInventory中分页并抽取内容

     如果需要重写，必须调用super.showPage()才能正确显示页面。
     */
    protected void showPage() {
        inventory.clear();

        int itemsPerPage = (rows - 1) * 9;
        int totalItems = multiPageInventory.size();
        int totalPages = Math.max(1, SimpleMath.ceil((double) totalItems / itemsPerPage));
        if (currentPage >= totalPages) currentPage = totalPages - 1;

        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, totalItems);

        List<Map.Entry<Integer, ItemStack>> entries = new ArrayList<>(multiPageInventory.entrySet());

        for (int i = start; i < end; i++) {
            inventory.setItem(i - start, entries.get(i).getValue());
        }

        int lastRowStart = (rows - 1) * 9;
        int prevSlot = lastRowStart;
        int nextSlot = lastRowStart + 8;

        if (currentPage > 0 && prePageItem != null) {
            inventory.setItem(prevSlot, prePageItem);
        }
        if (currentPage < totalPages - 1 && nextPageItem != null) {
            inventory.setItem(nextSlot, nextPageItem);
        }

        // 页面提示
        ItemStack pageItem = new ItemStack(XMaterial.CLOCK.get());
        ItemMeta meta = pageItem.getItemMeta();
        meta.setDisplayName(ColorT.t("&f&l页面 " + (currentPage + 1) + "/" + totalPages));
        pageItem.setAmount(currentPage);
        pageItem.setItemMeta(meta);
        inventory.setItem(lastRowStart + 4, pageItem);

    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        if (handlePageClick(slot)) return;
        else handleMultiPageClick(event);
    }

    protected boolean handlePageClick(int slot) {
        int lastRowStart = (rows - 1) * 9;
        int prevSlot = lastRowStart;
        int nextSlot = lastRowStart + 8;

        int itemsPerPage = (rows - 1) * 9;
        int totalPages = Math.max(1, (int) Math.ceil((double) multiPageInventory.size() / itemsPerPage));

        if (slot == prevSlot && currentPage > 0) {
            currentPage--;
            showPage();
            return true;
        }
        if (slot == nextSlot && currentPage < totalPages - 1) {
            currentPage++;
            showPage();
            return true;
        }
        return false;
    }

    protected abstract void handleMultiPageClick(InventoryClickEvent event);
}
