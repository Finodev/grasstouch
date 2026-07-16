package ru.example.grassregrow;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GrassCommand implements CommandExecutor {

    private final GrassConfig config;

    public GrassCommand(GrassConfig config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                config.load();
                sender.sendMessage(ChatColor.GREEN + "[GrassRegrow] Конфигурация перезагружена.");
            }
            case "settime" -> handleSetTime(sender, args);
            case "toggle" -> handleToggle(sender, args);
            case "status" -> sendStatus(sender);
            default -> sendUsage(sender);
        }
        return true;
    }

    private void handleSetTime(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "Использование: /grassregrow settime <short|tall> <секунды>");
            return;
        }

        long seconds;
        try {
            seconds = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Секунды должны быть целым числом.");
            return;
        }

        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Секунды не могут быть отрицательными.");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "short" -> {
                config.setShortGrassRespawnSeconds(seconds);
                sender.sendMessage(ChatColor.GREEN + "[GrassRegrow] Короткая трава возрождается через " + seconds + " сек.");
            }
            case "tall" -> {
                config.setTallGrassRespawnSeconds(seconds);
                sender.sendMessage(ChatColor.GREEN + "[GrassRegrow] Высокая трава возрождается через " + seconds + " сек.");
            }
            default -> sender.sendMessage(ChatColor.YELLOW + "Использование: /grassregrow settime <short|tall> <секунды>");
        }
    }

    private void handleToggle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Использование: /grassregrow toggle <all|short|tall>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "all" -> {
                boolean newState = !config.isEnabled();
                config.setEnabled(newState);
                sender.sendMessage(ChatColor.GREEN + "[GrassRegrow] Плагин: " + (newState ? "включен" : "выключен"));
            }
            case "short" -> {
                boolean newState = !config.isShortGrassEnabled();
                config.setShortGrassEnabled(newState);
                sender.sendMessage(ChatColor.GREEN + "[GrassRegrow] Короткая трава: " + (newState ? "включена" : "выключена"));
            }
            case "tall" -> {
                boolean newState = !config.isTallGrassEnabled();
                config.setTallGrassEnabled(newState);
                sender.sendMessage(ChatColor.GREEN + "[GrassRegrow] Высокая трава: " + (newState ? "включена" : "выключена"));
            }
            default -> sender.sendMessage(ChatColor.YELLOW + "Использование: /grassregrow toggle <all|short|tall>");
        }
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== GrassRegrow ===");
        sender.sendMessage(ChatColor.GRAY + "Плагин: " + stateText(config.isEnabled()));
        sender.sendMessage(ChatColor.GRAY + "Короткая трава: " + stateText(config.isShortGrassEnabled())
                + ChatColor.GRAY + " (" + config.getShortGrassRespawnSeconds() + " сек.)");
        sender.sendMessage(ChatColor.GRAY + "Высокая трава: " + stateText(config.isTallGrassEnabled())
                + ChatColor.GRAY + " (" + config.getTallGrassRespawnSeconds() + " сек.)");
    }

    private String stateText(boolean state) {
        return state ? ChatColor.GREEN + "вкл" : ChatColor.RED + "выкл";
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Команды GrassRegrow:");
        sender.sendMessage(ChatColor.GRAY + "/grassregrow reload " + ChatColor.DARK_GRAY + "- перечитать config.yml");
        sender.sendMessage(ChatColor.GRAY + "/grassregrow settime <short|tall> <секунды> " + ChatColor.DARK_GRAY + "- время возрождения");
        sender.sendMessage(ChatColor.GRAY + "/grassregrow toggle <all|short|tall> " + ChatColor.DARK_GRAY + "- вкл/выкл");
        sender.sendMessage(ChatColor.GRAY + "/grassregrow status " + ChatColor.DARK_GRAY + "- текущие настройки");
    }
}
