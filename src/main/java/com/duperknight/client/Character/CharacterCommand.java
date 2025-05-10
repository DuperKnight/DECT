package com.duperknight.client.Character;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CharacterCommand {
    private static final int ITEMS_PER_PAGE = 5;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("character")
                        .then(literal("create")
                                .then(argument("name", StringArgumentType.word())
                                        .then(argument("speechStyle", StringArgumentType.greedyString())
                                                .executes(ctx -> {
                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    String style = StringArgumentType.getString(ctx, "speechStyle");
                                                    if (CharacterManager.createCharacter(name, style)) {
                                                        ctx.getSource().sendFeedback(Text.of("Created: " + name));
                                                    } else {
                                                        ctx.getSource().sendFeedback(Text.of("Already exists: " + name));
                                                    }
                                                    return 1;
                                                }))))
                        .then(literal("info")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests((ctx, sb) -> suggestNames(sb))
                                        .executes(ctx -> {
                                            String n = StringArgumentType.getString(ctx, "name");
                                            CharacterData d = CharacterManager.getCharacter(n);
                                            if (d != null) {
                                                MutableText infoText = Text.literal("Name: " + d.name + ", Style: ");
                                                infoText.append(Text.literal(d.speechStyle.replace('&', '§')));
                                                ctx.getSource().sendFeedback(infoText);
                                            } else {
                                                ctx.getSource().sendFeedback(Text.of("Not found: " + n));
                                            }
                                            return 1;
                                        })))
                        .then(literal("config")
                                .then(literal("distance")
                                        .then(literal("get")
                                                .executes(ctx -> {
                                                    int currentDistance = CharacterManager.getBroadcastDistance();
                                                    ctx.getSource().sendFeedback(Text.of("Current broadcast distance: " + currentDistance));
                                                    return 1;
                                                }))
                                        .then(literal("set")
                                                .then(argument("value", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> {
                                                            int distance = IntegerArgumentType.getInteger(ctx, "value");
                                                            CharacterManager.setBroadcastDistance(distance);
                                                            ctx.getSource().sendFeedback(Text.of("Broadcast distance set to: " + distance));
                                                            return 1;
                                                        })))))
                        .then(literal("list")
                                .executes(ctx -> {
                                    List<String> names = CharacterManager.listNames();
                                    FabricClientCommandSource source = ctx.getSource();
                                    if (names.isEmpty()) {
                                        source.sendFeedback(Text.of("No characters defined."));
                                    } else {
                                        source.sendFeedback(Text.of("Characters:"));
                                        for (String name : names) {
                                            CharacterData data = CharacterManager.getCharacter(name);
                                            if (data != null) {
                                                MutableText characterEntry = Text.literal("- " + data.name + " -> '");
                                                characterEntry.append(Text.literal(data.speechStyle.replace('&', '§') + " My name is " + data.name + "§r'"));
                                                source.sendFeedback(characterEntry);
                                            }
                                        }
                                    }
                                    return 1;
                                }))
                        .then(literal("current")
                                .executes(ctx -> {
                                    String curr = CharacterManager.getCurrent();
                                    ctx.getSource().sendFeedback(
                                            Text.of(curr != null ? "Current: " + curr : "No character selected.")
                                    );
                                    return 1;
                                }))
                        .then(literal("select")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests((ctx, sb) -> suggestNames(sb))
                                        .executes(ctx -> {
                                            String n = StringArgumentType.getString(ctx, "name");
                                            if (CharacterManager.selectCharacter(n)) {
                                                ctx.getSource().sendFeedback(Text.of("Selected: " + n));
                                            } else {
                                                ctx.getSource().sendFeedback(Text.of("Not found: " + n));
                                            }
                                            return 1;
                                        })))
                        .then(literal("reset")
                                .executes(ctx -> {
                                    CharacterManager.resetCurrent();
                                    ctx.getSource().sendFeedback(Text.of("Selection reset."));
                                    return 1;
                                }))
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests((ctx, sb) -> suggestNames(sb))
                                        .executes(ctx -> {
                                            String n = StringArgumentType.getString(ctx, "name");
                                            if (CharacterManager.removeCharacter(n)) {
                                                ctx.getSource().sendFeedback(Text.of("Removed: " + n));
                                            } else {
                                                ctx.getSource().sendFeedback(Text.of("Not found: " + n));
                                            }
                                            return 1;
                                        })))
        );
    }

    private static CompletableFuture<Suggestions> suggestNames(SuggestionsBuilder sb) {
        for (String s : CharacterManager.listNames()) {
            if (s.toLowerCase().startsWith(sb.getRemaining().toLowerCase())) {
                sb.suggest(s);
            }
        }
        return sb.buildFuture();
    }
}
