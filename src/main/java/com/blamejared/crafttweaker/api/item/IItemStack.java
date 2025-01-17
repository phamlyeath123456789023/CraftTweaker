package com.blamejared.crafttweaker.api.item;


import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.NBTConverter;
import com.blamejared.crafttweaker.impl.actions.items.ActionSetBurnTime;
import com.blamejared.crafttweaker.impl.actions.items.ActionSetFood;
import com.blamejared.crafttweaker.impl.actions.items.ActionSetImmuneToFire;
import com.blamejared.crafttweaker.impl.actions.items.ActionSetMaxDamage;
import com.blamejared.crafttweaker.impl.actions.items.ActionSetMaxStackSize;
import com.blamejared.crafttweaker.impl.actions.items.ActionSetRarity;
import com.blamejared.crafttweaker.impl.data.MapData;
import com.blamejared.crafttweaker.impl.food.MCFood;
import com.blamejared.crafttweaker.impl.item.MCWeightedItemStack;
import com.blamejared.crafttweaker.impl.util.text.MCTextComponent;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.ZenWrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This represents an item.
 * It can be retrieved using an Item BEP.
 * Is an {@link IIngredient}
 *
 * @docParam this <item:minecraft:dirt>
 */
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.item.IItemStack")
@Document("vanilla/api/items/IItemStack")
@ZenWrapper(wrappedClass = "net.minecraft.item.ItemStack", displayStringFormat = "%s.getCommandString()", creationMethodFormat = "new MCItemStack(%s)", implementingClass = "com.blamejared.crafttweaker.impl.item.MCItemStack")
public interface IItemStack extends IIngredient, IIngredientWithAmount {
    
    @ZenCodeType.Field
    String CRAFTTWEAKER_DATA_KEY = "CraftTweakerData";
    
    /**
     * Creates a copy
     */
    @ZenCodeType.Method
    IItemStack copy();
    
    /**
     * Gets the registry name for the Item in this IItemStack
     *
     * @return registry name of the Item this IItemStack represents
     */
    @ZenCodeType.Getter("registryName")
    default ResourceLocation getRegistryName() {
        
        return getInternal().getItem().getRegistryName();
    }
    
    /**
     * Gets owning mod for the Item in this IItemStack
     *
     * @return owning mod of the Item this IItemStack represents
     */
    @ZenCodeType.Getter("owner")
    default String getOwner() {
        
        final ResourceLocation registryName = getInternal().getItem().getRegistryName();
        return registryName == null ? "error" : registryName.getNamespace();
    }
    
    /**
     * Returns if the ItemStack is empty
     *
     * @return true if empty, false if not
     */
    @ZenCodeType.Getter("empty")
    default boolean isEmpty() {
        
        return getInternal().isEmpty();
    }
    
    /**
     * Returns the max stack size of the Item in the ItemStack
     *
     * @return Max stack size of the Item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("maxStackSize")
    default int getMaxStackSize() {
        
        return getInternal().getItem().getItemStackLimit(getInternal());
    }
    
    /**
     * Sets the max stacksize of the Item.
     *
     * @param newMaxStackSize The new max stack size of the Item.
     *
     * @docParam newMaxStackSize 16
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("maxStackSize")
    default void setMaxStackSize(int newMaxStackSize) {
        
        CraftTweakerAPI.apply(new ActionSetMaxStackSize(this, newMaxStackSize, this.getInternal()
                .getItem().maxStackSize));
    }
    
    /**
     * Returns the rarity of the Item in the ItemStack
     *
     * @return Rarity of the Item.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("rarity")
    default Rarity getRarity() {
        
        return getInternal().getRarity();
    }
    
    /**
     * Sets the rarity of the Item.
     *
     * @param newRarity The new rarity of the Item.
     *
     * @docParam newRarity Rarity.UNCOMMON
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("rarity")
    default void setRarity(Rarity newRarity) {
        
        CraftTweakerAPI.apply(new ActionSetRarity(this, newRarity, this.getInternal().getRarity()));
    }
    
    
    /**
     * Gets the display name of the ItemStack
     *
     * @return formatted display name of the ItemStack.
     */
    @ZenCodeType.Getter("displayName")
    default String getDisplayName() {
        
        return getInternal().getDisplayName().getString();
    }
    
    //TODO remove / replace with the global name setter.
    
    /**
     * Sets the display name of the ItemStack
     *
     * @param name New name of the stack.
     *
     * @docParam name "totally not dirt"
     */
    @ZenCodeType.Method
    IItemStack setDisplayName(String name);
    
