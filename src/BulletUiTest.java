import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class BulletUiTest {
  private final Walls mManager;
  private final MyComponent mComponent;

  private float sx = 50;
  private float sy = 50;

  private final double dt = .001f;

  private double time = 0;

  private BulletUiTest(Walls manager) {
    mManager = manager;
    mComponent = new MyComponent();
    JFrame frame = new JFrame();
    frame.setTitle("AAA");
    frame.setSize(500, 500);
    frame.getContentPane().add(mComponent);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setVisible(true);


    new Timer(0, e -> {
      time += dt;
      mManager.update(time);
      mComponent.repaint();
    }).start();

  }

  private class MyComponent extends JComponent {
    @Override
    public void paint(Graphics g) {
      super.paint(g);
      g.setColor(Color.black);
      for (Walls.Wall w : mManager.walls) {
        g.drawLine(getX(w.ax), getY(w.ay), getX(w.bx), getY(w.by));
      }

      g.setColor(Color.red);
      for (Walls.Bullet b : mManager.bullets) {
        double dt = time - b.time;
        double x = b.px + dt * b.dx;
        double y = b.py + dt * b.dy;
        g.drawRect(getX(x), getY(y), 2, 2);
      }
    }

    private int getX(double f) {
      return (int) (f * getWidth() / sx) + getWidth() / 2;
    }

    private int getY(double f) {
      return (int) (f * getHeight() / sy) + getHeight() / 2;
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        new BulletUiTest(new Walls(new FileInputStream("src/input.txt"),
            new PrintStream(new FileOutputStream("src/output.txt"))));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    });
  }
}