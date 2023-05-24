package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int width;
    private int height;
    private double maxRatio;
    private TextColorSchema schema;

    public Converter() {
        schema = new Schema();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        StringBuilder conclusion = new StringBuilder();
        BufferedImage img = ImageIO.read(new URL(url));
        maximumRatio(img);

        int newWidth = 0;
        int newHeight = 0;

        int cofw = 0;
        int cofh = 0;
        if (img.getWidth() > width || img.getHeight() > height) {
            if (width != 0) {
                cofw = img.getWidth() / width;
            } else cofw = 1;
            if (height != 0) {
                cofh = img.getHeight() / height;
            } else cofh = 1;
            int maxcof = Math.max(cofw, cofh);
            newHeight = (img.getHeight() / maxcof);
            newWidth = (img.getWidth() / maxcof);
        } else {
            newWidth = img.getWidth();
            newHeight = img.getHeight();
        }


        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        WritableRaster bwRaster = bwImg.getRaster();

        char[][] arr = new char[newHeight][newWidth];
        for (int h = 0; h < newHeight; h++) {
            for (int w = 0; w < newWidth; w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                arr[h][w] = c;
            }
        }


        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                conclusion.append(arr[i][j]);
                conclusion.append(arr[i][j]);
            }
            conclusion.append("\n");
        }



        return conclusion.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema colorSchema) {
        this.schema = colorSchema;
    }

    private void maximumRatio(BufferedImage img) throws BadImageSizeException {
        double ratio;
        double width = img.getWidth();
        double height = img.getHeight();
        if (width / height > height / width) {
            ratio = width / height;
        } else {
            ratio = height / width;
        }
        if (ratio > maxRatio && maxRatio != 0) {
            throw new BadImageSizeException(ratio, maxRatio);
        }
    }
}
