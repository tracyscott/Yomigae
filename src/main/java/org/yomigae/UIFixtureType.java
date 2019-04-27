package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.studio.LXStudio;

public class UIFixtureType extends UIConfig {
  static public final String filename = "fixturetype.json";
  static public final String title = "FixtureType";

  public Output.LightType lightType;
  boolean parameterChanged = false;
  public LX lx;

  public UIFixtureType(final LXStudio.UI ui, LX lx) {
    super(ui, title, filename);
    this.lx = lx;
    registerDiscreteParameter("Type", 1, 1, Output.LightType.PRODWASH.getValue() + 1);
    // TODO(tracy): UIToggleSet is crashing for some reason, just use a discrete knob for now.
    String[] toggleOptions = {"LaLuce", "Prod Par", "Prod Wash"};
    buildUI(ui);
  }

  @Override
  public void onParameterChanged(LXParameter p) {
    super.onParameterChanged(p);
    int which = (int)p.getValue();
    lightType = Output.LightType.valueOf(which);
    parameterChanged = true;
  }

  @Override
  public void onSave() {
    if (parameterChanged) {
      boolean originalEnabled = lx.engine.output.enabled.getValueb();
      lx.engine.output.enabled.setValue(false);
      lx.engine.output.removeChild(Output.datagramOutput);
      Output.configureE131Output(lx, lightType);
      parameterChanged = false;
      lx.engine.output.enabled.setValue(originalEnabled);
    }
  }
}
