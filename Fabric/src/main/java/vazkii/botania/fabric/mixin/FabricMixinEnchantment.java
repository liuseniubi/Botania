package vazkii.botania.fabric.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import vazkii.botania.common.item.ModItems;

@Mixin(Enchantment.class)
public class FabricMixinEnchantment {
	@Inject(method = "canEnchant", cancellable = true, at = @At("HEAD"))
	public void onEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		Enchantment self = (Enchantment) (Object) this;
		if (self == Enchantments.MOB_LOOTING && stack.is(ModItems.elementiumAxe)) {
			cir.setReturnValue(true);
		}
	}
}
