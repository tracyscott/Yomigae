package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.output.ArtNetDatagram;
import heronarts.lx.output.LXDatagramOutput;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles output from our 'colors' buffer to our DMX lights.  Currently using E1.31.
 */
public class Output {
  private static final Logger logger = Logger.getLogger(Output.class.getName());

  public enum LightType {
    LALUCE(1),
    PRODPAR(2),
    PRODWASH(3),
    OPPSKPAR(4);

    private int value;
    private static Map map = new HashMap<Integer, LightType>();

    private LightType(int value) {
      this.value = value;
    }

    static {
      for (LightType lightType : LightType.values()) {
        map.put(lightType.value, lightType);
      }
    }

    public static LightType valueOf(int pageType) {
      return (LightType) map.get(pageType);
    }

    public int getValue() {
      return value;
    }
  }

  public static LXDatagramOutput datagramOutput = null;

  public static String artnetIpAddress = "127.0.0.1";
  public static int artnetPort = 6454;

  public static void configureArtnetOutput(LX lx) {
    // This only works if we have less than 170 lxpoints.
    int[] dmxChannelsForUniverse = new int[20];
    for (int i = 0; i < 20; i++) {
      dmxChannelsForUniverse[i] = i;
    }
    ArtNetDatagram artnetDatagram = new ArtNetDatagram(dmxChannelsForUniverse);

    try {
      artnetDatagram.setAddress(artnetIpAddress).setPort(artnetPort);
    } catch (UnknownHostException uhex) {
      logger.log(Level.SEVERE, "Configuring ArtNet: " + artnetIpAddress, uhex);
    }
    try {
      datagramOutput = new LXDatagramOutput(lx);
      datagramOutput.addDatagram(artnetDatagram);
    } catch (SocketException sex) {
      logger.log(Level.SEVERE, "Initializing LXDatagramOutput failed.", sex);
    }
    if (datagramOutput != null) {
      System.out.println("Output added");
      lx.engine.output.addChild(datagramOutput);
    } else {
      logger.log(Level.SEVERE, "Did not configure output, error during LXDatagramOutput init");
    }
  }

  public static void configureE131Output(LX lx, LightType lightType) {


    int[] dmxChannelsForUniverse = new int[2];

    // R,G,B,W + Master Dimmer value f
    // Multicast 239.255.UniverseHighByte.UniverseLowByte
    // Multicast 239.255.0.*universe number*  since we have only a few universes
    // Universe is 512 channels
    // 4 Universes = 2048 channels
    // 8 Universes = 4096 channels
    // 24 Universes = 12288 channels
    //
    // For Laluce test lights:
    // 5 Channels per light, 4 lights = 20 DMX channels
    // Master-Dimmer, Red-Dimmer, Green-Dimmer, Blue-Dimmer, White-Dimmer
    //
    // For E1.31 Ethernet Bridge:
    // 4 Universes, multicast input.  239.255.0.0, 239.255.0.1, 239.255.0.2, 239.255.0.2, 239.255.0.4
    // Note sure how many of these devices can be added to a network.  Also not sure how well the outputs
    // are synchronized.  Might not be too much of an issue since the update rate will be relatively slow.
    //
    // For target installation devices:
    // Led bar has 7/35/95 channels per bar. (35 is six addressable led groups)
    // 18 leds per bar, 40 bars both sides = 280, 1400, 3800 universes total
    // 20 spots with 6 channels = 120 universes total.
    // Minimum is 400 channels, i.e. 1 universe (not accounting for device per output limit)
    // Mid is 1520 or 3 universes (not accounting for device per output limit)
    // Max is 3920 or 8 universes (not accounting for device per output limit)
    // 20 devices per side or 2 universes per side per device limit constraint of 16 devices per physical output
    // 4 universes minimum per device limit constraint
    //
    //

    // For a single light, just pack the first colors[pointIndex] value into the datagram.
    for (int i = 0; i < 2; i++) {
      dmxChannelsForUniverse[i] = i;
    }
    E131DmxDatagram e131Datagram = new E131DmxDatagram(1, 512,
        dmxChannelsForUniverse);

    switch (lightType) {
      case LALUCE:
        FixtureLaluce24Par laluceFixture = new FixtureLaluce24Par(1);
        e131Datagram.addFixture(laluceFixture);
        e131Datagram.addFixture(laluceFixture);
        break;
      case OPPSKPAR:
        // NOTE(tracy): I'm testing the OPPSk along with existing LaLuce so I can test multiple light
        // types in a single universe.
        //laluceFixture = new FixtureLaluce24Par(1);
        FixtureOPPSk7LedPar oppSkFixture = new FixtureOPPSk7LedPar(1);
        //e131Datagram.addFixture(laluceFixture);
        e131Datagram.addFixture(oppSkFixture);
        break;
      case PRODPAR:
        FixtureProdPar prodParFixture = new FixtureProdPar(1);
        e131Datagram.addFixture(prodParFixture);
        break;
      case PRODWASH:
        FixtureProdWash prodWashFixture = new FixtureProdWash(1);
        e131Datagram.addFixture(prodWashFixture);
        break;
    }

    String dmxIpAddress = "239.255.0.1";
    try {
      e131Datagram.setAddress(dmxIpAddress);
    } catch (UnknownHostException uhex) {
      logger.log(Level.SEVERE, "Configuring ArtNet: " + dmxIpAddress, uhex);
    }

    try {
      datagramOutput = new LXDatagramOutput(lx);
      datagramOutput.addDatagram(e131Datagram);
    } catch (SocketException sex) {
      logger.log(Level.SEVERE, "Initializing LXDatagramOutput failed.", sex);
    }
    if (datagramOutput != null) {
      System.out.println("Output added");
      lx.engine.output.addChild(datagramOutput);
    } else {
      logger.log(Level.SEVERE, "Did not configure output, error during LXDatagramOutput init");
    }
  }
}
