package org.yomigae;

/**
 * Base class representing a DMX fixture.  Used by LXDmxDatagram to build a DMX512 frame.  The
 * LXDmxDatagram will contain a list of fixtures and process them in order to build the frame.
 */
public class FixtureProdPar extends DmxFixture {

  public FixtureProdPar(int numColors) {
    super(numColors);
  }

  /**
   * Called once after each RGB value is written.  This is to support DMX devices that require additional
   * channels beyond RGB.  For our production Par light, this are the White, Amber, and UV channels.
   * @return The number of bytes added to the packet.
   */
  protected int postRGBHook(byte[] buffer, int offset) {
    buffer[offset] = (byte)Yomigae.prodParWhiteControl.whiteOverride;
    buffer[offset+1] = (byte)Yomigae.prodParWhiteControl.amberOverride;
    buffer[offset+2] = (byte)Yomigae.prodParWhiteControl.uvOverride;
    return 3;
  }
}
