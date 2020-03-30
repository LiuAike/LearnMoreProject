package com.self.study.example;

import com.self.study.dto.BaseClass;
import com.self.study.dto.SubClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 存活在JDK8中的bug
 */
@Slf4j
public class Jdk8Debug {

    public static void main(String[] args) {
        TestOne();
        TestTwo();
        TestThree();
    }

    /**
     * SubClass 继承自BaseClass，由于SubClass数组中每一个元素都是SubClass对象，
     * 所以BaseClass[] baseArray = subArray;这种强制类型转换不会报错。
     * 这其实就是java对象的向上转型，子类数组转换成父类数组是允许的。
     * 但是由于数组中元素类型都是SubClass类型的，所以 baseArray[0] = new BaseClass();
     * 会报错java.lang.ArrayStoreException。
     * <p>
     * 总结：这也就是说假如我们有1个Object[]数组，
     * 并不代表着我们可以将Object对象存进去，这取决于数组中元素实际的类型。
     * <p>
     * 原文链接：https://blog.csdn.net/qq_33589510/article/details/104767849
     */
    public static void TestOne() {
        SubClass[] subArray = {new SubClass(), new SubClass()};
        System.out.println(subArray.getClass());//class [Lcom.self.study.dto.SubClass

        BaseClass[] baseArray = subArray;
        System.out.println(baseArray.getClass());//class [Lcom.self.study.dto.SubClass

        baseArray[0] = new BaseClass();//Exception in thread "main" java.lang.ArrayStoreException: com.self.study.dto.BaseClass
    }


    /**
     * List<String> list = Arrays.asList("abc");需要注意，可以知道返回的实际类型是java.util.Arrays$ArrayList，而不是ArrayList。
     * <p>
     * 我们调用Object[] objArray = list.toArray();返回是String[]数组，所以我们不能将Object对象，放到objArray数组中。
     * <p>
     * 原文链接：https://blog.csdn.net/qq_33589510/article/details/104767849
     */
    public static void TestTwo() {
        List<String> list = Arrays.asList("abc");
        System.out.println(list.getClass());//class java.util.Arrays$ArrayList

        //若是Integer类型
//        List<Integer> list = Arrays.asList(1,2);
//        System.out.println(list.toArray().getClass());//class [Ljava.lang.Integer

        Object[] objArray = list.toArray();
        System.out.println(objArray.getClass());//class [Ljava.lang.String

        objArray[0] = new Object(); //Exception in thread "main" java.lang.ArrayStoreException: java.lang.Object

    }

    public static void TestThree() {
        List<String> dataList = new ArrayList<String>();
        dataList.add("one");
        dataList.add("two");
        Object[] listToArray = dataList.toArray();
        System.out.println(listToArray.getClass());//class [Ljava.lang.Object;

        listToArray[0] = "";
        listToArray[0] = 123;
        listToArray[0] = new Object();

    }

    /**
     * 通过TestTwo和TestThree可以看出，如果我们有1个List<String> stringList对象，当我么调用Object[] objectArray = stringList.toArray();
     * 的时候，objectArray 并不一定能够放置Object对象。这就是源码中的注释：c.toArray might (incorrectly) not return Object[] (see 6260652)。
     *
     * 为了考虑这种情况，所以源码中进行了if判断，来防止错误的数组对象导致异常。Arrays.copyOf(elementData, size, Object[].class);
     * 这个方法就是用来创建1个Object[]数组，这样数组中就可以存放任意对象了
     */

    //如下：源码中进行了if判断，来防止错误的数组对象导致异常。Arrays.copyOf(elementData, size, Object[].class);

   /* public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    public CopyOnWriteArrayList(Collection<? extends E> c) {
        Object[] elements;
        if (c.getClass() == CopyOnWriteArrayList.class)
            elements = ((CopyOnWriteArrayList<?>)c).getArray();
        else {
            elements = c.toArray();
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elements.getClass() != Object[].class)
                elements = Arrays.copyOf(elements, elements.length, Object[].class);
        }
        setArray(elements);
    }*/
}
