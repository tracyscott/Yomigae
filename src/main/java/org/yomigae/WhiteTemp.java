package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

import java.awt.*;

@LXCategory(LXCategory.TEST)
public class WhiteTemp extends LXPattern {
  public final CompoundParameter tempKnob = new CompoundParameter("Kelvins", 1900f, 1900f, 7000f).setDescription("R");
  public final CompoundParameter intensityKnob = new CompoundParameter("Intensity", 1.0f, 0.0f, 1.0f);

  public WhiteTemp(LX lx) {
    super(lx);
    addParameter(tempKnob);
    addParameter(intensityKnob);
  }

  /**
   *  Allow manual specification of R, G, B.  Useful for testing lights for color correction purposes.
   * @param deltaMs
   */
  public void run(double deltaMs) {
    int[] rgb = new int[3];
    float k = tempKnob.getValuef();
    convertKToRGB(k, rgb);
    // Convert RGB to HSB and scale by intensityKnob
    float[] hsb = new float[3];
    Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
    hsb[2] = intensityKnob.getValuef() * hsb[2];
    for (LXPoint p : model.points) {
      colors[p.index] = LXColor.hsb(hsb[0] *360.0f, hsb[1] * 100.0f, hsb[2] * 100.0f);
    }
  }

  void convertKToRGB(float k, int[] rgb)
  {
    int red, green, blue;

    if (k < 1000)
      k = 1000;
    else if (k > 40000)
      k = 40000;
    float tmp = k / 100.0f;

    if (tmp <= 66.0f) {
      red = 255;
    } else {
      float tmpRed = 329.698727446f * (float)Math.pow(tmp - 60.0f, -0.1332047592f);
      if (tmpRed < 0) {
        red = 0;
      } else if (tmpRed > 255) {
        red = 255;
      } else {
        red = (int)tmpRed;
      }
    }

    float tmpGreen;
    if (tmp <= 66.0f) {
      tmpGreen = 99.4708025861f * (float)Math.log(tmp) - 161.1195681661f;
    } else {
      tmpGreen = 288.1221695283f *(float)Math.pow(tmp - 60.0f, -0.0755148492f);
    }
    if (tmpGreen < 0.0f) {
      green = 0;
    } else if (tmpGreen > 255.0f) {
      green = 255;
    } else {
      green = (int)tmpGreen;
    }

    if (tmp > 66.0f) {
      blue = 255;
    } else if (tmp < 19.0f) {
      blue = 0;
    } else {
      float tmpBlue = 138.5177312231f * (float)Math.log(tmp - 10.0f) - 305.0447927307f;
      if (tmpBlue < 0.0f) {
        blue = 0;
      } else if (tmpBlue > 255.0f) {
        blue = 255;
      } else {
        blue = (int)tmpBlue;
      }
    }
    rgb[0] = red;
    rgb[1] = green;
    rgb[2] = blue;
  }
}
