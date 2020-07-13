# Image Compressor

This image-compressor takes an image as input and returns a list of compressed images using progressive compression.



###################

How to Use

###################

Go to src folder, and run following command from terminal. Compressed image will be generated into the output folder.

```
java ImageCompressor -o ../output/YOUR_OUTPUT_FILE_NAME.png -compress YOUR_DESIRED_COMPRESSION_RATIO -progressive -i PATH_TO_THE_IMAGE_TO_COMPRESS
```



###################

Sample Usage

###################

```
java ImageCompressor -o ../output/output.png -compress 90 -progressive -i ../input/input.png
```

