package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.algs4.*;

public class TestArrayDequeEC {

    @Test
    public void testRandom() {
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();
        Integer actual = 0;
        Integer expected = 0;
        String message = "";
        for (int i = 0; i < 1000; i += 1) {
            int val = StdRandom.uniform(0, 4);
            Integer item = StdRandom.uniform(1, 100);
            if (val == 0) {
                student.addFirst(item);
                solution.addFirst(item);
                message += "addFirst(" + item + ")\n";
            } else if (val == 1) {
                student.addLast(item);
                solution.addLast(item);
                message += "addLast(" + item + ")\n";
            } else if (val == 2) {
                if (student.isEmpty() && solution.isEmpty()) {
                    continue;
                } else {
                    actual = student.removeFirst();
                    expected = solution.removeFirst();
                    message += "removeFirst()\n";
                }
            } else if (val == 3) {
                if (student.isEmpty() && solution.isEmpty()) {
                    continue;
                } else {
                    actual = student.removeLast();
                    expected = solution.removeLast();
                    message += "removeLast()\n";
                }
            }
            assertEquals(message, expected, actual);
        }
    }
}
