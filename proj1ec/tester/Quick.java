package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.algs4.*;

public class Quick {
    @Test
    public void quickTest() {
        StudentArrayDeque<Integer> studentDeque = new StudentArrayDeque<>();

        studentDeque.addLast(92);
        studentDeque.removeFirst();
        Integer item = studentDeque.removeFirst();

        System.out.println(item);
    }
}
