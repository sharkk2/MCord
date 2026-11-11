package org.sharkk2.mcord.shared;

import org.sharkk2.mcord.shared.enums.CommandOptionType;

public record CommandOption(String name, String description, CommandOptionType type, boolean required) {}