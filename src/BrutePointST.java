import dsa.*;
import stdlib.StdIn;
import stdlib.StdOut;

public class BrutePointST<Value> implements PointST<Value> {

    // Declares the underlying binary search tree
    RedBlackBinarySearchTreeST<Point2D, Value> bst;

    // Constructs an empty symbol table.
    public BrutePointST() {
        // Initializes the underlying binary search tree
        this.bst = new RedBlackBinarySearchTreeST<Point2D, Value>();
    }

    // Returns true if this symbol table is empty, and false otherwise.
    public boolean isEmpty() {
        // Uses the isEmpty method already created in the binary search tree data type on the underlying binary search tree
        return this.bst.isEmpty();
    }

    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        // Uses the size method already created in the binary search tree data type on the underlying binary search tree
        return this.bst.size();
    }

    // Inserts the given point and value into this symbol table.
    public void put(Point2D p, Value value) {
        // If the point or value to be inserted is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        // Uses the put method already created in the binary search tree data type on the underlying binary search tree
        this.bst.put(p, value);
    }

    // Returns the value associated with the given point in this symbol table, or null.
    public Value get(Point2D p) {
        // If the point to be retrieved is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Uses the get method already created in the binary search tree data type on the underlying binary search tree
        return this.bst.get(p);
    }

    // Returns true if this symbol table contains the given point, and false otherwise.
    public boolean contains(Point2D p) {
        // If the point to be checked for is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Uses the contains method already created in the binary search tree data type on the underlying binary search tree
        return this.bst.contains(p);
    }

    // Returns all the points in this symbol table.
    public Iterable<Point2D> points() {
        // Uses the keys method already created in the binary search tree data type on the underlying binary search tree
        return this.bst.keys();
    }

    // Returns all the points in this symbol table that are inside the given rectangle.
    public Iterable<Point2D> range(RectHV rect) {
        // If the given rectangle is null, throws the appropriate error
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }

        // Creates a new linked queue, q
        LinkedQueue<Point2D> q = new LinkedQueue<Point2D>();

        // Iterates over all the points stored in the underlying binary search tree
        for (Point2D point : this.bst.keys()) {

            // If the current point is inside the rectangle, enqueues the point
            if (rect.contains(point)) {
                q.enqueue(point);
            }
        }

        // Returns the queue
        return q;
    }

    // Returns the point in this symbol table that is different from and closest to the given point,
    // or null.
    public Point2D nearest(Point2D p) {
        // If the given point is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }

        // Initializes the nearest point to null
        Point2D nearest = null;

        // Creates variables to store the distances between points (with the closest distance encountered so far being infinity)
        double smallestDistance = Double.POSITIVE_INFINITY, currentDistance;

        // Iterates over all the points stored in the underlying binary search tree
        for (Point2D point : this.bst.keys()) {

            // Calculates the distance between the current point and the target point
            currentDistance = point.distanceTo(p);

            // If current point is not the same as the target point and the distance between the current point and the target point is smaller than the closest distance found so far...
            if (!point.equals(p) && currentDistance < smallestDistance) {

                // Sets the nearest point to the current point
                nearest = point;

                // Sets the closest distance to the current distance
                smallestDistance = currentDistance;
            }
        }

        // Returns the nearest point
        return nearest;
    }

    // Returns up to k points from this symbol table that are different from and closest to the
    // given point.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        // If the given point is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }

        // Creates a minimum priority queue that compares based on the distance to the target point called pq
        MinPQ<Point2D> pq = new MinPQ<Point2D>(p.distanceToOrder());

        // Iterates over all the points stored in the underlying binary search tree
        for (Point2D point : this.bst.keys()) {

            // Inserts the current point into pq so long as it is not the same as the target point
            if (!point.equals(p)) {
                pq.insert(point);
            }
        }

        // Creates a new queue, q
        LinkedQueue<Point2D> q = new LinkedQueue<Point2D>();

        // Adds the first k elements from pq to q
        for (int i = 0; i < k; i++) {
            q.enqueue(pq.delMin());
        }

        // Returns the queue
        return q;
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        BrutePointST<Integer> st = new BrutePointST<Integer>();
        double qx = Double.parseDouble(args[0]);
        double qy = Double.parseDouble(args[1]);
        int k = Integer.parseInt(args[2]);
        Point2D query = new Point2D(qx, qy);
        RectHV rect = new RectHV(-1, -1, 1, 1);
        int i = 0;
        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();
            Point2D p = new Point2D(x, y);
            st.put(p, i++);
        }
        StdOut.println("st.empty()? " + st.isEmpty());
        StdOut.println("st.size() = " + st.size());
        StdOut.printf("st.contains(%s)? %s\n", query, st.contains(query));
        StdOut.printf("st.range(%s):\n", rect);
        for (Point2D p : st.range(rect)) {
            StdOut.println("  " + p);
        }
        StdOut.printf("st.nearest(%s) = %s\n", query, st.nearest(query));
        StdOut.printf("st.nearest(%s, %d):\n", query, k);
        for (Point2D p : st.nearest(query, k)) {
            StdOut.println("  " + p);
        }
    }
}
