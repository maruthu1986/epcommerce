/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.test.util.mock;


/**
 * Wraps a mock with target type of T.
 * @param <T> the target type of the mock to be wrapped
 */
public class MockWrapperImpl<T> implements MockWrapper<T> {
	
	private T mock;
	
	@Override
	public void setMock(final T mock) {
		this.mock = mock;
	}

	@Override
	public T getMock() {
		return this.mock;
	}
	
}
