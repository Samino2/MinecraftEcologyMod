package ecomod.api.capabilities;

import ecomod.api.pollution.PollutionData;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.concurrent.Callable;

/**
 * An interface which forms Capability and is used to store PollutionData 
 *
 */
public interface IPollution
{
	PollutionData getPollution();
	
	void setPollution(PollutionData new_data);
	
	class Storage implements Capability.IStorage<IPollution>
	{

		@Override
		public NBTBase writeNBT(Capability<IPollution> capability, IPollution instance, EnumFacing side) {
			if(instance == null)
				return null;
			
			return instance.getPollution() != null ? instance.getPollution().writeToNBT(new NBTTagCompound()) : new PollutionData().writeToNBT(new NBTTagCompound());
		}

		@Override
		public void readNBT(Capability<IPollution> capability, IPollution instance, EnumFacing side, NBTBase nbt) {
			
			if(nbt == null || !(nbt instanceof NBTTagCompound))
				return;
			
			PollutionData data = new PollutionData();
			
			data.readFromNBT((NBTTagCompound) nbt);
			
			if(instance != null)
			{
				instance.setPollution(data);
			}
		}
		
	}
	
	class Factory implements Callable<IPollution>
	{
		@Override
		public IPollution call() throws Exception {
			return new PollutionImplementation();
		}
	}
}
