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
package detour.project;

import detour.project.ChunkyTriMesh;
import detour.project.DetourHandler;
import org.recast4j.recast.AreaModification;
import org.recast4j.recast.ConvexVolume;
import org.recast4j.recast.RecastVectors;
import org.recast4j.recast.geom.TriMesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static detour.project.DemoMath.vCross;
import static detour.project.DemoMath.vDot;
import static org.recast4j.detour.DetourCommon.vSub;


public class GameInputGeomProvider implements org.recast4j.recast.geom.InputGeomProvider {

    public final float[] vertices;
    public final int[] faces;
    public final float[] normals;
    final float[] bmin;
    final float[] bmax;
    final List<ConvexVolume> convexVolumes = new ArrayList<>();
    final ChunkyTriMesh chunkyTriMesh;

    public GameInputGeomProvider(List<Float> vertexPositions, List<Integer> meshFaces) {
        this(mapVertices(vertexPositions), mapFaces(meshFaces));
    }

    private static int[] mapFaces(List<Integer> meshFaces) {
        int[] faces = new int[meshFaces.size()];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = meshFaces.get(i);
        }
        return faces;
    }

    private static float[] mapVertices(List<Float> vertexPositions) {
        float[] vertices = new float[vertexPositions.size()];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = vertexPositions.get(i);
        }
        return vertices;
    }

    public GameInputGeomProvider(float[] vertices, int[] faces) {
        this.vertices = vertices;
        this.faces = faces;
        normals = new float[faces.length];
        calculateNormals();
        bmin = new float[3];
        bmax = new float[3];
        RecastVectors.copy(bmin, vertices, 0);
        RecastVectors.copy(bmax, vertices, 0);
        for (int i = 1; i < vertices.length / 3; i++) {
            RecastVectors.min(bmin, vertices, i * 3);
            RecastVectors.max(bmax, vertices, i * 3);
        }
        chunkyTriMesh = new ChunkyTriMesh(vertices, faces, faces.length / 3, 256);
    }

    @Override
    public float[] getMeshBoundsMin() {
        return bmin;
    }

    @Override
    public float[] getMeshBoundsMax() {
        return bmax;
    }

    public void calculateNormals() {
        for (int i = 0; i < faces.length; i += 3) {
            int v0 = faces[i] * 3;
            int v1 = faces[i + 1] * 3;
            int v2 = faces[i + 2] * 3;
            float[] e0 = new float[3], e1 = new float[3];
            for (int j = 0; j < 3; ++j) {
                e0[j] = vertices[v1 + j] - vertices[v0 + j];
                e1[j] = vertices[v2 + j] - vertices[v0 + j];
            }
            normals[i] = e0[1] * e1[2] - e0[2] * e1[1];
            normals[i + 1] = e0[2] * e1[0] - e0[0] * e1[2];
            normals[i + 2] = e0[0] * e1[1] - e0[1] * e1[0];
            float d = (float) Math
                    .sqrt(normals[i] * normals[i] + normals[i + 1] * normals[i + 1] + normals[i + 2] * normals[i + 2]);
            if (d > 0) {
                d = 1.0f / d;
                normals[i] *= d;
                normals[i + 1] *= d;
                normals[i + 2] *= d;
            }
        }
    }

    @Override
    public List<ConvexVolume> convexVolumes() {
        return convexVolumes;
    }

    @Override
    public Iterable<TriMesh> meshes() {
        return Collections.singletonList(new TriMesh(vertices, faces));
    }

    public Optional<Float> raycastMesh(float[] src, float[] dst) {

        // Prune hit ray.
        Optional<float[]> btminmax = isectSegAABB(src, dst, bmin, bmax);
        if (!btminmax.isPresent()) {
            return Optional.empty();
        }
        float btmin = btminmax.get()[0];
        float btmax = btminmax.get()[1];
        float[] p = new float[2], q = new float[2];
        p[0] = src[0] + (dst[0] - src[0]) * btmin;
        p[1] = src[2] + (dst[2] - src[2]) * btmin;
        q[0] = src[0] + (dst[0] - src[0]) * btmax;
        q[1] = src[2] + (dst[2] - src[2]) * btmax;

        List<ChunkyTriMesh.ChunkyTriMeshNode> chunks = chunkyTriMesh.getChunksOverlappingSegment(p, q);
        if (chunks.isEmpty()) {
            return Optional.empty();
        }

        float tmin = 1.0f;
        boolean hit = false;
        for (ChunkyTriMesh.ChunkyTriMeshNode chunk : chunks) {
            int[] tris = chunk.tris;
            for (int j = 0; j < chunk.tris.length; j += 3) {
                float[] v1 = new float[] { vertices[tris[j] * 3], vertices[tris[j] * 3 + 1],
                        vertices[tris[j] * 3 + 2] };
                float[] v2 = new float[] { vertices[tris[j + 1] * 3], vertices[tris[j + 1] * 3 + 1],
                        vertices[tris[j + 1] * 3 + 2] };
                float[] v3 = new float[] { vertices[tris[j + 2] * 3], vertices[tris[j + 2] * 3 + 1],
                        vertices[tris[j + 2] * 3 + 2] };
                Optional<Float> t = intersectSegmentTriangle(src, dst, v1, v2, v3);
                if (t.isPresent()) {
                    if (t.get() < tmin) {
                        tmin = t.get();
                    }
                    hit = true;
                }
            }
        }
        return hit ? Optional.of(tmin) : Optional.empty();
    }

    float rayStartY = 20;
    float rayEndY = -20;

    public boolean isPointOnMesh(float x, float z) {

        float[] src = new float[]{x,rayStartY,z};
        float[] dst = new float[]{x,rayEndY,z};

        // Prune hit ray.
        Optional<float[]> btminmax = isectSegAABB(src, dst, bmin, bmax);
        if (!btminmax.isPresent()) {
            return false;
        }
        float btmin = btminmax.get()[0];
        float btmax = btminmax.get()[1];
        float[] p = new float[2], q = new float[2];
        p[0] = src[0] + (dst[0] - src[0]) * btmin;
        p[1] = src[2] + (dst[2] - src[2]) * btmin;
        q[0] = src[0] + (dst[0] - src[0]) * btmax;
        q[1] = src[2] + (dst[2] - src[2]) * btmax;

        List<ChunkyTriMesh.ChunkyTriMeshNode> chunks = chunkyTriMesh.getChunksOverlappingSegment(p, q);
        if (chunks.isEmpty()) {
            return false;
        }

        float tmin = 1.0f;
        boolean hit = false;
        for (ChunkyTriMesh.ChunkyTriMeshNode chunk : chunks) {
            int[] tris = chunk.tris;
            for (int j = 0; j < chunk.tris.length; j += 3) {

                float[] min = new float[2];
                float[] max = new float[2];

                float[] v1 = new float[] { vertices[tris[j] * 3], vertices[tris[j] * 3 + 1],
                        vertices[tris[j] * 3 + 2] };

                min[0] = v1[0];
                min[1] = v1[2];
                max[0] = v1[0];
                max[1] = v1[2];
                float[] v2 = new float[] { vertices[tris[j + 1] * 3], vertices[tris[j + 1] * 3 + 1],
                        vertices[tris[j + 1] * 3 + 2] };

                min[0] = Math.min(min[0],v2[0]);
                min[1] = Math.min(min[1],v2[2]);
                max[0] = Math.max(max[0],v2[0]);
                max[1] = Math.max(max[1],v2[2]);

                float[] v3 = new float[] { vertices[tris[j + 2] * 3], vertices[tris[j + 2] * 3 + 1],
                        vertices[tris[j + 2] * 3 + 2] };

                min[0] = Math.min(min[0],v3[0]);
                min[1] = Math.min(min[1],v3[2]);
                max[0] = Math.max(max[0],v3[0]);
                max[1] = Math.max(max[1],v3[2]);

                if(x >= min[0] && x <= max[0] && z >= min[1] && z <= max[1]){
                    Optional<Float> t = intersectSegmentTriangle(src, dst, v1, v2, v3);
                    if (t.isPresent()) {
                        if (t.get() < tmin) {
                            tmin = t.get();
                        }
                        hit = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public float getPositionYOnMesh(float x, float z) {

        float[] src = new float[]{x,rayStartY,z};
        float[] dst = new float[]{x,rayEndY,z};

        // Prune hit ray.
        Optional<float[]> btminmax = isectSegAABB(src, dst, bmin, bmax);
        if (!btminmax.isPresent()) {
            return DetourHandler.invalidPos;
        }
        float btmin = btminmax.get()[0];
        float btmax = btminmax.get()[1];
        float[] p = new float[2], q = new float[2];
        p[0] = src[0] + (dst[0] - src[0]) * btmin;
        p[1] = src[2] + (dst[2] - src[2]) * btmin;
        q[0] = src[0] + (dst[0] - src[0]) * btmax;
        q[1] = src[2] + (dst[2] - src[2]) * btmax;

        List<ChunkyTriMesh.ChunkyTriMeshNode> chunks = chunkyTriMesh.getChunksOverlappingSegment(p, q);
        if (chunks.isEmpty()) {
            return DetourHandler.invalidPos;
        }

        float tmin = 1.0f;
        boolean hit = false;
        for (ChunkyTriMesh.ChunkyTriMeshNode chunk : chunks) {
            int[] tris = chunk.tris;
            for (int j = 0; j < chunk.tris.length; j += 3) {
                float[] v1 = new float[] { vertices[tris[j] * 3], vertices[tris[j] * 3 + 1],
                        vertices[tris[j] * 3 + 2] };
                float[] v2 = new float[] { vertices[tris[j + 1] * 3], vertices[tris[j + 1] * 3 + 1],
                        vertices[tris[j + 1] * 3 + 2] };
                float[] v3 = new float[] { vertices[tris[j + 2] * 3], vertices[tris[j + 2] * 3 + 1],
                        vertices[tris[j + 2] * 3 + 2] };
                Optional<Float> t = intersectSegmentTriangle(src, dst, v1, v2, v3);
                if (t.isPresent()) {
                    if (t.get() < tmin) {
                        tmin = t.get();
                    }
                    hit = true;
                    return rayStartY+(rayEndY-rayStartY)*tmin;
                }
            }
        }
        return DetourHandler.invalidPos;
    }



    private Optional<float[]> isectSegAABB(float[] sp, float[] sq, float[] amin, float[] amax) {

        float EPS = 1e-6f;

        float[] d = new float[3];
        d[0] = sq[0] - sp[0];
        d[1] = sq[1] - sp[1];
        d[2] = sq[2] - sp[2];
        float tmin = 0.0f;
        float tmax = 1.0f;

        for (int i = 0; i < 3; i++) {
            if (Math.abs(d[i]) < EPS) {
                if (sp[i] < amin[i] || sp[i] > amax[i]) {
                    return Optional.empty();
                }
            } else {
                float ood = 1.0f / d[i];
                float t1 = (amin[i] - sp[i]) * ood;
                float t2 = (amax[i] - sp[i]) * ood;
                if (t1 > t2) {
                    float tmp = t1;
                    t1 = t2;
                    t2 = tmp;
                }
                if (t1 > tmin) {
                    tmin = t1;
                }
                if (t2 < tmax) {
                    tmax = t2;
                }
                if (tmin > tmax) {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(new float[] { tmin, tmax });
    }

    Optional<Float> intersectSegmentTriangle(float[] sp, float[] sq, float[] a, float[] b, float[] c) {
        float v, w;
        float[] ab = DetourCommon.vSub(b, a);
        float[] ac = DetourCommon.vSub(c, a);
        float[] qp = DetourCommon.vSub(sp, sq);

        // Compute triangle normal. Can be precalculated or cached if
        // intersecting multiple segments against the same triangle
        float[] norm = DemoMath.vCross(ab, ac);

        // Compute denominator d. If d <= 0, segment is parallel to or points
        // away from triangle, so exit early
        float d = DemoMath.vDot(qp, norm);
        if (d <= 0.0f) {
            return Optional.empty();
        }

        // Compute intersection t value of pq with plane of triangle. A ray
        // intersects iff 0 <= t. Segment intersects iff 0 <= t <= 1. Delay
        // dividing by d until intersection has been found to pierce triangle
        float[] ap = DetourCommon.vSub(sp, a);
        float t = DemoMath.vDot(ap, norm);
        if (t < 0.0f) {
            return Optional.empty();
        }
        if (t > d) {
            return Optional.empty(); // For segment; exclude this code line for a ray test
        }

        // Compute barycentric coordinate components and test if within bounds
        float[] e = DemoMath.vCross(qp, ap);
        v = DemoMath.vDot(ac, e);
        if (v < 0.0f || v > d) {
            return Optional.empty();
        }
        w = -DemoMath.vDot(ab, e);
        if (w < 0.0f || v + w > d) {
            return Optional.empty();
        }

        // Segment/ray intersects triangle. Perform delayed division
        t /= d;

        return Optional.of(t);
    }

    public void addConvexVolume(float[] verts, float minh, float maxh, AreaModification areaMod) {
        ConvexVolume volume = new ConvexVolume();
        volume.verts = verts;
        volume.hmin = minh;
        volume.hmax = maxh;
        volume.areaMod = areaMod;
        convexVolumes.add(volume);
    }

}
