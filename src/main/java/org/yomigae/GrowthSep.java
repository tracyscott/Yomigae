package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;
import java.awt.Color;
import java.util.List;


@LXCategory(LXCategory.TEST)
public class GrowthSep extends LXPattern {
  public final CompoundParameter minIntensityP = new CompoundParameter
      ("minIntensity", 0.3f, 0.0f, 1.0f).setDescription("Minimum light intensity");
  public final CompoundParameter maxIntensityP = new CompoundParameter
      ("maxIntensity", 1.0f, 0.0f, 1.0f).setDescription("Maximum light intensity");
  public final CompoundParameter minKelvinsP = new CompoundParameter
      ("minK", 1900f, 1900f, 20000f).setDescription("Minimum kelvins");
  public final CompoundParameter maxKelvinsP = new CompoundParameter
      ("maxK", 7000f, 1900f, 20000f).setDescription("Maximum kelvins");

  // Lighting treatment #1: growth + separation: all columns start out low temp white and dim,
// higher temp white starts to appear starting at the center of the temple and get brighter,
// also start to spread to the hall portion of the temple, then at max brightness and max
// white temperature, brightness on the hall portion start to dim and the higher temp white
// spreads to the tunnel parts of the temple, moving slowly through the 8 columns, eventually
// everything becomes low temperature white again
  static public enum L1AnimPhases { START, BRIGHT_WHITE_HALL_GROWTH, GROWTH_HOLD, TUNNEL_START, TUNNEL_SPREAD, RESET };

  protected L1AnimPhases l1AnimPhase = L1AnimPhases.START;

  protected float minX = -100.0f;
  protected float maxX = 100.0f;

  protected float minKelvins = 1900f;
  protected float maxKelvins = 7000f;
  protected float kelvins = minKelvins;
  protected float intensity = 1.0f;
  protected float l1MinIntensity = 0.1f;
  protected float l1MaxIntensity = 1.0f;
  protected float time = 0.0f;
  protected float l1StartTime = 2.0f;
  protected float l1BrightWhiteHallAttack = 0.1f;
  protected float l1BrightWhiteHallGrowthTime = 10.0f;
  protected float l1GrowthHoldTime = 3.0f;
  protected float l1TunnelSpreadTime = 10.0f;
  protected float l1TunnelAttack = 0.3f;
  protected float l1ResetTime = 1.0f;
  protected boolean resetPhase = false;

  List<Tori> toris;

  public GrowthSep(LX lx) {
    super(lx);
    toris = Tori.CreateGates();
    minX = Tori.MinRefX(toris);
    maxX = Tori.MaxRefX(toris) + Tori.T1LegXWidth;
    addParameter(minIntensityP);
    addParameter(maxIntensityP);
    addParameter(minKelvinsP);
    addParameter(maxKelvinsP);
  }

  /**
   *  Allow manual specification of R, G, B.  Useful for testing lights for color correction purposes.
   * @param deltaMs
   */
  public void run(double deltaMs) {
    int[] rgb = new int[3];
    float[] hsb = new float[3];

    int lightNumber  = 1;

    l1MinIntensity = minIntensityP.getValuef();
    l1MaxIntensity = maxIntensityP.getValuef();
    minKelvins = minKelvinsP.getValuef();
    maxKelvins = maxKelvinsP.getValuef();

    boolean lightUpdated = true;
    boolean resetTime = false;
    for (LXPoint p : model.points) {
      lightUpdated = assignLightColor(p, lightNumber);
      ++lightNumber;
      if (lightUpdated) {
        ColorTemp.convertKToRGB(kelvins, rgb);
        // Convert RGB to HSB and scale by intensity
        Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
        hsb[2] = intensity * hsb[2];
        colors[p.index] = LXColor.hsb(hsb[0] * 360.0f, hsb[1] * 100.0f, hsb[2] * 100.0f);
      }
    }
    resetTime = updateAnimPhase();
    if (resetTime) {
      time = 0.0f;
      System.out.println("Changing anim phase to: " + l1AnimPhase);
    } else {
      time += deltaMs / 1000f;
    }
  }

  /**
   * Check current time and decide if we need to update our animation phase.
   * @return
   */
  public boolean updateAnimPhase() {
    switch (l1AnimPhase) {
      case START:
        if (time >= l1StartTime) {
          l1AnimPhase = L1AnimPhases.BRIGHT_WHITE_HALL_GROWTH;
          return true;
        }
        break;
      case BRIGHT_WHITE_HALL_GROWTH:
        if (time >= l1BrightWhiteHallGrowthTime) {
          l1AnimPhase = L1AnimPhases.GROWTH_HOLD;
          return true;
        }
        break;
      case GROWTH_HOLD:
        if (time >= l1GrowthHoldTime) {
          l1AnimPhase = L1AnimPhases.TUNNEL_SPREAD;
          return true;
        }
        break;
      case TUNNEL_SPREAD:
        if (time >= l1TunnelSpreadTime) {
          l1AnimPhase = L1AnimPhases.RESET;
          return true;
        }
        break;
      case RESET:
        if (time >= l1ResetTime) {
          l1AnimPhase = L1AnimPhases.START;
          return true;
        }
        break;
    }
    return false;
  }

