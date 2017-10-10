package codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Unmarshaller;

public class MarshallingDecoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws Exception {
        this.unmarshaller = MarshallingFactory.buildUnMarshalling();
    }

    protected Object decode(ByteBuf in) throws Exception {

        // message size
        int objectSize = in.readInt();
        ByteBuf byteBuf = in.slice(in.readerIndex(), objectSize);
        ChannelBufferByteInput input = new ChannelBufferByteInput(byteBuf);
        try{
            unmarshaller.start(input);
            Object object = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + objectSize);
            return object;
        }finally {
            unmarshaller.close();
        }
    }
}
