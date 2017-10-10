package codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

public class MarshallingEncoder {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingFactory.buildMarshalling();
    }

    protected void encode(Object message, ByteBuf out) throws Exception {
        try{
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER); // 先写四个空字节
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);

            marshaller.start(output);
            marshaller.writeObject(message);
            marshaller.finish();

            // 减去4 ?
            // ----- lengthPos
            // |
            // |
            // |
            // ------ get 4 bytes to length placeholder
            // |
            // |
            // |
            // ------ current WriterIndex

            // so (out.writerIndex() - lengthPos - 4) means the actual size of the message
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        }finally {
            marshaller.close();
        }
    }
}
