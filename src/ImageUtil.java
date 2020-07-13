import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Has utility methods to read an image from file and write to a file.
 */
public class ImageUtil {

  /**
   * Read image from the source and return the image pixels array.
   *
   * @param filename source directory of input image
   * @return image pixels array
   * @throws IOException when cannot read the input image
   */
  public static int[][][] readImage(String filename) throws IOException {
    BufferedImage input;

    input = ImageIO.read(new FileInputStream(filename));

    int[][][] result = new int[input.getHeight()][input.getWidth()][3];

    for (int i = 0; i < input.getHeight(); i++) {
      for (int j = 0; j < input.getWidth(); j++) {
        int color = input.getRGB(j, i);
        Color c = new Color(color);
        result[i][j][0] = c.getRed();
        result[i][j][1] = c.getGreen();
        result[i][j][2] = c.getBlue();
      }
    }
    return result;
  }

  /**
   * Read image from the source and return the image width.
   *
   * @param filename source directory of input image
   * @return image width
   * @throws IOException when cannot read the input image
   */
  public static int getWidth(String filename) throws IOException {
    BufferedImage input;

    input = ImageIO.read(new FileInputStream(filename));

    return input.getWidth();
  }

  /**
   * Read image from the source and return the image height.
   *
   * @param filename source directory of input image
   * @return image height
   * @throws IOException when cannot read the input image
   */
  public static int getHeight(String filename) throws IOException {
    BufferedImage input;

    input = ImageIO.read(new FileInputStream(filename));

    return input.getHeight();
  }

  /**
   * Write a image with the give image pixels array, width, height and target directory.
   *
   * @param rgb      image pixels array
   * @param width    image width
   * @param height   image height
   * @param filename target output image directory
   * @throws IOException when cannot write the output image
   */
  public static void writeImage(int[][][] rgb, int width, int height, String
          filename)
          throws IOException {

    BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int r = rgb[i][j][0];
        int g = rgb[i][j][1];
        int b = rgb[i][j][2];

        // color is stored in 1 integer, with the 4 bytes storing ARGB in that
        // order. Each of r,g,b are stored in 8 bits (hence between 0 and 255).
        // So we put them all in one integer by using bit-shifting << as below
        int color = (r << 16) + (g << 8) + b;
        output.setRGB(j, i, color);
      }
    }
    String extension = filename.substring(filename.lastIndexOf(".") + 1);
    ImageIO.write(output, extension, new FileOutputStream(filename));
  }
}
