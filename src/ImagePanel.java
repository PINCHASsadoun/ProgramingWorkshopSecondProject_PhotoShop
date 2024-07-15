import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private List<Point> points;

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
        g.setColor(Color.RED);
        if (points != null) {
            for (Point point : points) {
                g.fillOval(point.x - 5, point.y - 5, 10, 10);
            }
            if (points.size() == 4) {
                g.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
                g.drawLine(points.get(1).x, points.get(1).y, points.get(2).x, points.get(2).y);
                g.drawLine(points.get(2).x, points.get(2).y, points.get(3).x, points.get(3).y);
                g.drawLine(points.get(3).x, points.get(3).y, points.get(0).x, points.get(0).y);
            }
        }
    }
}
