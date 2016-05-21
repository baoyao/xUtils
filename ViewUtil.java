package com.example.testxutil.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class ViewUtil {

	public static void findAllView(Object obj) {
		if (obj == null) {
			return;
		}
		Field field = null;
		Method method = null;
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			if ((fields != null) && (fields.length > 0)) {
				for (int i = 0; i < fields.length; i++) {
					field = fields[i];
					Id id = (Id) field.getAnnotation(Id.class);
					if (id != null) {
						View view = ((Activity) obj).findViewById(id.value());
						field.setAccessible(true);
						field.set(obj, view);
					}
				}
			}

			Method[] methods = obj.getClass().getDeclaredMethods();
			if ((methods != null) && (methods.length > 0)) {
				for (int i = 0; i < methods.length; i++) {
					method = methods[i];
					Id id = (Id) method.getAnnotation(Id.class);
					if (id != null) {
						View view = ((Activity) obj).findViewById(id.value());
						if (view != null) {
							final Object tempObj = obj;
							final Method tempMethod = method;

							Class<?> listenerType = id.type();
							String listenerSetter = "set"
									+ listenerType.getSimpleName();

							Object listener = Proxy.newProxyInstance(
									listenerType.getClassLoader(),
									new Class[] { listenerType },
									new InvocationHandler() {
										@Override
										public Object invoke(Object proxy,
												Method method, Object[] args)
												throws Throwable {
											boolean setAccessible = false;
											if (!tempMethod.isAccessible()) {
												setAccessible = true;
												tempMethod.setAccessible(true);
											}
											Object result = tempMethod.invoke(
													tempObj, args);
											if (setAccessible) {
												tempMethod.setAccessible(false);
											}
											return result;
										}
									});

							Method setEventListenerMethod = view.getClass()
									.getMethod(listenerSetter,
											new Class[] { listenerType });
							setEventListenerMethod.invoke(view,
									new Object[] { listener });
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public @interface Id {
		int value();

		Class<?> type() default OnClickListener.class;
	}
}
