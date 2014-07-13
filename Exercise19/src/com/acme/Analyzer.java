package com.acme;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Analyzer {

	public static void main(final String[] args) {
		Class clazz = Test.class;
		// TODO: analyze the class and print all information about the class which may be obtained via reflection
	
		System.out.println("Simple name: " + clazz.getSimpleName());
		System.out.println("Full name: " + clazz.getName());
		System.out.println("Modifiers: " + Modifier.toString(clazz.getModifiers()));
		System.out.println("Package: " + clazz.getPackage());
		System.out.println("Superclass: " + clazz.getSuperclass());
		System.out.println("Interfaces: ");
		for ( Class interfc : clazz.getInterfaces() ) {
			System.out.println(interfc.getName());
		}
		
		System.out.println("Constructors: ");
		for ( Constructor construct : clazz.getConstructors() ) {
			System.out.println(construct.toString());
		}
		
		System.out.println("Fields: ");
		for ( Field field : clazz.getDeclaredFields() ) {
			System.out.println(field);
		}
		
		System.out.println("Methods: ");
		for ( Method method : clazz.getMethods() ) {
			System.out.println(method.toString());
		}
		
		System.out.println("Annotations: ");
		for ( Annotation annotation : clazz.getDeclaredAnnotations() ) {
			System.out.println(annotation.toString());
		}
	}
}