    /**
     * Sets the display name of the ItemStack
     *
     * @param text New name of the stack.
     *
     * @docParam name "totally not dirt"
     */
    @ZenCodeType.Method
    default IItemStack withDisplayName(MCTextComponent text) {
        
        return setDisplayName(text.asString());
    }
    
    /**
     * Clears any custom name set for this ItemStack
     */
    @ZenCodeType.Method
    default void clearCustomName() {
        
        getInternal().clearCustomName();
    }
    
    /**
     * Returns true if the ItemStack has a display name.
     *
     * @return true if a display name is present on the ItemStack.
     */
    @ZenCodeType.Getter("hasDisplayName")
    default boolean hasDisplayName() {
        
        return getInternal().hasDisplayName();
    }
    
    /**
     * Returns true if this ItemStack has an effect.
     *
     * @return true if there is an effect.
     */
    @ZenCodeType.Getter("hasEffect")
    default boolean hasEffect() {
        
        return getInternal().hasEffect();
    }
    
    /**
     * Can this ItemStack be enchanted?
     *
     * @return true if this ItemStack can be enchanted.
     */
    @ZenCodeType.Getter("isEnchantable")
    default boolean isEnchantable() {
        
        return getInternal().isEnchantable();
    }
    
    /**
     * Is this ItemStack enchanted?
     *
     * @return true if this ItemStack is enchanted.
     */
    @ZenCodeType.Getter("isEnchanted")
    default boolean isEnchanted() {
        
        return getInternal().isEnchanted();
    }
    
    /**
     * Gets the repair cost of the ItemStack, or 0 if no repair is defined.
     *
     * @return ItemStack repair cost or 0 if no repair is set.
     */
    @ZenCodeType.Getter("getRepairCost")
    default int getRepairCost() {
        
        return getInternal().getRepairCost();
    }
    
    /**
     * Gets the amount of Items in the ItemStack
     *
     * @return ItemStack's amount
     */
    @ZenCodeType.Getter("amount")
    default int getAmount() {
        
        return getInternal().getCount();
    }
    
