package com.h1ggsk.radon.utils.rotation;

import com.h1ggsk.radon.Radon;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import com.h1ggsk.radon.event.EventListener;
import com.h1ggsk.radon.event.events.PostMotionEvent;
import com.h1ggsk.radon.event.events.PreMotionEvent;

public final class RotationFaker {
	private boolean fakeRotation;
	private float serverYaw;
	private float serverPitch;
	private float realYaw;
	private float realPitch;


	@EventListener
	public void onPreMotion(PreMotionEvent event)
	{
		if(!fakeRotation)
			return;
		
		ClientPlayerEntity player = Radon.mc.player;
		realYaw = player.getYaw();
		realPitch = player.getPitch();
		player.setYaw(serverYaw);
		player.setPitch(serverPitch);
	}
	
	@EventListener
	public void onPostMotion(PostMotionEvent event)
	{
		if(!fakeRotation)
			return;
		
		ClientPlayerEntity player = Radon.mc.player;
		player.setYaw(realYaw);
		player.setPitch(realPitch);
		fakeRotation = false;
	}
	
	public void faceVectorPacket(Vec3d vec)
	{
		Rotation needed = RotationUtils.getNeededRotations(vec);
		ClientPlayerEntity player = Radon.mc.player;
		
		fakeRotation = true;
		serverYaw =
			RotationUtils.limitAngleChange(player.getYaw(), needed.yaw());
		serverPitch = needed.pitch();
	}
	
	public void faceVectorClient(Vec3d vec)
	{
		Rotation needed = RotationUtils.getNeededRotations(vec);
		
		ClientPlayerEntity player = Radon.mc.player;
		player.setYaw(
			RotationUtils.limitAngleChange(player.getYaw(), needed.yaw()));
		player.setPitch(needed.pitch());
	}
	
	public void faceVectorClientIgnorePitch(Vec3d vec)
	{
		Rotation needed = RotationUtils.getNeededRotations(vec);
		
		ClientPlayerEntity player = Radon.mc.player;
		player.setYaw(
			RotationUtils.limitAngleChange(player.getYaw(), needed.yaw()));
		player.setPitch(0);
	}
	
	public float getServerYaw()
	{
		return fakeRotation ? serverYaw : Radon.mc.player.getYaw();
	}
	
	public float getServerPitch()
	{
		return fakeRotation ? serverPitch : Radon.mc.player.getPitch();
	}
}