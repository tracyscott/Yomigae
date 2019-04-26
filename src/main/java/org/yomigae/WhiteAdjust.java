package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;

@LXCategory(LXCategory.TEST)
public class WhiteAdjust extends LXPattern {
  public final CompoundParameter amberLvl = new CompoundParameter("AmberLvl", 0.5f, 0.0f, 1.0f).setDescription("Amber LED intensity");
  public final CompoundParameter whiteLvl = new CompoundParameter("WhiteLvl", 0.5f, 0.0f, 1.0f).setDescription("White LED intensity");
  public final CompoundParameter uvLvl = new CompoundParameter("UvLvl", 0.0f, 0.0f, 1.0f).setDescription("UV LED intensity");
  public final CompoundParameter rgbLvl = new CompoundParameter("RgbLvl", 0.5f, 0.0f, 1.0f).setDescription("RBG led intensity");

  public WhiteAdjust(LX lx) {
    super(lx);
    addParameter(amberLvl);
    addParameter(whiteLvl);
    addParameter(uvLvl);
    addParameter(rgbLvl);
  }

  /**
   * TODO(tracy): Need to control various LED groups separately.  Each fixture consists of 18 w-a-u-r-g-b led groups.
   * Need to reconcile with LX Studio definition color buffer.  Simplest way would just to be to have 6 co-located
   * points per led position.  For now, let's just set everything to 50% gray until we get E1.31 output working.
   *
   * @param deltaMs
   */
  public void run(double deltaMs) {
    for (LXPoint p : model.points) {
      colors[p.index] = LXColor.gray(0.5f);
    }
  }
}
