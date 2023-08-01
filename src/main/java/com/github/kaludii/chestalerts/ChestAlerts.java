package com.github.kaludii.chestalerts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public final class ChestAlerts extends JavaPlugin implements Listener {

    private File customConfigFile;
    private FileConfiguration customConfig;
    private boolean alertsEnabled;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        alertsEnabled = true;
        Bukkit.getPluginManager().registerEvents(this, this);
        createCustomConfig();

        // Check if bStats is enabled in the config before initializing
        if (getConfig().getBoolean("settings.bStatsEnabled", true)) {
            Metrics metrics = new Metrics(this, 19344);
            metrics.addCustomChart(new Metrics.SingleLineChart("players", new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return Bukkit.getOnlinePlayers().size();
                }
            }));
        }

        Bukkit.getLogger().info("ChestAlerts plugin enabled!");
    }

    @Override
    public void onDisable() {
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "messages.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("ChestAlerts")) {
            if (!player.hasPermission("chestalerts.use")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getCustomConfig().getString("no_permission_message")));
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getCustomConfig().getString("help_message")));
                return true;
            }

            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getCustomConfig().getString("help_message")));
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("chestalerts.reload")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getCustomConfig().getString("no_permission_message")));
                    return true;
                }
                this.reloadConfig();
                this.createCustomConfig();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', getCustomConfig().getString("reload_message")));
                return true;
            }

            if (args[0].equalsIgnoreCase("toggle")) {
                if (!player.hasPermission("chestalerts.toggle")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', getCustomConfig().getString("no_permission_message")));
                    return true;
                }
                alertsEnabled = !alertsEnabled;
                String status = alertsEnabled ? getCustomConfig().getString("toggle_on_message") : getCustomConfig().getString("toggle_off_message");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', status));
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("ChestAlerts") && args.length == 1) {
            return Arrays.asList("help", "reload", "toggle");
        }

        return null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!alertsEnabled) {
            return;
        }

        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Material blockType = block.getType();
            String blockTypeName = "";
            if (blockType.equals(Material.CHEST)) {
                blockTypeName = "Chest";
            } else if (blockType.equals(Material.ENDER_CHEST)) {
                blockTypeName = "Ender Chest";
            } else if (blockType.equals(Material.BARREL)) {
                blockTypeName = "Barrel";
            } else if (blockType.equals(Material.SHULKER_BOX) || blockType.name().endsWith("_SHULKER_BOX")) {
                // Matches any color of shulker box
                blockTypeName = "Shulker Box";
            } else {
                return;
            }

            String playerName = player.getName();
            String worldName = block.getLocation().getWorld().getName();
            int x = block.getLocation().getBlockX();
            int y = block.getLocation().getBlockY();
            int z = block.getLocation().getBlockZ();
            String blockLocation = "World: " + worldName + ", X: " + x + ", Y: " + y + ", Z: " + z;

            String webhookUrl = getConfig().getString("settings.discord-webhook-url");
            if (webhookUrl != null && !webhookUrl.isEmpty()) {
                sendWebhook(playerName, blockLocation, blockTypeName);
            }

            if (getConfig().getBoolean("settings.announce-in-terminal")) {
                System.out.println(playerName + " Opened " + blockTypeName + " At " + blockLocation);
            }
        }
    }

    private void sendWebhook(String playerName, String blockLocation, String blockTypeName) {
        String webhookUrl = getConfig().getString("settings.discord-webhook-url");
        String webhookName = getConfig().getString("settings.discord-webhook-name");
        String webhookImage = getConfig().getString("settings.discord-webhook-image");

        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }

        String playerAvatarUrl = "https://cravatar.eu/avatar/" + playerName + "/64.png";
        String message = "Opened " + blockTypeName + " At " + blockLocation;

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");

            String payload = String.format(
                    "{" +
                            "\"username\": \"%s\"," +
                            "\"avatar_url\": \"%s\"," +
                            "\"embeds\": [{" +
                            "\"author\": {" +
                            "\"name\": \"%s\"" +
                            "}," +
                            "\"thumbnail\": {" +
                            "\"url\": \"%s\"" +
                            "}," +
                            "\"description\": \"%s\"," +
                            "\"color\": 5814783," + // This is the decimal RGB color for purple.
                            "\"footer\": {" +
                            "\"text\": \"Time: " + new Date().toString() + "\"" +
                            "}" +
                            "}]" +
                            "}",
                    webhookName, webhookImage, playerName, playerAvatarUrl, message
            );

            byte[] out = payload.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            httpCon.setFixedLengthStreamingMode(length);
            httpCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpCon.connect();

            try (OutputStream os = httpCon.getOutputStream()) {
                os.write(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
