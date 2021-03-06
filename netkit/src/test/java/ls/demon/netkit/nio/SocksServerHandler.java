/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ls.demon.netkit.nio;

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
import ls.demon.netkit.util.PipeUtils;

@ChannelHandler.Sharable
public final class SocksServerHandler extends SimpleChannelInboundHandler<SocksMessage> {
    /**
    * Logger for this class
    */
    private static final Logger            logger   = LoggerFactory
        .getLogger(SocksServerHandler.class);

    public static final SocksServerHandler INSTANCE = new SocksServerHandler();

    private SocksServerHandler() {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,
                             SocksMessage socksRequest) throws Exception {
        logger.info("{} channelRead0 = {}", ctx, socksRequest);
        switch (socksRequest.version()) {
            case SOCKS4a:
                logger.info("s4:{}", socksRequest);
                Socks4CommandRequest socksV4CmdRequest = (Socks4CommandRequest) socksRequest;
                if (socksV4CmdRequest.type() == Socks4CommandType.CONNECT) {
                    ctx.pipeline().addLast(new SocksServerConnectHandler());
                    ctx.pipeline().remove(this);
                    ctx.fireChannelRead(socksRequest);
                } else {
                    ctx.close();
                }
                break;
            case SOCKS5:
                logger.info("s5:{}", socksRequest);
                if (socksRequest instanceof Socks5InitialRequest) {
                    logger.info("s5:init {}", ctx.pipeline().hashCode());
                    // 
                    PipeUtils.showAll("init-1", ctx.pipeline());

                    // auth support example
                    //ctx.pipeline().addFirst(new Socks5PasswordAuthRequestDecoder());
                    //ctx.write(new DefaultSocks5AuthMethodResponse(Socks5AuthMethod.PASSWORD));
                    ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                    ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));

                    // 
                    PipeUtils.showAll("init-2", ctx.pipeline());
                } else if (socksRequest instanceof Socks5PasswordAuthRequest) {
                    logger.info("s5:auth {}", ctx.pipeline().hashCode());
                    // 
                    PipeUtils.showAll("fffff", ctx.pipeline());
                    ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
                    ctx.write(
                        new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS));

                    // 
                    PipeUtils.showAll("fff", ctx.pipeline());
                } else if (socksRequest instanceof Socks5CommandRequest) {
                    logger.info("s5:cmd {}", ctx.pipeline().hashCode());
                    // 
                    PipeUtils.showAll("s5:cmd-0", ctx.pipeline());
                    Socks5CommandRequest socks5CmdRequest = (Socks5CommandRequest) socksRequest;
                    if (socks5CmdRequest.type() == Socks5CommandType.CONNECT) {
                        ctx.pipeline().addLast(new SocksServerConnectHandler());
                        ctx.pipeline().remove(this);
                        // 
                        PipeUtils.showAll("s5:cmd-1", ctx.pipeline());

                        ctx.fireChannelRead(socksRequest);

                        // 
                        PipeUtils.showAll("s5:cmd-2", ctx.pipeline());
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
        logger.info("xx-channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        //        throwable.printStackTrace();
        logger.error("", throwable);
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
