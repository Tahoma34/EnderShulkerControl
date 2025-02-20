package org.tahoma.endershulker;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class EnderCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("endershulkercontrol.admin")) {
            sender.sendMessage(getMsg("commands-messages.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        EnderConfig cfg = EnderShulkerControl.getInstance().getEnderConfig();
        switch (args[0].toLowerCase()) {
            case "additem":
                if (args.length < 3) {
                    sender.sendMessage(getMsg("commands-messages.usage-additem"));
                    return true;
                }
                addItem(cfg, sender, args[1], args[2]);
                return true;

            case "removeitem":
                if (args.length < 3) {
                    sender.sendMessage(getMsg("commands-messages.usage-removeitem"));
                    return true;
                }
                removeItem(cfg, sender, args[1], args[2]);
                return true;

            case "addworld":
                if (args.length < 2) {
                    sender.sendMessage(getMsg("commands-messages.usage-addworld"));
                    return true;
                }
                addWorld(cfg, sender, args[1]);
                return true;

            case "removeworld":
                if (args.length < 2) {
                    sender.sendMessage(getMsg("commands-messages.usage-removeworld"));
                    return true;
                }
                removeWorld(cfg, sender, args[1]);
                return true;

            case "reload":
                EnderShulkerControl.getInstance().reloadConfig();
                EnderShulkerControl.getInstance().getEnderConfig().saveConfig();
                sender.sendMessage(getMsg("commands-messages.plugin-reloaded"));
                return true;

            default:
                sendHelp(sender);
                return true;
        }
    }

    private void addItem(EnderConfig cfg, CommandSender sender, String type, String materialName) {
        Material mat = Material.getMaterial(materialName.toUpperCase());
        if (mat == null) {
            sender.sendMessage(getMsg("commands-messages.material-invalid")
                    .replace("%material%", materialName));
            return;
        }

        if (type.equalsIgnoreCase("ender")) {
            List<String> list = cfg.getBlockedEnderChestItems();
            if (!list.contains(mat.name())) {
                list.add(mat.name());
                cfg.setBlockedEnderChestItems(list);
                sender.sendMessage(getMsg("commands-messages.item-added-ender")
                        .replace("%material%", mat.name()));
            } else {
                sender.sendMessage(getMsg("commands-messages.item-exists-ender")
                        .replace("%material%", mat.name()));
            }
        } else if (type.equalsIgnoreCase("shulker")) {
            List<String> list = cfg.getBlockedShulkerBoxItems();
            if (!list.contains(mat.name())) {
                list.add(mat.name());
                cfg.setBlockedShulkerBoxItems(list);
                sender.sendMessage(getMsg("commands-messages.item-added-shulker")
                        .replace("%material%", mat.name()));
            } else {
                sender.sendMessage(getMsg("commands-messages.item-exists-shulker")
                        .replace("%material%", mat.name()));
            }
        } else {
            sender.sendMessage(getMsg("commands-messages.material-invalid")
                    .replace("%material%", type));
        }
    }

    private void removeItem(EnderConfig cfg, CommandSender sender, String type, String materialName) {
        Material mat = Material.getMaterial(materialName.toUpperCase());
        if (mat == null) {
            sender.sendMessage(getMsg("commands-messages.material-invalid")
                    .replace("%material%", materialName));
            return;
        }

        if (type.equalsIgnoreCase("ender")) {
            List<String> list = cfg.getBlockedEnderChestItems();
            if (list.contains(mat.name())) {
                list.remove(mat.name());
                cfg.setBlockedEnderChestItems(list);
                sender.sendMessage(getMsg("commands-messages.item-removed-ender")
                        .replace("%material%", mat.name()));
            } else {
                sender.sendMessage(getMsg("commands-messages.item-notfound-ender")
                        .replace("%material%", mat.name()));
            }
        } else if (type.equalsIgnoreCase("shulker")) {
            List<String> list = cfg.getBlockedShulkerBoxItems();
            if (list.contains(mat.name())) {
                list.remove(mat.name());
                cfg.setBlockedShulkerBoxItems(list);
                sender.sendMessage(getMsg("commands-messages.item-removed-shulker")
                        .replace("%material%", mat.name()));
            } else {
                sender.sendMessage(getMsg("commands-messages.item-notfound-shulker")
                        .replace("%material%", mat.name()));
            }
        } else {
            sender.sendMessage(getMsg("commands-messages.material-invalid")
                    .replace("%material%", type));
        }
    }

    private void addWorld(EnderConfig cfg, CommandSender sender, String worldName) {
        List<String> list = cfg.getBlacklistedWorlds();
        if (!list.contains(worldName)) {
            list.add(worldName);
            cfg.setBlacklistedWorlds(list);
            sender.sendMessage(getMsg("commands-messages.addworld-added")
                    .replace("%world%", worldName));
        } else {
            sender.sendMessage(getMsg("commands-messages.addworld-exists")
                    .replace("%world%", worldName));
        }
    }

    private void removeWorld(EnderConfig cfg, CommandSender sender, String worldName) {
        List<String> list = cfg.getBlacklistedWorlds();
        if (list.contains(worldName)) {
            list.remove(worldName);
            cfg.setBlacklistedWorlds(list);
            sender.sendMessage(getMsg("commands-messages.removeworld-removed")
                    .replace("%world%", worldName));
        } else {
            sender.sendMessage(getMsg("commands-messages.removeworld-notfound")
                    .replace("%world%", worldName));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(getMsg("commands-messages.help-header"));
        sender.sendMessage(getMsg("commands-messages.help-additem"));
        sender.sendMessage(getMsg("commands-messages.help-removeitem"));
        sender.sendMessage(getMsg("commands-messages.help-addworld"));
        sender.sendMessage(getMsg("commands-messages.help-removeworld"));
        sender.sendMessage(getMsg("commands-messages.help-reload"));
    }

    private String getMsg(String path) {
        String msg = EnderShulkerControl.getInstance().getConfig().getString(path,
                "§c[Не найдено в config.yml: " + path + "]");
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
