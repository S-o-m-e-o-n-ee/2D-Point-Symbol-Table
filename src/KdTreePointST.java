import dsa.LinkedQueue;
import dsa.MaxPQ;
import dsa.Point2D;
import dsa.RectHV;
import stdlib.StdIn;
import stdlib.StdOut;

public class KdTreePointST<Value> implements PointST<Value> {

    // Declares the instance variables
    Node root;
    int n;

    // Constructs an empty symbol table.
    public KdTreePointST() {
        // Initializes the root node to null and the size to zero
        this.root = null;
        this.n = 0;
    }

    // Returns true if this symbol table is empty, and false otherwise.
    public boolean isEmpty() {
        return this.n == 0;
    }

    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        return this.n;
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

        // If this is the first item added to the symbol table...
        if (this.root == null) {

            // Sets the size to one
            n = 1;

            // Creates the infinite rectangle for the root node
            RectHV rect = new RectHV(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

            // Sets components of the root node to the correct items
            this.root = new Node(p, value, rect);
        } else { // Otherwise, calls the private put method with the correct arguments
            this.root = put(this.root, p, value, this.root.rect, true);
        }
    }

    // Returns the value associated with the given point in this symbol table, or null.
    public Value get(Point2D p) {
        // If the point to be retrieved is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }

        // Calls the private get method with the correct arguments
        return get(this.root, p, true);
    }

    // Returns true if this symbol table contains the given point, and false otherwise.
    public boolean contains(Point2D p) {
        // If the point to be checked for is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }

        // Returns true so long as the get method for the target point returns a real value and not null
        return this.get(p) != null;
    }

    // Returns all the points in this symbol table.
    public Iterable<Point2D> points() {
        // Creates a queue to store the points in the symbol table called collection
        LinkedQueue<Point2D> collection = new LinkedQueue<Point2D>();

        // Creates a queue to store the nodes that are being traveled on called traversal
        LinkedQueue<Node> traversal = new LinkedQueue<Node>();

        // Enqueues the root to traversal
        traversal.enqueue(root);

        // While traversal is not empty...
        while (!traversal.isEmpty()) {

            // Sets the current node that we are working with to the first node dequeued from traversal
            Node currentNode = traversal.dequeue();

            // Enqueues the point from the current node to collection
            collection.enqueue(currentNode.p);

            // So long as they are not null, enqueues the left and right children of the current node to traversal
            if (currentNode.lb != null) {
                traversal.enqueue(currentNode.lb);
            }
            if (currentNode.rt != null) {
                traversal.enqueue(currentNode.rt);
            }
        }

        // Returns collection
        return collection;
    }

    // Returns all the points in this symbol table that are inside the given rectangle.
    public Iterable<Point2D> range(RectHV rect) {
        // If the given rectangle is null, throws the appropriate error
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }

        // Creates a new linked queue, q
        LinkedQueue<Point2D> q = new LinkedQueue<Point2D>();

        // Calls the private range method with the correct arguments to fill q
        range(root, rect, q);

