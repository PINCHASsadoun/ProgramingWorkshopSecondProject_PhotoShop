import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageProcessor frame = new ImageProcessor();
            frame.setVisible(true);
        });
    }
}
