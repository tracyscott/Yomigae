package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;

/**
 * Lighting treatment #2:  A Kelvin step wave with attack slope moves from edges to center.
 * Once the step wave has reached the center, switch to a brightness ramp to max brightness.
 * Once max brightness reached, switch to simultaneous kelvin and brightness ramp down.
 */
@LXCategory(LXCategory.FORM)
public class ConvRebirth extends YPattern {

  // Values specific to ConvRebirth.
  public float stepSlope = 0.2f;
  // TODO(tracy): Maybe this should be computed based on stepSlope and how long we want it to
  // be off the edge of the lights.
  public float distancePastGate = 25.0f;

  public ConvRebirth(LX lx) {
    super(lx);

    registerPhase("Start", 3.0f, 60.0f, "Start duration");
    registerPhase("KStep", 10.0f, 60.0f, "Kelvin increase duration");
    registerPhase("RampBt", 6.0f, 60.0f, "Brightness ramp duration");
    registerPhase("BtHold", 6.0f, 60.0f, "Brightness hold duration");
    registerPhase("DeRamp", 5.0f, 60.0f, "Deramp brightness+kelvin duration");
  }


  /**
   * Called for each light.  Based on the light's position and the current animation phase,
   * and the current time, we assign a color temperature and intensity value to the light.
   * @param phaseName The name of the animation phase we are executing.
   * @param p The LXPoint representing the light.  Contains the position.
   * @param lightNumber The light's index number.  Determines which type of tori the light is on.
   * @param percentDone The percent done of the duration of this phase.
   */
  @Override
  public boolean assignLightColor(String phaseName, LXPoint p, int lightNumber, float percentDone) {
    float waveDistance = Tori.toriGateDistance;

    switch (phaseName) {
      case "Start":
        kelvins = minKelvins;
        intensity = minIntensity;
        return true;
      case "KStep":
        waveDistance = Tori.toriGateDistance + distancePastGate;
        float stepPosX1 = minX - distancePastGate + percentDone * waveDistance;
        float stepPosX2 = maxX + distancePastGate - percentDone * waveDistance;
        // Support stepwaves coming from left and come from right towards center.
        float valueLeft = AnimUtils.stepWave(stepPosX1, stepSlope, p.x, true);
        float valueRight = AnimUtils.stepWave(stepPosX2, stepSlope, p.x, false);
        kelvins = valueLeft * (maxKelvins - minKelvins) + minKelvins;
        float secondKelvins = valueRight * (maxKelvins - minKelvins) + minKelvins;
        // Second set of gates on the right goes backwards.
        if (!Tori.isLeftHalfTori(lightNumber)) {
          kelvins = secondKelvins;
        }
        intensity = minIntensity;
        return true;
      case "RampBt":
        intensity = minIntensity + (percentDone) * (maxIntensity - minIntensity);
        //kelvins = maxKelvins;
        return true;
      case "BtHold":
        kelvins = maxKelvins;
        intensity = maxIntensity;
        return true;
      case "DeRamp":
        // From high to low.
        kelvins = minKelvins + (1.0f - percentDone) * (maxKelvins - minKelvins);
        // From high to low.
        intensity = minIntensity + (1.0f - percentDone) * (maxIntensity - minIntensity);
        return true;
    }
    return false;
  }
}
