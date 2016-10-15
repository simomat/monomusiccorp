package de.infonautika.monomusiccorp.app.controller.utils.links;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.cglib.proxy.*;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class InvocationProxy {

    private static ObjenesisStd OBJENESIS = new ObjenesisStd();

    static <T> T methodOn(Class<T> type) {
        InvocationInterceptor interceptor = new InvocationInterceptor();
        return getProxyWithInterceptor(type, interceptor, type.getClassLoader());
    }

    private static <T> T getProxyWithInterceptor(Class<?> type, InvocationInterceptor interceptor, ClassLoader classLoader) {

        if (type.isInterface()) {
            return createInterfaceProxy(type, interceptor);
        }

        return createClassProxy(type, interceptor, classLoader);
    }

    private static <T> T createClassProxy(Class<?> type, InvocationInterceptor interceptor, ClassLoader classLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setInterfaces(new Class<?>[] { Invocation.class });
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setClassLoader(classLoader);

        Factory factory = (Factory) OBJENESIS.newInstance(enhancer.createClass());

        factory.setCallbacks(new Callback[] {interceptor});
        //noinspection unchecked
        return (T) factory;
    }

    private static <T> T createInterfaceProxy(Class<?> type, InvocationInterceptor interceptor) {
        ProxyFactory factory = new ProxyFactory(EmptyTargetSource.INSTANCE);
        factory.addInterface(type);
        factory.addInterface(Invocation.class);
        factory.addAdvice(interceptor);

        //noinspection unchecked
        return (T) factory.getProxy();
    }

    private static class InvocationInterceptor implements MethodInterceptor, org.aopalliance.intercept.MethodInterceptor, Invocation {

        private static final Method GET_METHOD;
        private static final Method GET_ARGUMENTS;

        static {
            GET_METHOD = ReflectionUtils.findMethod(Invocation.class, "getMethod");
            GET_ARGUMENTS = ReflectionUtils.findMethod(Invocation.class, "getArguments");
        }

        private Method method;
        private Object[] parameters;

        @Override
        public Object intercept(Object obj, Method method, Object[] parameters, MethodProxy methodProxy) throws Throwable {

            if (GET_METHOD.equals(method)) {
                return getMethod();
            }

            if (GET_ARGUMENTS.equals(method)) {
                return getArguments();
            }

            this.method = method;
            this.parameters = parameters;

            Class<?> returnType = method.getReturnType();
            return returnType.cast(getProxyWithInterceptor(returnType, this, obj.getClass().getClassLoader()));
        }

        @Override
        public Object[] getArguments() {
            return parameters;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return intercept(invocation.getThis(), invocation.getMethod(), invocation.getArguments(), null);
        }
    }
}
