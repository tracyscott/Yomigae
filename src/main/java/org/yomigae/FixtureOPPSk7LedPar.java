package org.yomigae;

/**
 * Base class representing a DMX fixture.  Used by LXDmxDatagram to build a DMX512 frame.  The
 * LXDmxDatagram will contain a list of fixtures and process them in order to build the frame.
 * This is an OPPSK 7Led 70 Watt Par with RGBWA.
 */
public class FixtureOPPSk7LedPar extends DmxFixture {

  public FixtureOPPSk7LedPar(int numColors) {
    super(numColors);
  }

  /**
   * Pre-RGB DMX channel info.  For this fixture, we have a master dimmer.  Always set it 255.
   * @param buffer
   * @param offset
   * @return
   */
  protected int preRGBHook(byte[] buffer, int offset) {
    buffer[offset] = (byte)0xff;
    return 1;
  }

  /**
   * Called once after each RGB value is written.  This is to support DMX devices that require additional
   * channels beyond RGB.  For this par light, this are the White, Amber.
   * It also includes:
   * 7 - Strobe Mode: 0-9 No function, 10-255 Strobe speed (slow to fast) CH1 must be on. 2,3,4,5,6 to select or add color
   * 8 - Program: 0-50 No function, 101-150 Fade, 151-200 Pulse, 201-250 Autoplay, 251-255 Sound activated
   * 9 - Speed Adjustment: 0-255 Speed for ch8 (slow to fast)
   * @return The number of bytes added to the packet.
   */
  protected int postRGBHook(byte[] buffer, int offset) {
    buffer[offset] = (byte)Yomigae.prodParWhiteControl.whiteOverride;
    buffer[offset+1] = (byte)Yomigae.prodParWhiteControl.amberOverride;
    buffer[offset+2] = 0; // Strobe
    buffer[offset+3] = 0; // Program
    buffer[offset+4] = 0; // Speed Adjustment
    return 5;
  }
}