    /**
     * Sets the amount of the ItemStack
     *
     * @param amount new amount
     *
     * @docParam amount 3
     */
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MUL)
    IItemStack setAmount(int amount);
    
    /**
     * Grows this IItemStack's stack size by the given amount, or 1 if no amount is given.
     *
     * @param amount The amount to grow by.
     *
     * @return This IItemStack if mutable, a new one with the new amount otherwise.
     *
     * @docParam amount 2
     */
    @ZenCodeType.Method
    IItemStack grow(@ZenCodeType.OptionalInt(1) int amount);
    
    /**
     * Shrinks this IItemStack's stack size by the given amount, or 1 if no amount is given.
     *
     * @param amount The amount to shrink by.
     *
     * @return This IItemStack if mutable, a new one with the new amount otherwise.
     *
     * @docParam amount 2
     */
    @ZenCodeType.Method
    IItemStack shrink(@ZenCodeType.OptionalInt(1) int amount);
    
    /**
     * Returns if the ItemStack can have an amount greater than 1
     * I.E Swords and tools are not stackable, sticks are.
     *
     * @return true if this ItemStack can contain more than one item.
     */
    @ZenCodeType.Getter("stackable")
    default boolean isStackable() {
        
        return getInternal().isStackable();
    }
    
    /**
     * Sets the damage of the ItemStack
     *
     * @param damage the new damage value
     *
     * @docParam damage 10
     */
    @ZenCodeType.Method
    IItemStack withDamage(int damage);
    
    /**
     * Adds an AttributeModifier to this IItemStack using a specific UUID.
     *
     * The UUID can be used to override an existing attribute on an ItemStack with this new modifier.
     * You can use `/ct hand attributes` to get the UUID of the attributes on an ItemStack.
     *
     * Attributes added with this method will only appear on this specific IItemStack.
     *
     * By defaults, adding a modifier will remove the default Attribute Modifiers on the Item, like the Diamond Chestplate's Armor and Toughness values.
     * When `preserveDefaults` is set to true (by default it is false.), the default Attribute Modifiers will be preserved when adding this modifier.
     * This means that if you were adding the `forge:nametag_distance` attribute to an Item, it would keep its default attributes (like Armor and Toughness values).
     *
     * @param uuid             The unique identifier of the modifier to replace.
     * @param attribute        The Attribute of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Should the default Item Attribute Modifiers be preserved when adding this modifier.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam uuid "8c1b5535-9f79-448b-87ae-52d81480aaa3"
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<equipmentslottype:chest>]
     * @docParam preserveDefaults true
     */
    @ZenCodeType.Method
    IItemStack withAttributeModifier(Attribute attribute, String uuid, String name, double value, AttributeModifier.Operation operation, EquipmentSlotType[] slotTypes, @ZenCodeType.OptionalBoolean boolean preserveDefaults);
    
    /**
     * Adds an AttributeModifier to this IItemStack.
     *
     * The UUID can be used to override an existing attribute on an ItemStack with this new modifier.
     * You can use `/ct hand attributes` to get the UUID of the attributes on an ItemStack.
     *
     * Attributes added with this method will only appear on this specific IItemStack.
     *
     * By defaults, adding a modifier will remove the default Attribute Modifiers on the Item, like the Diamond Chestplate's Armor and Toughness values.
     * When `preserveDefaults` is set to true (by default it is false.), the default Attribute Modifiers will be preserved when adding this modifier.
     * This means that if you were adding the `forge:nametag_distance` attribute to an Item, it would keep its default attributes (like Armor and Toughness values).
     *
     * @param attribute        The Attribute of the modifier.
     * @param name             The name of the modifier.
     * @param value            The value of the modifier.
     * @param operation        The operation of the modifier.
     * @param slotTypes        What slots the modifier is valid for.
     * @param preserveDefaults Should the default Item Attribute Modifiers be preserved when adding this modifier.
     *
     * @docParam attribute <attribute:minecraft:generic.attack_damage>
     * @docParam name "Extra Power"
     * @docParam value 10
     * @docParam operation AttributeOperation.ADDITION
     * @docParam slotTypes [<equipmentslottype:chest>]
     * @docParam preserveDefaults true
     */
    @ZenCodeType.Method
    IItemStack withAttributeModifier(Attribute attribute, String name, double value, AttributeModifier.Operation operation, EquipmentSlotType[] slotTypes, @ZenCodeType.OptionalBoolean boolean preserveDefaults);
    
    /**
     * Gets the Attributes and the AttributeModifiers on this IItemStack for the given EquipmentSlotType
     *
     * @param slotType The slot to get the Attributes for.
     *
     * @return A Map of Attribute to a List of AttributeModifier for the given EquipmentSlotType.
     *
     * @docParam slotType <equipmentslottype:chest>
     */
    @ZenCodeType.Method
    default Map<Attribute, List<AttributeModifier>> getAttributes(EquipmentSlotType slotType) {
        
        // I don't think we expose Collection, so just convert it to a list.
        return getInternal().getAttributeModifiers(slotType)
                .asMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, attributeAttributeModifierEntry -> new ArrayList<>(attributeAttributeModifierEntry
                        .getValue())));
    }
    
    /**
     * Returns if the ItemStack is damageable
     * I.E Swords and tools are damageable, sticks are not.
     *
     * @return true if this ItemStack can take damage.
     */
    @ZenCodeType.Getter("damageable")
    default boolean isDamageable() {
        
        return getInternal().isDamageable();
    }
    
    /**
     * Returns if the ItemStack is damaged
     * I.E a Swords that is no at full durability is damaged.
     *
     * @return true if this ItemStack is damaged.
     */
    @ZenCodeType.Getter("damaged")
    default boolean isDamaged() {
        
        return getInternal().isDamaged();
    }
    
    /**
     * Returns the max damage of the ItemStack
     * This is the max durability of the ItemStack.
     *
     * @return The ItemStack's max durability.
     */
    @ZenCodeType.Getter("maxDamage")
    default int getMaxDamage() {
        
        return getInternal().getMaxDamage();
    }
    
    /**
     * Sets the max damage of the ItemStack.
     *
     * Setting the damage to `0` will make the item unbreakable.
     *
     * @param maxDamage The new max damage of the ItemStack
     *
     * @docParam maxDamage 5
     */
    @ZenCodeType.Setter("maxDamage")
    default void setMaxDamage(int maxDamage) {
        
        CraftTweakerAPI.apply(new ActionSetMaxDamage(this, maxDamage, this.getInternal().getMaxDamage()));
    }
    
    /**
     * Returns the unlocalized Name of the Item in the ItemStack
     *
     * @return the unlocalized name.
     */
    @ZenCodeType.Getter("translationKey")
    default String getTranslationKey() {
        
        return getInternal().getTranslationKey();
    }
    
    /**
     * Sets the tag for the ItemStack.
     *
     * @param tag The tag to set.
     *
     * @return This itemStack if it is mutable, a new one with the changed property otherwise
     *
     * @docParam tag {Display: {lore: ["Hello"]}}
     */
    @ZenCodeType.Method
    IItemStack withTag(IData tag);
    
    /**
     * Removes the tag from this ItemStack.
     *
     * @return This itemStack if it is mutable, a new one with the changed property otherwise
     */
    @ZenCodeType.Method
    IItemStack withoutTag();
    
    
    /**
     * Returns true if this ItemStack has a Tag
     *
     * @return true if tag is present.
     */
    @ZenCodeType.Getter("hasTag")
    default boolean hasTag() {
        
        return getInternal().hasTag();
    }
    
    /**
     * Returns the NBT tag attached to this ItemStack.
     *
     * @return IData of the ItemStack NBT Tag, null if it doesn't exist.
     */
    @ZenCodeType.Getter("tag")
    default IData getTag() {
        
        return NBTConverter.convert(getInternal().getTag());
    }
    
    /**
     * Returns the NBT tag attached to this ItemStack or makes a new tag.
     *
     * @return IData of the ItemStack NBT Tag, empty tag if it doesn't exist.
     */
    @ZenCodeType.Getter("getOrCreate")
    default IData getOrCreateTag() {
        
        if(getInternal().getTag() == null) {
            getInternal().setTag(new CompoundNBT());
        }
        return NBTConverter.convert(getInternal().getTag());
    }
    
    @Override
    default boolean matches(IItemStack stack, boolean ignoreDamage) {
        
        ItemStack stack1 = getInternal();
        ItemStack stack2 = stack.getInternal();
        
        if(stack1.isEmpty() != stack2.isEmpty()) {
            return false;
        }
        if(stack1.getItem() != stack2.getItem()) {
            return false;
        }
        if(stack1.getCount() > stack2.getCount()) {
            return false;
        }
        // This is really just an early exit, since damage is NBT based, it is checked again in the NBT contains
        if(!ignoreDamage) {
            if(stack1.getDamage() != stack2.getDamage()) {
                return false;
            }
        }
        CompoundNBT stack1Tag = stack1.getTag();
        CompoundNBT stack2Tag = stack2.getTag();
        if(stack1Tag == null && stack2Tag == null) {
            return true;
        }
        
        // Lets just use the partial nbt
        MapData stack2Data = (MapData) NBTConverter.convert(stack2Tag);
        MapData stack1Data = (MapData) NBTConverter.convert(stack1Tag);
        if(stack1Data == null) {
            return true;
        }
        if(ignoreDamage) {
            stack1Data = (MapData) stack1Data.copyInternal();
            stack1Data.remove("Damage");
            if(stack2Data != null) {
                stack2Data = (MapData) stack2Data.copyInternal();
                stack2Data.remove("Damage");
            }
        }
        return stack2Data != null && stack2Data.contains(stack1Data);
    }
    
    
    /**
     * Gets the use duration of the ItemStack
     *
     * @return use duration
     */
    @ZenCodeType.Getter("useDuration")
    default int getUseDuration() {
        
        return getInternal().getUseDuration();
    }
    
    /**
     * Returns true if this stack is considered a crossbow item
     *
     * @return true if stack is a crossbow
     */
    @ZenCodeType.Getter("isCrossbow")
    default boolean isCrossbowStack() {
        
        return getInternal().isCrossbowStack();
    }
    
    @ZenCodeType.Getter("food")
    @ZenCodeType.Nullable
    default MCFood getFood() {
        
        final Food food = getInternal().getItem().getFood();
        return food == null ? null : new MCFood(food);
    }
    
    @ZenCodeType.Setter("food")
    default void setFood(MCFood food) {
        
        CraftTweakerAPI.apply(new ActionSetFood(this, food, this.getInternal().getItem().getFood()));
    }
    
    @ZenCodeType.Method
    default boolean isFood() {
        
        return getInternal().isFood();
    }
    
    @ZenCodeType.Getter("burnTime")
    default int getBurnTime() {
        
        return ForgeHooks.getBurnTime(getInternal());
    }
    
    /**
     * Sets the burn time of this item, for use in the furnace and other machines
     *
     * @param time the new burn time
     *
     * @docParam time 500
     */
    @ZenCodeType.Setter("burnTime")
    default void setBurnTime(int time) {
        
        CraftTweakerAPI.apply(new ActionSetBurnTime(this, time));
    }
    
    /**
     * Sets if this IItemStack is immune to fire / lava.
     *
     * If true, the item will not burn when thrown into fire or lava.
     *
     * @param immuneToFire Should the item be immune to fire.
     *
     * @docParam immuneToFire true
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("immuneToFire")
    default void setImmuneToFire(boolean immuneToFire) {
        
        CraftTweakerAPI.apply(new ActionSetImmuneToFire(this, immuneToFire, this.getInternal().getItem().burnable));
    }
    
    
    /**
     * Checks if this IItemStack burns when thrown into fire / lava or damaged by fire.
     *
     * @return True if this IItemStack is immune to fire. False otherwise.
     */
    @ZenCodeType.Method
    @ZenCodeType.Getter("immuneToFire")
    default boolean isImmuneToFire() {
        
        return getInternal().getItem().isImmuneToFire();
    }
    
    @ZenCodeType.Operator(ZenCodeType.OperatorType.MOD)
    default MCWeightedItemStack percent(int percentage) {
        
        return weight(percentage / 100.0D);
    }
    
    @ZenCodeType.Method
    default MCWeightedItemStack weight(double weight) {
        
        return new MCWeightedItemStack(this, weight);
    }
    
    @ZenCodeType.Caster(implicit = true)
    default MCWeightedItemStack asWeightedItemStack() {
        
        return weight(1.0D);
    }
    
    @ZenCodeType.Method
    @ZenCodeType.Getter("definition")
    @ZenCodeType.Caster(implicit = true)
    default Item getDefinition() {
        
        return getInternal().getItem();
    }
    
    // TODO - BREAKING - change this to `asMutable`.
    @ZenCodeType.Method
    IItemStack mutable();
    
    @ZenCodeType.Method
    IItemStack asImmutable();
    
    @ZenCodeType.Method
    @ZenCodeType.Getter("isImmutable")
    boolean isImmutable();
    
    @ZenCodeType.Getter("damage")
    int getDamage();
    
    @ZenCodeType.Getter("toolTypes")
    default ToolType[] getToolTypes() {
        
        return getInternal().getToolTypes().toArray(new ToolType[0]);
    }
    
    @ZenCodeType.Method
    @ZenCodeType.Getter("enchantments")
    default Map<Enchantment, Integer> getEnchantments() {
        
        return EnchantmentHelper.getEnchantments(getInternal());
    }
    
    /**
     * Sets the enchantments on this IItemStack.
     *
     * @param enchantments The new enchantments
     *
     * @return This itemStack if it is mutable, a new one with the enchantments otherwise
     */
    @ZenCodeType.Method
    @ZenCodeType.Setter("enchantments")
    IItemStack setEnchantments(Map<Enchantment, Integer> enchantments);
    
    
    /**
     * Gets the level of the given enchantment on the item. Returns 0 if the item doesn't have the given enchantment.
     */
    @ZenCodeType.Method
    default int getEnchantmentLevel(Enchantment enchantment) {
        
        return getEnchantments().getOrDefault(enchantment, 0);
    }
    
    /**
     * Enchants this IItemStack with the given Enchantment.
     *
     * @param enchantment The enchantment to add.
     * @param level       The level of the enchantment
     *
     * @return This itemStack if it is mutable, a new one with the enchantment added otherwise
     *
     * @docParam enchantment <enchantment:minecraft:riptide>
     * @docParam level 2
     */
    @ZenCodeType.Method
    IItemStack withEnchantment(Enchantment enchantment, @ZenCodeType.OptionalInt(1) int level);
    
    /**
     * Removes the given enchantment from this IItemStack.
     *
     * @param enchantment The enchantment to remove.
     *
     * @return This itemStack if it is mutable, a new one with the enchantment removed otherwise
     *
     * @docParam enchantment <enchantment:minecraft:riptide>
     */
    @ZenCodeType.Method
    IItemStack removeEnchantment(Enchantment enchantment);
    
    /**
     * Gets the internal {@link ItemStack} for this IItemStack.
     *
     * @return internal ItemStack
     */
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    ItemStack getInternal();
    
    @ZenCodeType.Method
    ItemStack getImmutableInternal();
    
    @ZenCodeType.Method
    @ZenCodeType.Caster(implicit = true)
    default IIngredientWithAmount asIIngredientWithAmount() {
        
        return this;
    }
    
    @Override
    default IItemStack getIngredient() {
        
        return this;
    }
    
}
