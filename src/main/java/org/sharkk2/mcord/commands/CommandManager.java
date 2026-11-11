package org.sharkk2.mcord.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.sharkk2.mcord.MCord;
import org.sharkk2.mcord.commands.discord.SlashCommand;
import org.sharkk2.mcord.shared.CommandOption;
import org.sharkk2.mcord.shared.enums.CommandAccess;
import org.sharkk2.mcord.shared.enums.CommandOptionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandManager extends ListenerAdapter {
    private final Map<String, SlashCommand> discordCmds = new HashMap<>();
    private final List<SlashCommand> pendingDiscordCmds = new CopyOnWriteArrayList<>(); //thread safe i think
    private final MCord mcord;
    private boolean listenerRegistered = false;
    public CommandManager(MCord mcord) {
        this.mcord = mcord;
    }

    public void registerDiscordCommand(SlashCommand cmd) {
        this.pendingDiscordCmds.add(cmd);
    }

    // todo
    public void registerMinecraftCommand() {}

    private OptionType mapType(CommandOptionType type) {
        return switch (type) {
            case ROLE -> OptionType.ROLE;
            case USER -> OptionType.USER;
            case NUMBER -> OptionType.NUMBER;
            case STRING -> OptionType.STRING;
            case INTEGER -> OptionType.INTEGER;
            case BOOLEAN -> OptionType.BOOLEAN;
            case CHANNEL -> OptionType.CHANNEL;
        };
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommand cmd = discordCmds.get(event.getName());
        if (cmd == null) {
            event.reply(mcord.getConfig().getString("XEmoji") + " | Unknown command!").setEphemeral(true).queue();
            return;
        }

        if (cmd.spec().access() == CommandAccess.ADMIN && (event.getMember() == null || !event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
            event.reply(mcord.getConfig().getString("XEmoji") + " | You can't use this!").setEphemeral(true).queue();
            return;
        }

        cmd.onUse(event);
    }

    public void loadDiscordCommands(JDA jda) {
        if (!listenerRegistered) {
            jda.addEventListener(this);
            listenerRegistered = true;
        }
        List<SlashCommandData> data = pendingDiscordCmds.stream()
                .map(cmd -> {
                    discordCmds.put(cmd.name(), cmd);
                    return Commands.slash(cmd.name(), cmd.description())
                                    .addOptions(cmd.spec().options().stream()
                                            .map(o -> new OptionData(mapType(o.type()), o.name(), o.description(), o.required()))
                                            .toList())
                                    .setDefaultPermissions(cmd.spec().access() == CommandAccess.ADMIN
                                            ? DefaultMemberPermissions.DISABLED
                                            : DefaultMemberPermissions.ENABLED);
                }).toList();

        jda.updateCommands().addCommands(data).queue();
        pendingDiscordCmds.clear();
        mcord.getLogger().info("Loaded " + data.size() + " discord commands!");
    }

    public void cleanup() {discordCmds.clear(); pendingDiscordCmds.clear();}
}