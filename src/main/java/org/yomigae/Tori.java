package org.yomigae;


import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Tori Gate.  Allows for lights to be placed relative to this
 * gate.
 */
public class Tori
{
  public enum GateType { T1, T2, T3, T4, T5, T6};

  public static float InchesPerMeter = 39.3701f;
  public static float T3ModelReferencePosX = -124.63f;
  public static float T3ModelReferencePosXAdjusted = -124.63f + 0.5f * 12.0f / InchesPerMeter;
  public static float T3ModelReferencePosY = 39.31f;
  public static float T3ModelReferencePosZ = 17.61f;

  public static float T4TridlyRefX = -35.956f;
  public static float T4TridlyRefY = 0.0f;
  public static float T4TridlyRefZ = -19.07f;

  public static float T4ModelReferencePosX = -126.371f;
  public static float T4ModelReferencePosY = 39.177f;
  public static float T4ModelReferencePosZ = 17.276f;

  public static float ModelReferencePosX = T4TridlyRefX;
  public static float ModelReferencePosY = T4TridlyRefY;
  public static float ModelReferencePosZ = T4TridlyRefZ;

  public static int NUM_T1_GATES_PER_SIDE = 5;

  //static float SpotFeetFromWall = 2.0f;
  //static float SpotHeightFromGround = 0.1f;
  //static float SpotRotateAngle = -80.0f;
  public static float BigGateLegXWidth = (8.0f * 12.0f + 1.5f) / InchesPerMeter;
  //static float SpotMetersFromWall = SpotFeetFromWall * 12.0f / InchesPerMeter;
  //static float EaveLightDropHeight = 0.5f * 12.0f / InchesPerMeter;
  public static float T6GapMeters = (11.0f * 12.0f + 11.5f) / InchesPerMeter;
  public static float T6GapCenterToCenter = (22.0f * 12.0f + 0.5f) / InchesPerMeter;

  //static float GroundSpotY = ModelReferencePosY + SpotHeightFromGround * 12.0f / InchesPerMeter;
  // The spot should be outsite the gate wall.  For the front wall, that means that we need to reduce Z.
  //static float GroundSpotZFront = ModelReferencePosZ - SpotMetersFromWall;
  // Last factor is just visual fudging.  Still need to move about half a foot to the right.
  //static float GroundSpotT1XBase = ModelReferencePosXAdjusted + (3.0f * 12.0f / InchesPerMeter) / 2.0f;

  public static float T1LegXWidth = (8.0f * 12.0f + 1.5f) / InchesPerMeter;

  public static float T1Spacing = (10.0f * 12.0f + 0.5f) / InchesPerMeter;
  // This is the gap bewteen T1 Tori gates.
  public static float T1Gap = T1Spacing - T1LegXWidth;

  public static float GateHeightStep = (5.0f * 12.0f) / InchesPerMeter;
  public static float GateTopThickness = (3.0f * 12.0f + 1.5f) / InchesPerMeter;

  public static float T1TopSpan = (26.0f * 12.0f + 11.5f) / InchesPerMeter;
  public static float T1LegOutside = (19.0f * 12.0f + 3.5f) / InchesPerMeter;
  public static float T1EaveDepth = (T1TopSpan - T1LegOutside) / 2.0f;
  public static float T1LegHeight = (14.0f * 12.0f + 11.25f) / InchesPerMeter;

  public static float T2TopSpan = (34.0f * 12.0f + 2.75f) / InchesPerMeter;
  public static float T2LegOutside = (23.0f * 12.0f + 7.5f) / InchesPerMeter;
  public static float T2EaveDepth = (T2TopSpan - T2LegOutside) / 2.0f;
  public static float T2LegHeight = (19.0f * 12.0f + 11.25f) / InchesPerMeter;

