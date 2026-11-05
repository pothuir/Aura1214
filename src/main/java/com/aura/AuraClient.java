
package com.aura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.text.Text;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AuraClient implements ClientModInitializer {

    private static boolean enabled = false;

    @Override
    public void onInitializeClient() {

        KeyBinding toggle = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.aura1214.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KeyBinding.MISC_CATEGORY
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (toggle.wasPressed()) {
                enabled = !enabled;

                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal(enabled
                            ? "Aura1214: ON"
                            : "Aura1214: OFF"),
                        true
                    );
                }
            }

            if (!enabled || client.player == null ||
                client.world == null ||
                client.interactionManager == null) return;

            LivingEntity target = null;
            double closest = 3.5 * 3.5;

            for (Entity entity : client.world.getEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;
                if (living == client.player || !living.isAlive()) continue;

                double distance =
                    client.player.squaredDistanceTo(living);

                if (distance <= closest) {
                    closest = distance;
                    target = living;
                }
            }

            if (target != null &&
                client.player.getAttackCooldownProgress(0.0F) >= 1.0F) {

                client.interactionManager.attackEntity(
                    client.player, target
                );

                client.player.swingHand(Hand.MAIN_HAND);
            }
        });
    }
}
