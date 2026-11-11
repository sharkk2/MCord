package org.sharkk2.mcord.commands.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.ChatColor;
import org.sharkk2.mcord.MCord;
import org.sharkk2.mcord.MCordBot;
import org.sharkk2.mcord.shared.CommandOption;
import org.sharkk2.mcord.shared.CommandSpec;
import org.sharkk2.mcord.shared.enums.CommandAccess;
import org.sharkk2.mcord.shared.enums.CommandOptionType;

import java.awt.*;
import java.util.List;

public class SetupCommand implements SlashCommand {
    private final MCord mcord;
    public SetupCommand(MCord mcord) {this.mcord = mcord;}
    private final CommandSpec data = new CommandSpec(
            "setup",
            "Setup MCord server bridging",
            CommandAccess.ADMIN,
            List.of(
                    new CommandOption("chat_channel", "The live minecraft-discord chat channel", CommandOptionType.CHANNEL, true),
                    new CommandOption("console_channel", "A channel that will act as the Minecraft server console", CommandOptionType.CHANNEL, true)
            )
    );


    @Override
    public CommandSpec spec() {
        return data;
    }

    @Override
    public void onUse(SlashCommandInteractionEvent event) {

        OptionMapping chatOption = event.getOption("chat_channel");
        OptionMapping consoleOption = event.getOption("console_channel");

        if (!mcord.getConfig().getList("admins").contains(event.getUser().getName())) {
            event.reply(mcord.getConfig().getString("XEmoji") + " | You are not a plugin admin!").setEphemeral(true).queue();
            return;
        }

        if (chatOption.getChannelType() != ChannelType.TEXT || consoleOption.getChannelType() != ChannelType.TEXT) {
            event.reply(mcord.getConfig().getString("XEmoji") + " | Both channels must be text channels").setEphemeral(true).queue();
            return;
        }

        TextChannel chatChannel = chatOption.getAsChannel().asTextChannel();
        TextChannel consoleChannel = consoleOption.getAsChannel().asTextChannel();

        mcord.getDatabox().setChatChannelId(chatChannel.getIdLong());
        mcord.getDatabox().setConsoleChannelId(consoleChannel.getIdLong());
        Color color = new Color(71, 230, 111);
        EmbedBuilder embed = new EmbedBuilder()
                .setDescription(mcord.getConfig().getString("tickEmoji") + " | Setup complete")
                .setColor(color);
        String reply = mcord.getConfig().getString("replyEmoji");
        String reply2 = mcord.getConfig().getString("reply2Emoji");
        EmbedBuilder embed2 = new EmbedBuilder()
                .setDescription("**Channels**:\n" + reply2 + " Live chat (" + chatChannel.getJumpUrl() + ")\n" + reply + " Console channel (" + consoleChannel.getJumpUrl() + ")")
                .setColor(color);

        event.replyEmbeds(embed.build(), embed2.build()).setEphemeral(true).queue();
        consoleChannel.sendMessage("# This is now a live console bridge to the Minecraft server\n-# MCord").queue();
        consoleChannel.getManager().setTopic("🟢 Console is online! type your commands. | Bukkit: " + mcord.getServer().getBukkitVersion() + " (mcord-"+mcord.getPluginMeta().getVersion() + ") | MC: " + mcord.getServer().getMinecraftVersion()).queue();

        chatChannel.sendMessage("# This is now a live chat bridge to the Minecraft server\n-# MCord").queue();
        chatChannel.getManager().setTopic("🟢 Live chat is online! Chat here to talk with players in-game. (add '!!' before your messages to prevent broadcasting)").queue();
    }
}