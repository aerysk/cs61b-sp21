package deque;
import java.util.Iterator;

/** The Linked List Deque implementation.
 *  @author Emily Nguyen */
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        Node(T x, Node p, Node n) {
            item = x;
            prev = p;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    /** Creates an empty linked list deque. */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    /** Adds an T x to the front of the deque.
     *  Assume x is never null. */
    @Override
    public void addFirst(T x) {
        Node first = new Node(x, sentinel, sentinel.next);
        sentinel.next.prev = first;
        sentinel.next = first;
        size++;
    }

    /** Adds an T x to the back of the deque.
     *  Assume x is never null. */
    @Override
    public void addLast(T x) {
        Node last = new Node(x, sentinel.prev, sentinel);
        sentinel.prev.next = last;
        sentinel.prev = last;
        size++;
    }

    /** Returns the number of items in the deque in constant time. */
    @Override
    public int size() {
        return size;
    }

    /** Prints all items in the deque, separated by a space, then
     *  finally a new line. */
    @Override
    public void printDeque() {
        if (isEmpty()) {
            return;
        }
        Node p = sentinel.next;
        while (p.next != sentinel) {
            System.out.print(p.item + " ");
            p = p.next;
        }
        System.out.print(p.item);
        System.out.println();
    }

    /** Removes and returns the element at the front of the deque.
     *  If no such item exists, returns null. */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T first = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size--;
        return first;
    }

    /** Removes and returns the element at the back of the deque.
     *  If no such item exists, returns null. */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T last = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size--;
        return last;
    }

    /** An iterative method for returning the element at index.
     *  If no such item exists, returns null. */
    @Override
    public T get(int index) {
        if (isEmpty()) {
            return null;
        }
        Node p = sentinel.next;
        while (index > 0) {
            p = p.next;
            index--;
        }
        return p.item;
    }

    /** A recursive method for returning the element at index. */
    public T getRecursive(int index) {
        if (isEmpty()) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }

    /** A helper method for getRecursive that takes an index and node
     *  and returns the item at that index. */
    private T getRecursiveHelper(int index, Node n) {
        if (index == 0) {
            return n.item;
        } else {
            return getRecursiveHelper(index - 1, n.next);
        }
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int position;

        LinkedListDequeIterator() {
            position = 0;
        }

        public boolean hasNext() {
            return position < size;
        }

        public T next() {
            T item = get(position);
            position += 1;
            return item;
        }
    }

    /** Returns a LinkedListDequeIterator. */
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    /** Returns whether or not Object o equals the deque, meaning it
     *  contains the same contents in the same order. */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size; i += 1) {
            T current = get(i);
            if (!current.equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static void main(String[] args) {
        /**
        LinkedListDeque<Integer> intL1 = new LinkedListDeque<>();
        LinkedListDeque<String> strL1 = new LinkedListDeque<>();

        intL1.addFirst(7);
        intL1.addLast(11);
        System.out.println(intL1.size());
        System.out.println(intL1.isEmpty());

        strL1.addFirst("Emily");
        strL1.addFirst("Hello, ");
        strL1.printDeque();
        strL1.removeFirst();
        strL1.addLast("codes");
        strL1.addLast("CS61B");
        System.out.println(strL1.get(2));
        strL1.removeLast();
        strL1.printDeque();

        System.out.println("My birthday is on:");
        System.out.print(intL1.get(0) + " ");
        System.out.print(intL1.getRecursive(1));
         */

        LinkedListDeque<Integer> linked = new LinkedListDeque<>();
        ArrayDeque<Integer> array = new ArrayDeque<>();

        linked.addLast(0);
        array.addLast(0);
        linked.addLast(1);
        array.addLast(1);
        linked.addLast(2);
        array.addLast(2);

        boolean help = linked.equals(array);
        System.out.println(help);
    }
}
