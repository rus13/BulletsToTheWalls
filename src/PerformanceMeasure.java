import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class PerformanceMeasure {

  public static void main(String args[]) {
    String in = "src/input.txt";
    String out = "src/output.txt";
    int n = 10000;
    int k = 5000;
    int m = 10000;
    float maxTime = 3f;
    try {
      TestGenerator.generateRandom(n, k, m, maxTime, new PrintStream(new FileOutputStream(in)));
      FileInputStream fis = new FileInputStream(in);
      PrintStream ps = new PrintStream(new FileOutputStream(out));
      double start = System.nanoTime();
      new Walls(fis, ps);
      double elapsed = System.nanoTime() - start;
      System.out.println("walls: " + n + " bullets: " + k + " requests: " + m + " max time: " + maxTime);
      System.out.printf("elapsed time: %.4f s", elapsed * 1e-9);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
