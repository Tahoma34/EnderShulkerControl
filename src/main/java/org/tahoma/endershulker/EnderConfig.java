package org.tahoma.endershulker;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class EnderConfig {

    private final FileConfiguration config;
    private List<String> blockedEnderChestItems;
    private List<String> blockedShulkerBoxItems;
    private List<String> blacklistedWorlds;
    private String messageOnBlock;
    private String logMessage;
    private Sound blockSound;

    // Новое поле для включения/отключения логирования:
    private boolean consoleLoggingEnabled;

    public EnderConfig(FileConfiguration config) {
        this.config = config;
        loadConfigValues();
    }

    private void loadConfigValues() {
        // Читаем списки запрещённых предметов
        blockedEnderChestItems = config.getStringList("blocked-items.enderchest");
        blockedShulkerBoxItems = config.getStringList("blocked-items.shulkerbox");

        // Читаем чёрный список миров
        blacklistedWorlds = config.getStringList("blacklisted-worlds");

        // Сообщения
        messageOnBlock = ChatColor.translateAlternateColorCodes('&',
                config.getString("messages.on-block", "&cНельзя класть этот предмет сюда!"));
        logMessage = "[EnderShulkerControl] Игрок %player% пытался положить запрещённый предмет %item%";

        // Настройка звука
        try {
            blockSound = Sound.valueOf(config.getString("sounds.block", "ENTITY_VILLAGER_NO"));
        } catch (IllegalArgumentException e) {
            blockSound = Sound.ENTITY_VILLAGER_NO;
        }

        // Читаем включение/выключение логирования
        consoleLoggingEnabled = config.getBoolean("logging.console-enabled", true);
    }

    /**
     * Сохраняет изменения в config.yml.
     */
    public void saveConfig() {
        EnderShulkerControl.getInstance().saveConfig();
    }

    public List<String> getBlockedEnderChestItems() {
        return blockedEnderChestItems != null ? blockedEnderChestItems : new ArrayList<>();
    }

    public List<String> getBlockedShulkerBoxItems() {
        return blockedShulkerBoxItems != null ? blockedShulkerBoxItems : new ArrayList<>();
    }

    public List<String> getBlacklistedWorlds() {
        return blacklistedWorlds != null ? blacklistedWorlds : new ArrayList<>();
    }

    public void setBlockedEnderChestItems(List<String> items) {
        this.blockedEnderChestItems = items;
        config.set("blocked-items.enderchest", items);
        saveConfig();
    }

    public void setBlockedShulkerBoxItems(List<String> items) {
        this.blockedShulkerBoxItems = items;
        config.set("blocked-items.shulkerbox", items);
        saveConfig();
    }

    public void setBlacklistedWorlds(List<String> worlds) {
        this.blacklistedWorlds = worlds;
        config.set("blacklisted-worlds", worlds);
        saveConfig();
    }

    public String getMessageOnBlock() {
        return messageOnBlock;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public Sound getBlockSound() {
        return blockSound;
    }

    // Новый геттер для включения/выключения логирования:
    public boolean isConsoleLoggingEnabled() {
        return consoleLoggingEnabled;
    }
}