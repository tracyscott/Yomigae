package org.yomigae;

import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.BoundedParameter;
import heronarts.lx.output.LXDatagram;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Datagram output meant for DMX devices.  This will provide hooks for supporting interleaved
 * DMX channels for DMX controls other than R,G,B.  For example, the minimum channel option for
 * LaLuce lights is 5 channels with the first channel being a master dimmer for all RGB channels.
 * It also includes a White LED channel.  Typically the White LED could be used for color mixing
 * purposes to increase the available gamut of the R,G,B leds.  Since we are attempting maximum
 * white output with a targeted color temperature we aren't interested in increasing the gamut.
 * For example, we will put white at maximum output and add as much R,G,B as possible while
 * targeting a specific color temperature.
 */
public abstract class LXDmxDatagram extends LXDatagram {

  /**
   * Construct a LXDmxDatagram that represents a single UDP packet.
   * @param bufferSize The buffer size for the packet.  Note, for a DMX light with 5 channels
   *                   such as a master dimmer with separate R, G, and B dimmers, and W dimmers this should
   *                   be 5 * the number of LXPoints for example.
   */
  protected LXDmxDatagram(int bufferSize) {
    super(bufferSize);
  }

  /**
   * Called once before the entire block of RGB color values are serialized to the packet. This is
   * to support DMX devices that require additional channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int preRGBBlockHook(int offset) {
    return 0;
  }

  /**
   * Called once after the entire block of RGB color values are serialized to the packet.  This is
   * to support DMX devices that require additional channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int postRGBBlockHook(int offset) {
    return 0;
  }

  /**
   * Called once before each RGB value is written.  This is to support DMX devices that require additional
   * channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int preRGBHook(int offset) {
    // LaLuce hack.  It always has a master dimmer channel with just simple RGB mode.
    this.buffer[offset] = (byte)(255 & 0xff);
    return 1;
  }

  /**
   * Called once after each RGB value is written.  This is to support DMX devices that require additional
   * channels beyond RGB.
   * @return The number of bytes added to the packet.
   */
  protected int postRGBHook(int offset) {
    // LaLuce hack, set the white LED to 0.
    this.buffer[offset] = 0;
    return 1;
  }

  /**
   * Helper for subclasses to copy a list of points into the data buffer at a
   * specified offset. For many subclasses which wrap RGB buffers, onSend() will
   * be a simple call to this method with the right parameters.
   *
   * @param colors Array of color values
   * @param glut Look-up table of gamma-corrected brightness values
   * @param indexBuffer Array of point indices
   * @param offset Offset in buffer to write
   * @return this
   */
  protected LXDmxDatagram copyPoints(int[] colors, byte[] glut, int[] indexBuffer, int offset) {
    int[] byteOffset = BYTE_ORDERING[this.byteOrder.ordinal()];
    offset += preRGBBlockHook(offset);
    for (int index : indexBuffer) {
      offset += preRGBHook(offset);
      int color = (index >= 0) ? colors[index] : 0;
      this.buffer[offset + byteOffset[0]] = glut[((color >> 16) & 0xff)]; // R
      this.buffer[offset + byteOffset[1]] = glut[((color >> 8) & 0xff)]; // G
      this.buffer[offset + byteOffset[2]] = glut[(color & 0xff)]; // B
      offset += 3;
      offset += postRGBHook(offset);
    }
    postRGBBlockHook(offset);
    return this;
  }

  /**
   * Invoked by engine to send this packet when new color data is available. The
   * LXDatagram should update the packet object accordingly to contain the
   * appropriate buffer.
   *
   * @param colors Color buffer
   * @param glut Look-up table with gamma-adjusted brightness values
   */
  public abstract void onSend(int[] colors, byte[] glut);
}
