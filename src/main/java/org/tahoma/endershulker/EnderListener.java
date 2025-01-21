package org.tahoma.endershulker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class EnderListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Получаем верхний инвентарь
        Inventory topInv = event.getView().getTopInventory();

        // Проверяем, эндер-сундук ли это
        boolean isEnderInv = (topInv.getType() == InventoryType.ENDER_CHEST);

        // Проверяем, шалкер ли это
        boolean isShulkerInv = isShulkerBoxInventory(topInv.getHolder());

        if (!isEnderInv && !isShulkerInv) {
            return; // Если это не эндер-сундук и не шалкер, пропускаем
        }

        // Проверка чёрного списка миров
        EnderConfig cfg = EnderShulkerControl.getInstance().getEnderConfig();
        String worldName = event.getWhoClicked().getWorld().getName();
        if (cfg.getBlacklistedWorlds().contains(worldName)) {
            return;
        }

        // Проверка обхода
        if (event.getWhoClicked().hasPermission("endershulkercontrol.bypass")) {
            return;
        }

        // SHIFT-клик
        if (event.isShiftClick()) {
            // Если SHIFT-клик сделан по нижнему инвентарю — предмет пытаются переложить в верхний
            if (event.getClickedInventory() != null
                    && event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {

                ItemStack currentItem = event.getCurrentItem();
                if (currentItem == null || currentItem.getType() == Material.AIR) {
                    return;
                }
                Material mat = currentItem.getType();

                if (isEnderInv && cfg.getBlockedEnderChestItems().contains(mat.name())) {
                    blockAction(event, mat);
                } else if (isShulkerInv && cfg.getBlockedShulkerBoxItems().contains(mat.name())) {
                    blockAction(event, mat);
                }
            }
        }
        // Обычный клик (когда предмет на курсоре)
        else {
            // Проверяем, что клик завершён по верхнему инвентарю
            if (event.getClickedInventory() != null
                    && event.getClickedInventory().equals(topInv)) {

                ItemStack cursorItem = event.getCursor();
                if (cursorItem == null || cursorItem.getType() == Material.AIR) {
                    return;
                }
                Material mat = cursorItem.getType();

                if (isEnderInv && cfg.getBlockedEnderChestItems().contains(mat.name())) {
                    blockAction(event, mat);
                } else if (isShulkerInv && cfg.getBlockedShulkerBoxItems().contains(mat.name())) {
                    blockAction(event, mat);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Верхний инвентарь
        Inventory topInv = event.getView().getTopInventory();
        boolean isEnderInv = (topInv.getType() == InventoryType.ENDER_CHEST);
        boolean isShulkerInv = isShulkerBoxInventory(topInv.getHolder());

        if (!isEnderInv && !isShulkerInv) {
            return;
        }

        EnderConfig cfg = EnderShulkerControl.getInstance().getEnderConfig();
        String worldName = event.getWhoClicked().getWorld().getName();
        if (cfg.getBlacklistedWorlds().contains(worldName)) {
            return;
        }

        if (event.getWhoClicked().hasPermission("endershulkercontrol.bypass")) {
            return;
        }

        // Предмет, который игрок «тянет»
        ItemStack dragged = event.getOldCursor();
        if (dragged == null || dragged.getType() == Material.AIR) {
            return;
        }
        Material mat = dragged.getType();

        // Проверяем, затрагиваются ли слоты в верхнем инвентаре
        for (int slot : event.getRawSlots()) {
            Inventory slotInv = event.getView().getInventory(slot);
            if (slotInv != null && slotInv.equals(topInv)) {
                if (isEnderInv && cfg.getBlockedEnderChestItems().contains(mat.name())) {
                    blockDrag(event, mat);
                    break;
                } else if (isShulkerInv && cfg.getBlockedShulkerBoxItems().contains(mat.name())) {
                    blockDrag(event, mat);
                    break;
                }
            }
        }
    }

    /**
     * Новый обработчик для проверки, не пытается ли воронка (HOPPER) переложить запрещённые предметы в шалкер.
     */
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        // Проверяем, что источник — воронка, а цель — шалкер-бокс
        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        if (source.getType() == InventoryType.HOPPER && isShulkerBoxInventory(destination.getHolder())) {
            EnderConfig cfg = EnderShulkerControl.getInstance().getEnderConfig();

            // Проверяем мир (если нужно). Если бы у воронки потенциально был "владелец",
            // то можно получить локацию, у которой есть мир. Для примера пропускаем этот шаг,
            // либо используем ещё одну проверку, если в проекте есть методы
            // для получения мира из контейнера/воронки.

            // Проверяем предмет
            Material mat = event.getItem().getType();
            if (cfg.getBlockedShulkerBoxItems().contains(mat.name())) {
                event.setCancelled(true);

                // Лог в консоль (по желанию)
                if (cfg.isConsoleLoggingEnabled()) {
                    String msg = cfg.getLogMessage()
                            .replace("%player%", "HOPPER") // места для имени игрока нет, но можно указать "HOPPER"
                            .replace("%item%", mat.name());
                    Bukkit.getLogger().info(msg);
                }
            }
        }
    }

    private void blockAction(InventoryClickEvent event, Material itemType) {
        event.setCancelled(true);

        EnderConfig cfg = EnderShulkerControl.getInstance().getEnderConfig();
        event.getWhoClicked().sendMessage(cfg.getMessageOnBlock());

        // Воспроизводим звук
        event.getWhoClicked().getWorld().playSound(
                event.getWhoClicked().getLocation(),
                cfg.getBlockSound(),
                1.0f,
                1.0f
        );

        // Лог в консоль (только если включено в config.yml)
        if (cfg.isConsoleLoggingEnabled()) {
            String msg = cfg.getLogMessage()
                    .replace("%player%", event.getWhoClicked().getName())
                    .replace("%item%", itemType.name());
            Bukkit.getLogger().info(msg);
        }
    }

    private void blockDrag(InventoryDragEvent event, Material itemType) {
        event.setCancelled(true);

        EnderConfig cfg = EnderShulkerControl.getInstance().getEnderConfig();
        event.getWhoClicked().sendMessage(cfg.getMessageOnBlock());

        // Воспроизводим звук
        event.getWhoClicked().getWorld().playSound(
                event.getWhoClicked().getLocation(),
                cfg.getBlockSound(),
                1.0f,
                1.0f
        );

        // Лог (только если включено в config.yml)
        if (cfg.isConsoleLoggingEnabled()) {
            String msg = cfg.getLogMessage()
                    .replace("%player%", event.getWhoClicked().getName())
                    .replace("%item%", itemType.name());
            Bukkit.getLogger().info(msg);
        }
    }

    /**
     * Проверяем, является ли holder шалкер-боксом.
     */
    private boolean isShulkerBoxInventory(InventoryHolder holder) {
        return holder instanceof ShulkerBox;
    }
}