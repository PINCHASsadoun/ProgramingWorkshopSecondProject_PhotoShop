import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Function;

public class ImageManipulation {

    public static BufferedImage toGrayscale(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return result;
    }

    public static BufferedImage toBlackWhite(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics g = result.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return result;
    }

    public static BufferedImage toPosterize(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = ((rgb >> 16) & 255) & 224;
                int g = ((rgb >> 8) & 255) & 224;
                int b = (rgb & 255) & 224;
                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    public static BufferedImage toTint(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = Math.min(255, ((rgb >> 16) & 255) + 40);
                int g = Math.min(255, ((rgb >> 8) & 255) + 40);
                int b = (rgb & 255);
                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    public static BufferedImage colorShiftRight(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 255;
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;
                int newRgb = (b << 16) | (r << 8) | g;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    public static BufferedImage mirrorImage(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                result.setRGB(img.getWidth() - 1 - x, y, rgb);
            }
        }
        return result;
    }

    public static BufferedImage pixelateImage(BufferedImage img) {
        int pixelSize = 10;
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y += pixelSize) {
            for (int x = 0; x < img.getWidth(); x += pixelSize) {
                int rgb = img.getRGB(x, y);
                for (int dy = 0; dy < pixelSize && y + dy < img.getHeight(); dy++) {
                    for (int dx = 0; dx < pixelSize && x + dx < img.getWidth(); dx++) {
                        result.setRGB(x + dx, y + dy, rgb);
                    }
                }
            }
        }
        return result;
    }

    public static BufferedImage showBorders(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        int[][] sobelX = {
                { -1, 0, 1 },
                { -2, 0, 2 },
                { -1, 0, 1 }
        };
        int[][] sobelY = {
                { -1, -2, -1 },
                { 0, 0, 0 },
                { 1, 2, 1 }
        };
        for (int y = 1; y < img.getHeight() - 1; y++) {
            for (int x = 1; x < img.getWidth() - 1; x++) {
                int pixelX = (
                        (sobelX[0][0] * (img.getRGB(x - 1, y - 1) & 255)) +
                                (sobelX[0][1] * (img.getRGB(x, y - 1) & 255)) +
                                (sobelX[0][2] * (img.getRGB(x + 1, y - 1) & 255)) +
                                (sobelX[1][0] * (img.getRGB(x - 1, y) & 255)) +
                                (sobelX[1][2] * (img.getRGB(x + 1, y) & 255)) +
                                (sobelX[2][0] * (img.getRGB(x - 1, y + 1) & 255)) +
                                (sobelX[2][1] * (img.getRGB(x, y + 1) & 255)) +
                                (sobelX[2][2] * (img.getRGB(x + 1, y + 1) & 255))
                );
                int pixelY = (
                        (sobelY[0][0] * (img.getRGB(x - 1, y - 1) & 255)) +
                                (sobelY[0][1] * (img.getRGB(x, y - 1) & 255)) +
                                (sobelY[0][2] * (img.getRGB(x + 1, y - 1) & 255)) +
                                (sobelY[1][0] * (img.getRGB(x - 1, y) & 255)) +
                                (sobelY[1][2] * (img.getRGB(x + 1, y) & 255)) +
                                (sobelY[2][0] * (img.getRGB(x - 1, y + 1) & 255)) +
                                (sobelY[2][1] * (img.getRGB(x, y + 1) & 255)) +
                                (sobelY[2][2] * (img.getRGB(x + 1, y + 1) & 255))
                );
                int magnitude = (int) Math.sqrt((pixelX * pixelX) + (pixelY * pixelY));
                int newRgb = (magnitude << 16) | (magnitude << 8) | magnitude;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    public static BufferedImage eliminateRed(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int g = (rgb >> 8) & 255;
                int b = rgb & 255;
                int newRgb = (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    public static BufferedImage negativeImage(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = 255 - ((rgb >> 16) & 255);
                int g = 255 - ((rgb >> 8) & 255);
                int b = 255 - (rgb & 255);
                int newRgb = (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

    public static BufferedImage manipulateSelectedArea(BufferedImage img, List<Point> points, Function<BufferedImage, BufferedImage> manipulation) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        Graphics g = result.getGraphics();
        g.drawImage(img, 0, 0, null);

        int minX = Math.min(Math.min(points.get(0).x, points.get(1).x), Math.min(points.get(2).x, points.get(3).x));
        int maxX = Math.max(Math.max(points.get(0).x, points.get(1).x), Math.max(points.get(2).x, points.get(3).x));
        int minY = Math.min(Math.min(points.get(0).y, points.get(1).y), Math.min(points.get(2).y, points.get(3).y));
        int maxY = Math.max(Math.max(points.get(0).y, points.get(1).y), Math.max(points.get(2).y, points.get(3).y));

        BufferedImage selectedArea = img.getSubimage(minX, minY, maxX - minX, maxY - minY);
        BufferedImage manipulatedArea = manipulation.apply(selectedArea);

        g.drawImage(manipulatedArea, minX, minY, null);
        g.dispose();
        return result;
    }
}
