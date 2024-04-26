package deque;
import java.util.Comparator;

/** The MaxArrayDeque implementation.
 * @author Emily Nguyen */
public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    /** Creates a MaxArrayDeque with the given Comparator. */
    public MaxArrayDeque(Comparator<T> c) {
        comparator = c;
    }

    /** Returns the maximum element in the deque as governed by
     *  the previously given Comparator. If empty, returns null. */
    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maximum = get(0);
        for (int i = 1; i < size(); i += 1) {
            T compareWith = get(i);
            if (comparator.compare(maximum, compareWith) < 0) {
                maximum = compareWith;
            }
        }
        return maximum;
    }

    /** Returns the maximum element in the deque as governed by
     *  the parameter Comparator c. If empty, returns null. */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maximum = get(0);
        for (int i = 1; i < size(); i += 1) {
            T compareWith = get(i);
            if (c.compare(maximum, compareWith) < 0) {
                maximum = compareWith;
            }
        }
        return maximum;
    }
}
