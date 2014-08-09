/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.mods.mfr.machines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.annotations.ModOnly;
import minetweaker.api.block.IBlock;
import minetweaker.api.block.IBlockPattern;
import minetweaker.api.item.WeightedItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.mods.mfr.MFRHacks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.FactoryRegistry;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.api.IFactoryFruit;
import powercrystals.minefactoryreloaded.api.IFactoryHarvestable;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 *
 * @author Stan
 */
@ZenClass("mods.mfr.Harvester")
@ModOnly("MineFactoryReloaded")
public class Harvester {
	@ZenMethod
	public static void addHarvestable(IBlockPattern block, WeightedItemStack drop, @Optional String type) {
		addHarvestable(block, new WeightedItemStack[] { drop }, type);
	}
	
	@ZenMethod
	public static void addHarvestable(IBlockPattern block, WeightedItemStack[] drops, @Optional String type) {
		TweakerHarvestable harvestable = new TweakerHarvestable(block, drops, type);
		MineTweakerAPI.apply(new AddHarvestableAction(harvestable));
	}
	
	@ZenMethod
	public static void removeHarvestable(IBlockPattern block) {
		if (MFRHacks.harvestables == null) {
			MineTweakerAPI.logWarning("Could not remove MFR Harvester harvestable");
		} else {
			MineTweakerAPI.apply(new RemoveHarvestableAction(block));
		}
	}
	
	// #####################
	// ### Inner classes ###
	// #####################
	
	private static class TweakerHarvestable {
		private final IBlockPattern block;
		private final WeightedItemStack[] possibleDrops;
		private final HarvestType type;
		
		public TweakerHarvestable(IBlockPattern block, WeightedItemStack[] possibleDrops, String stringType) {
			this.block = block;
			this.possibleDrops = possibleDrops;
			
			HarvestType type = HarvestType.Normal;
			if (stringType.equals("normal")) {
				type = HarvestType.Normal;
			} else if (stringType.equals("column")) {
				type = HarvestType.Column;
			} else if (stringType.equals("leaveBottom")) {
				type = HarvestType.LeaveBottom;
			} else if (stringType.equals("tree")) {
				type = HarvestType.Tree;
			} else if (stringType.equals("treeFlipped")) {
				type = HarvestType.TreeFlipped;
			} else if (stringType.equals("treeLeaf")) {
				type = HarvestType.TreeLeaf;
			} else {
				throw new IllegalArgumentException("Unknown harvestable type: " + stringType);
			}
			
			this.type = type;
		}
	}
	
	private static class TweakerHarvestablePartial implements IFactoryHarvestable {
		private final int blockId;
		private final List<TweakerHarvestable> harvestables;
		
		public TweakerHarvestablePartial(int blockId) {
			this.blockId = blockId;
			harvestables = new ArrayList<TweakerHarvestable>();
		}

		@Override
		public int getPlantId() {
			return blockId;
		}

		@Override
		public HarvestType getHarvestType() {
			// WARNING: first type will be used
			return harvestables.get(0).type;
		}

		@Override
		public boolean breakBlock() {
			return true;
		}

		@Override
		public boolean canBeHarvested(World world, Map<String, Boolean> map, int x, int y, int z) {
			for (TweakerHarvestable fruit : harvestables) {
				if (fruit.block.matches(MineTweakerMC.getBlock(world, x, y, z)))
					return true;
			}
			
			return false;
		}

		@Override
		public List<ItemStack> getDrops(World world, Random random, Map<String, Boolean> map, int x, int y, int z) {
			IBlock block = MineTweakerMC.getBlock(world, x, y, z);
			
			for (TweakerHarvestable harvestable : harvestables) {
				if (harvestable.block.matches(block))
					return Arrays.asList(MineTweakerMC.getItemStacks(WeightedItemStack.pickRandomDrops(random, harvestable.possibleDrops)));
			}
			
			return Collections.EMPTY_LIST;
		}

		@Override
		public void preHarvest(World world, int x, int y, int z) {
			
		}

		@Override
		public void postHarvest(World world, int x, int y, int z) {
			
		}
	}
	
	// ######################
	// ### Action classes ###
	// ######################
	
	private static class AddHarvestableAction implements IUndoableAction {
		private final TweakerHarvestable harvestable;
		
		public AddHarvestableAction(TweakerHarvestable harvestable) {
			this.harvestable = harvestable;
		}
		
		@Override
		public void apply() {
			Map<Integer, IFactoryHarvestable> harvestables = MFRHacks.harvestables;
			for (IBlock partial : harvestable.block.getBlocks()) {
				int blockId = MineTweakerMC.getBlockId(partial.getDefinition());
				if (harvestable != null && harvestables.containsKey(blockId)) {
					IFactoryHarvestable existingHarvestable = harvestables.get(blockId);
					if (existingHarvestable instanceof TweakerHarvestablePartial) {
						TweakerHarvestablePartial existingHarvestablePartial = (TweakerHarvestablePartial) existingHarvestable;
						if (!existingHarvestablePartial.harvestables.contains(harvestable)) {
							existingHarvestablePartial.harvestables.add(harvestable);
						}
					} else {
						MineTweakerAPI.logError("A non-MineTweaker fruit already exists for this ID");
					}
				} else {
					TweakerHarvestablePartial factoryFruit = new TweakerHarvestablePartial(blockId);
					FactoryRegistry.registerHarvestable(factoryFruit);
				}
			}
		}

		@Override
		public boolean canUndo() {
			return MFRHacks.fruitBlocks != null;
		}

		@Override
		public void undo() {
			Map<Integer, IFactoryHarvestable> harvestables = MFRHacks.harvestables;
			for (IBlock partial : harvestable.block.getBlocks()) {
				int blockId = MineTweakerMC.getBlockId(partial.getDefinition());
				IFactoryHarvestable factoryHarvestable = harvestables.get(blockId);
				if (factoryHarvestable != null && factoryHarvestable instanceof TweakerHarvestablePartial) {
					((TweakerHarvestablePartial) factoryHarvestable).harvestables.remove(harvestable);
				}
			}
		}

		@Override
		public String describe() {
			return "Adding Harvester harvestable block " + harvestable.block.getDisplayName();
		}

		@Override
		public String describeUndo() {
			return "Removing Harvester harvestable block " + harvestable.block.getDisplayName();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
	
	private static class RemoveHarvestableAction implements IUndoableAction {
		private final IBlockPattern block;
		private final Map<Integer, IFactoryFruit> removed;
		
		public RemoveHarvestableAction(IBlockPattern block) {
			this.block = block;
			
			Map<Integer, IFactoryFruit> fruits = MFRHacks.fruitBlocks;
			removed = new HashMap<Integer, IFactoryFruit>();
			for (IBlock partial : block.getBlocks()) {
				int blockId = MineTweakerMC.getBlockId(partial.getDefinition());
				if (fruits.containsKey(blockId)) {
					removed.put(blockId, fruits.get(blockId));
				}
			}
		}
		
		@Override
		public void apply() {
			Map<Integer, IFactoryFruit> fruits = MFRHacks.fruitBlocks;
			for (Integer key : removed.keySet()) {
				fruits.remove(key);
			}
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			Map<Integer, IFactoryFruit> fruits = MFRHacks.fruitBlocks;
			for (Map.Entry<Integer, IFactoryFruit> restore : removed.entrySet()) {
				fruits.put(restore.getKey(), restore.getValue());
			}
		}

		@Override
		public String describe() {
			return "Removing Harvester harvestable block " + block.getDisplayName();
		}

		@Override
		public String describeUndo() {
			return "Restoring Harvester harvestable block " + block.getDisplayName();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
