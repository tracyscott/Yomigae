package org.yomigae;

/**
 * Simple implementation of RGBWA led washer bar based on specs on Google Drive.
 * TODO(tracy): Bar supports a 35 channel mode (6 groups of 3 rgbwa) or 95 channel mode
 * (all 18 rgbwa groups are individually controllable).
 */
public class FixtureProdWash extends DmxFixture {
  public FixtureProdWash(int numColors) {
    super(numColors);
  }
  protected int preRGBHook(byte[] buffer, int offset) {
    // Prod led washer bars have a master dimmer and a strobe channel.
    buffer[offset] = (byte)0xff;
    buffer[offset+1] = 0;
    return 2;
  }


  /**
   * Called once after each RGB value is written.  This is to support DMX devices that require additional
   * channels beyond RGB.  For our production Par light, this are the White, Amber, and UV channels.
   * @return The number of bytes added to the packet.
   */
  protected int postRGBHook(byte[] buffer, int offset) {
    buffer[offset] = (byte)Yomigae.prodWashWhiteControl.whiteOverride;
    buffer[offset+1] = (byte)Yomigae.prodWashWhiteControl.amberOverride;
    return 2;
  }

}
