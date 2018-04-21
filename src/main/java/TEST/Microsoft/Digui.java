package TEST.Microsoft;

public class Digui {
    public static void a(int i) {
        if (i == 0) {
            return;
        }
//        System.out.println(i);
        a(--i);
        System.out.print((i + 1) + " ");
    }

    public static void main(String[] args) {
        a(10);
        System.out.println();
    }
}
