package TEST;

/**
 * 接口及其实现以及控制反转
 */
public class Test5 {
    public static void main(String[] args) {
//        Person person = new Person();
//        person.Wear();
        Wear sweater = new Sweater();
//        sweater.plan();

        Person person = new Person();
        person.setClothe(sweater);
        person.Wear();
    }
}

class Person {
    private Wear clothe;

    public Wear getClothe() {
        return clothe;
    }

    public void setClothe(Wear clothe) {
        this.clothe = clothe;
    }

    public void Wear() {
//        clothe = new Sweater();
        clothe.WearClothe();
    }
}

interface Wear {
    void WearClothe();
//    void plan();
}

class Sweater implements Wear {
    @Override
    public void WearClothe() {
        System.out.println("穿毛衣");
    }

    public void plan() {

    }
}

class Shirt implements Wear {
    @Override
    public void WearClothe() {
        System.out.println("穿衬衫");
    }
}