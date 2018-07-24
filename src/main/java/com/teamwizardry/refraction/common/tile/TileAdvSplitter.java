package com.teamwizardry.refraction.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.refraction.Refraction;
import com.teamwizardry.refraction.api.Beam;
import com.teamwizardry.refraction.api.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

@TileRegister("advanced_splitter")
public class TileAdvSplitter extends TileMirrorBase {

    public static int PANE_SLOT = 0;
    private static final ItemStack redPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 14);
    private static final ItemStack greenPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 13);
    private static final ItemStack bluePane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 11);
    private ItemStack lastPane = new ItemStack(Blocks.STAINED_GLASS_PANE, 1, 0);

    @Module
    public ModuleInventory inventory = new ModuleInventory(1 );

    @Override
    protected boolean handleMirrorBeam(Beam beam, Vec3d incomingDir, Vec3d normal) {
        Vec3d outgoingDir = incomingDir.subtract(normal.scale(incomingDir.dotProduct(normal) * 2));

        int newAlpha = 0;//beam.alpha / 2;
        ItemStack currentPane = inventory.getHandler().getStackInSlot(PANE_SLOT);
        if ( checkFireBeam(beam, currentPane, redPane)) {
            //Effect incommingEffect= EffectTracker.getEffect(new Color(beam.getColor().getRed(), 0, 0, newAlpha));
            //Effect outgoingEffect  = EffectTracker.getEffect(new Color(0, beam.getColor().getGreen(), beam.getColor().getBlue(), newAlpha));

            //beam.createSimilarBeam(outgoingDir).setEffect(outgoingEffect).spawn();
            //beam.createSimilarBeam(incomingDir).setEffect(incommingEffect).spawn();
        } else if ( checkFireBeam(beam, currentPane, greenPane)) {
            //Effect incommingEffect = EffectTracker.getEffect(new Color(0, beam.getColor().getGreen(), 0, newAlpha));
            //Effect outgoingEffect  = EffectTracker.getEffect(new Color(beam.getColor().getRed(), 0, beam.getColor().getBlue(), newAlpha));

            // beam.createSimilarBeam(outgoingDir).setEffect(outgoingEffect).spawn();
            //beam.createSimilarBeam(incomingDir).setEffect(incommingEffect).spawn();
        } else if ( checkFireBeam(beam, currentPane, bluePane)) {
            //Effect incommingEffect = EffectTracker.getEffect(new Color(0, 0, beam.getColor().getBlue(), newAlpha));
            //Effect outgoingEffect  = EffectTracker.getEffect(new Color(beam.getColor().getRed(), beam.getColor().getGreen(), 0, newAlpha));

            // beam.createSimilarBeam(outgoingDir).setEffect(outgoingEffect).spawn();
            //beam.createSimilarBeam(incomingDir).setEffect(incommingEffect).spawn();
        } else {
            //Effect effect = EffectTracker.getEffect(new Color(beam.getColor().getRed(), beam.getColor().getGreen(), beam.getColor().getBlue(), newAlpha));

            //beam.createSimilarBeam(outgoingDir).setEffect(effect).spawn();
            //beam.createSimilarBeam(incomingDir).setEffect(effect).spawn();
        }
        return true;
    }

    @Nonnull
    @Override
    public ResourceLocation getMirrorHeadLocation() {
        ItemStack pane = inventory.getHandler().getStackInSlot(PANE_SLOT);
        if (Utils.simpleAreStacksEqual(redPane, pane))
            return new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_splitter_red");
        if (Utils.simpleAreStacksEqual(greenPane, pane))
            return new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_splitter_green");
        if (Utils.simpleAreStacksEqual(bluePane, pane))
            return new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_splitter_blue");
        return new ResourceLocation(Refraction.MOD_ID, "blocks/mirror_splitter");
    }

    @Override
    public boolean reloadTexture() {
        ItemStack currentPane = inventory.getHandler().getStackInSlot(PANE_SLOT);
        if (!Utils.simpleAreStacksEqual(lastPane, currentPane)){
            lastPane = currentPane;
            return true;
        }
        return false;
    }

    private boolean checkFireBeam(Beam beam, ItemStack currentPane, ItemStack paneToCheck) {
        return //(!(beam.effect instanceof EffectMundane) || ConfigValues.ADV_SPLITTER_CAN_SPLIT_MUNDANE) &&
                Utils.simpleAreStacksEqual(currentPane, paneToCheck);
    }

    public boolean isValidPane(ItemStack input) {
        return Utils.simpleAreStacksEqual(redPane, input) ||
                Utils.simpleAreStacksEqual(greenPane, input) ||
                Utils.simpleAreStacksEqual(bluePane, input);
    }

    public boolean hasPane() {
        return inventory.getHandler().getStackInSlot(PANE_SLOT) != ItemStack.EMPTY;
    }
}
