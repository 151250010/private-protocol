package auth;

import entity.Header;
import entity.MessageType;
import entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Boolean> nodeCheck = new HashMap<>();
    private String[] whiteList = {"127.0.0.1"};

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object message) {

        NettyMessage nettyMessage = (NettyMessage) message;
        if (nettyMessage.getHeader() != null && nettyMessage.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            String nodeIndex = channelHandlerContext.channel().remoteAddress().toString();
            NettyMessage loginResp;
            if (nodeCheck.containsKey(nodeIndex)) {
                loginResp = buildResponse((byte) -1);
            } else {
                InetSocketAddress address = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();
                boolean isOk = false;
                for (String wip : whiteList) {
                    if (wip.equals(ip)) {
                        System.out.println(ip + " IP 通过白名单");
                        isOk = true;
                        break;
                    }
                }
                loginResp = isOk ? buildResponse((byte) 0) : buildResponse((byte) -1);

                if (isOk) {
                    nodeCheck.put(nodeIndex, true);
                }
            }

            System.out.println("The login response is : " + loginResp + " body [" + loginResp.getBody() + "]");
            channelHandlerContext.writeAndFlush(loginResp);
        }else {
            // 不是握手请求
            channelHandlerContext.fireChannelRead(message);
        }
    }

    private NettyMessage buildResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }
}
