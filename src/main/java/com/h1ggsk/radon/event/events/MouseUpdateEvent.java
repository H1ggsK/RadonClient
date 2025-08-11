package com.h1ggsk.radon.event.events;

import com.h1ggsk.radon.event.Event;

public class MouseUpdateEvent implements Event {
    private double deltaX;
    private double deltaY;
    private final double defaultDeltaX;
    private final double defaultDeltaY;
    
    public MouseUpdateEvent(double deltaX, double deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.defaultDeltaX = deltaX;
        this.defaultDeltaY = deltaY;
    }
    
    public double getDeltaX() {
        return deltaX;
    }
    
    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }
    
    public double getDeltaY() {
        return deltaY;
    }
    
    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }
    
    public double getDefaultDeltaX() {
        return defaultDeltaX;
    }
    
    public double getDefaultDeltaY() {
        return defaultDeltaY;
    }
    
    public void addDeltaX(double deltaX) {
        this.deltaX += deltaX;
    }
    
    public void addDeltaY(double deltaY) {
        this.deltaY += deltaY;
    }
}