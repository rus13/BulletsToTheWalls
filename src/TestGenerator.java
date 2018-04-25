import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class TestGenerator {
  private static final int SEED = 123;

  public static void main(String[] args) throws IOException {
    String name = "src/input.txt";
    generateRandom(100, 10, 100, 1f, new PrintStream(name));
  }

  static void generateRandom(int numWalls, int numBullets, int numQueries, float maxTime, PrintStream stream) {
    stream.printf("%d %d %d\n", numWalls, numBullets, numQueries);
    Random random = new Random(SEED);

    int max = 10;
    for (int i = 0; i < numWalls; i++) {
      stream.printf("%f %f %f %f\n", random.nextGaussian() * max, random.nextGaussian() * max,
          random.nextGaussian() * max, random.nextGaussian() * max);
    }
    for (int i = 0; i < numBullets; i++) {
      stream.printf("%f %f %f %f\n", random.nextGaussian() * max, random.nextGaussian() * max,
          random.nextGaussian(), random.nextGaussian());
    }
    for (int i = 0; i < numQueries; i++) {
      boolean update = random.nextBoolean();
      if (update) {
        stream.printf("u %f\n", random.nextFloat() * maxTime);
      } else {
        stream.printf("p %d\n", random.nextInt(numBullets));
      }
    }
  }
}