package Revocatioon.CSC_CPABE;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 统计各个函数耗时
 */
public class TimeCountProxyHandle implements InvocationHandler {

    private Object proxied;

    public TimeCountProxyHandle(Object obj) {
        proxied = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long begin = System.currentTimeMillis();
        Object result = method.invoke(proxied, args);
        long end = System.currentTimeMillis();
        System.out.println(method.getName() + "过程耗时:" + (end - begin) + "ms");
//        System.out.println();
        return result;
    }
}
