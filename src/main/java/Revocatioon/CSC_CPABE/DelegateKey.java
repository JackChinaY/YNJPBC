package Revocatioon.CSC_CPABE;

import it.unisa.dia.gas.jpbc.Element;

public class DelegateKey implements Runnable {
    private Element element;
    private Element r;

    public DelegateKey(Element element, Element r) {
        this.element = element;
        this.r = r;
    }

    @Override
    public void run() {
        element.powZn(r);
        System.out.println("计算完毕");
    }
}
