package com.ia.core.util.bean;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Custom helper class that can introspect Classes as well as Interfaces. Java's own Introspector
 * has a bug and doesn't work for Interfaces which have super interfaces.
 *
 * @see {@linkplain http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4275879}
 *
 */
public class BeanHelper {
    private static class InterfaceBeanInfo extends SimpleBeanInfo {
        private final BeanInfo main;

        private final Iterable<BeanInfo> inherited;

        public InterfaceBeanInfo(final BeanInfo main, final Iterable<BeanInfo> inherited) {
            this.main = main;
            this.inherited = inherited;
        }

        @Override
        public BeanInfo[] getAdditionalBeanInfo() {
            final Set<BeanInfo> infos = new LinkedHashSet<BeanInfo>();
            infos.addAll(Arrays.asList(main.getAdditionalBeanInfo()));
            for (final BeanInfo i : inherited) {
                infos.addAll(Arrays.asList(i.getAdditionalBeanInfo()));
            }
            return infos.toArray(new BeanInfo[infos.size()]);
        }

        @Override
        public BeanDescriptor getBeanDescriptor() {
            return main.getBeanDescriptor();
        }

        @Override
        public MethodDescriptor[] getMethodDescriptors() {
            final Set<MethodDescriptor> methods = new LinkedHashSet<MethodDescriptor>();
            methods.addAll(Arrays.asList(main.getMethodDescriptors()));
            for (final BeanInfo i : inherited) {
                methods.addAll(Arrays.asList(i.getMethodDescriptors()));
            }
            return methods.toArray(new MethodDescriptor[methods.size()]);
        }

        @Override
        public PropertyDescriptor[] getPropertyDescriptors() {
            final Set<PropertyDescriptor> properties = new LinkedHashSet<PropertyDescriptor>();
            properties.addAll(Arrays.asList(main.getPropertyDescriptors()));
            for (final BeanInfo i : inherited) {
                properties.addAll(Arrays.asList(i.getPropertyDescriptors()));
            }
            return properties.toArray(new PropertyDescriptor[properties.size()]);
        }
    }

    public static BeanInfo getBeanInfo(final Class<?> beanClass) {
        try {
            BeanInfo info = Introspector.getBeanInfo(beanClass);
            if (beanClass.isInterface()) {
                info = new InterfaceBeanInfo(info, getInheritedBeanInfo(beanClass.getInterfaces()));
            }
            return info;
        }
        catch (final IntrospectionException e) {
            throw new IllegalArgumentException(
                    "Cannot get bean info on target type " + beanClass.getName());
        }
    }

    private static Iterable<BeanInfo> getInheritedBeanInfo(final Class<?>[] interfaces) {
        final ArrayList<BeanInfo> infos = new ArrayList<BeanInfo>();
        for (final Class<?> i : interfaces) {
            infos.add(getBeanInfo(i));
        }
        return infos;
    }
}
