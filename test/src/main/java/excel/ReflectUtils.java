package excel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @PackageName: excel
 * @ClassName: ReflectUtils
 * @Description:
 * @author: 熊杰
 * @data：20202020/5/2416:03
 */
public class ReflectUtils {
    public static final String SET = "set";
    public static final String GET = "get";

    private ReflectUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 抛出异常的反射
     * @param obj obj
     * @param att att
     * @param value value
     * @param param param
     */
    public static void setterThrow(Object obj, String att, Object value, Class<?> param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = SET + initStr(att);
        Method met = obj.getClass().
                getMethod(methodName, param);
        met.invoke(obj, value);
    }

    public static void stringValueSetterThrow(Object obj, String att, Object value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        setterThrow(obj, att, value, String.class);
    }

    public static Object getter(Object obj, String att) {
        try {
            Method met = obj.getClass().getMethod(GET + initStr(att));
            return met.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static String initStr(String old) {    // 将单词的首字母大写
        return old.substring(0, 1).toUpperCase() + old.substring(1);
    }

    public static <T> T newTClass(Class<T> clazz) {
        T a = null;
        try {
            a = clazz.newInstance();
        } catch (Exception ex) {
            // 实例对象出错
        }
        return a;
    }
}
