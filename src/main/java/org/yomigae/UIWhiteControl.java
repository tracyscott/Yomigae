package org.yomigae;

import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;

public class UIWhiteControl extends UIConfig {
  public static final String WHITE = "White";
  public static final String AMBER = "Amber";
  public static final String UV = "UV";

  public String filename;

  public int whiteOverride = 0;
  public int amberOverride = 0;
  public int uvOverride = 0;

  public UIWhiteControl(final LXStudio.UI ui, String title, String filenameBase) {
    super(ui, title, filenameBase + ".json");
    filename = filenameBase + ".json";

    registerCompoundParameter(WHITE, 0f, 0f, 255f);
    registerCompoundParameter(AMBER, 0f, 0f, 255f);
    registerCompoundParameter(UV, 0f, 0f, 255f);
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
