package gameart.tools.proto;

/**
 * @author Stone.w
 * @Date 2019/3/26
 */
public class DateType {
    String type;
    String data;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static DateType valueOf(String messagerepeated, String strings) {
        DateType dateType = new DateType();
        dateType.setData(strings);
        dateType.setType(messagerepeated);
        return dateType;
    }

    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
