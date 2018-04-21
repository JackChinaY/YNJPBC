package TEST.Microsoft;

/**
 * 递归操作
 */
public class Digui {
    public static void a(int i) {
        if (i == 0) {
            return;
        }
        System.out.print(i + " ");
        a(--i);
//        System.out.print((i + 1) + " ");
    }

    public static void main(String[] args) {
        a(10);
        System.out.println();
    }
}
