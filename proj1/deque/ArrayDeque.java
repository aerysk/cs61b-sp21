package deque;
import java.util.Iterator;

/** The Array Deque implementation.
 *  @author Emily Nguyen */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private T[] items;
    private int front; // index of front element
    private int end; // index of back element
    private int size;
    private static final double FACTOR = 1.3;

    /** Creates an empty array deque. */
    public ArrayDeque() {
        items = (T[]) new Object[8];
        front = 3;
        end = 4;
        size = 0;
    }

    /** Resizes the array deque to be of max size capacity. */
    private void resize(int capacity) {
        T[] replace = (T[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            int index = (i + front + 1) % capacity;
            replace[index] = get(i);
        }
        front = front % capacity;
        end = (front + size + 1) % capacity;
        items = replace;
    }

    /** Adds an T x to the front of the array deque.
     *  Assume x is never null. */
    @Override
    public void addFirst(T x) {
        items[front] = x;
        front = (front - 1 + items.length) % items.length;
        size++;
        if (size == items.length) {
            resize((int) Math.ceil((double) size * FACTOR));
        }
    }

    /** Adds an T x to the back of the array deque.
     *  Assume x is never null. */
    @Override
    public void addLast(T x) {
        items[end] = x;
        end = (end + 1) % items.length;
        size++;
        if (size == items.length) {
            resize((int) Math.ceil((double) size * FACTOR));
        }
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
        for (int i = 0; i < size; i += 1) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    /** Removes and returns the element at the front of the deque.
     *  If no such item exists, returns null. */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        front = (front + 1) % items.length;
        T first = items[front];
        items[front] = null;
        size--;
        if (((double) (size / items.length) < 0.25) && (items.length >= 16)
                && (size < items.length / 2)) {
            //int cap = Math.max(8, (int) Math.ceil((double) size * 2));
            resize(items.length / 2);
        }
        return first;
    }

    /** Removes and returns the element at the back of the deque.
     *  If no such item exists, returns null. */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        end = (end - 1 + items.length) % items.length;
        T last = items[end];
        items[end] = null;
        size--;
        if (((double) (size / items.length) < 0.25) && (items.length >= 16)
                && (size < items.length / 2)) {
            //int cap = Math.max(8, (int) Math.ceil((double) size * 2));
            resize(items.length / 2);
        }
        return last;
    }

    /** Returns the item at index. */
    @Override
    public T get(int index) {
        int i = (front + index + 1) % items.length;
        return items[i];
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int position;

        ArrayDequeIterator() {
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

    /** Returns an ArrayDequeIterator. */
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    /** Returns whether or not Object o equals the deque, meaning it
     *  contains the same contents in the same order. */
    @Override
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
        for (int i = 0; i < this.size(); i += 1) {
            T current = get(i);
            if (!current.equals(other.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static void main(String[] args) {
        ArrayDeque<Integer> intAD1 = new ArrayDeque<>();
        System.out.println(intAD1.isEmpty());

        intAD1.addLast(13);
        intAD1.addLast(14);
        intAD1.addLast(15);
        intAD1.addFirst(12);
        intAD1.addLast(16);
        intAD1.addLast(17);
        intAD1.addLast(18);
        intAD1.addLast(19); // 12, 13, 14, 15, 16, 17, 18, 19
        System.out.println(intAD1.removeFirst()); // 12
        System.out.println(intAD1.removeLast()); // 19
        intAD1.addFirst(2);
        intAD1.addFirst(1);
        intAD1.addFirst(0);

        System.out.println("At index 5: " + intAD1.get(5)); // 15
        System.out.println("Size: " + intAD1.size()); // 9
        intAD1.printDeque();
    }
}
