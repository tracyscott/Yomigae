package org.yomigae;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;

import java.util.ArrayList;
import java.util.List;

// TODO(tracy): This doesn't really need to extend LXModel anymore.  We can just construct it directly
// with wrapper.  YomigaeModel3D should not extend LXModel and should encompass the Tori gate dimensions,
// etc.  It should have a method generateLXModel() that generates our points.  Also, there could be
// multiple versions of generateLXModel() that generate multiple light placement strategies but that
// ship has sailed.
public class YomigaeModel3D extends LXModel {

  public final static int SIZE = 20;

  public YomigaeModel3D(List<LXPoint> points) {
    super(points);
  }

  static public YomigaeModel3D generateLXModel() {
    return new YomigaeModel3D(generatePoints());
  }

  public static List<LXPoint> generatePoints() {
    List<LXPoint> yomigaePoints = new ArrayList<LXPoint>();
    for (int z = 0; z < 1; z++) {
      for (int x = 0; x < 1; x++) {
        yomigaePoints.add(new LXPoint(x * 1.5f, 10.0f, z * 4.0f));
      }
    }
    return yomigaePoints;
  }
}
