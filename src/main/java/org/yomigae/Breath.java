package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;

/**
 Lighting treatment #3: In breath: all columns start out low temp white and dim, then higher
 temp white comes in starting from 6 o'clock side of the temple and moving to include the hall
 section of the 6 o'clock side.  At the end of this portion, all columns on the 6 o'clock side
 of the temple are higher temp white with brightness gradient that falls off as you move away
 from center of the temple.  The deep playa side of the temple during this time is getting brighter
 but maintain the lower temp white.  (I'm not sure how to visualize out breath at this point
 without seeing it, happy to hear any ideas you might have.)
 */

public class Breath extends YPattern {

  public float distancePastGate = 25.0f;
  public float attackSlope = 0.1f;

  public Breath(LX lx) {
    super(lx);

    registerPhase("Start", 20.0f, 60.0f, "Start duration");
    registerPhase("BtWt6", 10.0f, 60.0f, "Bright white six o'clock duration");
    registerPhase("Reset", 3.0f, 60.0f, "Reset duration");
  }

  @Override
  public boolean assignLightColor(String phaseName, LXPoint p, int lightNumber, float percentDone) {
    switch (phaseName) {
      case "Start":
        intensity = minIntensity;
        kelvins = minKelvins;
        return true;
      case "BtWt6":
        float waveDistance = Tori.toriGateDistance + distancePastGate;
        float halfTime = curPhaseDuration / 2.0f;
        if (!Tori.isLeftHalfTori(lightNumber) && percentDone < 0.5f) {
          // This is the total distance of the step wave.  We want to start it off the edge of the lighting area
          // and move it into the lighted area.
          // TODO(tracy): This should be computed based on the slope.  We need to find the x-coordinate intersection of the
          // slope with a y value of l3MinIntensity.
          float stepPosX2 = maxX + distancePastGate - (time / halfTime) * waveDistance;
          // stepwave coming from right towards center.
          float valueRight = AnimUtils.stepWave(stepPosX2, attackSlope, p.x, false);
          kelvins = valueRight * (maxKelvins - minKelvins) + minKelvins;
          // Second set of gates on the right goes backwards.
          // Intensity should be minIntensity + percent distance from right to left where center temple is
          // 100%.  The center gate should be (maxX - toriGateDistance).
          intensity = minIntensity + (maxIntensity - minIntensity) * valueRight;
          float clampIntensity = minIntensity + (maxIntensity - minIntensity) * (maxX - p.x) / Tori.toriGateDistance;
          if (intensity > clampIntensity)
            intensity = clampIntensity;
        } else if (!Tori.isLeftHalfTori(lightNumber) && percentDone >= 0.5f) {
          return false;  // Hold the previous value.
        }
        // After half time, ramp up color temperatures on left side with step wave clamped by linear ramp with max
        // at center and min at left edge.
        if (Tori.isLeftHalfTori(lightNumber)) {
          kelvins = minKelvins;
          intensity = minIntensity;
          if (time > halfTime) {
            // time = l3StartTime / 2.0f, 2nd expr = 0
            // time = l3StartTime, 2nd expr = 1
            float stepPosX1 = minX + waveDistance - waveDistance * ((time - halfTime) / (halfTime));
            float valueLeft = AnimUtils.stepWave(stepPosX1, attackSlope, p.x, false);
            intensity = minIntensity + (maxIntensity - minIntensity) * valueLeft;
            float clampIntensity = minIntensity + (maxIntensity - minIntensity) * (p.x - minX) / Tori.toriGateDistance;
            if (intensity > clampIntensity)
              intensity = clampIntensity;
          }
        }
        return true;
      case "Reset":
        // We need to store the phaseStartIntensity and phaseStartKelvins PER LIGHT.
        intensity = minIntensity + (prevPhaseIntensities[p.index] - minIntensity) * (1.0f - percentDone);
        kelvins = minKelvins + (prevPhaseKelvins[p.index] - minKelvins) * (1.0f - percentDone);
        return true;
    }
    return false;
  }
}
