/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.mc172.block;

import minetweaker.api.block.IBlockDefinition;
import net.minecraft.block.Block;

/**
 *
 * @author Stan
 */
public class MCBlockDefinition implements IBlockDefinition {
	private final Block block;
	
	public MCBlockDefinition(Block block) {
		this.block = block;
	}

	@Override
	public String getId() {
		return Block.blockRegistry.getNameForObject(block);
	}
}
