import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

class Walls {
  private double time = 0f;
  final Set<Wall> walls = new HashSet<>();
  final List<Bullet> bullets = new ArrayList<>();
  private final TreeSet<Collision> collisions = new TreeSet<>();

  public static void main(String[] args) throws IOException {
    new Walls(System.in, System.out, true);
  }

  Walls(InputStream in, PrintStream out, boolean update) {
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
      Collision next = nextCollision(new Collision(0, b, null));
      if (next != null) collisions.add(next);
    }

    if (update) {
      for (int i = 0; i < k; i++) {
        char c = scan.next().charAt(0);
        if (c == 'u') {
          double dt = scan.nextDouble();
          update(dt);
        } else {
          bullets.get(scan.nextInt()).printPosition(out);
        }
      }
    }
  }

  void update(double dt) {
    time += dt;
    while (!collisions.isEmpty() && collisions.first().time <= time) {
      Collision e = collisions.pollFirst();
      if (walls.contains(e.wall)) {
        Bullet b = e.bullet;
//        System.out.printf("bullet: %f %f  collision: %f %f", b.getX(), b.getY());
        b.reflect(e.wall);
        b.lastCollisionTime = e.time;
        walls.remove(e.wall);
      }
      Collision next = nextCollision(e);
      if (next != null) collisions.add(next);
    }

  }

  private Collision nextCollision(Collision e) {
    Wall nearest = null;
    double t = Float.POSITIVE_INFINITY;
    for (Wall wall : walls) {
      if (wall == e.wall) continue;
      double t0 = e.bullet.trace(wall);
      if (t0 >= 0 && t0 < t) {
        t = t0;
        nearest = wall;
      }
    }
    return nearest != null ? new Collision(e.bullet.lastCollisionTime + t, e.bullet, nearest) : null;
  }

  class Bullet {
    double px, py, dx, dy;
    double lastCollisionTime = 0f;

    Bullet(double px, double py, double dx, double dy) {
      this.px = px;
      this.py = py;
      this.dx = dx;
      this.dy = dy;
    }

    double getX() {
      double dt = time - lastCollisionTime;
      return px + dx * dt;
    }

    double getY() {
      double dt = time - lastCollisionTime;
      return py + dy * dt;
    }

    void printPosition(PrintStream out) {
      double dt = time - lastCollisionTime;
      double x = px + dx * dt;
      double y = py + dy * dt;
      out.printf("%f %f\n", x, y);
    }

    void reflect(Wall w) {
      double t = trace(w);
      if (t < 0) return;
      double nx = (w.bx - w.ax);
      double ny = (w.by - w.ay);
      double l2 = nx * nx + ny * ny;
      if (l2 == 0) return;
      double dot = dx * nx + dy * ny;

      px += dx * t;
      py += dy * t;

      dx = -dx + 2 * dot * nx / l2;
      dy = -dy + 2 * dot * ny / l2;
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

    @Override
    public String toString() {
      return String.format("(%f, %f) - (%f %f)", ax, ay, bx, by);
    }

    @Override
    public int compareTo(Wall o) {
      return Integer.compare(hashCode(), o.hashCode());
    }
  }

  private class Collision implements Comparable<Collision> {
    double time;
    Bullet bullet;
    Wall wall;

    Collision(double time, Bullet bullet, Wall wall) {
      this.time = time;
      this.bullet = bullet;
      this.wall = wall;
    }

    @Override
    public int compareTo(Collision o) {
      int cmpTime = Double.compare(time, o.time);
      return cmpTime == 0 ? wall.compareTo(o.wall) : cmpTime;
    }
  }
}
