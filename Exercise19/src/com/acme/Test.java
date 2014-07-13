package com.acme;

import java.io.Serializable;

public abstract class Test implements Serializable, Cloneable {
	private int field;
	protected String str;
	public double koef;

	public Test(final Object field) {
	}

	@Deprecated
	protected static void method(final String[] params) {
	}
}
