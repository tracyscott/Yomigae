package org.yomigae;

import com.google.common.reflect.ClassPath;
import heronarts.lx.LXEffect;
import heronarts.lx.LXPattern;
import heronarts.lx.model.LXModel;
import heronarts.lx.studio.LXStudio;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import processing.core.PApplet;

public class Yomigae extends PApplet {
	
	static {
    System.setProperty(
        "java.util.logging.SimpleFormatter.format",
        "%3$s: %1$tc [%4$s] %5$s%6$s%n");
  }

    /**
   * Set the main logging level here.
   *
   * @param level the new logging level
   */
  public static void setLogLevel(Level level) {
    // Change the logging level here
    Logger root = Logger.getLogger("");
    root.setLevel(level);
    for (Handler h : root.getHandlers()) {
      h.setLevel(level);
    }
  }


  /**
   * Adds logging to a file. The file name will be appended with a dash, date stamp, and
   * the extension ".log".
   *
   * @param prefix prefix of the log file name
   * @throws IOException if there was an error opening the file.
   */
  public static void addLogFileHandler(String prefix) throws IOException {
    String suffix = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    Logger root = Logger.getLogger("");
    Handler h = new FileHandler(prefix + "-" + suffix + ".log");
    h.setFormatter(new SimpleFormatter());
    root.addHandler(h);
  }

  private static final Logger logger = Logger.getLogger(Yomigae.class.getName());

  public static void main(String[] args) {
    PApplet.main(Yomigae.class.getName(), args);
  }

  private static final String LOG_FILENAME_PREFIX = "yomigae";

  // Reference to top-level LX instance
  private heronarts.lx.studio.LXStudio lx;

  public static PApplet pApplet;
  public static final int GLOBAL_FRAME_RATE = 40;

  public static UIWhiteControl laluceWhiteControl;
  public static UIWhiteControl prodParWhiteControl;
  public static UIWhiteControl prodWashWhiteControl;
  public static UIWhiteControl oppSkWhiteControl;
  public static UIFixtureType fixtureTypeControl;

    @Override
  public void settings() {
    size(1024, 600, P3D);
  }

  /**
   * Registers all patterns and effects that LX doesn't already have registered.
   * This check is important because LX just adds to a list.
   *
   * @param lx the LX environment
   */
  private void registerAll(LXStudio lx) {
    List<Class<? extends LXPattern>> patterns = lx.getRegisteredPatterns();
    List<Class<? extends LXEffect>> effects = lx.getRegisteredEffects();
    final String parentPackage = getClass().getPackage().getName();

    try {
      ClassPath classPath = ClassPath.from(getClass().getClassLoader());
      for (ClassPath.ClassInfo classInfo : classPath.getAllClasses()) {
        // Limit to this package and sub-packages
        if (!classInfo.getPackageName().startsWith(parentPackage)) {
          continue;
        }
        Class<?> c = classInfo.load();
        if (Modifier.isAbstract(c.getModifiers())) {
          continue;
        }
        if (LXPattern.class.isAssignableFrom(c)) {
          Class<? extends LXPattern> p = c.asSubclass(LXPattern.class);
          if (!patterns.contains(p)) {
            lx.registerPattern(p);
            logger.info("Added pattern: " + p);
          }
        } else if (LXEffect.class.isAssignableFrom(c)) {
          Class<? extends LXEffect> e = c.asSubclass(LXEffect.class);
          if (!effects.contains(e)) {
            lx.registerEffect(e);
            logger.info("Added effect: " + e);
          }
        }
      }
    } catch (IOException ex) {
      logger.log(Level.WARNING, "Error finding pattern and effect classes", ex);
    }
  }

  @Override
  public void setup() {
    // Processing setup, constructs the window and the LX instance
    pApplet = this;

    try {
      addLogFileHandler(LOG_FILENAME_PREFIX);
    } catch (IOException ex) {
      logger.log(Level.SEVERE, "Error creating log file: " + LOG_FILENAME_PREFIX, ex);
    }

    LXModel model = YomigaeModel3D.generateLXModel();
    LXStudio.Flags flags = new LXStudio.Flags();
    flags.showFramerate = false;
    flags.isP3LX = true;
    flags.immutableModel = true;

    logger.info("Current renderer:" + sketchRenderer());
    logger.info("Current graphics:" + getGraphics());
    logger.info("Current graphics is GL:" + getGraphics().isGL());
    logger.info("Multithreaded hint: " + MULTITHREADED);
    logger.info("Multithreaded actually: " + (MULTITHREADED && !getGraphics().isGL()));
    lx = new LXStudio(this, flags, model);

    lx.ui.setResizable(true);

    fixtureTypeControl = (UIFixtureType) new UIFixtureType(lx.ui, lx).setExpanded(true).addToContainer(lx.ui.leftPane.global);

    laluceWhiteControl = (UIWhiteControl) new UIWhiteControl(lx.ui, "LALUCE WHITE", "laluce_whites").
        setExpanded(false).addToContainer(lx.ui.leftPane.global);
    prodParWhiteControl = (UIWhiteControl) new UIWhiteControl(lx.ui, "PRODPAR WHITE", "prodpar_whites")
        .setExpanded(false).addToContainer(lx.ui.leftPane.global);
    prodWashWhiteControl = (UIWhiteControl) new UIWhiteControl(lx.ui, "PRODWASH WHITE", "prodwash_whites")
        .setExpanded(false).addToContainer(lx.ui.leftPane.global);
    oppSkWhiteControl = (UIWhiteControl) new UIWhiteControl(lx.ui, "OPPSK WHITE", "oppskpar_whites")
        .setExpanded(false).addToContainer(lx.ui.leftPane.global);

    if (enableOutput) {
      Output.configureE131Output(lx, Output.LightType.OPPSKPAR);
      //Output.configureArtnetOutput(lx);
    }
    if (disableOutputOnStart)
      lx.engine.output.enabled.setValue(false);

    frameRate(GLOBAL_FRAME_RATE);
  }

  public void initialize(final LXStudio lx, LXStudio.UI ui) {
    // Add custom components or output drivers here
    // Register settings
    // lx.engine.registerComponent("yomigaeSettings", new Settings(lx, ui));

    // Common components
    // registry = new Registry(this, lx);

    // Register any patterns and effects LX doesn't recognize
    registerAll(lx);
  }

  public void onUIReady(LXStudio lx, LXStudio.UI ui) {

  }

  public void draw() {
    // All is handled by LX Studio
  }

  // Configuration flags
  private final static boolean MULTITHREADED = false;  // Disabled for anything GL
                                                       // Enable at your own risk!
                                                       // Could cause VM crashes.
  private final static boolean RESIZABLE = true;

  // Helpful global constants
  final static float INCHES = 1.0f / 12.0f;
  final static float IN = INCHES;
  final static float FEET = 1.0f;
  final static float FT = FEET;
  final static float CM = IN / 2.54f;
  final static float MM = CM * .1f;
  final static float M = CM * 100;
  final static float METER = M;

  public static final boolean enableOutput = true;
  public static final boolean disableOutputOnStart = true;

  public static final int LEDS_PER_UNIVERSE = 170;
}