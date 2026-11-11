package org.sharkk2.mcord;

import org.bukkit.plugin.java.JavaPlugin;
import org.sharkk2.mcord.commands.CommandManager;
import org.sharkk2.mcord.commands.discord.SetupCommand;

public final class MCord extends JavaPlugin {
    private final MCordBot bot = new MCordBot(this);
    private final MCordDatabox databox = new MCordDatabox(this);
    private CommandManager commandManager;

    public MCordDatabox getDatabox() {return databox;}

    @Override
    public void onEnable() {
        getLogger().info("Initializing MCord...");
        saveDefaultConfig();
        databox.loadData();
        commandManager = new CommandManager(this);
        commandManager.registerDiscordCommand(new SetupCommand(this));
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            bot.startBot();
            commandManager.loadDiscordCommands(bot.getJDA());
        });
        getLogger().info("MCord is ready to go!");
    }

    @Override
    public void onDisable() {
        if (commandManager!=null) commandManager.cleanup();
        bot.stopBot();
    }
}
