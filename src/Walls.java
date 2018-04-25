import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

class Walls {
  final Set<Wall> walls = new HashSet<>();
  final List<Bullet> bullets = new ArrayList<>();
  private final TreeSet<Collision> collisions = new TreeSet<>();

  public static void main(String[] args) throws IOException {
    new Walls(System.in, System.out);
  }

  Walls(InputStream in, PrintStream out) {
    Scanner scan = new Scanner(in);
    int n = scan.nextInt();
    int m = scan.nextInt();
    int k = scan.nextInt();

    for (int i = 0; i < n; i++) {
      walls.add(new Wall(scan.nextFloat(), scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
    }

    for (int i = 0; i < m; i++) {
      bullets.add(new Bullet(scan.nextFloat(), scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
    }

    for (Bullet b : bullets) {
      Collision next = nextCollision(b);
      if (next != null) collisions.add(next);
    }

    double time = 0;
    for (int i = 0; i < k; i++) {
      char c = scan.next().charAt(0);
      if (c == 'u') {
        time += scan.nextDouble();
        update(time);
      } else {
        bullets.get(scan.nextInt()).printPosition(out, time);
      }
    }
  }

  void update(double time) {
    while (!collisions.isEmpty() && collisions.first().time <= time) {
      Collision e = collisions.pollFirst();
      Bullet b = e.bullet;
      Wall w = e.wall;
      if (walls.contains(w)) {
        b.reflect(w, e.time);
        walls.remove(w);
      }
      Collision next = nextCollision(b);
      if (next != null) collisions.add(next);
    }

  }

  private Collision nextCollision(Bullet b) {
    Wall nearest = null;
    double t = Float.POSITIVE_INFINITY;
    for (Wall wall : walls) {
      double t0 = b.trace(wall);
      if (t0 >= 0 && t0 < t) {
        t = t0;
        nearest = wall;
      }
    }
    return nearest != null ? new Collision(b.time + t, b, nearest) : null;
  }

  class Bullet {
    double px, py, dx, dy, time = 0;

    Bullet(double px, double py, double dx, double dy) {
      this.px = px;
      this.py = py;
      this.dx = dx;
      this.dy = dy;
    }

    void printPosition(PrintStream out, double currentTime) {
      double dt = currentTime - time;
      double x = px + dx * dt;
      double y = py + dy * dt;
      out.printf("%f %f\n", x, y);
    }

    void reflect(Wall w, double collisionTime) {
      double nx = (w.bx - w.ax);
      double ny = (w.by - w.ay);
      double l2 = nx * nx + ny * ny;
      if (l2 == 0) return;
      double dot = dx * nx + dy * ny;

      double t = collisionTime - time;
      px += dx * t;
      py += dy * t;
      dx = -dx + 2 * dot * nx / l2;
      dy = -dy + 2 * dot * ny / l2;
      time = collisionTime;
    }

    double trace(Wall w) {
      double dwx = w.bx - w.ax;
      double dwy = w.by - w.ay;
      double sx = w.ax - px;
      double sy = w.ay - py;
      double l = (dx * -dwy + dy * dwx);
      if (l == 0) return -1;

      double tb = (sx * -dwy + sy * dwx) / l;
      double tw = (sx * -dy + sy * dx) / l;

      if (tw < 0 || tw > 1) return -1;
      if (tb < 0) return -1;
      return tb;
    }
  }

  class Wall implements Comparable<Wall> {
    final double ax, ay, bx, by;

    Wall(double ax, double ay, double bx, double by) {
      this.ax = ax;
      this.ay = ay;
      this.bx = bx;
      this.by = by;
    }

    public int compareTo(Wall o) {
      return Integer.compare(hashCode(), o.hashCode());
    }
  }

  private class Collision implements Comparable<Collision> {
    final double time;
    final Bullet bullet;
    final Wall wall;

    Collision(double time, Bullet bullet, Wall wall) {
      this.time = time;
      this.bullet = bullet;
      this.wall = wall;
    }

    public int compareTo(Collision o) {
      int cmpTime = Double.compare(time, o.time);
      return cmpTime == 0 ? wall.compareTo(o.wall) : cmpTime;
    }
  }
}
