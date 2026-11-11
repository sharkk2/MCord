package org.sharkk2.mcord.shared;

import org.sharkk2.mcord.shared.enums.CommandAccess;

import java.util.List;

public record CommandSpec(String name, String description, CommandAccess access, List<CommandOption> options) { }