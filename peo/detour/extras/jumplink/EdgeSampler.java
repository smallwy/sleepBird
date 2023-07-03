package detour.extras.jumplink;

import java.util.ArrayList;
import java.util.List;

import static org.recast4j.detour.DetourCommon.*;

public class EdgeSampler {

    public final GroundSegment start = new GroundSegment();
    public final List<GroundSegment> end = new ArrayList<>();
    public final Trajectory trajectory;

    final float ax[] = new float[3];
    final float ay[] = new float[3];
    final float az[] = new float[3];

    public EdgeSampler(Edge edge, Trajectory trajectory) {
        this.trajectory = trajectory;
        DetourCommon.vCopy(ax, DetourCommon.vSub(edge.sq, edge.sp));
        DetourCommon.vNormalize(ax);
        DetourCommon.vSet(az, ax[2], 0, -ax[0]);
        DetourCommon.vNormalize(az);
        DetourCommon.vSet(ay, 0, 1, 0);
    }

}
