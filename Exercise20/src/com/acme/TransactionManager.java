package com.acme;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionManager implements java.lang.reflect.InvocationHandler {

	private Object obj;

	public static Object applyTransactionDemarcation(final Object obj) {
		return java.lang.reflect.Proxy.newProxyInstance(obj.getClass()
				.getClassLoader(), obj.getClass().getInterfaces(),
				new TransactionManager(obj));
	}

	private TransactionManager(final Object obj) {
		this.obj = obj;
	}

	public Object invoke(final Object proxy, final Method m, final Object[] args)
			throws Throwable {
		Object result;
		try {
			Method method = obj.getClass().getMethod(m.getName(),
					m.getParameterTypes());
						
			// annotation process
			for ( Annotation annotation : obj.getClass().getAnnotations() ) {
				if ( annotation instanceof Transactional  ) {
					// necessary annotation  
					Transactional transact = (Transactional) annotation;
					System.out.println("Begin: " + transact.begin());
				}
			}

			result = method.invoke(obj, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} catch (Exception e) {
			throw new RuntimeException("unexpected invocation exception: "
					+ e.getMessage());
		}
		return result;
	}
}
