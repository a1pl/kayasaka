package cc.squall.client.events.pLaYeR;


import cc.squall.client.events.CustomEvent;
import cc.squall.client.utils.IMinecraft;
import cc.squall.client.utils.module.rots.MoveUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class EventPlayerStrafe extends CustomEvent implements IMinecraft {

    private float forward;
    private float strafe;
    private float friction;
    private float yaw;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(final double speed) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        MoveUtil.stop();
    }
}