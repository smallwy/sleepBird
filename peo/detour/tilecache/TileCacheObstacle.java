/*
Copyright (c) 2009-2010 Mikko Mononen memon@inside.org
recast4j copyright (c) 2015-2019 Piotr Piastucki piotr@jtilia.org

This software is provided 'as-is', without any express or implied
warranty.  In no event will the authors be held liable for any damages
arising from the use of this software.
Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:
1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
3. This notice may not be removed or altered from any source distribution.
*/
package detour.tilecache;

import java.util.ArrayList;
import java.util.List;

public class TileCacheObstacle {

    public enum TileCacheObstacleType {
        CYLINDER, BOX, ORIENTED_BOX
    };

    final int index;
    TileCacheObstacleType type;
    final float[] pos = new float[3];
    final float[] bmin = new float[3];
    final float[] bmax = new float[3];
    float radius, height;
    final float[] center = new float[3];
    final float[] extents = new float[3];
    final float[] rotAux = new float[2]; // { cos(0.5f*angle)*sin(-0.5f*angle); cos(0.5f*angle)*cos(0.5f*angle) - 0.5 }
    List<Long> touched = new ArrayList<>();
    final List<Long> pending = new ArrayList<>();
    int salt;
    ObstacleState state = ObstacleState.DT_OBSTACLE_EMPTY;
    TileCacheObstacle next;

    public TileCacheObstacle(int index) {
        salt = 1;
        this.index = index;
    }

}
