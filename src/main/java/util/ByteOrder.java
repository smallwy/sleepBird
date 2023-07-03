/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package util;


import java.io.Serializable;

/**
 * A typesafe enumeration for byte orders.
 *
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */

public final class ByteOrder implements Serializable {

    private String name;

    public ByteOrder(String name) {
        this.name = name;
    }

    /**
     * Constant denoting big-endian byte order.  In this order, the bytes of a
     * multibyte value are ordered from most significant to least significant.
     */
    public static final ByteOrder BIG_ENDIAN
        = new ByteOrder("BIG_ENDIAN");

    /**
     * Constant denoting little-endian byte order.  In this order, the bytes of
     * a multibyte value are ordered from least significant to most
     * significant.
     */
    public static final ByteOrder LITTLE_ENDIAN
        = new ByteOrder("LITTLE_ENDIAN");

    /**
     * Constructs a string describing this object.
     *
     * <p> This method returns the string <tt>"BIG_ENDIAN"</tt> for {@link
     * #BIG_ENDIAN} and <tt>"LITTLE_ENDIAN"</tt> for {@link #LITTLE_ENDIAN}.
     * </p>
     *
     * @return  The specified string
     */
    public String toString() {
        return name;
    }

}
