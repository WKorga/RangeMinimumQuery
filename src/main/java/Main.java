import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Scanner;

/**
 *      RANGE MINIMUM QUERY ALGORITHM WITH LINEAR SPACE AND TIME PREPROCESSING COMPLEXITY
 *      AND A CONSTANT QUERY TIME
 *
 *      WOJCIECH KORGA
 *      UNIVERSITY OF WROCŁAW
 *      2018
 *
 *      The algorithm preprocesses in linear time and space a given array A
 *      Resulting structure is able to answer every RMQ query:
 *          "What is an index of the smallest element of A from index i to j"
 *      in constant time
 */
public class Main {
    public static void main(String[] args) {
        /**
         * The algorithm preprocesses in linear time and space a given array A
         * Resulting structure is able to answer every RMQ query:
         *  "What is an index of the smallest element of A from index i to j"
         * in constant time
         */
        Scanner scanner = new Scanner(System.in);
        System.out.println("input:");
        String input = scanner.nextLine();
        ConstRMQ constRMQ = new ConstRMQ();
        constRMQ.preprocessInput(input);

        while (true){
            System.out.println("i:");
            int i = scanner.nextInt();
            System.out.println("j:");
            int j = scanner.nextInt();
            System.out.println(input.charAt(constRMQ.answerQuery(i,j)));
        }
    }
}
