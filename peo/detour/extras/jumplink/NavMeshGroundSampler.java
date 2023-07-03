package detour.extras.jumplink;

import org.recast4j.detour.*;
import org.recast4j.recast.RecastBuilder.RecastBuilderResult;

import java.util.List;

class NavMeshGroundSampler extends AbstractGroundSampler {

    private final QueryFilter filter = new NoOpFilter();

    private static class NoOpFilter implements QueryFilter {

        @Override
        public boolean passFilter(long ref, MeshTile tile, Poly poly) {
            return true;
        }

        @Override
        public float getCost(float[] pa, float[] pb, long prevRef, MeshTile prevTile, Poly prevPoly, long curRef,
                MeshTile curTile, Poly curPoly, long nextRef, MeshTile nextTile, Poly nextPoly) {
            return 0;
        }

    }

    @Override
    public void sample(JumpLinkBuilderConfig acfg, RecastBuilderResult result, EdgeSampler es) {
        NavMeshQuery navMeshQuery = createNavMesh(result, acfg.agentRadius, acfg.agentHeight, acfg.agentClimb);
        sampleGround(acfg, es, (pt, h) -> getNavMeshHeight(navMeshQuery, pt, acfg.cellSize, h));
    }

    private NavMeshQuery createNavMesh(RecastBuilderResult r, float agentRadius, float agentHeight, float agentClimb) {
        NavMeshDataCreateParams params = new NavMeshDataCreateParams();
        params.verts = r.getMesh().verts;
        params.vertCount = r.getMesh().nverts;
        params.polys = r.getMesh().polys;
        params.polyAreas = r.getMesh().areas;
        params.polyFlags = r.getMesh().flags;
        params.polyCount = r.getMesh().npolys;
        params.nvp = r.getMesh().nvp;
        params.detailMeshes = r.getMeshDetail().meshes;
        params.detailVerts = r.getMeshDetail().verts;
        params.detailVertsCount = r.getMeshDetail().nverts;
        params.detailTris = r.getMeshDetail().tris;
        params.detailTriCount = r.getMeshDetail().ntris;
        params.walkableRadius = agentRadius;
        params.walkableHeight = agentHeight;
        params.walkableClimb = agentClimb;
        params.bmin = r.getMesh().bmin;
        params.bmax = r.getMesh().bmax;
        params.cs = r.getMesh().cs;
        params.ch = r.getMesh().ch;
        params.buildBvTree = true;
        return new NavMeshQuery(new NavMesh(NavMeshBuilder.createNavMeshData(params), params.nvp, 0));
    }

    private Tupple2<Boolean, Float> getNavMeshHeight(NavMeshQuery navMeshQuery, float[] pt, float cs,
            float heightRange) {
        float[] halfExtents = new float[] { cs, heightRange, cs };
        Result<List<Long>> result = navMeshQuery.queryPolygons(pt, halfExtents, filter);
        float minHeight = pt[1];
        float maxHeight = pt[1] + heightRange;
        boolean found = false;
        if (result.succeeded()) {
            for (long ref : result.result) {
                Result<Float> h = navMeshQuery.getPolyHeight(ref, pt);
                if (h.succeeded()) {
                    float y = h.result;
                    if (y > minHeight && y < maxHeight) {
                        minHeight = y;
                        found = true;
                    }
                }
            }
        }
        if (found) {
            return new Tupple2<>(true, minHeight);
        }
        return new Tupple2<>(false, pt[1]);
    }

}
