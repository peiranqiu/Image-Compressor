import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents an image using a 3D array of integers.
 */
public class Image {
  private int originalWidth;
  private int originalHeight;
  private int[][][] pixels;

  /**
   * Construct an image using the provided 3d array, width and height.
   *
   * @param array  contents of this image
   * @param width  width of the image
   * @param height height of the image
   * @throws IllegalArgumentException if the color info is invalid or width/height is non-positive
   */
  public Image(int[][][] array, int width, int height) throws IllegalArgumentException {
    if (array == null || array.length == 0 || array[0].length == 0 || array[0][0].length == 0
            || width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Invalid Input.");
    }

    this.pixels = array;
    this.originalWidth = width;
    this.originalHeight = height;
  }

  /**
   * Construct a black image using the provided width and height.
   *
   * @param width  width of the image
   * @param height height of the image
   * @throws IllegalArgumentException if width/height is non-positive
   */
  public Image(int width, int height) throws IllegalArgumentException {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Invalid Input.");
    }

    this.originalWidth = width;
    this.originalHeight = height;
    this.pixels = new int[height][width][3];
  }

  /**
   * Get the image width.
   *
   * @return the width of the image
   */
  public int getWidth() {
    return this.originalWidth;
  }

  /**
   * Get the image height.
   *
   * @return the height of the image
   */
  public int getHeight() {
    return this.originalHeight;
  }

  /**
   * Returns the contents of this image as a 3D array.
   *
   * @return the contents of this image
   */
  public int[][][] toArray() {
    int[][][] newPixels = new int[originalHeight][originalWidth][3];
    for (int row = 0; row < originalHeight; row++) {
      for (int column = 0; column < originalWidth; column++) {
        for (int channel = 0; channel < 3; channel++) {
          newPixels[row][column][channel] = pixels[row][column][channel];
        }
      }
    }

    return newPixels;
  }

  /**
   * Return an integer closest to a double.
   *
   * @param val a provided double
   * @return an integer closest to the double
   */
  private int round(double val) {
    double leftDiff = val - ((int) val);
    double rightDiff = ((int) val) + 1 - val;
    return leftDiff < rightDiff ? ((int) val) : ((int) val) + 1;
  }

  /**
   * Normalize the array of the image.
   */
  private void normalize() {
    int max = Integer.MIN_VALUE;
    int min = Integer.MAX_VALUE;
    int height = pixels.length;
    int width = pixels[0].length;

    for (int row = 0; row < height; row++) {
      for (int column = 0; column < width; column++) {
        for (int channel = 0; channel < 3; channel++) {
          max = Math.max(max, pixels[row][column][channel]);
          min = Math.min(min, pixels[row][column][channel]);
        }
      }
    }

    for (int row = 0; row < height; row++) {
      for (int column = 0; column < width; column++) {
        for (int channel = 0; channel < 3; channel++) {
          double v = pixels[row][column][channel];
          v = (v - min) * 255.0 / (max - min);
          pixels[row][column][channel] = round(v);
        }
      }
    }
  }

  /**
   * Determine whether the color array in this image needs to be normalized.
   *
   * @return need normalize or not
   */
  private boolean needNormalize() {
    int height = pixels.length;
    int width = pixels[0].length;

    for (int row = 0; row < height; row++) {
      for (int column = 0; column < width; column++) {
        for (int channel = 0; channel < 3; channel++) {
          int v = pixels[row][column][channel];
          if (v > 255 || v < 0) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Pad extra rows and columns to create a square image whose dimensions are a power of 2.
   *
   * @param maxCurr the height or width of the image which is larger
   * @return the next power of 2 of the maxCurr
   */
  private int squareLength(int maxCurr) {
    if (maxCurr == 1 || maxCurr == 2) {
      return maxCurr;
    }

    int squareLength = 1;
    while (squareLength * 2 < maxCurr) {
      squareLength *= 2;
    }
    return squareLength * 2;
  }

  /**
   * Apply the Haar wavelet transform to a 1D array.
   *
   * @param oneDimPixels the given 1D array
   * @return the transformed 1D array
   */
  public double[] oneDimHaarWaveletTransform(double[] oneDimPixels) {
    int size = oneDimPixels.length;
    List<Double> newPixelsList = null;
    while (size >= 2) {
      newPixelsList = new ArrayList<>(size);
      for (int i = 0; i <= size - 2; i += 2) {
        newPixelsList.add((oneDimPixels[i] + oneDimPixels[i + 1]) / Math.sqrt(2));
      }
      for (int i = 0; i <= size - 2; i += 2) {
        newPixelsList.add((oneDimPixels[i] - oneDimPixels[i + 1]) / Math.sqrt(2));
      }

      for (int i = 0; i < newPixelsList.size(); i++) {
        oneDimPixels[i] = newPixelsList.get(i);
      }
      size /= 2;
    }

    return oneDimPixels;
  }

  /**
   * Transpose the 2D array by exchanging row and column.
   *
   * @param pixels the given 2D array
   * @return the transposed 2D array
   */
  private double[][] transpose(double[][] pixels) {
    int length = pixels.length;
    double[][] resultPixels = new double[length][length];

    // TRANSPOSE
    for (int column = 0; column < length; column++) {
      for (int row = 0; row < length; row++) {
        resultPixels[column][row] = pixels[row][column];
      }
    }

    return resultPixels;
  }

  /**
   * Apply the Haar wavelet transform to a 2D array.
   *
   * @param twoDimPixels the given 2D array
   * @return the transformed 2D array
   */
  private double[][] twoDimHaarWaveletTransform(double[][] twoDimPixels) {
    int rows = twoDimPixels.length;

    double[][] newTwoDimPixels = new double[rows][rows];

    // make it to J
    for (int row = 0; row < rows; row++) {
      double[] resultRow = oneDimHaarWaveletTransform(twoDimPixels[row]);
      for (int i = 0; i < resultRow.length; i++) {
        newTwoDimPixels[row][i] = resultRow[i];
      }
    }

    // make it to K
    double[][] transposePixels = transpose(newTwoDimPixels);
    for (int row = 0; row < rows; row++) {
      double[] resultRow = oneDimHaarWaveletTransform(transposePixels[row]);
      for (int i = 0; i < resultRow.length; i++) {
        newTwoDimPixels[row][i] = resultRow[i];
      }
    }

    // transpose back
    return transpose(newTwoDimPixels);
  }

  /**
   * Determine the threshold to be used based on the specified compression ratio.
   *
   * @param compressionRatio the given compression ratio
   * @param array            the array storing the nonzero data
   * @return the calculated threshold
   */
  private double threshold(float compressionRatio, List<Double> array) {
    Collections.sort(array);

    int size = array.size();

    int pos = (int) (size * compressionRatio);
    System.out.println("Threshold: " + array.get(pos));

    return array.get(pos);
  }

  /**
   * Apply the Inverse Haar wavelet transform to a 1D array.
   *
   * @param oneDimPixels the given 1D array
   * @return the transformed 1D array
   */
  public double[] oneDimInverseHaarWaveletTransform(double[] oneDimPixels) {
    int length = oneDimPixels.length;
    int size = 2;
    List<Double> newPixelsList = null;
    while (size <= length) {
      newPixelsList = new ArrayList<>(size);
      for (int i = 0; i < (size / 2); i++) {
        int j = i + (size / 2);
        newPixelsList.add((oneDimPixels[i] + oneDimPixels[j]) / Math.sqrt(2));
        newPixelsList.add((oneDimPixels[i] - oneDimPixels[j]) / Math.sqrt(2));
      }

      for (int i = 0; i < size; i++) {
        oneDimPixels[i] = newPixelsList.get(i);
      }
      size *= 2;
    }

    return oneDimPixels;
  }

  /**
   * Apply the Inverse Haar wavelet transform to a 2D array.
   *
   * @param twoDimPixels the given 2D array
   * @return the transformed 2D array
   */
  private double[][] twoDimInverseHaarWaveletTransform(double[][] twoDimPixels) {
    // FIRST TRANSPOSE array
    // first apply it to each column of K to get J
    double[][] transposePixels = transpose(twoDimPixels);
    int rows = twoDimPixels.length;
    double[][] newTwoDimPixels = new double[rows][rows];

    // make it from K to J
    for (int row = 0; row < rows; row++) {
      double[] resultRow = oneDimInverseHaarWaveletTransform(transposePixels[row]);
      for (int i = 0; i < resultRow.length; i++) {
        newTwoDimPixels[row][i] = resultRow[i];
      }
    }

    transposePixels = transpose(newTwoDimPixels);

    // make it from J to I
    for (int row = 0; row < rows; row++) {
      double[] resultRow = oneDimInverseHaarWaveletTransform(transposePixels[row]);
      for (int i = 0; i < resultRow.length; i++) {
        newTwoDimPixels[row][i] = resultRow[i];
      }
    }

    return newTwoDimPixels;
  }

  /**
   * Compress the image with a given compression ratio.
   *
   * @param compressionRatio the given compression ratio
   * @return the compressed image
   * @throws IllegalArgumentException if the compression ratio is out of the range [0,1]
   */
  public Image compress(float compressionRatio) throws IllegalArgumentException {
    if (compressionRatio < 0 || compressionRatio > 1) {
      throw new IllegalArgumentException("Compression Ratio should be in [0, 1].");
    }

    int squareLength = squareLength(Math.max(originalHeight, originalWidth));

    double[][] newPixelsRed = new double[squareLength][squareLength];
    double[][] newPixelsGreen = new double[squareLength][squareLength];
    double[][] newPixelsBlue = new double[squareLength][squareLength];

    int currHeight = pixels.length;
    int currWidth = pixels[0].length;

    for (int row = 0; row < currHeight; row++) {
      for (int column = 0; column < currWidth; column++) {
        newPixelsRed[row][column] = pixels[row][column][0];
        newPixelsGreen[row][column] = pixels[row][column][1];
        newPixelsBlue[row][column] = pixels[row][column][2];
      }
    }

    newPixelsRed = twoDimHaarWaveletTransform(newPixelsRed);
    newPixelsGreen = twoDimHaarWaveletTransform(newPixelsGreen);
    newPixelsBlue = twoDimHaarWaveletTransform(newPixelsBlue);

    List<Double> array = new ArrayList<>(squareLength * squareLength * 3);

    for (int row = 0; row < squareLength; row++) {
      for (int column = 0; column < squareLength; column++) {
        if (Math.abs(newPixelsRed[row][column]) - 0 > 0.000001) {
          array.add(Math.abs(newPixelsRed[row][column]));
        }
        if (Math.abs(newPixelsGreen[row][column]) - 0 > 0.000001) {
          array.add(Math.abs(newPixelsGreen[row][column]));
        }
        if (Math.abs(newPixelsBlue[row][column]) - 0 > 0.000001) {
          array.add(Math.abs(newPixelsBlue[row][column]));
        }
      }
    }

    // Lossy Compression
    if (compressionRatio == 1) {
      for (int row = 0; row < squareLength; row++) {
        for (int column = 0; column < squareLength; column++) {
          newPixelsRed[row][column] = 0;
          newPixelsGreen[row][column] = 0;
          newPixelsBlue[row][column] = 0;
        }
      }
    } else if (compressionRatio != 0) {
      double threshold = threshold(compressionRatio, array);

      for (int row = 0; row < squareLength; row++) {
        for (int column = 0; column < squareLength; column++) {
          if (Math.abs(newPixelsRed[row][column]) <= threshold) {
            newPixelsRed[row][column] = 0;
          }
          if (Math.abs(newPixelsGreen[row][column]) <= threshold) {
            newPixelsGreen[row][column] = 0;
          }
          if (Math.abs(newPixelsBlue[row][column]) <= threshold) {
            newPixelsBlue[row][column] = 0;
          }
        }
      }
    }

    newPixelsRed = twoDimInverseHaarWaveletTransform(newPixelsRed);
    newPixelsGreen = twoDimInverseHaarWaveletTransform(newPixelsGreen);
    newPixelsBlue = twoDimInverseHaarWaveletTransform(newPixelsBlue);

    int[][][] newPixels = new int[originalHeight][originalWidth][3];

    for (int row = 0; row < originalHeight; row++) {
      for (int column = 0; column < originalWidth; column++) {
        newPixels[row][column][0] = round(newPixelsRed[row][column]);
        newPixels[row][column][1] = round(newPixelsGreen[row][column]);
        newPixels[row][column][2] = round(newPixelsBlue[row][column]);
      }
    }

    Image result = new Image(newPixels, originalWidth, originalHeight);
    if (result.needNormalize()) {
      result.normalize();
    }

    return result;
  }

  /**
   * Generates an image with progressive compression, provided with current RGB content and length.
   *
   * @param red    the red channel of the image
   * @param green  the green channel of the image
   * @param blue   the blue channel of the image
   * @param length the square length of next power of two
   * @return the image created by one iteration of progressive compression
   */
  private Image compress3Of4(double[][] red, double[][] green, double[][] blue, int length) {
    int boardLength = red.length;
    for (int i = 0; i < boardLength; i++) {
      for (int j = 0; j < boardLength; j++) {
        if (i >= (length / 2) || j >= (length / 2)) {
          red[i][j] = 0;
          green[i][j] = 0;
          blue[i][j] = 0;
        }
      }
    }

    double[][] newPixelsRed = twoDimInverseHaarWaveletTransform(red);
    double[][] newPixelsGreen = twoDimInverseHaarWaveletTransform(green);
    double[][] newPixelsBlue = twoDimInverseHaarWaveletTransform(blue);

    int[][][] newPixels = new int[originalHeight][originalWidth][3];

    for (int row = 0; row < originalHeight; row++) {
      for (int column = 0; column < originalWidth; column++) {
        newPixels[row][column][0] = round(newPixelsRed[row][column]);
        newPixels[row][column][1] = round(newPixelsGreen[row][column]);
        newPixels[row][column][2] = round(newPixelsBlue[row][column]);
      }
    }

    Image result = new Image(newPixels, originalWidth, originalHeight);
    if (result.needNormalize()) {
      result.normalize();
    }

    return result;
  }

  /**
   * Returns a list of images with the progressive compression, from original image to the most
   * compressed image.
   *
   * @return a list of images with the progressive compression
   */
  public List<Image> progressive() {
    List<Image> resultList = new ArrayList<>();

    int squareLength = squareLength(Math.max(originalHeight, originalWidth));

    double[][] newPixelsRed = new double[squareLength][squareLength];
    double[][] newPixelsGreen = new double[squareLength][squareLength];
    double[][] newPixelsBlue = new double[squareLength][squareLength];

    int currHeight = pixels.length;
    int currWidth = pixels[0].length;

    for (int row = 0; row < currHeight; row++) {
      for (int column = 0; column < currWidth; column++) {
        newPixelsRed[row][column] = pixels[row][column][0];
        newPixelsGreen[row][column] = pixels[row][column][1];
        newPixelsBlue[row][column] = pixels[row][column][2];
      }
    }

    newPixelsRed = twoDimHaarWaveletTransform(newPixelsRed);
    newPixelsGreen = twoDimHaarWaveletTransform(newPixelsGreen);
    newPixelsBlue = twoDimHaarWaveletTransform(newPixelsBlue);

    int examBoardLength = squareLength * 2;

    while (examBoardLength > 1) {
      Image image = compress3Of4(newPixelsRed, newPixelsGreen, newPixelsBlue, examBoardLength);
      resultList.add(image);
      examBoardLength /= 2;
    }

    return resultList;
  }
}
