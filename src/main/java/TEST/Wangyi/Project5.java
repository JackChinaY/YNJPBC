package TEST.Wangyi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Project5 {
}
//import java.io.BufferedReader;
//        import java.io.IOException;
//        import java.io.InputStreamReader;
//        import java.util.Arrays;
//
//public class Main {
//    public static void main(String[] args) throws IOException {
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        String line;
//        while ((line = br.readLine()) != null) {
//            String[] s = line.trim().split(" ");
//            int n = Integer.parseInt(s[0]);
//            int k = Integer.parseInt(s[1]);
//            line = br.readLine();
//            String[] a = line.trim().split(" ");
//            int[] fenshu = new int[n];
//            for (int i = 0; i < n; i++) {
//                fenshu[i] = Integer.parseInt(a[i]);
//            }
//            line = br.readLine();
//            String[] b = line.trim().split(" ");
//            int[] qingxing = new int[n];
//            for (int i = 0; i < n; i++) {
//                qingxing[i] = Integer.parseInt(b[i]);
//            }
//
//
//            int[] temp = new int[n];
//
//            int c = 0;
//            for (int i = 0; i <= n - k; i++) {
//                if (qingxing[i] == 0) {
//                    for (int j = 0; j < k; j++) {
//                        c += fenshu[i + j];
//                    }
//                    temp[i] = c;
//                    c = 0;
//                }
//            }
//
//            int aar_Max = temp[0], aar_index = 0;
//            for (int i = 0; i < temp.length; i++) {
//                if (temp[i] > aar_Max) {//比较后赋值
//                    aar_Max = temp[i];
//                    aar_index = i;
//                }
//            }
////            System.out.println(n + " " + k);
////            System.out.println(Arrays.toString(fenshu));
////            System.out.println(Arrays.toString(qingxing));
////            System.out.println(Arrays.toString(temp));
////            System.out.println("aar数组中最大的数为： " + aar_Max + " 下标是： " + aar_index);
//            int count = 0;
//            for (int i = 0; i < n; i++) {
//                if (qingxing[i] == 1 && (i < aar_index || i > aar_index + k)) {
//                    count += fenshu[i];
//                }
//            }
//            count += aar_Max;
//            System.out.println(count);
//        }
//    }
//}