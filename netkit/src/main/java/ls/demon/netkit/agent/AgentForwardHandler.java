/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.agent;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v4.Socks4ServerDecoder;
import io.netty.handler.codec.socksx.v4.Socks4ServerEncoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import ls.demon.netkit.agent.socks.AgentSocksHandler;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: AgentForwardHandler.java, v 0.1 2017年8月2日 下午2:26:33 song.li@witontek.com Exp $
 */
public class AgentForwardHandler extends ByteToMessageDecoder {
    /**
    * Logger for this class
    */
    private static final Logger       logger = LoggerFactory.getLogger(AgentForwardHandler.class);

    private final Socks5ServerEncoder socks5encoder;

    /**
     * 
     */
    public AgentForwardHandler() {
        this(Socks5ServerEncoder.DEFAULT);
    }

    /**
     * @param socks5encoder
     */
    public AgentForwardHandler(Socks5ServerEncoder socks5encoder) {
        if (socks5encoder == null) {
            throw new NullPointerException("socks5encoder");
        }
        this.socks5encoder = socks5encoder;
    }

    /** 
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception {
        logger.debug("{} {}", ctx, in);
        final int readerIndex = in.readerIndex();
        if (in.writerIndex() == readerIndex) {
            return;
        }

        ChannelPipeline p = ctx.pipeline();
        final byte versionVal = in.getByte(readerIndex);
        SocksVersion version = SocksVersion.valueOf(versionVal);

        switch (version) {
            case SOCKS4a:
                logKnownVersion(ctx, version);
                p.addAfter(ctx.name(), null, AgentSocksHandler.INSTANCE);
                p.addAfter(ctx.name(), null, Socks4ServerEncoder.INSTANCE);
                p.addAfter(ctx.name(), null, new Socks4ServerDecoder());
                p.remove(this);
                return;
            case SOCKS5:
                p.addAfter(ctx.name(), null, AgentSocksHandler.INSTANCE);
                logKnownVersion(ctx, version);
                p.addAfter(ctx.name(), null, socks5encoder);
                p.addAfter(ctx.name(), null, new Socks5InitialRequestDecoder());
                p.remove(this);
                return;
            default:
                logUnknownVersion(ctx, versionVal);

                switch (versionVal) {
                    case 'C':
                    case 'c':
                    case 'P':
                    case 'p':

                        p.remove(this);
                        return;
                    default:
                        in.skipBytes(in.readableBytes());
                        ctx.close();
                        return;
                }
        }
    }

    private static void logKnownVersion(ChannelHandlerContext ctx, SocksVersion version) {
        logger.debug("{} Protocol version: {}({})", ctx.channel(), version);
    }

    private static void logUnknownVersion(ChannelHandlerContext ctx, byte versionVal) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} Unknown protocol version: {}", ctx.channel(), versionVal & 0xFF);
        }
    }
}
