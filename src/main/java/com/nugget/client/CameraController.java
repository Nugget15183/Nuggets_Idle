package com.nugget.client;
import com.nugget.NuggetSIdle;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.util.FreeCamera;
import org.slf4j.Logger;

public class CameraController {
    boolean enabled=false;
    int currentCamState=0;
    float alpha = 0.0f;
    Logger LOGGER=null;
    FreeCamera cam = null;
    BlockPos lookAtBlockpos=null;

    public CameraController(Logger l) {
        LOGGER = l;
    }

    Vec3[][] basepositions = {
            {new Vec3(1.5d, 2.00d, 3.00d), new Vec3(-1.5d, 2.00d, 3.00d)}, //across the front of the player
            {new Vec3(3.00d, 2.00d, 1.5d), new Vec3(3.00d, 2.00d, -1.5d)}, //across the side
            {new Vec3(-1.5d, 2.00d, 3.00d), new Vec3(1.5d, 2.00d, 3.00d)}, //across the front of the player (reverse)
            {new Vec3(-3.00d, 2.00d, -1.5d), new Vec3(-3.00d, 2.00d, 1.5d)}, //across the other side
            {new Vec3(3.00d, 2.00d, -1.5d), new Vec3(3.00d, 2.00d, 1.5d)}, //across side (reverse)
            {new Vec3(-3.00d, 2.00d, 1.5d), new Vec3(-3.00d, 2.00d, -1.5d)}, //across the other side (reverse)
            {new Vec3(0.00d, 2.00d, -5.0d), new Vec3(0.00d, 2.00d, -1.5d)},
            {new Vec3(0.00d, 0.2d, 0.75d), new Vec3(0.00d, 1.0d, 0.75d)},
    };

    Vec3[][] baseorientations = {
            {new Vec3(20.0d, 180.0d, 0.0d), new Vec3(20.0d, 180.0d, 0.0d)},
            {new Vec3(20.0d, 90.0d, 0.0d), new Vec3(20.0d, 90.0d, 0.0d)},
            {new Vec3(20.0d, 180.0d, 0.0d), new Vec3(20.0d, 180.0d, 0.0d)},
            {new Vec3(20.0d, -90.0d, 0.0d), new Vec3(20.0d, -90.0d, 0.0d)},
            {new Vec3(20.0d, 90.0d, 0.0d), new Vec3(20.0d, 90.0d, 0.0d)},
            {new Vec3(20.0d, -90.0d, 0.0d), new Vec3(20.0d, -90.0d, 0.0d)},
            {new Vec3(0.0d, 0.0d, 0.0d), new Vec3(20.0d, 0.0d, 0.0d)},
            {new Vec3(0.0d, 180.0d, 0.0d), new Vec3(0.0d, 180.0d, 0.0d)},
    };

    float[] basedurations = {
            5,
            5,
            5,
            5,
            5,
            5,
            5,
            5,
    };

    Vec3[][] positions = new Vec3[basepositions.length][2];
    Vec3[][] orientations = new Vec3[baseorientations.length][2];
    float[] durations = new float[basedurations.length];
    int[] order = new int[basepositions.length];
    private final java.util.Random random = new java.util.Random();

    Vec3 vec3Lerp(Vec3 a, Vec3 b, float alpha) {
        double x = a.x + (b.x - a.x) * alpha;
        double y = a.y + (b.y - a.y) * alpha;
        double z = a.z + (b.z - a.z) * alpha;
        return new Vec3(x, y, z);
    }

    Vec3 rotateOffsetByYaw(Vec3 offset, float yawDegrees) {
        double rad = Math.toRadians(yawDegrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        double x = offset.x * cos - offset.z * sin;
        double z = offset.x * sin + offset.z * cos;
        return new Vec3(x, offset.y, z);
    }

    public void toggle(boolean state) {
        if(Freecam.isEnabled() != state) Freecam.toggle();
        if (state) {
            currentCamState = 0;
            shuffle();
        }
        enabled=state;
        cam=Freecam.getFreeCamera();
        alpha=0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void shuffle() {
        int len = positions.length;

        for(int i=0; i<len; i++) {
            this.order[i]=i;
        }

        for(int i = len - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = this.order[i];
            this.order[i] = this.order[j];
            this.order[j] = temp;
        }

        for(int i=0; i<len; i++) {
            this.positions[i]=basepositions[this.order[i]];
            this.orientations[i]=baseorientations[this.order[i]];
            this.durations[i]=basedurations[this.order[i]];
        }
    }

    public void tick(Minecraft client) {
        NuggetSIdle.hideGUI=enabled;
        if(enabled && client.player != null && cam != null) {
            Vec3[] positions = this.positions[currentCamState];
            Vec3[] orientations = this.orientations[currentCamState];
            float duration_seconds = this.durations[currentCamState];

            double timePerTick = 1.0f / (duration_seconds * 20);
            this.alpha = (float) Math.clamp(this.alpha + timePerTick, 0.00d, 1.00d);

            Vec3 plrPos = client.player.position();
            float plrYaw = client.player.getYRot();

            Vec3 startPos = plrPos.add(rotateOffsetByYaw(positions[0], plrYaw));
            Vec3 endPos = plrPos.add(rotateOffsetByYaw(positions[1], plrYaw));
            Vec3 finalPos = vec3Lerp(startPos, endPos, alpha);

            Vec3 startOrientation = new Vec3(orientations[0].x, orientations[0].y + plrYaw, orientations[0].z);
            Vec3 endOrientation = new Vec3(orientations[1].x, orientations[1].y + plrYaw, orientations[1].z);
            Vec3 finalOrientation = vec3Lerp(startOrientation, endOrientation, alpha);

            cam.setYRot((float) finalOrientation.y);
            cam.setXRot((float) finalOrientation.x);
            cam.setPos(finalPos);

            if(this.alpha >= 1.0f) {
                this.alpha = 0.0f;
                this.currentCamState = (this.currentCamState + 1) % this.positions.length;
            }
        }
    }
}