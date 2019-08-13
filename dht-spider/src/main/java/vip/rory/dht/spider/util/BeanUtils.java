package vip.rory.dht.spider.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.TreeMap;
import java.util.Map;

/**
 * @author zhanghangtian
 * @date 2019年7月2日 上午11:11:19
 */
public class BeanUtils {

    @SuppressWarnings("unchecked")
    public static <T, K, V> T mapToBean(Map<K, V> map, TypeReference<T> typeReference)
            throws IllegalArgumentException, InvocationTargetException, ClassNotFoundException, Exception {
        Type type = typeReference.getRawType();
        return (T) mapToBean(map, Class.forName(type.getTypeName()));
    }

    public static <T, K, V> T mapToBean(Map<K, V> map, Class<T> clazz)
            throws Exception, IllegalArgumentException, InvocationTargetException {
        T t = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            t = clazz.newInstance();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    Method setter = property.getWriteMethod();// Java中提供了用来访问某个属性的getter/setter方法
                    setter.invoke(t, value);
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) {
            return null;
        }
        Map<String, Object> map = new TreeMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!key.equals("class")) {
                    Method getter = property.getReadMethod();// Java中提供了用来访问某个属性的 getter/setter方法
                    Object value;
                    value = getter.invoke(bean);
                    map.put(key, value);
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();

        }
        return map;
    }

    public static Map<String, Object> beanToMapComplate(Object bean) {
        if (bean == null) {
            return null;
        }
        Map<String, Object> map = new TreeMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!key.equals("class")) {
                    Method getter = property.getReadMethod();// Java中提供了用来访问某个属性的 getter/setter方法
                    Object value = getter.invoke(bean);
                    Class<? extends Object> clazz = value.getClass();
                    if (!clazz.getName().startsWith("java") && !clazz.getName().startsWith("[")) {
                        Map<String, Object> beanToMapComplate = beanToMapComplate(value);
                        value = beanToMapComplate;
                    }
                    map.put(key, value);
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();

        }
        return map;
    }

}
