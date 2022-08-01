package netty.buffer;

import java.nio.IntBuffer;

public class BufferDay01 {
    public static void main(String[] args) {

        IntBuffer intBuffer = IntBuffer.allocate(20);
        intBuffer.put(1);
        intBuffer.put(2);
        intBuffer.put(3);
        intBuffer.put(4);
        intBuffer.put(5);
        System.out.println("position----" + intBuffer.position());
        System.out.println("limit----" + intBuffer.limit());
        System.out.println("capacity----" + intBuffer.capacity());

        intBuffer.flip();
        System.out.println("position----" + intBuffer.position());
        System.out.println("limit----" + intBuffer.limit());
        System.out.println("capacity----" + intBuffer.capacity());


    }
}
