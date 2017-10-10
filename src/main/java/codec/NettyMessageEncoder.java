package codec;

import entity.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

public class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    private MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage message, ByteBuf sendBuf) throws Exception {

        if (message == null || message.getHeader() == null) {
            throw new Exception("The encode message is null");
        }

        //写入数据，按照NettyMessage的格式
        sendBuf.writeInt((message.getHeader().getCrcCode()));
        sendBuf.writeInt((message.getHeader().getLength()));
        sendBuf.writeLong((message.getHeader().getSessionId()));
        sendBuf.writeByte((message.getHeader().getType()));
        sendBuf.writeByte((message.getHeader().getPriority()));
        sendBuf.writeInt((message.getHeader().getAttachment().size()));

        // for each 写入attachment
        message.getHeader().getAttachment().forEach((name,value)->{
            try {
                byte[] nameArray = name.getBytes("UTF-8");
                sendBuf.writeInt(nameArray.length);
                sendBuf.writeBytes(nameArray);
                marshallingEncoder.encode(value, sendBuf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // netty message body part
        if (message.getBody() != null) {
            marshallingEncoder.encode(message.getBody(), sendBuf);
        }else {
            sendBuf.writeInt(0);
        }

        sendBuf.setInt(4, sendBuf.readableBytes() - 8); // 覆盖第四位指向的字节总长度
    }
}
