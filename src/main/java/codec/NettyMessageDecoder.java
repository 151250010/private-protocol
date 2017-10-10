package codec;

import entity.Header;
import entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.HashMap;
import java.util.Map;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private MarshallingDecoder decoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws Exception {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        decoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext handlerContext, ByteBuf byteBuf) throws Exception {

        ByteBuf frame = (ByteBuf) super.decode(handlerContext, byteBuf);
        if (frame == null) {
            return null;
        }

        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionId(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        int size = frame.readInt();
        if (size > 0) {
            Map<String, Object> attachment = new HashMap<>(size);

            int keySize;
            byte[] byteArray;
            String key;
            for (int i = 0; i < size; i++) {
                keySize = frame.readInt();
                byteArray = new byte[keySize];
                frame.readBytes(byteArray);
                key = new String(byteArray, "UTF-8");
                attachment.put(key, decoder.decode(frame));
            }

            header.setAttachment(attachment);
        }

        if (frame.readableBytes() > 4) {
            // if body is not null ,then at least has an int replying the size of the body
            nettyMessage.setBody(decoder.decode(frame));
        }

        nettyMessage.setHeader(header);
        return nettyMessage;
    }
}
