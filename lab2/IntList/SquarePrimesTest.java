package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }

    /**
     *  Here is a test for the squarePrimes method. This one has multiple
     *  prime numbers and is made to check if squarePrimes continues
     *  through the list instead of stopping after the first prime number.
     */
    @Test
    public void testSquarePrimesMultiple() {
        IntList lst = IntList.of(16, 17, 18, 19);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("16 -> 289 -> 18 -> 361", lst.toString());
        assertTrue(changed);
    }
}
