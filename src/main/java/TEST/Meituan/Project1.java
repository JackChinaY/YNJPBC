package TEST.Meituan;

import java.util.Scanner;

public class Project1 {
}


//package TEST.Meituan;
//
//        import java.util.Scanner;
//
//public class Main {
//    public static int GCD(int a, int b) {
//        int r;
//        while (b > 0) {
//            r = a % b;
//            a = b;
//            b = r;
//        }
//        return a;
//    }
//
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        int N, n, m, p;
//        while (sc.hasNext()) {
//            N = sc.nextInt();
//            n = sc.nextInt();
//            m = sc.nextInt();
//            p = sc.nextInt();
//            int[] array = new int[N];
//            array[0] = p;
//            for (int i = 1; i < N; i++) {
//                array[i] = (array[i - 1] + 153) % p;
//            }
//            int sum = 0;
//            for (int i = 1; i <= n; i++) {
//                for (int j = 1; j <= m; j++) {
//                    sum += array[GCD(i, j) - 1];
//                }
//            }
//            System.out.println(sum);
//        }
//    }
//}

