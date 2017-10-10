package heartbeat;

import entity.Header;
import entity.MessageType;
import entity.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * 心跳包发送
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		NettyMessage message = (NettyMessage) msg;
		//握手成功，主动发送心跳消息
		if(message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
			ctx.executor().scheduleAtFixedRate(
					new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
		} else if (message.getHeader() != null && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
			System.out.println("Client receive server heart beat response message : " + message);
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	private class HeartBeatTask implements Runnable{

		private final ChannelHandlerContext ctx;
		
		public HeartBeatTask(final ChannelHandlerContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public void run() {
			NettyMessage heartBeat = buildHeartBeat();
			System.out.println("Client send heart beat message to server : " + heartBeat);
			ctx.writeAndFlush(heartBeat);
		}
		
		private NettyMessage buildHeartBeat(){
			NettyMessage message = new NettyMessage();
			Header header = new Header();
			header.setType(MessageType.HEARTBEAT_REQ.value());
			message.setHeader(header);
			return message;
		}
		
	}
}
