package me.oringo.oringoclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

public class StencilUtils {
   static Minecraft mc = Minecraft.func_71410_x();

   public static void checkSetupFBO(Framebuffer framebuffer) {
      if (framebuffer != null && framebuffer.field_147624_h > -1) {
         setupFBO(framebuffer);
         framebuffer.field_147624_h = -1;
      }

   }

   public static void setupFBO(Framebuffer framebuffer) {
      EXTFramebufferObject.glDeleteRenderbuffersEXT(framebuffer.field_147624_h);
      int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
      EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferID);
      EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, mc.field_71443_c, mc.field_71440_d);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferID);
      EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferID);
   }

   public static void initStencil() {
      initStencil(mc.func_147110_a());
   }

   public static void initStencil(Framebuffer framebuffer) {
      framebuffer.func_147610_a(false);
      checkSetupFBO(framebuffer);
      GL11.glClear(1024);
      GL11.glEnable(2960);
   }

   public static void bindWriteStencilBuffer() {
      GL11.glStencilFunc(519, 1, 1);
      GL11.glStencilOp(7681, 7681, 7681);
      GL11.glColorMask(false, false, false, false);
   }

   public static void bindReadStencilBuffer(int ref) {
      GL11.glColorMask(true, true, true, true);
      GL11.glStencilFunc(514, ref, 1);
      GL11.glStencilOp(7680, 7680, 7680);
   }

   public static void uninitStencil() {
      GL11.glDisable(2960);
   }
}
