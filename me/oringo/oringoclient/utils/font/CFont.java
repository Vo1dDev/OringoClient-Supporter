package me.oringo.oringoclient.utils.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFont {
   float imgSize = 512.0F;
   CFont.CharData[] charData = new CFont.CharData[256];
   Font font;
   boolean antiAlias;
   boolean fractionalMetrics;
   int fontHeight = -1;
   int charOffset = 0;
   DynamicTexture tex;

   public CFont(Font font, boolean antiAlias, boolean fractionalMetrics) {
      this.font = font;
      this.antiAlias = antiAlias;
      this.fractionalMetrics = fractionalMetrics;
      this.tex = this.setupTexture(font, antiAlias, fractionalMetrics, this.charData);
   }

   protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CFont.CharData[] chars) {
      BufferedImage img = this.generateFontImage(font, antiAlias, fractionalMetrics, chars);

      try {
         return new DynamicTexture(img);
      } catch (Exception var7) {
         var7.printStackTrace();
         return null;
      }
   }

   protected BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CFont.CharData[] chars) {
      int imgSize = (int)this.imgSize;
      BufferedImage bufferedImage = new BufferedImage(imgSize, imgSize, 2);
      Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
      graphics.setFont(font);
      graphics.setColor(new Color(255, 255, 255, 0));
      graphics.fillRect(0, 0, imgSize, imgSize);
      graphics.setColor(Color.WHITE);
      graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
      graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      int charHeight = 0;
      int positionX = 0;
      int positionY = 1;

      for(int index = 0; index < chars.length; ++index) {
         char c = (char)index;
         CFont.CharData charData = new CFont.CharData();
         Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(c), graphics);
         charData.width = dimensions.getBounds().width + 8;
         charData.height = dimensions.getBounds().height;
         if (positionX + charData.width >= imgSize) {
            positionX = 0;
            positionY += charHeight;
            charHeight = 0;
         }

         if (charData.height > charHeight) {
            charHeight = charData.height;
         }

         charData.storedX = positionX;
         charData.storedY = positionY;
         if (charData.height > this.fontHeight) {
            this.fontHeight = charData.height;
         }

         chars[index] = charData;
         graphics.drawString(String.valueOf(c), positionX + 2, positionY + fontMetrics.getAscent());
         positionX += charData.width;
      }

      return bufferedImage;
   }

   public void drawChar(CFont.CharData[] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
      if (chars[c] != null) {
         this.drawQuad(x, y, (float)chars[c].width, (float)chars[c].height, (float)chars[c].storedX, (float)chars[c].storedY, (float)chars[c].width, (float)chars[c].height);
      }
   }

   protected void drawQuad(float x2, float y2, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
      float renderSRCX = srcX / this.imgSize;
      float renderSRCY = srcY / this.imgSize;
      float renderSRCWidth = srcWidth / this.imgSize;
      float renderSRCHeight = srcHeight / this.imgSize;
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x2 + width), (double)y2);
      GL11.glTexCoord2f(renderSRCX, renderSRCY);
      GL11.glVertex2d((double)x2, (double)y2);
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x2, (double)(y2 + height));
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x2, (double)(y2 + height));
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x2 + width), (double)y2);
   }

   public void drawMultiColoredCharTB(CFont.CharData[] chars, char c, float x, float y, int topColor, int bottomColor) throws ArrayIndexOutOfBoundsException {
      if (chars[c] != null) {
         this.drawQuadTB(x, y, (float)chars[c].width, (float)chars[c].height, (float)chars[c].storedX, (float)chars[c].storedY, (float)chars[c].width, (float)chars[c].height, topColor, bottomColor);
      }
   }

   public void drawMulticoloredCharLR(CFont.CharData[] chars, char c, float x, float y, int leftColor, int rightColor) throws ArrayIndexOutOfBoundsException {
      if (chars[c] != null) {
         this.drawQuadLR(x, y, (float)chars[c].width, (float)chars[c].height, (float)chars[c].storedX, (float)chars[c].storedY, (float)chars[c].width, (float)chars[c].height, leftColor, rightColor);
      }
   }

   protected void drawQuadTB(float x2, float y2, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight, int topColor, int bottomColor) {
      float renderSRCX = srcX / this.imgSize;
      float renderSRCY = srcY / this.imgSize;
      float renderSRCWidth = srcWidth / this.imgSize;
      float renderSRCHeight = srcHeight / this.imgSize;
      GlStateManager.func_179124_c((float)(topColor >> 16 & 255) / 255.0F, (float)(topColor >> 8 & 255) / 255.0F, (float)(topColor & 255) / 255.0F);
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x2 + width), (double)y2);
      GL11.glTexCoord2f(renderSRCX, renderSRCY);
      GL11.glVertex2d((double)x2, (double)y2);
      GlStateManager.func_179124_c((float)(bottomColor >> 16 & 255) / 255.0F, (float)(bottomColor >> 8 & 255) / 255.0F, (float)(bottomColor & 255) / 255.0F);
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x2, (double)(y2 + height));
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x2, (double)(y2 + height));
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
      GlStateManager.func_179124_c((float)(topColor >> 16 & 255) / 255.0F, (float)(topColor >> 8 & 255) / 255.0F, (float)(topColor & 255) / 255.0F);
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x2 + width), (double)y2);
   }

   protected void drawQuadLR(float x2, float y2, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight, int leftColor, int rightColor) {
      float renderSRCX = srcX / this.imgSize;
      float renderSRCY = srcY / this.imgSize;
      float renderSRCWidth = srcWidth / this.imgSize;
      float renderSRCHeight = srcHeight / this.imgSize;
      GlStateManager.func_179124_c((float)(rightColor >> 16 & 255) / 255.0F, (float)(rightColor >> 8 & 255) / 255.0F, (float)(rightColor & 255) / 255.0F);
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x2 + width), (double)y2);
      GlStateManager.func_179124_c((float)(leftColor >> 16 & 255) / 255.0F, (float)(leftColor >> 8 & 255) / 255.0F, (float)(leftColor & 255) / 255.0F);
      GL11.glTexCoord2f(renderSRCX, renderSRCY);
      GL11.glVertex2d((double)x2, (double)y2);
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x2, (double)(y2 + height));
      GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)x2, (double)(y2 + height));
      GlStateManager.func_179124_c((float)(rightColor >> 16 & 255) / 255.0F, (float)(rightColor >> 8 & 255) / 255.0F, (float)(rightColor & 255) / 255.0F);
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
      GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
      GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
      GL11.glVertex2d((double)(x2 + width), (double)y2);
   }

   public void setAntiAlias(boolean antiAlias) {
      if (this.antiAlias != antiAlias) {
         this.antiAlias = antiAlias;
         this.tex = this.setupTexture(this.font, antiAlias, this.fractionalMetrics, this.charData);
      }

   }

   public boolean isFractionalMetrics() {
      return this.fractionalMetrics;
   }

   public void setFractionalMetrics(boolean fractionalMetrics) {
      if (this.fractionalMetrics != fractionalMetrics) {
         this.fractionalMetrics = fractionalMetrics;
         this.tex = this.setupTexture(this.font, this.antiAlias, fractionalMetrics, this.charData);
      }

   }

   public void setFont(Font font) {
      this.font = font;
      this.tex = this.setupTexture(font, this.antiAlias, this.fractionalMetrics, this.charData);
   }

   protected static class CharData {
      public int width;
      public int height;
      public int storedX;
      public int storedY;
   }
}
