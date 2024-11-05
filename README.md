The goal of this project was to create a symbol table data type that uses 2D points as keys. The two sections of this
project uses two different approaches to this goal. The first, BrutePointST.java, uses a red-black binary search tree
as its underlying data structure. Because of that, the implementation of many methods (put, get, contains, ect.) were
simply calling the corresponding BST method on the underlying data structure. The main downside of this
implementation is that the search methods (range and nearest) are not as efficient as possible. To solve that, the
second approach is used, KdTreePointST.java. The underlying data structure for this implementation is at 2dTree, and
it stores a rectangle inside each node that contains all the points in its subtrees. This allows for the "pruning"
of subtrees that would definitely not have the point(s) that we need inside them, thus the search times are
drastically reduced. These are two different ways to create a symbol table that uses 2D points as keys.
