package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.model.LXPoint;

@LXCategory(LXCategory.FORM)
public class GrowthSep extends YPattern {
  protected float l1StartTime = 2.0f;
  protected float l1BrightWhiteHallAttack = 0.1f;
  protected float l1BrightWhiteHallGrowthTime = 10.0f;
  protected float l1GrowthHoldTime = 3.0f;
  protected float l1TunnelSpreadTime = 10.0f;
  protected float l1TunnelAttack = 0.3f;
  protected float l1ResetTime = 1.0f;

  public GrowthSep(LX lx) {
    super(lx);
    registerPhase("Start", l1StartTime, 60.0f, "Start duration");
    registerPhase("HallGrwth", l1BrightWhiteHallGrowthTime, 60.0f, "Bright white hall growth duration");
    registerPhase("GrwthHld", l1GrowthHoldTime, 60.0f, "Growth hold duration.");
    registerPhase("TnlSprd", l1TunnelSpreadTime, 60.0f, "Tunnel spread duration");
    registerPhase("Reset", l1ResetTime, 60.0f, "Reset duration");
  }

  /**
   * Called for each light.  Based on the light's position and the current animation phase,
   * and the current time, we assign a color temperature and intensity value to the light.
   * @param p The LXPoint representing the light.  Contains the position.
   * @param lightNumber The light's index number.  Determines which type of tori the light is on.
   */
  public boolean assignLightColor(String phaseName, LXPoint p, int lightNumber, float percentDone) {
      float waveDistance = Tori.toriGateDistance;

      switch (phaseName) {
        case "Start":
          kelvins = minKelvins;
          intensity = minIntensity;
          return true;
        case "HallGrwth":
          float startOffset = 1.0f / l1BrightWhiteHallAttack;
          waveDistance = Tori.bigToriGateDistance + startOffset;

          if (Tori.isHallTori(lightNumber) && Tori.isLeftHalfTori(lightNumber)) {
            float stepPosX1 = minX + Tori.toriGateDistance + startOffset - waveDistance * percentDone;
            float valueLeft = AnimUtils.stepWave(stepPosX1, l1BrightWhiteHallAttack, p.x, false);
            intensity = minIntensity + (maxIntensity - minIntensity) * valueLeft;
            kelvins = valueLeft * (maxKelvins - minKelvins) + minKelvins;
            return true;
          } else if (Tori.isHallTori(lightNumber) && !Tori.isLeftHalfTori(lightNumber)) {
            float stepPosX2 = (maxX - Tori.toriGateDistance - startOffset) + waveDistance * percentDone;
            float valueRight = AnimUtils.stepWave(stepPosX2, l1BrightWhiteHallAttack, p.x, true);
            intensity = minIntensity + (maxIntensity - minIntensity) * valueRight;
            kelvins = valueRight * (maxKelvins - minKelvins) + minKelvins;
            return true;
          } else {
            intensity = minIntensity;
            kelvins = minKelvins;
            return true;
          }
        case "GrwthHld":
          return false;
        case "TnlSprd":
          // Start a tSriangle wave just off the edge of the tunnel.  mall tori gates are updated with
          // a traveling triangle wave.  Big Tori gates/Hall Tori will use a step function with attack to return to
          // dim state.
          if (!Tori.isHallTori(lightNumber)) {
            // getTriangleIntensity is not normalized to 1.0, so we need to multiply by our range to get
            // the appropriate start offset.
            float tunnelSpreadStartOffset = 1.0f / l1TunnelAttack;

            waveDistance = Tori.smallToriGateDistance + tunnelSpreadStartOffset;
            // Note, for a triangle wave, we need to travel extra far because not only do we have to account for the
            // attack slope on the front we also have to account for the decay slope on the back.
            float peakPosX1 = minX + waveDistance - (waveDistance + tunnelSpreadStartOffset) * percentDone;
            float peakPosX2 = maxX - waveDistance + (waveDistance + tunnelSpreadStartOffset) * percentDone;
            float valueLeft = AnimUtils.triangleWave(peakPosX1, l1TunnelAttack, p.x);
            float valueRight = AnimUtils.triangleWave(peakPosX2, l1TunnelAttack, p.x);

            if (lightNumber <= 5) {
              intensity = minIntensity + valueLeft * (maxIntensity - minIntensity);
              kelvins = minKelvins + valueLeft * (maxKelvins - minKelvins);
            } else {
              intensity = minIntensity + valueRight * (maxIntensity - minIntensity);
              kelvins = minKelvins + valueRight * (maxKelvins - minKelvins);
            }
            return true;
          } else {
            // Big Tori's using step wave to dim.
            startOffset = 1.0f / l1BrightWhiteHallAttack;
            waveDistance = Tori.bigToriGateDistance + startOffset;
            if (Tori.isLeftHalfTori(lightNumber) && Tori.isHallTori(lightNumber)) {
              float stepPosX1 = minX + Tori.toriGateDistance + startOffset - waveDistance + waveDistance * percentDone;
              float valueLeft = AnimUtils.stepWave(stepPosX1, l1BrightWhiteHallAttack, p.x, false);
              intensity = minIntensity + (maxIntensity - minIntensity) * valueLeft;
              kelvins = valueLeft * (maxKelvins - minKelvins) + minKelvins;
            } else if (!Tori.isLeftHalfTori(lightNumber) && Tori.isHallTori(lightNumber)) {
              float stepPosX2 = (maxX - Tori.toriGateDistance) + Tori.bigToriGateDistance - waveDistance * percentDone;
              float valueRight = AnimUtils.stepWave(stepPosX2, l1BrightWhiteHallAttack, p.x, true);
              intensity = minIntensity + (maxIntensity - minIntensity) * valueRight;
              kelvins = valueRight * (maxKelvins - minKelvins) + minKelvins;
            }
            return true;
          }
        case "Reset":
          kelvins = minKelvins;
          intensity = minIntensity;
          return true;
      }
      return false;
  }
}
