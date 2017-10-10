package auth;

import entity.Header;
import entity.MessageType;
import entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.writeAndFlush(buildLoginRequest());
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) {

        NettyMessage nettyMessage = (NettyMessage) message;

        if (nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) nettyMessage.getBody();
            if (loginResult == (byte) 0) {
                System.out.println("Login is OK: " + nettyMessage);
                channelHandlerContext.fireChannelRead(message);
            } else {
                channelHandlerContext.close();
            }
        } else {
            channelHandlerContext.fireChannelRead(message);
        }
    }

    private NettyMessage buildLoginRequest() {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        nettyMessage.setHeader(header);
        return nettyMessage;
    }
}
