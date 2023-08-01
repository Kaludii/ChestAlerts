
# ChestAlerts Plugin

**ChestAlerts** is a simple and customizable Spigot plugin that sends an alert when a chest, shulker box, barrel, or ender chest is opened in console as well as to discord through a webhook.

## ChestAlerts In Action

**Console Alerts (Can Be Turned Off In Config):**

![console-alerts](https://github.com/Kaludii/ChestAlerts/blob/main/images/console-alerts.png?raw=true)

**Discord Alerts (Webhook Configurable In Config):**

![webhook-alerts](https://github.com/Kaludii/ChestAlerts/blob/main/images/webhook-alerts.png?raw=true)

## Features

-   **Instant Alerts**: Get alerted instantly when a player opens a chest, shulker box, barrel, or ender chest.
-   **Discord Integration**: Send alerts directly to a Discord channel using a webhook.
-   **Permission Support**: Comes with permission nodes for using the toggle alert command and reloading the plugin.

## Plugin.yml

    name: ChestAlerts
    version: '1.0'
    main: com.github.kaludii.chestalerts.ChestAlerts
    api-version: '1.20'
    description: Send an alert to Discord and the console when a player opens a chest.
    authors: [Kaludi]
    website: https://github.com/Kaludii
    load: STARTUP
    commands:
      ChestAlerts:
        description: Main command for the ChestAlerts plugin.
        usage: /<command>
        permission: chestalerts.use
        permission-message: '&d&lChestAlerts &2&l►You do not have permission to use this command.'
    permissions:
      chestalerts.*:
        description: Gives access to all ChestAlerts commands.
        default: op
      chestalerts.use:
        description: Allows use of the ChestAlerts command.
        default: op
      chestalerts.reload:
        description: Allows use of the /ChestAlerts reload command.
        default: op
      chestalerts.toggle:
        description: Allows use of the /ChestAlerts toggle command.
        default: op

## Commands

-   `/ChestAlerts reload`: Reloads the plugin configuration.
-   `/ChestAlerts toggle`: Toggles chest alerts on or off.
-   `/ChestAlerts help`: Shows help message.

## Permissions

-   `chestalerts.reload`: Allows the use of the `/ChestAlerts reload` command.
-   `chestalerts.toggle`: Allows the use of the `/ChestAlerts toggle` command.

## Configuration

**Config.yml**

    # Configuration file for the ChestAlerts plugin by Kaludi.
    #
    # 'discord-webhook-url': The URL for the Discord webhook to post to.
    # 'discord-webhook-name': The name for the Discord webhook.
    # 'discord-webhook-image': The URL for the image to use for the Discord webhook posts.
    # 'announce-in-terminal': Whether to announce to the console when a chest is opened.
    # 'bStatsEnabled': Whether to enable bStats metrics for this plugin.
    #
    # After making changes to this file, save and do '/ChestAlerts reload' or restart your server.
    settings:
    discord-webhook-url: ''
    discord-webhook-name: ChestAlerts
    discord-webhook-image: 'https://media.discordapp.net/attachments/705961866962403328/1135798490417610792/chest.png'
    announce-in-terminal: true
    bStatsEnabled: true

**Messages.yml**

    # Messages file for the ChestAlerts plugin by Kaludi.
    #
    # You can use color codes using '&'. For example: '&c' is red.
    # Don't forget to save and do '/ChestAlerts reload' or restart your server after making changes.
    #
    help_message: '&d&lChestAlerts &2&l► &bThis is the help message for ChestAlerts. Available commands are: /ChestAlerts help, /ChestAlerts reload, /ChestAlerts toggle'
    no_permission_message: '&d&lChestAlerts &2&l► &cYou do not have permission to use this command.'
    reload_message: '&d&lChestAlerts &2&l► &bThe ChestAlerts plugin has been reloaded.'
    toggle_on_message: '&d&lChestAlerts &2&l► &bChestAlerts have been enabled.'
    toggle_off_message: '&d&lChestAlerts &2&l► &bChestAlerts have been disabled.'

**This plugin was only tested on 1.19 and 1.20, no other previous versions.**

## Support

For any help or support questions, join our [Discord Server](https://discord.gg/ckh7Cvh8).
