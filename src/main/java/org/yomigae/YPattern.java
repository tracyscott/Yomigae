package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility base class for Yomigae patterns that are based on animation phases.
 */
public abstract class YPattern extends LXPattern {

  public final CompoundParameter minIntensityP = new CompoundParameter
      ("minIntensity", 0.5f, 0.0f, 1.0f).setDescription("Minimum light intensity");
  public final CompoundParameter maxIntensityP = new CompoundParameter
      ("maxIntensity", 1.0f, 0.0f, 1.0f).setDescription("Maximum light intensity");
  public final CompoundParameter minKelvinsP = new CompoundParameter
      ("minK", 1900f, 1900f, 20000f).setDescription("Minimum kelvins");
  public final CompoundParameter maxKelvinsP = new CompoundParameter
      ("maxK", 7000f, 1900f, 20000f).setDescription("Maximum kelvins");

  protected float minX = -100.0f;
  protected float maxX = 100.0f;

  protected float minKelvins = 1900f;
  protected float maxKelvins = 7000f;
  protected float kelvins = minKelvins;
  protected float intensity = 1.0f;

  public float minIntensity = 0.5f;
  public float maxIntensity = 1.0f;
  protected float phaseStartIntensity = minIntensity;
  protected float phaseStartKelvins = minKelvins;

  protected float time = 0.0f;
  protected float curPhaseDuration = 0.0f;

  List<Tori> toris;
  List<String> phaseNames = new ArrayList<String>();
  List<CompoundParameter> phaseDurations = new ArrayList<CompoundParameter>();
  float[] prevFrameIntensities;
  float[] prevFrameKelvins;
  float[] prevPhaseIntensities;
  float[] prevPhaseKelvins;

  int curAnimPhase;

  // Values specific to ConvRebirth.
  public float stepSlope = 0.2f;
  // TODO(tracy): Maybe this should be computed.
  public float distancePastGate = 25.0f;

  public YPattern(LX lx) {
    super(lx);
    toris = Tori.CreateGates();
    minX = Tori.MinRefX(toris);
    maxX = Tori.MaxRefX(toris) + Tori.T1LegXWidth;
    addParameter(minIntensityP);
    addParameter(maxIntensityP);
    addParameter(minKelvinsP);
    addParameter(maxKelvinsP);
    prevFrameIntensities = new float[lx.getModel().getPoints().size()];
    prevFrameKelvins = new float[lx.getModel().getPoints().size()];
    prevPhaseIntensities = new float[lx.getModel().getPoints().size()];
    prevPhaseKelvins = new float[lx.getModel().getPoints().size()];
  }

  /**
   * Registers an animation phase.
   * @param phaseName The name of the phase.
   * @param phaseTime The default time length of the phase.  A knob will be created for tuning.
   * @param maxPhaseTime The maximum phase duration.
   * @param description Description to be shown for the time knob.
   */
  protected void registerPhase(String phaseName, float phaseTime, float maxPhaseTime, String description) {
    phaseNames.add(phaseName);
    CompoundParameter p = new CompoundParameter(phaseName, phaseTime, 0.1f, maxPhaseTime).setDescription(description);
    phaseDurations.add(p);
    addParameter(p);
  }

  public String getCurrentPhaseName() {
    if (curAnimPhase < phaseNames.size())
      return phaseNames.get(curAnimPhase);
    else
      return "";
  }

  /**
   *  Allow manual specification of R, G, B.  Useful for testing lights for color correction purposes.
   * @param deltaMs
   */
  public void run(double deltaMs) {
    int[] rgb = new int[3];
    float[] hsb = new float[3];

    int lightNumber  = 1;

    minIntensity = minIntensityP.getValuef();
    maxIntensity = maxIntensityP.getValuef();
    minKelvins = minKelvinsP.getValuef();
    maxKelvins = maxKelvinsP.getValuef();

    boolean lightUpdated = true;
    boolean resetTime = false;
    curPhaseDuration = phaseDurations.get(curAnimPhase).getValuef();
    float percentDone = time / curPhaseDuration;

    // First we iterate over all the points and potentially update intensity and kelvins.  Note, that
    // some calls to assignLightColor don't result in an update.  For example, if we are just in an
    // animation phase that is holding a value.
    for (LXPoint p : model.points) {
      lightUpdated = assignLightColor(phaseNames.get(curAnimPhase), p, lightNumber, percentDone);
      ++lightNumber;
      // TODO(tracy): We may want to store an extra colors buffer in our pattern and re-init
      // the colors[] buffer with those values for scenarios where assignLightColor doesn't
      // perform updates (such as just holding lights to their previous values).
      // I saw some color flicker but I can reproduce it. Not sure if our colors[] buffer is
      // being modified somewhere else?
      if (lightUpdated) {
        prevFrameIntensities[p.index] = intensity;
        prevFrameKelvins[p.index] = kelvins;
      }
    }
    // Now that we have updated our intensity/kelvins values, push them to our 'colors' buffer.
    copyIntensityKelvinsToColors();
    resetTime = updateAnimPhase();
    if (resetTime) {
      // Store the last intensities and kelvins when we switch phases.  This allows us to morph any
      // set of varying values for each light to some target intensity/kelvins.  See Breath for an example.
      System.arraycopy(prevFrameIntensities, 0, prevPhaseIntensities, 0, prevPhaseIntensities.length);
      System.arraycopy(prevFrameKelvins, 0, prevPhaseKelvins, 0, prevPhaseKelvins.length);
      phaseStartKelvins = kelvins;
      time = 0.0f;
      System.out.println("Changing anim phase to: " + phaseNames.get(curAnimPhase));
    } else {
      time += deltaMs / 1000f;
    }
  }

  protected void copyIntensityKelvinsToColors() {
    int[] rgb = new int[3];
    float[] hsb = new float[3];
    for (int i = 0; i < prevFrameIntensities.length; i++) {
      float intensity = prevFrameIntensities[i];
      float kelvins = prevFrameKelvins[i];
      ColorTemp.convertKToRGB(kelvins, rgb);
      // Convert RGB to HSB and scale by intensity
      Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);
      hsb[2] = intensity * hsb[2];
      colors[i] = LXColor.hsb(hsb[0] * 360.0f, hsb[1] * 100.0f, hsb[2] * 100.0f);
    }
  }

  public boolean updateAnimPhase() {
    float curPhaseLength = phaseDurations.get(curAnimPhase).getValuef();
    if (time >= curPhaseLength) {
      curAnimPhase++;
      if (curAnimPhase >= phaseNames.size())
        curAnimPhase = 0;
      return true;
    }
    return false;
  }

  /**
   * Called for each light.  Based on the light's position and the current animation phase,
   * and the current time, we assign a color temperature and intensity value to the light.
   * @param phaseName The name of the animation phase we are executing.
   * @param p The LXPoint representing the light.  Contains the position.
   * @param lightNumber The light's index number.  Determines which type of tori the light is on.
   * @param percentDone The percent done of the duration of this phase.
   */
  public abstract boolean assignLightColor(String phaseName, LXPoint p, int lightNumber, float percentDone);
}
