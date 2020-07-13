import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class takes the path of the input image file and options as command-line arguments.
 */
public class ImageCompressor {

  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  /**
   * The main method can be used in terminal.
   *
   * @param args arguments for the main method to generate images
   * @throws IOException when file has something wrong
   */
  public static void main(String[] args) throws IOException {
    
    if (args.length >= 8 || args.length < 2) {
      LOGGER.log(Level.INFO, "You have wrong number of arguments.");
      System.exit(-1);
    }

    String outputFile = "out.png";
    String inputFile = null;
    String fileName = "";
    boolean needProgressive = false;
    boolean needCompress = false;
    float ratio = -0.1F;
    Image image = null;
    Image compressedImage = null;
    int j;
    int i = 0;

    while (i < args.length && args[i].startsWith("-")) {
      if (args[i].equals("-compress")) {
        needCompress = true;
        try {
          ratio = Float.parseFloat(args[++i]) / 100;
        } catch (NumberFormatException e) {
          LOGGER.log(Level.INFO, "Your compression ratio is incorrect.");
          System.exit(-1);
        }
      } else if (args[i].equals("-o")) {
        outputFile = args[++i];
      } else if (args[i].equals("-i")) {
        inputFile = args[++i];
      } else if (args[i].equals("-progressive")) {
        needProgressive = true;

        if ((i + 1) < args.length && !(args[i + 1]).startsWith("-")) {
          LOGGER.log(Level.INFO, "Cannot pass an arg after -progressive.");
          System.exit(-1);
        }
      } else {
        LOGGER.log(Level.INFO, "Illegal command passed.");
        System.exit(-1);
      }
      ++i;
    }

    if (inputFile == null) {
      LOGGER.log(Level.INFO, "Please provide an input file.");
      System.exit(-1);
    }

    try {
      int[][][] colors = ImageUtil.readImage(inputFile);
      image = new Image(colors, ImageUtil.getWidth(inputFile), ImageUtil
        .getHeight(inputFile));
    } catch (IOException e) {
      LOGGER.log(Level.INFO, "Error occured when getting the input image.");
      System.exit(-1);
    }

    if ((ratio < 0 || ratio > 1) && needCompress) {
      LOGGER.log(Level.INFO, "Please provide a valid compression ratio.");
      System.exit(-1);
    }

    if (ratio != -0.1F && needCompress) {
      compressedImage = image.compress(ratio);

      try {
        ImageUtil.writeImage(compressedImage.toArray(), compressedImage.getWidth(),
          compressedImage.getHeight(), outputFile);
      } catch (IOException e) {
        LOGGER.log(Level.INFO, "Error occured when creating a compressed image.");
        System.exit(-1);
      } finally {
        LOGGER.log(Level.INFO, "Compressed Image Created Successfully.");
      }
    }

    fileName = outputFile.substring(0, outputFile.lastIndexOf("."));

    if (needProgressive) {
      List<Image> list = image.progressive();
      for (j = 0; j < list.size(); ++j) {
        int k = list.size() - 1 - j;
        String outputImageName = fileName + "-" + k + ".png";
        try {
          ImageUtil.writeImage(list.get(j).toArray(), list.get(j).getWidth(),
            list.get(j).getHeight(), outputImageName);
        } catch (IOException e) {
          LOGGER.log(Level.INFO, "Error occured when creating a progressive image.");
          System.exit(-1);
        }
      }
      LOGGER.log(Level.INFO, "Progressive Image Created Successfully.");
    }
  }
}
