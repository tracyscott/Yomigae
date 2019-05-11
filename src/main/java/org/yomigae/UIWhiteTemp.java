package org.yomigae;

import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;

/**
 * Class for configuring the white temperature values for a given fixture.  Once we have an accurate measurement
 * of their temperature profile, we can combine them to achieve our target temperature value.
 */
public class UIWhiteTemp extends UIConfig {
  public static final String RGB10 = "Rgb10";
  public static final String RGB25 = "Rgb25";
  public static final String RGB50 = "Rgb50";
  public static final String RGB75 = "Rgb75";
  public static final String RGB100 = "Rgb100";
  public static final String WHITE = "White";
  public static final String AMBER = "Amber";
  public static final String UV = "UV";

  public String filename;

  public int whiteOverride = 0;
  public int amberOverride = 0;
  public int uvOverride = 0;

  /**
   * Notes:(tracy)  For the production pars:
   * RGB combined: 2780 @ 26, 2410 @ 64, 2840 @ 127, 3860 @
   * @param ui
   * @param title
   * @param filenameBase
   */
  public UIWhiteTemp(final LXStudio.UI ui, String title, String filenameBase) {
    super(ui, title, filenameBase + ".json");
    filename = filenameBase + ".json";

    // Par Temp = 2780, Cd = 32.9, LED Bar Temp = 12,200K, Cd = 108
    registerCompoundParameter(RGB10, 2780f, 0f, 20000f);
    // Par Temp 2410, Cd = 167, LED Bar Temp = 13,300K, Cd = 250
    registerCompoundParameter(RGB25, 2410f, 0f, 20000f);
    // Par Temp 2840, Cd = 456, LED Bar Temp = 13,500K, Cd = 334
    registerCompoundParameter(RGB50, 2840f, 0f, 20000f);
    // Par Temp 3860, Cd = 1160, LED Bar Temp = 13,600K, Cd = 509
    registerCompoundParameter(RGB75, 3860f, 0f, 20000f);
    // Par Temp 4560, Cd = 1590, LED Bar Temp = 13000K, Cd = 801
    registerCompoundParameter(RGB100, 4560f, 0f, 20000f);
    // Par Temp 5280, Cd = 696, LED Bar Temp = 5370K, Cd = 357
    registerCompoundParameter(WHITE, 5280f, 0f, 10000f);
    // Par Temp 1670, Cd = 292, Led Bar Temp = 1750K, Cd = 105
    registerCompoundParameter(AMBER, 1670f, 0f, 10000f);
    // Cd = 72.9
    registerCompoundParameter(UV, 100000f, 0f, 200000f);
    save();
    buildUI(ui);
  }

  @Override
  public void onParameterChanged(LXParameter p) {
    super.onParameterChanged(p);
    if (WHITE.equals(p.getLabel())) {
      whiteOverride = (int)p.getValuef();
    } else if (AMBER.equals(p.getLabel())) {
      amberOverride = (int)p.getValuef();
    } else if (UV.equals(p.getLabel())) {
      uvOverride = (int)p.getValuef();
    }
  }
}
