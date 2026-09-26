package com.nugget;

import com.nugget.client.CameraController;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.model.animal.camel.CamelModel;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;


public class NuggetSIdle implements ModInitializer {
	public static final String MOD_ID = "nuggets-idle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static CameraController camcontroller;
    public static boolean hideGUI = false;
    public static int lastInput=0;
    public static int curTick=0;

    @Override
    public void onInitialize() {
        camcontroller = new CameraController(LOGGER);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("trigger")
                    .executes(context -> {
                        context.getSource().sendSuccess(() -> Component.literal("Triggering..."), false);
                        camcontroller.toggle(true);
                        return 1;
                    }));
            dispatcher.register(Commands.literal("shuffle")
                    .executes(context -> {
                        context.getSource().sendSuccess(() -> Component.literal("Shuffling..."), false);
                        camcontroller.shuffle();
                        return 1;
                    }));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            camcontroller.tick(client);
            curTick++;

            //2400=2 minutes
            if(curTick-lastInput>2400 && !camcontroller.isEnabled()) {
                camcontroller.toggle(true);
            }
        });

        LOGGER.info("Hello Fabric world!");
    }

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
