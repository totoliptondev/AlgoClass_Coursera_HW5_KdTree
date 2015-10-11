public class KdTree {
   private Node root;
   private int size;

   private static class Node {
       private Point2D p;      // the point
       private RectHV rect;    // the axis-aligned rectangle corresponding to this node
       private Node lb;        // the left/bottom subtree
       private Node rt;        // the right/top subtree
       public Node(Point2D p, RectHV rect) {
         this.p = p;
         if (rect == null) this.rect = new RectHV(0.0, 0.0, 1.0, 1.0);
         else              this.rect = rect;
       }
   }

   public KdTree()
   {
     root = null;
     size = 0;
   }
   public boolean isEmpty()
   {
     return size == 0;
   }

   public int size()
   {
     return size;
   }

   public void insert(Point2D p)   // add the point to the set (if it is not already in the set)
   {
     if (isEmpty())    root = insert(root, p, true, null);
     root = insert(root, p, true, root.rect);
   }

   // helper insert function, vert toggles between vertical and horizontal.
   private Node insert(Node v, Point2D p, boolean vert, RectHV rect) {
     if (v == null)
     {
       size++;
       return new Node(p, rect);
     }
     if (v.p.equals(p)) return v;   //set do not allow repetition.
     RectHV r;
     if (vert) {
       if      (Point2D.X_ORDER.compare(p, v.p) < 0) {
         if (v.lb == null) r = new RectHV(rect.xmin(), rect.ymin(), v.p.x(), rect.ymax());
         else              r = v.lb.rect;
         v.lb = insert(v.lb, p, !vert, r);
       }
       else {
         if (v.rt == null) r = new RectHV(v.p.x(), rect.ymin(), rect.xmax(), rect.ymax());
         else              r = v.rt.rect;
         v.rt = insert(v.rt, p, !vert, r);
       }
     } else {
       if      (Point2D.Y_ORDER.compare(p, v.p) < 0) {
         if (v.lb == null) r = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), v.p.y());
         else              r = v.lb.rect;
         v.lb = insert(v.lb, p, !vert, r);
       }
       else {
         if (v.rt == null) r = new RectHV(rect.xmin(), v.p.y(), rect.xmax(), rect.ymax());
         else              r = v.rt.rect;
         v.rt = insert(v.rt, p, !vert, r);
       }
     }
     return v;
   }

   public boolean contains(Point2D p)    // does the set contain point p?
   {
     return contains(root, p, true);
   }

   private boolean contains(Node v, Point2D p, boolean vert)
   {
     if (v == null)     return false;
     if (v.p.equals(p)) return true;
     int cmp = vert ?
        Point2D.X_ORDER.compare(p, v.p)
          : Point2D.Y_ORDER.compare(p, v.p);
     if       (cmp < 0) return contains(v.lb, p, !vert);
     else               return contains(v.rt, p, !vert);

   }

   public void draw()    // all points recursively, inefficient.
   {
     drawhelper(root, true);
   }

   private void drawhelper(Node v, boolean vert) {
     if (v == null) return;

     StdDraw.setPenRadius(.02);
     StdDraw.setPenColor(StdDraw.BLACK);
     v.p.draw();

     if (vert)
     {
       StdDraw.setPenRadius(.002);
       StdDraw.setPenColor(StdDraw.RED);
       Point2D p1 = new Point2D(v.p.x(), v.rect.ymin());
       Point2D p2 = new Point2D(v.p.x(), v.rect.ymax());
       p1.drawTo(p2);
       drawhelper(v.lb, !vert);
       drawhelper(v.rt, !vert);
     }
     else {
       StdDraw.setPenRadius(.002);
       StdDraw.setPenColor(StdDraw.BLUE);
       Point2D p1 = new Point2D(v.rect.xmin(), v.p.y());
       Point2D p2 = new Point2D(v.rect.xmax(), v.p.y());
       p1.drawTo(p2);
       drawhelper(v.lb, !vert);
       drawhelper(v.rt, !vert);
     }
   }

   public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle
   {
     Stack<Point2D> s = new Stack<Point2D>();
     search(root, rect, s);
     return s;
   }

   // return node if p is in rect
   private void search(Node v, RectHV rect, Stack<Point2D> s) {
     if (v == null) return;
     if (!rect.intersects(v.rect)) return; // stop if no intersection.
     if (rect.contains(v.p)) s.push(v.p);
     search(v.lb, rect, s);
     search(v.rt, rect, s);
   }

   public Point2D nearest(Point2D p)     // a nearest neighbor in the set to point p; null if the set is empty
   {
      if (isEmpty()) return null;
      return nearest(root, p, root.p, true);
   }

   private Point2D nearest(Node v, Point2D p, Point2D minp, boolean vert)
   {
     if (v == null) return minp;

     // the check below prunes the subtree if p to rect is longer than
     // p to the minimum point found.
     if (v.rect.distanceSquaredTo(p) > minp.distanceSquaredTo(p)) return minp;

     Point2D mp = minp;
     if (p.distanceSquaredTo(v.p) < p.distanceSquaredTo(minp)) mp = v.p;

     // Move to the side that is closer to p first.
     // ?_Order.p < v.p or cmp < 0 means left child is closer to p.
     int cmp = vert
        ? Point2D.X_ORDER.compare(p, v.p)
          : Point2D.Y_ORDER.compare(p, v.p);
     boolean checkLeftFirst = cmp < 0;

     mp = nearest(checkLeftFirst ? v.lb : v.rt, p, mp, !vert);
     mp = nearest(checkLeftFirst ? v.rt : v.lb, p, mp, !vert);
     return mp;
   }

//   public static void main(String[] args)                  // unit testing of the methods (optional)
}
