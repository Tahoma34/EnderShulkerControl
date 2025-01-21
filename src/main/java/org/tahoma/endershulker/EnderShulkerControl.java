package org.tahoma.endershulker;

import org.bukkit.plugin.java.JavaPlugin;

public final class EnderShulkerControl extends JavaPlugin {

    private static EnderShulkerControl instance;
    private EnderConfig enderConfig;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // Создаём config.yml, если его нет
        this.enderConfig = new EnderConfig(getConfig());

        // Регистрируем слушатель
        getServer().getPluginManager().registerEvents(new EnderListener(), this);

        // Регистрируем команды
        getCommand("esc").setExecutor(new EnderCommands());

        getLogger().info("Плагин EnderShulkerControl включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Плагин EnderShulkerControl выключен!");
    }

    public static EnderShulkerControl getInstance() {
        return instance;
    }

    public EnderConfig getEnderConfig() {
        return enderConfig;
    }
}
