package org.sharkk2.mcord.commands.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.sharkk2.mcord.shared.CommandSpec;
import org.sharkk2.mcord.shared.enums.CommandAccess;

public interface SlashCommand {
    CommandSpec spec();

    void onUse(SlashCommandInteractionEvent event);
    default String name() {
        return spec().name();
    }

    default String description() {
        return spec().description();
    }
}