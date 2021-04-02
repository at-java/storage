package ru.atoroschin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandlerNetty extends ChannelInboundHandlerAdapter {
    private Map<Integer, FileLoaded> uploadedFiles;
    private FileWorker fileWorker;
    private boolean active;
    private final Logger logger = Logger.getLogger(ClientHandlerNetty.class.getName());
    private static final String LOCAL_DIR = "dirclient";

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws IOException {
        uploadedFiles = new HashMap<>();
        fileWorker = new FileWorker(LOCAL_DIR, "", -1);
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        active = true;
        consoleRead(channelHandlerContext);
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        if (buf.readableBytes() > 0) {
            byte firstByte = buf.readByte();
            Commands command = Commands.getCommand(firstByte);
            try {
                command.receive(channelHandlerContext, buf, fileWorker, uploadedFiles);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Не удалось выполнить команду", e);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        active = false;
        logger.log(Level.WARNING, "Разорвано соединение", cause);
        channelHandlerContext.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        active = false;
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        Commands command = Commands.EXIT;
        command.sendToServer(ctx, "", fileWorker);
    }

    public void consoleRead(ChannelHandlerContext ctx) {
        Thread threadConsole = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (active) {
                String readLine;
                System.out.println("local: " + fileWorker.getCurrentDir() + ">");
                System.out.print("server: home" + File.separator + fileWorker.getServerPathOnClient() + ">");
                if (scanner.hasNext()) {
                    readLine = scanner.nextLine();
                    String[] commandPart = readLine.split("\\s", 2);
                    Commands command;
                    try {
                        command = Commands.valueOf(commandPart[0].toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException ex) {
                        command = Commands.UNKNOWN;
                    }
                    String commandParam = commandPart.length > 1 ? commandPart[1] : commandPart[0];

                    command.sendToServer(ctx, commandParam, fileWorker);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadConsole.setDaemon(true);
        threadConsole.start();
    }

}
