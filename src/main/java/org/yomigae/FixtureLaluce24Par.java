package org.yomigae;

/**
 * Base class representing a DMX fixture.  Used by LXDmxDatagram to build a DMX512 frame.  The
 * LXDmxDatagram will contain a list of fixtures and process them in order to build the frame.
 */
public class FixtureLaluce24Par extends DmxFixture {

  public FixtureLaluce24Par(int numColors) {
    super(numColors);
  }

  /**
   * Called once before each RGB value is written.  This is to support DMX devices that require additional
   * channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int preRGBHook(byte[] buffer, int offset) {
    // LaLuce hack.  It always has a master dimmer channel with just simple RGB mode.
    buffer[offset] = (byte)0xff;
    return 1;
  }

  /**
   * Called once after each RGB value is written.  This is to support DMX devices that require additional
   * channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int postRGBHook(byte[] buffer, int offset) {
    buffer[offset] = (byte)Yomigae.laluceWhiteControl.whiteOverride;
    return 1;
  }
}