  public static float T3TopSpan = (41.0f * 12.0f + 8.75f) / InchesPerMeter;
  public static float T3LegOutside = (27.0f * 12.0f + 11.5f) / InchesPerMeter;
  public static float T3EaveDepth = (T3TopSpan - T3LegOutside) / 2.0f;
  public static float T3LegHeight = (24.0f * 12.0f + 11.25f) / InchesPerMeter;

  public static float T4TopSpan = (48.0f * 12.0f + 1.5f) / InchesPerMeter;
  public static float T4LegOutside = (32.0f * 12.0f + 3.5f) / InchesPerMeter;
  public static float T4EaveDepth = (T4TopSpan - T4LegOutside) / 2.0f;
  public static float T4LegHeight = (29.0f * 12.0f + 11.25f) / InchesPerMeter;

  public static float T5TopSpan = (55.0f * 12.0f + 11.25f) / InchesPerMeter;
  public static float T5LegOutside = (36.0f * 12.0f + 7.5f) / InchesPerMeter;
  public static float T5EaveDepth = (T5TopSpan - T5LegOutside) / 2.0f;
  public static float T5LegHeight = (34.0f * 12.0f + 11.25f) / InchesPerMeter;

  public static float T6TopSpan = (60.0f * 12.0f + 2.5f) / InchesPerMeter;
  public static float T6LegOutside = (40.0f * 12.0f + 11.5f) / InchesPerMeter;
  public static float T6EaveDepth = (T6TopSpan - T6LegOutside) / 2.0f;
  public static float T6LegHeight = (39.0f * 12.0f + 11.25f) / InchesPerMeter;

  public static float toriGateDistance = 5.0f * Tori.T1Spacing - Tori.T1Gap + 5.0f * Tori.BigGateLegXWidth;
  public static float smallToriGateDistance = 5.0f * Tori.T1Spacing;
  public static float bigToriGateDistance = 5.0f * Tori.BigGateLegXWidth;


  public GateType type;
  public boolean isFront;
  public int typeInstanceNum;  // For T1 gates, we have 8 separate gates
  public float gateX;
  public float gateY;
  public float gateZ;

  public Tori(GateType gateType, int typeInstanceNum, boolean front, float gateX, float gateY, float gateZ)
  {
    type = gateType;
    this.typeInstanceNum = typeInstanceNum;
    this.isFront = front;
    this.gateX = gateX;
    this.gateY = gateY;
    this.gateZ = gateZ;
  }

  // Returns the coodinates of this gate in worldspace.
  // TODO(tracy): This needs to be modified based on the gate.
  public float[] getGateRefPt()
  {
    float[] pt = new float[3];
    pt[0] = gateX;
    pt[1] = gateY;
    pt[2] = gateZ;
    return pt;
  }

  // Convert a local coordinate space point into world coordinates.  Note, this
  // modifies the passed in point.
  public void localToWorld(float[] pt)
  {
    float[] world = getGateRefPt();
    pt[0] += world[0];
    pt[1] += world[1];
    pt[2] += world[2];
  }

  // Returns the X width of the Tori gate leg.  This is the face upon which a
  // ground spot would be centered for example.
  public float getLegXWidth()
  {
    if (type == GateType.T1)
    {
      return T1LegXWidth;
    }
    else
    {
      return BigGateLegXWidth;
    }
  }

  // Returns the depth of overhang on the eave of this gate. Varies with GateType.
  public float getEaveDepth()
  {
    switch (type)
    {
      case T1:
        return T1EaveDepth;
      case T2:
        return T2EaveDepth;
      case T3:
        return T3EaveDepth;
      case T4:
        return T4EaveDepth;
      case T5:
        return T5EaveDepth;
      case T6:
        return T6EaveDepth;
    }
    return 0.1f;
  }

  // Returns the height of the underside of the eave. Varies based on GateType.
  public float getEaveHeight()
  {
    switch (type)
    {
      case T1:
        return T1LegHeight;
      case T2:
        return T2LegHeight;
      case T3:
        return T3LegHeight;
      case T4:
        return T4LegHeight;
      case T5:
        return T5LegHeight;
      case T6:
        return T6LegHeight;
    }
    return 0.1f;
  }

