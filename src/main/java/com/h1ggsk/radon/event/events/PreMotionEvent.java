package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.CancellableEvent;

public class PreMotionEvent extends CancellableEvent {
	private static final PreMotionEvent INSTANCE = new PreMotionEvent();

	public static PreMotionEvent get() {
		return INSTANCE;
	}
}
