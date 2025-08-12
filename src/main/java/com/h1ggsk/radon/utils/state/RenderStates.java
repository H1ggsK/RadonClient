package com.h1ggsk.radon.utils.state;

public class RenderStates {
    /**
     * Initializes all render states.
     * This method should be called during the mod initialization phase.
     * currently, it is only used to pre init pipelines, so nothing will fail if this is not called. no clue what it will do in the future.
     */
    public static void init() {
        CircleRenderState.init();
    }
}
