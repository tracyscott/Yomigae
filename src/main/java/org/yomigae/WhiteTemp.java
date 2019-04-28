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
    ColorTemp.convertKToRGB(k, rgb);
    // Convert RGB to HSB and scale by intensityKnob
    float[] hsb = new float[3];
    Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
    hsb[2] = intensityKnob.getValuef() * hsb[2];
    for (LXPoint p : model.points) {
      colors[p.index] = LXColor.hsb(hsb[0] *360.0f, hsb[1] * 100.0f, hsb[2] * 100.0f);
    }
  }
}
