/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.mana;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ManaNetworkCallback {
	Event<ManaNetworkCallback> EVENT = EventFactory.createArrayBacked(ManaNetworkCallback.class,
			listeners -> (be, typ, act) -> {
				for (ManaNetworkCallback listener : listeners) {
					listener.onNetworkChange(be, typ, act);
				}
			});

	void onNetworkChange(BlockEntity be, ManaBlockType type, ManaNetworkAction action);
}
