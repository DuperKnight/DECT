package com.duperknight.client;

import net.fabricmc.api.ClientModInitializer;
import com.duperknight.client.Character.CharacterManager;
import com.duperknight.client.Character.CharacterCommand;
import com.duperknight.client.Character.CharacterData;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;

public class DectClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CharacterManager.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            CharacterCommand.register(dispatcher);
        });

        ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
            String currentCharacterName = CharacterManager.getCurrent();
            if (currentCharacterName != null) {
                CharacterData characterData = CharacterManager.getCharacter(currentCharacterName);
                if (characterData != null) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player != null && client.player.networkHandler != null) {
                        String commandToSend = "localbroadcast " + CharacterManager.getBroadcastDistance() + " " + characterData.speechStyle + " " + message;
                        client.player.networkHandler.sendChatCommand(commandToSend);
                        return false;
                    }
                }
            }
            return true;
        });
    }
}
