/** Class that prints the Collatz sequence starting from a given number.
 *  @author Emily Nguyen
 */
public class Collatz {
    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
    }
    public static int nextNumber(int n) {
        /**
         * if n is even, divide n by 2
         * if n is odd, triple n and add 1
         * repeat these steps until n is 1
         */
        if (n % 2 == 0) {
            n /= 2;
        } else {
            n = 3*n + 1;
        }
        return n;
    }
}