        // Returns q
        return q;
    }

    // Returns the point in this symbol table that is different from and closest to the given point,
    // or null.
    public Point2D nearest(Point2D p) {
        // If the given point is null, throws the appropriate error
        if (p == null) {
            throw new NullPointerException("p is null");
        }

        // Calls the private nearest method with the correct arguments
        return nearest(root, p, null, true);
    }

    // Returns up to k points from this symbol table that are different from and closest to the
    // given point.
    public Iterable<Point2D> nearest(Point2D p, int k) {

        // Creates a maximum priority queue that compares based on the distance to the target point called pq
        MaxPQ<Point2D> pq = new MaxPQ<Point2D>(p.distanceToOrder());

        // Calls the private nearest method with the correct arguments to fill pq
        nearest(root, p, k, pq, true);

        // Returns pq
        return pq;
    }

    // Note: In the helper methods that have lr as a parameter, its value specifies how to
    // compare the point p with the point x.p. If true, the points are compared by their
    // x-coordinates; otherwise, the points are compared by their y-coordinates. If the
    // comparison of the coordinates (x or y) is true, the recursive call is made on x.lb;
    // otherwise, the call is made on x.rt.

    // Inserts the given point and value into the KdTree x having rect as its axis-aligned
    // rectangle, and returns a reference to the modified tree.
    private Node put(Node x, Point2D p, Value value, RectHV rect, boolean lr) {

        // If the current node is null, increments the number of key-value pairs and returns a new node with the appropriate values
        if (x == null) {
            n++;
            return new Node(p, value, rect);
        }

        // If the point in x is the same as the point to be added, changes the value corresponding to that point to the new value
        if (x.p.equals(p)) {
            x.value = value;
        } else { // Otherwise...

            // If we should be comparing based on the x values...
            if (lr) {

                // ... and the x value of the point in the current node is less than the x value of the point to be added...
                if (p.x() < x.p.x()) {

                    // Creates a new rectangle of the appropriate size
                    RectHV newRect = new RectHV(x.rect.xMin(), x.rect.yMin(), x.p.x(), x.rect.yMax());

                    // Calls put recursively on the left child of the current node
                    x.lb = put(x.lb, p, value, newRect, false);
                } else { // ... and the x value of the point in the current node is greater than or equal to the x value of the point to be added...

                    // Creates a new rectangle of the appropriate size
                    RectHV newRect = new RectHV(x.p.x(), x.rect.yMin(), x.rect.xMax(), x.rect.yMax());

                    // Calls put recursively on the right child of the current node
                    x.rt = put(x.rt, p, value, newRect, false);
                }
            } else { // If we should be comparing based on the y values...

                // ... and the y value of the point in the current node is less than the y value of the point to be added...
                if (p.y() < x.p.y()) {

                    // Creates a new rectangle of the appropriate size
                    RectHV newRect = new RectHV(x.rect.xMin(), x.rect.yMin(), x.rect.xMax(), x.p.y());

                    // Calls put recursively on the left child of the current node
                    x.lb = put(x.lb, p, value, newRect, true);
                } else { // ... and the y value of the point in the current node is greater than or equal to the y value of the point to be added...

                    // Creates a new rectangle of the appropriate size
                    RectHV newRect = new RectHV(x.rect.xMin(), x.p.y(), x.rect.xMax(), x.rect.yMax());

                    // Calls put recursively on the right child of the current node
                    x.rt = put(x.rt, p, value, newRect, true);
                }
            }
        }

        // Returns the current node
        return x;
    }

    // Returns the value associated with the given point in the KdTree x, or null.
    private Value get(Node x, Point2D p, boolean lr) {

        // If the node we are looking at does not exist, returns null
        if (x == null) {
            return null;
        }

        // If we have found the point we are looking for, returns the associated value
        if (x.p.equals(p)) {
            return x.value;
        }

        // If we should be comparing based on the x values...
        if (lr) {

            // ... and the x value of the point in the current node is less than the x value of the point to be found...
            if (p.x() < x.p.x()) {

                // Calls get recursively on the left child of the current node
                return get(x.lb, p, false);
            } else { // ... and the x value of the point in the current node is greater than or equal to the x value of the point to be found...

                // Calls get recursively on the right child of the current node
                return get(x.rt, p, false);
            }
        } else { // If we should be comparing based on the y values...

            // ... and the y value of the point in the current node is less than the y value of the point to be found...
            if (p.y() < x.p.y()) {

                // Calls get recursively on the left child of the current node
                return get(x.lb, p, true);
            } else { // ... and the y value of the point in the current node is greater than or equal to the y value of the point to be found...

                // Calls get recursively on the right child of the current node
                return get(x.rt, p, true);
            }
        }
    }

    // Collects in the given queue all the points in the KdTree x that are inside rect.
    private void range(Node x, RectHV rect, LinkedQueue<Point2D> q) {

        // If the node we are looking at does not exist, returns null
        if (x == null) {
            return;
        }

        // If the rectangle belonging to the current node intersects with the given rectangle...
        if (x.rect.intersects(rect)) {

            // If the point in the current node is contained in the rectangle, adds it to the queue
            if (rect.contains(x.p)) {
                q.enqueue(x.p);
            }

            // Recursively calls range on the left and right children of the current node
            range(x.lb, rect, q);
            range(x.rt, rect, q);
        }
    }

    // Returns the point in the KdTree x that is closest to p, or null; nearest is the closest
    // point discovered so far.
    private Point2D nearest(Node x, Point2D p, Point2D nearest, boolean lr) {

        // If the node we are looking at does not exist, returns nearest
        if (x == null) {
            return nearest;
        }

        // Declares the variable to store the distance to the target point from the nearest point encountered so far
        double distanceNearest;

        // If the nearest is null, sets the distance to positive infinity, otherwise calculates using the distance squared method
        if (nearest == null) {
            distanceNearest = Double.POSITIVE_INFINITY;
        } else {
            distanceNearest = nearest.distanceSquaredTo(p);
        }

        // If the distance between the point and the nearest found so far is greater than the distance to the rectangle of the current node...
        if (distanceNearest >= x.rect.distanceSquaredTo(p)) {

            // If the point in the current node is not the same as the target point, and it's distance to the target point is less than the nearest distance found so far...
            if (!x.p.equals(p) && distanceNearest > x.p.distanceSquaredTo(p)) {
                // Sets nearest to the point in the current node
                nearest = x.p;
            }

            // If we should be comparing based on the x values...
            if (lr) {

                // ... and the x value of the point in the current node is less than the x value of the target point...
                if (p.x() < x.p.x()) {

                    // Calls the nearest method recursively on the left child and then the right child of the current node
                    nearest = nearest(x.lb, p, nearest, false);
                    nearest = nearest(x.rt, p, nearest, false);
                } else { // ... and the x value of the point in the current node is greater than or equal to the x value of the target point...

                    // Calls the nearest method recursively on the right child and then the left child of the current node
                    nearest = nearest(x.rt, p, nearest, false);
                    nearest = nearest(x.lb, p, nearest, false);
                }
            } else { // If we should be comparing based on the y values...

                // ... and the y value of the point in the current node is less than the y value of the target point...
                if (p.y() < x.p.y()) {

                    // Calls the nearest method recursively on the left child and then the right child of the current node
                    nearest = nearest(x.lb, p, nearest, true);
                    nearest = nearest(x.rt, p, nearest, true);
                } else { // ... and the y value of the point in the current node is greater than or equal to the y value of the target point...

                    // Calls the nearest method recursively on the right child and then the left child of the current node
                    nearest = nearest(x.rt, p, nearest, true);
                    nearest = nearest(x.lb, p, nearest, true);
                }
            }
        }
        return nearest;
    }

    // Collects in the given max-PQ up to k points from the KdTree x that are different from and
    // closest to p.
    private void nearest(Node x, Point2D p, int k, MaxPQ<Point2D> pq, boolean lr) {

        // If the node we are looking at does not exist or pq is too big, returns null
        if (x == null || pq.size() > k) {
            return;
        }

        // Calculates the distance between the max value on pq and the target point (setting it to infinity if pq is empty)
        double distance;
        if (pq.isEmpty()) {
            distance = Double.POSITIVE_INFINITY;
        } else {
            distance = pq.max().distanceSquaredTo(p);
        }

        // If the distance between the rectangle of the current node and the target point is less than the distance between the max value in pq and the target point...
        if (x.rect.distanceSquaredTo(p) < distance) {

            // If the point in the current node is not the same as the target point, inserts it to pq
            if (!x.p.equals(p)) {
                pq.insert(x.p);
            }

            // If pq is too big, deletes the maximum value
            if (pq.size() > k) {
                pq.delMax();
            }

            // If we should be comparing based on the x values...
            if (lr) {

                // ... and the x value of the point in the current node is less than the x value of the target point...
                if (p.x() < x.p.x()) {

                    // Calls the nearest method recursively on the left child and then the right child of the current node
                    nearest(x.lb, p, k, pq, false);
                    nearest(x.rt, p, k, pq, false);
                } else { // ... and the x value of the point in the current node is greater than or equal to the x value of the target point...

                    // Calls the nearest method recursively on the right child and then the left child of the current node
                    nearest(x.rt, p, k, pq, false);
                    nearest(x.lb, p, k, pq, false);
                }
            } else { // If we should be comparing based on the y values...

                // ... and the y value of the point in the current node is less than the y value of the target point...
                if (p.y() < x.p.y()) {

                    // Calls the nearest method recursively on the left child and then the right child of the current node
                    nearest(x.lb, p, k, pq, true);
                    nearest(x.rt, p, k, pq, true);
                } else { // ... and the y value of the point in the current node is greater than or equal to the y value of the target point...

                    // Calls the nearest method recursively on the right child and then the left child of the current node
                    nearest(x.rt, p, k, pq, true);
                    nearest(x.lb, p, k, pq, true);
                }
            }
        }
    }

    // A representation of node in a KdTree in two dimensions (ie, a 2dTree). Each node stores a
    // 2d point (the key), a value, an axis-aligned rectangle, and references to the left/bottom
    // and right/top subtrees.
    private class Node {
        private Point2D p;   // the point (key)
        private Value value; // the value
        private RectHV rect; // the axis-aligned rectangle
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree

        // Constructs a node given the point (key), the associated value, and the
        // corresponding axis-aligned rectangle.
        Node(Point2D p, Value value, RectHV rect) {
            this.p = p;
            this.value = value;
            this.rect = rect;
        }
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        KdTreePointST<Integer> st = new KdTreePointST<>();
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
