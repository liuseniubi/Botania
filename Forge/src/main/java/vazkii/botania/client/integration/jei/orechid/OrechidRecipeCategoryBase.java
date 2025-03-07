/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei.orechid;

import com.google.common.collect.ListMultimap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;

import vazkii.botania.api.recipe.IOrechidRecipe;
import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.handler.OrechidManager;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.List;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public abstract class OrechidRecipeCategoryBase implements IRecipeCategory<IOrechidRecipe> {

	private final IDrawableStatic background;
	private final Component localizedName;
	private final IDrawableStatic overlay;
	private final IDrawable icon;
	private final ItemStack iconStack;

	public OrechidRecipeCategoryBase(IGuiHelper guiHelper, ItemStack iconStack, Component localizedName) {
		overlay = guiHelper.createDrawable(prefix("textures/gui/pure_daisy_overlay.png"),
				0, 0, 64, 44);
		background = guiHelper.createBlankDrawable(96, 44);
		this.localizedName = localizedName;
		this.icon = guiHelper.createDrawableIngredient(iconStack);
		this.iconStack = iconStack;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return localizedName;
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(IOrechidRecipe recipe, IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, new ItemStack(recipe.getInput(), 64));

		final int myWeight = recipe.getWeight();
		final int amount = Math.max(1, Math.round((float) myWeight * 64 / getTotalOreWeight(getOreWeights(recipe.getInput()), myWeight)));

		// Shouldn't ever return an empty list since the ore weight
		// list is filtered to only have ores with ItemBlocks
		List<ItemStack> stackList = recipe.getOutput().getDisplayedStacks();

		stackList.forEach(s -> s.setCount(amount));
		ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(stackList));
	}

	public static float getTotalOreWeight(List<? extends IOrechidRecipe> weights, int myWeight) {
		return weights.stream()
				.map(IOrechidRecipe::getWeight)
				.reduce(Integer::sum).orElse(myWeight * 64 * 64);
	}

	protected abstract RecipeType<? extends IOrechidRecipe> recipeType();

	protected List<? extends IOrechidRecipe> getOreWeights(Block input) {
		ListMultimap<Block, ? extends IOrechidRecipe> multimap = OrechidManager.getFor(Minecraft.getInstance().level.getRecipeManager(), recipeType());
		return multimap.get(input);
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IOrechidRecipe recipe, @Nonnull IIngredients ingredients) {
		final IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

		itemStacks.init(0, true, 9, 12);
		itemStacks.set(0, ingredients.getInputs(VanillaTypes.ITEM).get(0));

		itemStacks.init(1, true, 39, 12);
		itemStacks.set(1, iconStack);

		itemStacks.init(2, true, 68, 12);
		itemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

		StateIngredient catalyst = recipe.getOutput();
		List<Component> description = catalyst.descriptionTooltip();
		if (!description.isEmpty()) {
			recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
				if (slotIndex == 2) {
					tooltip.addAll(description);
				}
			});
		}
	}

	@Override
	public void draw(IOrechidRecipe recipe, PoseStack ms, double mouseX, double mouseY) {
		RenderSystem.enableBlend();
		overlay.draw(ms, 17, 0);
		RenderSystem.disableBlend();
	}

}
