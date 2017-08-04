/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.agent.socks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandType;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialResponse;
import io.netty.handler.codec.socksx.v5.DefaultSocks5PasswordAuthResponse;
import io.netty.handler.codec.socksx.v5.Socks5AuthMethod;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandType;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequest;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequest;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthStatus;
import ls.demon.netkit.util.SocksServerUtils;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: AgentSocksHandler.java, v 0.1 2017年8月3日 上午9:29:18 song.li@witontek.com Exp $
 */
@ChannelHandler.Sharable
public class AgentSocksHandler extends SimpleChannelInboundHandler<SocksMessage> {

    /**
    * Logger for this class
    */
    private static final Logger           logger   = LoggerFactory
        .getLogger(AgentSocksHandler.class);

    public static final AgentSocksHandler INSTANCE = new AgentSocksHandler();

    private AgentSocksHandler() {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             SocksMessage socksRequest) throws Exception {
        logger.debug("{} channelRead0 = {}", ctx.channel(), socksRequest);
        switch (socksRequest.version()) {
            case SOCKS4a:
                logger.debug("s4:{}", socksRequest);
                Socks4CommandRequest socksV4CmdRequest = (Socks4CommandRequest) socksRequest;
                if (socksV4CmdRequest.type() == Socks4CommandType.CONNECT) {
                    ctx.pipeline().addLast(new AgentSocksConnectHandler());
                    ctx.pipeline().remove(this);
                    ctx.fireChannelRead(socksRequest);
                } else {
                    ctx.close();
                }
                break;
            case SOCKS5:
                logger.debug("s5:{}", socksRequest);
                if (socksRequest instanceof Socks5InitialRequest) {
                    logger.debug("s5:init {}", ctx.pipeline().hashCode());
                    // 
                    //                    PipeUtils.showAll("init-1", ctx.pipeline());

                    // auth support example
                    //ctx.pipeline().addFirst(new Socks5PasswordAuthRequestDecoder());
                    //ctx.write(new DefaultSocks5AuthMethodResponse(Socks5AuthMethod.PASSWORD));

                    ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                    ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));

                    // 
                    //                    PipeUtils.showAll("init-2", ctx.pipeline());
                } else if (socksRequest instanceof Socks5PasswordAuthRequest) {
                    logger.debug("s5:auth {}", ctx.pipeline().hashCode());
                    // 
                    //                    PipeUtils.showAll("fffff", ctx.pipeline());
                    ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                    ctx.write(
                        new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS));

                    // 
                    //                    PipeUtils.showAll("fff", ctx.pipeline());
                } else if (socksRequest instanceof Socks5CommandRequest) {
                    logger.debug("s5:cmd {}", ctx.pipeline().hashCode());
                    // 
                    //                    PipeUtils.showAll("s5:cmd-0", ctx.pipeline());
                    Socks5CommandRequest socks5CmdRequest = (Socks5CommandRequest) socksRequest;
                    if (socks5CmdRequest.type() == Socks5CommandType.CONNECT) {
                        ctx.pipeline().addLast(new AgentSocksConnectHandler());
                        ctx.pipeline().remove(this);
                        // 
                        //                        PipeUtils.showAll("s5:cmd-1", ctx.pipeline());

                        ctx.fireChannelRead(socksRequest);

                        // 
                        //                        PipeUtils.showAll("s5:cmd-2", ctx.pipeline());
                    } else {
                        ctx.close();
                    }
                } else {
                    ctx.close();
                }
                break;
            case UNKNOWN:
                ctx.close();
                break;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.debug("xx-channelReadComplete {}", ctx.channel());
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        //        throwable.printStackTrace();
        logger.error("连接异常 {}", ctx.channel());
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