  /**
   * Called for each light.  Based on the light's position and the current animation phase,
   * and the current time, we assign a color temperature and intensity value to the light.
   * @param p The LXPoint representing the light.  Contains the position.
   * @param lightNumber The light's index number.  Determines which type of tori the light is on.
   */
  public boolean assignLightColor(LXPoint p, int lightNumber) {
      float waveDistance = Tori.toriGateDistance;

      switch (l1AnimPhase) {
        case START:
          kelvins = minKelvins;
          intensity = l1MinIntensity;
          return true;
        case BRIGHT_WHITE_HALL_GROWTH:
          float startOffset = 1.0f / l1BrightWhiteHallAttack;
          waveDistance = Tori.bigToriGateDistance + startOffset;

          if (Tori.isHallTori(toris, lightNumber) && Tori.isLeftHalfTori(toris, lightNumber)) {
            float stepPosX1 = minX + Tori.toriGateDistance + startOffset - waveDistance * (time / l1BrightWhiteHallGrowthTime);
            float valueLeft = AnimUtils.stepWave(stepPosX1, l1BrightWhiteHallAttack, p.x, false);
            intensity = l1MinIntensity + (l1MaxIntensity - l1MinIntensity) * valueLeft;
            kelvins = valueLeft * (maxKelvins - minKelvins) + minKelvins;
            return true;
          } else if (Tori.isHallTori(toris, lightNumber) && !Tori.isLeftHalfTori(toris, lightNumber)) {
            float stepPosX2 = (maxX - Tori.toriGateDistance - startOffset) + waveDistance * (time / l1BrightWhiteHallGrowthTime);
            float valueRight = AnimUtils.stepWave(stepPosX2, l1BrightWhiteHallAttack, p.x, true);
            intensity = l1MinIntensity + (l1MaxIntensity - l1MinIntensity) * valueRight;
            kelvins = valueRight * (maxKelvins - minKelvins) + minKelvins;
            return true;
          } else {
            intensity = l1MinIntensity;
            kelvins = minKelvins;
            return true;
          }
        case GROWTH_HOLD:
          return false;
        case TUNNEL_SPREAD:
          // Start a triangle wave just off the edge of the tunnel.  Small tori gates are updated with
          // a traveling triangle wave.  Big Tori gates will use a step function with attack to return to
          // dim state.
          if (lightNumber <= 5 || lightNumber >= 16) {
            // getTriangleIntensity is not normalized to 1.0, so we need to multiply by our range to get
            // the appropriate start offset.
            float tunnelSpreadStartOffset = 1.0f / l1TunnelAttack;

            waveDistance = Tori.smallToriGateDistance + tunnelSpreadStartOffset;
            // Note, for a triangle wave, we need to travel extra far because not only do we have to account for the
            // attack slope on the front we also have to account for the decay slope on the back.
            float peakPosX1 = minX + waveDistance - (waveDistance + tunnelSpreadStartOffset) * (time / l1TunnelSpreadTime);
            float peakPosX2 = maxX - waveDistance + (waveDistance + tunnelSpreadStartOffset) * (time / l1TunnelSpreadTime);
            float valueLeft = AnimUtils.triangleWave(peakPosX1, l1TunnelAttack, p.x);
            float valueRight = AnimUtils.triangleWave(peakPosX2, l1TunnelAttack, p.x);

            if (lightNumber <= 5) {
              intensity = l1MinIntensity + valueLeft * (l1MaxIntensity - l1MinIntensity);
              kelvins = minKelvins + valueLeft * (maxKelvins - minKelvins);
            } else {
              intensity = l1MinIntensity + valueRight * (l1MaxIntensity - l1MinIntensity);
              kelvins = minKelvins + valueRight * (maxKelvins - minKelvins);
            }
            return true;
          } else {
            // Big Tori's using step wave to dim.
            startOffset = 1.0f / l1BrightWhiteHallAttack;
            waveDistance = Tori.bigToriGateDistance + startOffset;
            if (Tori.isLeftHalfTori(toris, lightNumber) && Tori.isHallTori(toris, lightNumber)) {
              float stepPosX1 = minX + Tori.toriGateDistance + startOffset - waveDistance + waveDistance * (time / l1TunnelSpreadTime);
              float valueLeft = AnimUtils.stepWave(stepPosX1, l1BrightWhiteHallAttack, p.x, false);
              intensity = l1MinIntensity + (l1MaxIntensity - l1MinIntensity) * valueLeft;
              kelvins = valueLeft * (maxKelvins - minKelvins) + minKelvins;
            } else if (!Tori.isLeftHalfTori(toris, lightNumber) && Tori.isHallTori(toris, lightNumber)) {
              float stepPosX2 = (maxX - Tori.toriGateDistance) + Tori.bigToriGateDistance - waveDistance * (time / l1TunnelSpreadTime);
              float valueRight = AnimUtils.stepWave(stepPosX2, l1BrightWhiteHallAttack, p.x, true);
              intensity = l1MinIntensity + (l1MaxIntensity - l1MinIntensity) * valueRight;
              kelvins = valueRight * (maxKelvins - minKelvins) + minKelvins;
            }
            return true;
          }
        case RESET:
          kelvins = minKelvins;
          intensity = l1MinIntensity;
          return true;
      }
      return false;
  }
}
