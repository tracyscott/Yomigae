package org.yomigae;

import heronarts.lx.LX;
import heronarts.lx.output.LXDatagramOutput;
import heronarts.lx.output.StreamingACNDatagram;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles output from our 'colors' buffer to our DMX lights.  Currently using E1.31.
 */
public class Output {
  private static final Logger logger = Logger.getLogger(Output.class.getName());

  public static LXDatagramOutput datagramOutput = null;

  public static void configureLaluceOutput(LX lx) {

    // R,G,B,W + Master Dimmer value
    int[] dmxChannelsForUniverse = new int[1];
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
    for (int i = 0; i < 1; i++) {
      dmxChannelsForUniverse[i] = i;
    }
    E131DmxDatagram laluceDatagram = new E131DmxDatagram(1, 5 * dmxChannelsForUniverse.length,
        dmxChannelsForUniverse);

    String dmxIpAddress = "239.255.0.1";
    try {
      laluceDatagram.setAddress(dmxIpAddress);
    } catch (UnknownHostException uhex) {
      logger.log(Level.SEVERE, "Configuring ArtNet: " + dmxIpAddress, uhex);
    }

    LXDatagramOutput datagramOutput = null;
    try {
      datagramOutput = new LXDatagramOutput(lx);
      datagramOutput.addDatagram(laluceDatagram);
    } catch (SocketException sex) {
      logger.log(Level.SEVERE, "Initializing LXDatagramOutput failed.", sex);
    }
    if (datagramOutput != null) {
      lx.engine.output.addChild(datagramOutput);
    } else {
      logger.log(Level.SEVERE, "Did not configure output, error during LXDatagramOutput init");
    }
  }
}
