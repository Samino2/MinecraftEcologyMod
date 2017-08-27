package ecomod.common.items;

import java.util.List;

import ecomod.api.EcomodStuff;
import ecomod.api.client.IRenderableHeadArmor;
import ecomod.api.pollution.IRespirator;
import ecomod.api.pollution.PollutionData.PollutionType;
import ecomod.common.pollution.PollutionUtils;
import ecomod.common.utils.EMUtils;
import ecomod.core.EcologyMod;
import ecomod.core.stuff.EMAchievements;
import ecomod.core.stuff.EMConfig;
import ecomod.core.stuff.EMItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRespirator extends ItemArmor implements IRespirator, IRenderableHeadArmor
{
	public ItemRespirator() {
		super(EMItems.RESPIRATOR_MATERIAL, 1, EntityEquipmentSlot.HEAD);
		
		this.setCreativeTab(EcomodStuff.ecomod_creative_tabs);
		
	}

	@Override
	public boolean isRespirating(EntityLivingBase entity, ItemStack stack, boolean decr)
	{
		if(entity instanceof EntityPlayer)
		{		
			NBTTagCompound nbt = stack.getTagCompound();
			
			EntityPlayer player = (EntityPlayer)entity;
			/*
			if(player.ticksExisted % 60 == 0)
			if(player.getHeldItemMainhand().hasCapability(EcomodStuff.CAPABILITY_POLLUTION, null))
			{
				//EcologyMod.log.info("Has cap pollution!!!");
				//player.getHeldItemMainhand().getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).setPollution(player.getHeldItemMainhand().getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution().clone().add(PollutionType.AIR, 50));
				EcologyMod.log.info(player.getHeldItemMainhand().getCapability(EcomodStuff.CAPABILITY_POLLUTION, null).getPollution());
				
				// INVOKEVIRTUAL net/minecraft/item/Item.onEntityItemUpdate (Lnet/minecraft/entity/item/EntityItem;)Z
				// stack.getItem().onEntityItemUpdate(null);
			}*/
			
			if(nbt != null)
			{
				int nf;
			
				if(nbt.hasKey("filter"))
				{
					if(nbt.getInteger("filter") > 0)
					{
						if(decr)
							nbt.setInteger("filter", nbt.getInteger("filter")-((entity.getHealth() >= (entity.getMaxHealth()/2)) ? 1 : 2));
						
						return true;
					}
					else
					{
						int k = getFilterInInventory(player);
						
						if(k != -1)
						{
							ItemStack stk = player.inventory.getStackInSlot(k);
							stk.shrink(1);
							player.inventory.setInventorySlotContents(k, stk);
							
							nbt.setInteger("filter", EMConfig.filter_durability);
							
							stack.setTagCompound(nbt);
							
							return true;
						}
					}
				}
				else
				{
					int k = getFilterInInventory(player);
					
					if(k != -1)
					{
						ItemStack stk = player.inventory.getStackInSlot(k);
						stk.shrink(1);
						player.inventory.setInventorySlotContents(k, stk);
						
						nbt.setInteger("filter", EMConfig.filter_durability);
						
						stack.setTagCompound(nbt);
						
						return true;
					}
				}
			}
			else
			{
				nbt = new NBTTagCompound();
				
				int k = getFilterInInventory(player);
				
				if(k != -1)
				{
					ItemStack stk = player.inventory.getStackInSlot(k);
					stk.shrink(1);
					player.inventory.setInventorySlotContents(k, stk);
					
					nbt.setInteger("filter", EMConfig.filter_durability);
				}
				
				stack.setTagCompound(nbt);
			}
		}
		return false;
	}

	
	public int getFilterInInventory(EntityPlayer player)
	{
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            
            if(!itemstack.isEmpty())
            	if(itemstack.getItem() instanceof ItemCore && itemstack.getMetadata() == 0)
            	{
            		return i;
            	}
        }
		
		return -1;
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) 
	{
		super.onArmorTick(world, player, itemStack);
		
		if(!world.isRemote)
		{
			if(player.ticksExisted % (player.getHealth() >= (player.getMaxHealth() / 2) ? 60 : 30) == 0)
			{
				if(PollutionUtils.isEntityRespirating(player, false))
					world.playSound(null, new BlockPos(player.posX, player.posY, player.posZ), SoundEvents.ENTITY_PLAYER_BREATH, SoundCategory.PLAYERS, 1.5F, 0.35F+world.rand.nextInt(35)/100F);
			}
		}
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if(nbt != null && nbt.hasKey("filter"))
		{
			int i = (int)(((float)Math.max(nbt.getInteger("filter"), 0))/EMConfig.filter_durability * 100);
			if(i == 0)
				tooltip.add("Insert a filter!");
			tooltip.add("Filter capacity: "+i+"%");
		}
		else
		{
			tooltip.add("Insert a filter!");
		}
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		
		if(worldIn.isRemote)
			return;
		/*
		Achievement ach = EMAchievements.ACHS.get("respirator");
		
		if(ach != null)
		if(!playerIn.hasAchievement(ach))
		{
			playerIn.addStat(ach);
		}*/
	}
}
