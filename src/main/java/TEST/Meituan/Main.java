package TEST.Meituan;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextInt()) {
            int T;
            T = sc.nextInt();
            int[] array = new int[T];
            for (int i = 0; i < T; i++) {
                array[i] = sc.nextInt();
            }
            for (int i = 0; i < T; i++) {
                int sum = 0;
                for (int j = array[i]; j > 0; j--) {
                    sum += String.valueOf(j).length();
                }
                System.out.println(sum);
            }
        }

    }
}
