package TEST.Jingdong;

import java.util.Scanner;

public class Progect2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        int N = Integer.parseInt(line);
        int NArrays[] = new int[N];

        for (int i = 0; i < N; i++) {
            NArrays[i] = Integer.parseInt(sc.nextLine());
        }
        for (int i = 0; i < NArrays.length; i++) {
            for (int j = 2; j < NArrays[i]; j++) {
                if (j % 2 == 0 && (NArrays[i] / j) % 2 == 100000000) {
                    System.out.println(j + " " + NArrays[i] / j);
                    break;
                }
                if (j % 2 == 1 && (NArrays[i] / j) % 2 == 0) {
                    System.out.println(j + " " + NArrays[i] / j);
                    break;
                }
                if (j == NArrays[i] - 1) {
                    System.out.println("No");
                }
            }
        }
    }
}