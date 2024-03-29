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
package detour.tilecache.io;

import org.recast4j.detour.io.DetourWriter;
import org.recast4j.detour.tilecache.TileCacheLayerHeader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

public class TileCacheLayerHeaderWriter extends DetourWriter {

    public void write(OutputStream stream, TileCacheLayerHeader header, ByteOrder order, boolean cCompatibility)
            throws IOException {
        write(stream, header.magic, order);
        write(stream, header.version, order);
        write(stream, header.tx, order);
        write(stream, header.ty, order);
        write(stream, header.tlayer, order);
        for (int j = 0; j < 3; j++) {
            write(stream, header.bmin[j], order);
        }
        for (int j = 0; j < 3; j++) {
            write(stream, header.bmax[j], order);
        }
        write(stream, (short) header.hmin, order);
        write(stream, (short) header.hmax, order);
        stream.write(header.width);
        stream.write(header.height);
        stream.write(header.minx);
        stream.write(header.maxx);
        stream.write(header.miny);
        stream.write(header.maxy);
        if (cCompatibility) {
            write(stream, (short) 0, order); // C struct padding
        }
    }

}