  // Returns centered ground spot coordinates relative to reference point.  You must
  // add the reference point coordinates from getGateRefPt to convert these coordinates
  // to worldspace.
  public float[] getCenteredGroundSpotPt(float spotHeightFromGround, float spotDistanceFromWall)
  {
    float[] pt = new float[3];
    pt[0] = getLegXWidth() / 2.0f;
    pt[1] = spotHeightFromGround;
    pt[2] = (isFront) ? -spotDistanceFromWall : spotDistanceFromWall;
    return pt;
  }

  // Returns coodinates for an eave light that is centered in both X and Z on the face of
  // the eave.
  public float[] getCenteredEaveDropLightPt(float eaveLightDropHeight)
  {
    float[] pt = new float[3];
    pt[0] = getLegXWidth() / 2.0f;
    pt[1] = getEaveHeight() - eaveLightDropHeight;
    pt[2] = (isFront) ? -getEaveDepth() / 2.0f : getEaveDepth() / 2.0f;
    return pt;
  }

  // Returns the coodinates for an eave light that is on the outside edge of the underside of
  // the eave.
  public float[] getOutsideEaveEdgeLightBar(float eaveLightDropHeight)
  {
    float[] pt = new float[3];
    pt[0] = getLegXWidth() / 2.0f;
    pt[1] = getEaveHeight() - eaveLightDropHeight;
    pt[2] = (isFront) ? -getEaveDepth() + 0.3f : getEaveDepth() - 0.3f;
    return pt;
  }

  // Returns the position for the gate top lighting.
  public float[] getGateTopLightPt(GateType gateType, boolean left)
  {
    float[] pt = new float[3];

    pt[0] = (left? -2.0f : BigGateLegXWidth  + 2.0f);  // distance away from eve.
    switch (gateType)
    {
      case T1:
        pt[1] = T1LegHeight + GateTopThickness;
        pt[2] = T1LegOutside / 2.0f;
        break;
      case T2:
        pt[1] = T1LegHeight + GateTopThickness + 0.5f;
        pt[2] = T2LegOutside / 2.0f;
        break;
      case T3:
        pt[1] = T2LegHeight + GateTopThickness + 0.5f;
        pt[2] = T3LegOutside / 2.0f;
        break;
      case T4:
        pt[1] = T3LegHeight + GateTopThickness + 0.5f;
        pt[2] = T4LegOutside / 2.0f;
        break;
      case T5:
        pt[1] = T4LegHeight + GateTopThickness + 0.5f;
        pt[2] = T5LegOutside / 2.0f;
        break;
      case T6:
        pt[1] = T5LegHeight + GateTopThickness + 0.5f;
        pt[2] = T6LegOutside / 2.0f;
        break;
    }
    return pt;
  }

