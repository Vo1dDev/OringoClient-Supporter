package me.oringo.oringoclient.qolfeatures;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import me.oringo.oringoclient.OringoClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Updater {
   @SubscribeEvent
   public void onGuiCreate(Post event) {
      if (event.gui instanceof GuiMainMenu && OringoClient.shouldUpdate) {
         event.buttonList.add(new GuiButton(-2137, 5, 50, 150, 20, "Update Oringo Client"));
      }

   }

   @SubscribeEvent
   public void onClick(net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post event) {
      if (event.gui instanceof GuiMainMenu && event.button.field_146127_k == -2137) {
         try {
            Desktop.getDesktop().browse(new URI(OringoClient.vers[1]));
            OringoClient.mc.func_71400_g();
         } catch (URISyntaxException | IOException var3) {
            var3.printStackTrace();
         }
      }

   }
}
