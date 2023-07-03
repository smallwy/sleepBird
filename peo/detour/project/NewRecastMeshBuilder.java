package detour.project;

import org.recast4j.detour.NavMesh;
import org.recast4j.detour.NavMeshParams;
import org.recast4j.detour.tilecache.*;
import org.recast4j.detour.tilecache.io.compress.TileCacheCompressorFactory;
import org.recast4j.recast.*;
import org.recast4j.recast.RecastConstants.PartitionType;
import org.recast4j.recast.geom.InputGeomProvider;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static org.recast4j.detour.DetourCommon.vCopy;
import static org.recast4j.recast.RecastVectors.copy;

public class NewRecastMeshBuilder extends AbstractTileLayersBuilder {
    private final static float m_cellSize = 0.3f;
    private final static float m_cellHeight = 0.2f;
    private final static float m_agentHeight = 2.0f;
    private final static float m_agentRadius = 0f;
    private final static float m_agentMaxClimb = 0.6f;
    private final static float m_agentMaxSlope = 60.0f;
    private final static int m_regionMinSize = 8;
    private final static int m_regionMergeSize = 20;
    private final static float m_edgeMaxLen = 12.0f;
    private final static float m_edgeMaxError = 1.3f;
    private final static int m_vertsPerPoly = 6;
    private final static float m_detailSampleDist = 6.0f;
    private final static float m_detailSampleMaxError = 1.0f;

    private static final int EXPECTED_LAYERS_PER_TILE = 4;
    private static final int m_tileSize = 48;

    private final RecastConfig rcConfig;
    private final InputGeomProvider geom;
    private final int tw;
    private final int th;

    private TileCache tileCache;

    public NewRecastMeshBuilder(InputGeomProvider m_geom) throws IOException {
//        this(m_geom, PartitionType.WATERSHED,
//                m_cellSize, m_cellHeight, m_agentHeight, m_agentRadius, m_agentMaxClimb, m_agentMaxSlope,
//                m_regionMinSize, m_regionMergeSize, m_edgeMaxLen, m_edgeMaxError, m_vertsPerPoly, m_detailSampleDist,
//                m_detailSampleMaxError);
        geom = m_geom;
        rcConfig = new RecastConfig(PartitionType.WATERSHED, m_cellSize, m_cellHeight, m_agentHeight, m_agentRadius,
                m_agentMaxClimb, m_agentMaxSlope, m_regionMinSize, m_regionMergeSize, m_edgeMaxLen, m_edgeMaxError,
                m_vertsPerPoly, m_detailSampleDist, m_detailSampleMaxError, m_tileSize,
                SampleAreaModifications.SAMPLE_AREAMOD_GROUND);
        float[] bmin = m_geom.getMeshBoundsMin();
        float[] bmax = m_geom.getMeshBoundsMax();
        int[] twh = Recast.calcTileCount(bmin, bmax, m_cellSize, m_tileSize);
        tw = twh[0];
        th = twh[1];

        createTileCache(geom,ByteOrder.LITTLE_ENDIAN,true);

    }

    @Override
    public List<byte[]> build(int tx, int ty, ByteOrder order, boolean cCompatibility) {
        HeightfieldLayerSet lset = getHeightfieldSet(tx, ty);
        List<byte[]> result = new ArrayList<>();
        if (lset != null) {
            TileCacheBuilder builder = new TileCacheBuilder();
            for (int i = 0; i < lset.layers.length; ++i) {
                HeightfieldLayerSet.HeightfieldLayer layer = lset.layers[i];

                // Store header
                TileCacheLayerHeader header = new TileCacheLayerHeader();
                header.magic = TileCacheLayerHeader.DT_TILECACHE_MAGIC;
                header.version = TileCacheLayerHeader.DT_TILECACHE_VERSION;

                // Tile layer location in the navmesh.
                header.tx = tx;
                header.ty = ty;
                header.tlayer = i;
                DetourCommon.vCopy(header.bmin, layer.bmin);
                DetourCommon.vCopy(header.bmax, layer.bmax);

                // Tile info.
                header.width = layer.width;
                header.height = layer.height;
                header.minx = layer.minx;
                header.maxx = layer.maxx;
                header.miny = layer.miny;
                header.maxy = layer.maxy;
                header.hmin = layer.hmin;
                header.hmax = layer.hmax;
                result.add(builder.compressTileCacheLayer(header, layer.heights, layer.areas, layer.cons, order,
                        cCompatibility));
            }
        }
        return result;
    }

    protected HeightfieldLayerSet getHeightfieldSet(int tx, int ty) {
        RecastBuilder rcBuilder = new RecastBuilder();
        float[] bmin = geom.getMeshBoundsMin();
        float[] bmax = geom.getMeshBoundsMax();
        RecastBuilderConfig cfg = new RecastBuilderConfig(rcConfig, bmin, bmax, tx, ty, true);
        HeightfieldLayerSet lset = rcBuilder.buildLayers(geom, cfg);
        return lset;
    }

    public void createTileCache(InputGeomProvider geom, ByteOrder order, boolean cCompatibility) {
        try{
            TileCacheParams params = new TileCacheParams();
            int[] twh = Recast.calcTileCount(geom.getMeshBoundsMin(), geom.getMeshBoundsMax(), m_cellSize, m_tileSize);
            params.ch = m_cellHeight;
            params.cs = m_cellSize;
            DetourCommon.vCopy(params.orig, geom.getMeshBoundsMin());
            params.height = m_tileSize;
            params.width = m_tileSize;
            params.walkableHeight = m_agentHeight;
            params.walkableRadius = m_agentRadius;
            params.walkableClimb = m_agentMaxClimb;
            params.maxSimplificationError = m_edgeMaxError;
            params.maxTiles = twh[0] * twh[1] * EXPECTED_LAYERS_PER_TILE;
            params.maxObstacles = 128;
            NavMeshParams navMeshParams = new NavMeshParams();
            RecastVectors.copy(navMeshParams.orig, geom.getMeshBoundsMin());
            navMeshParams.tileWidth = m_tileSize * m_cellSize;
            navMeshParams.tileHeight = m_tileSize * m_cellSize;
            navMeshParams.maxTiles = 256;
            navMeshParams.maxPolys = 16384;
            NavMesh navMesh = new NavMesh(navMeshParams, 6);
            tileCache = new TileCache(params, new TileCacheStorageParams(order, cCompatibility), navMesh, TileCacheCompressorFactory.get(cCompatibility), new AbstractTileCache.TestTileCacheMeshProcess());

            List<byte[]> layers = build(ByteOrder.LITTLE_ENDIAN, true, 1,tw,th);
            for(byte[] data : layers){
                long ref = tileCache.addTile(data, 0);
                tileCache.buildNavMeshTile(ref);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public TileCache getTileCache() {

        return tileCache;
    }

}