  static public List<Tori> CreateGates()
  {
    float xStart = Tori.ModelReferencePosX;
    float yStart = Tori.ModelReferencePosY;
    float zStart = Tori.ModelReferencePosZ;
    Tori t;
    List<Tori> toris = new ArrayList<Tori>();

    List<Tori> frontRightGates = toris;

    for (int i = 0; i < NUM_T1_GATES_PER_SIDE; i++)
    {
      t = new Tori(GateType.T1, i, true, xStart, yStart, zStart);
      frontRightGates.add(t);
      xStart += Tori.T1Spacing;
    }
    // First big gate is adjacent to the last T1 small gate, so we need to subtract the gap spacing.
    xStart -= Tori.T1Gap;
    t = new Tori(GateType.T2, 0, true, xStart, yStart, zStart - (Tori.T2LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T3, 0, true, xStart, yStart, zStart - (Tori.T3LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T4, 0, true, xStart, yStart, zStart - (Tori.T4LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T5, 0, true, xStart, yStart, zStart - (Tori.T5LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T6, 0, true, xStart, yStart, zStart - (Tori.T6LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.T6GapCenterToCenter;
    t = new Tori(GateType.T6, 0, true, xStart, yStart, zStart - (Tori.T6LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T5, 0, true, xStart, yStart, zStart - (Tori.T5LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T4, 0, true, xStart, yStart, zStart - (Tori.T4LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T3, 0, true, xStart, yStart, zStart - (Tori.T3LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    t = new Tori(GateType.T2, 0, true, xStart, yStart, zStart - (Tori.T2LegOutside - Tori.T1LegOutside) / 2.0f);
    frontRightGates.add(t);
    xStart += Tori.BigGateLegXWidth;
    for (int i = 0; i < NUM_T1_GATES_PER_SIDE; i++)
    {
      t = new Tori(GateType.T1, 0, true, xStart, yStart, zStart);
      frontRightGates.add(t);
      xStart += Tori.T1Spacing;
    }

    return toris;
  }

  /**
   * Returns the minimum gateX reference coordinate among a set of gates.
   * Note, this point is the front, bottom, left point on a gate when facing
   * the gate from the default view (i.e. positive X to the right).
   */
  public static float MinRefX(List<Tori> toris)
  {
    float minX = 100000.0f;
    for (int i = 0; i < toris.size(); i++)
    {
      if (toris.get(i).gateX < minX)
      {
        minX = toris.get(i).gateX;
      }
    }
    return minX;
  }

  /**
   * Returns the maximum gateX reference coordinate among a set of gates.
   * Note, this point is the front, bottom, left point on a gate when facing
   * the gate from the dfault view (i.e. positive X to the right).  Also note,
   * this will not be the actual maximum x coordinate of all the points on the
   * mesh.  That would be this point + the X-Width of the leg of the tori gate
   * that corresponds to this point.  As of now, that would simply be T1LegXWidth
   */
  public static float MaxRefX(List<Tori> toris)
  {
    float maxX = -1000000.0f;
    for (int i = 0; i < toris.size(); i++)
    {
      if (toris.get(i).gateX > maxX)
      {
        maxX = toris.get(i).gateX;
      }
    }
    return maxX;
  }

  static public float SpotDistanceFromWall = 2.0f;
  static public float SpotHeightFromGround = 0.1f * 12.0f / Tori.InchesPerMeter;

  static public List<LXPoint> createLXPoints(boolean front) {
    List<LXPoint> points = new ArrayList<LXPoint>();
    List<Tori> toris = Tori.CreateGates();

    // Place initial ground spots along small toris
    for (int i = 0; i < 5; i++)
    {
      Tori tg = toris.get(i);
      float[] spotPos = tg.getCenteredGroundSpotPt(SpotHeightFromGround, SpotDistanceFromWall); // tg.getCenteredEaveDropLightPt(EaveLightDropHeight); //
      tg.localToWorld(spotPos);
      points.add(new LXPoint(spotPos[0], spotPos[1], spotPos[2]));
    }

    for (int i = 5; i < 15; i++)
    {
      Tori tg = toris.get(i);

      float[] spotPos = tg.getOutsideEaveEdgeLightBar(-0.00f);
      tg.localToWorld(spotPos);
      points.add(new LXPoint(spotPos[0], spotPos[1], spotPos[2]));
    }

    for (int i = 15; i < toris.size(); i++)
    {
      Tori tg = toris.get(i);
      float[] spotPos = tg.getCenteredGroundSpotPt(SpotHeightFromGround, SpotDistanceFromWall); // tg.getCenteredEaveDropLightPt(EaveLightDropHeight); //
      tg.localToWorld(spotPos);
      points.add(new LXPoint(spotPos[0], spotPos[1], spotPos[2]));
    }
    return points;
  }

  static public boolean isLeftHalfTori(List<Tori> toris, int lightNumber) {
    return lightNumber <= toris.size() / 2;
  }

  static public boolean isHallTori(List<Tori> toris, int lightNumber) {
    return (lightNumber > 5 && lightNumber < 16);
  }
}
