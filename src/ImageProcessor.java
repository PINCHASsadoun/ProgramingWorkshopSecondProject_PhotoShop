import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

public class ImageProcessor extends JFrame {
    private ImagePanel imagePanel = new ImagePanel();
    private List<Point> points = new ArrayList<>();
    private JComboBox<String> manipulationComboBox;
    private Stack<BufferedImage> historyStack = new Stack<>();

    public ImageProcessor() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(ImageProcessor.class.getResource("icon.png")));
        setTitle("Photoshop");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton openButton = new JButton("Choose Image");
        openButton.addActionListener(e -> loadImage());
        add(openButton, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 5));

        manipulationComboBox = new JComboBox<>(new String[] {
                "Grayscale", "Black-White", "Posterize", "Tint", "Color Shift Right",
                "Mirror", "Pixelate", "Show Borders", "Eliminate Red", "Negative"
        });
        buttonPanel.add(manipulationComboBox);


        JButton applyButton = new JButton("Apply Manipulation");
        applyButton.addActionListener(e -> applyManipulationToSelectedArea());
        buttonPanel.add(applyButton);


        JButton clearButton = new JButton("Clear Points");
        clearButton.addActionListener(e -> {
            points.clear();
            imagePanel.setPoints(points);
            repaint();
        });
        buttonPanel.add(clearButton);


        JButton saveButton = new JButton("Save Image");
        saveButton.addActionListener(e -> applyManipulationToSelectedArea());
        buttonPanel.add(saveButton);



        add(buttonPanel, BorderLayout.SOUTH);
        add(imagePanel, BorderLayout.CENTER);

        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (points.size() < 4) {
                    points.add(e.getPoint());
                    imagePanel.setPoints(points);
                    repaint();
                }
            }
        });

        setupUndoAction();
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedFile);
                imagePanel.setImage(img);
                points.clear();
                imagePanel.setPoints(points);
                historyStack.clear();
                historyStack.push(deepCopy(img));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedImage img = imagePanel.getImage(); // Get the image from the image panel
                ImageIO.write(img, "jpg", selectedFile); // Write the image to the selected file
                historyStack.push(deepCopy(img)); // Push a deep copy of the image to the history stack
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void applyManipulationToSelectedArea() {
        if (imagePanel.getImage() != null && points.size() == 4) {
            String selectedManipulation = (String) manipulationComboBox.getSelectedItem();
            Function<BufferedImage, BufferedImage> manipulation = getManipulationFunction(selectedManipulation);
            if (manipulation != null) {
                BufferedImage img = ImageManipulation.manipulateSelectedArea(imagePanel.getImage(), points, manipulation);
                imagePanel.setImage(img);
                points.clear();
                imagePanel.setPoints(points);
                historyStack.push(deepCopy(img));
                repaint();
            }
        }
    }

    private Function<BufferedImage, BufferedImage> getManipulationFunction(String manipulationName) {
        if (manipulationName.equals("Grayscale")) {
            return ImageManipulation::toGrayscale;
        } else if (manipulationName.equals("Black-White")) {
            return ImageManipulation::toBlackWhite;
        } else if (manipulationName.equals("Posterize")) {
            return ImageManipulation::toPosterize;
        } else if (manipulationName.equals("Tint")) {
            return ImageManipulation::toTint;
        } else if (manipulationName.equals("Color Shift Right")) {
            return ImageManipulation::colorShiftRight;
        } else if (manipulationName.equals("Mirror")) {
            return ImageManipulation::mirrorImage;
        } else if (manipulationName.equals("Pixelate")) {
            return ImageManipulation::pixelateImage;
        } else if (manipulationName.equals("Show Borders")) {
            return ImageManipulation::showBorders;
        } else if (manipulationName.equals("Eliminate Red")) {
            return ImageManipulation::eliminateRed;
        } else if (manipulationName.equals("Negative")) {
            return ImageManipulation::negativeImage;
        }
        return null;
    }

    private void setupUndoAction() {
        Action undoAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoLastManipulation();
            }
        };

        String keyStrokeAndKey = "control Z";
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);

        InputMap inputMap = imagePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(keyStroke, keyStrokeAndKey);
        ActionMap actionMap = imagePanel.getActionMap();
        actionMap.put(keyStrokeAndKey, undoAction);
    }

    private void undoLastManipulation() {
        if (historyStack.size() > 1) {
            historyStack.pop();
            BufferedImage previousImage = historyStack.peek();
            imagePanel.setImage(previousImage);
            repaint();
        }
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
