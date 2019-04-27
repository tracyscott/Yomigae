package org.yomigae;

/**
 * Base class representing a DMX fixture.  Used by LXDmxDatagram to build a DMX512 frame.  The
 * LXDmxDatagram will contain a list of fixtures and process them in order to build the frame.
 */
public class YFixture {

  protected int numColors;

  /**
   * Each custom fixture must specify the number of colors that it will consume.  Subclasses need only to
   * implement pre-rgb and post-rgb hooks.  This class will handle serialization into the DMX512 packet.
   * @param numColors
   */
  public YFixture (int numColors) {
    this.numColors = numColors;
  }

  /**
   * @return Returns the number of colors this fixture expects.
   */
  public int getNumberOfColors() {
    return numColors;
  }

  /**
   * Called once before the entire block of RGB color values are serialized to the packet. This is
   * to support DMX devices that require additional channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int preRGBBlockHook(byte[] buffer, int offset) {
    return 0;
  }

  /**
   * Called once after the entire block of RGB color values are serialized to the packet.  This is
   * to support DMX devices that require additional channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int postRGBBlockHook(byte[] buffer, int offset) {
    return 0;
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
    // LaLuce hack, set the white LED to 0.
    buffer[offset] = 0;
    return 1;
  }
}
