public class PointSET {
   private SET<Point2D> pset;
   private int size;


   public PointSET()     // construct an empty set of points
   {
     pset = new SET<Point2D>();
     size = 0;
   }

   public boolean isEmpty()
   {
     return size == 0;
   }

   public int size()                      // number of points in the set
   {
     return size;
   }

   public void insert(Point2D p)          // add the point to the set (if it is not already in the set)
   {
     if (!contains(p)) {
       pset.add(p);
       size++;
     }
   }

   public boolean contains(Point2D p)      // does the set contain point p?
   {
     return pset.contains(p);
   }

   public void draw()                      // draw all points to standard draw
   {
     for (Point2D p : pset)
     {
       p.draw();
     }
   }
   public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle
   {
     Stack<Point2D> s = new Stack<Point2D>();
     for (Point2D p : pset)
     {
       if (rect.contains(p)) {
         s.push(p);
       }
     }
     return s;

   }
   public Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty
   {
     double mindist = Double.MAX_VALUE;
     Point2D nearestq = null;
     for (Point2D q : pset)
     {
       if (q.distanceSquaredTo(p) < mindist) {
         mindist = p.distanceSquaredTo(q);
         nearestq = q;
       }
     }
     return nearestq;
   }

   public static void main(String[] args)                  // unit testing of the methods (optional)
   {


   }
}
