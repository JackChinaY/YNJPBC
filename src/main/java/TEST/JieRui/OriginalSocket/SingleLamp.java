package TEST.JieRui.OriginalSocket;

/**
 * 单灯结构体
 */
public class SingleLamp {
    /**
     * 单灯的编号,8位字符串，如：“12345678”
     */
    private String id;
    /**
     * 可以表示单灯的亮度值、状态码（如错误码）、能耗值
     * 亮度值范围：0-100，0表示关闭，100表示满亮
     * 状态码范围：1-9，1表示单灯损坏，失联，2表示单灯损坏，在线
     * 能耗值范围：0-100，表示百分比
     */
    private int value;

    public SingleLamp(String id, int value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
