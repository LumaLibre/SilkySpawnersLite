package us.thezircon.play.silkyspawnerslite.nms;
/*
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTTagString;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NMS_1_13 implements nmsHandler {

    @Override
    public ItemStack set(String key, ItemStack item, String data) {
        net.minecraft.server.v1_13_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nmsItemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();

        if (nmsItemCompound == null) {
            return null;
        }

        //EntityType entity = EntityType.valueOf(data); //

        nmsItemCompound.set(key, new NBTTagString(data));
        //nmsItemCompound.set(key, NBTTagString.a(entity.name()));
        nmsItem.setTag(nmsItemCompound);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public String get(String key, ItemStack item) {
        net.minecraft.server.v1_13_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nmsItemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        if (nmsItemCompound == null) {
            return null;
        }

        String nbtKey = nmsItemCompound.getString(key);
        //EntityType entity = null;

        //try {
        //    entity = EntityType.valueOf(nbtKey);
        //} catch (Exception ignore) {}

        //return entity;
        return nbtKey;
    }
}

 */