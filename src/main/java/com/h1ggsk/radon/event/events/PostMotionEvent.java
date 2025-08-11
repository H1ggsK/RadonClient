package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class PostMotionEvent extends CancellableEvent {
	private static final PostMotionEvent INSTANCE = new PostMotionEvent();

	public static PostMotionEvent get() {
		return INSTANCE;
	}
}
