package ru.atoroschin;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import ru.atoroschin.auth.AuthService;
import ru.atoroschin.auth.BaseAuthService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerStorage {
    private static final int SERVER_PORT = 8189;
    private static final int PART_SIZE = 10 * 1024 * 1024;
    private final Logger logger = Logger.getLogger("");
    private static final String LOG_PROP = "logserver" + File.separator + "logging.properties";
    private final int port;
    private final AuthService authService;

    public ServerStorage(int port, AuthService authService) {
        this.port = port;
        this.authService = authService;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final int lengthFieldMes = 4;
        final int countAdjustment = -5;

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, PART_SIZE,
                                            1, lengthFieldMes, countAdjustment, 0, true))
                                    .addLast(new AuthHandler(authService))
                                    .addLast(new InboundHandler(authService));
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            logger.info("Server started");
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            LogManager manager = LogManager.getLogManager();
            manager.readConfiguration(new FileInputStream(LOG_PROP));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = SERVER_PORT;
        }
        new ServerStorage(port, new BaseAuthService()).run();
    }

}
