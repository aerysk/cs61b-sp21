package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> noResize = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        noResize.addLast(4);
        buggy.addLast(4);
        noResize.addLast(5);
        buggy.addLast(5);
        noResize.addLast(6);
        buggy.addLast(6);

        assertEquals(true, noResize.removeLast() == buggy.removeLast());
        assertEquals(true, noResize.removeLast() == buggy.removeLast());
        assertEquals(true, noResize.removeLast() == buggy.removeLast());

        assertEquals(true, noResize.size() == buggy.size());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                // System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int sizeL = L.size();
                int sizeB = B.size();
                // System.out.println("L size: " + sizeL + ", B size: " + sizeB);
            } else if (L.size() == 0 || B.size() == 0) {
                continue;
            } else if (operationNumber == 2) {
                // getLast
                // System.out.println("got last (L, B): " + L.getLast() + ", " + B.getLast());
            } else if (operationNumber == 3) {
                // removeLast
                int lastL = L.removeLast();
                int lastB = B.removeLast();
                // System.out.println("removed last (L, B): " + lastL + ", " + lastB);
            }
        }
    }
}
