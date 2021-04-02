package ru.atoroschin.commands;

import ru.atoroschin.BufWorker;
import ru.atoroschin.FileLoaded;
import ru.atoroschin.FileWorker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

public class CommandCD implements Command {
    @Override
    public void send(ChannelHandlerContext ctx, String content, FileWorker fileWorker, byte signal) {
        fileWorker.sendCommandWithStringList(ctx, List.of(content), signal);
    }

    @Override
    public void response(ChannelHandlerContext ctx, ByteBuf buf, FileWorker fileWorker, Map<Integer,
            FileLoaded> uploadedFiles, byte signal) {
        String newDir = BufWorker.readFileListFromBuf(buf).get(0);
        fileWorker.changeCurrentDir(newDir);
        String serverDir = fileWorker.getServerPathOnServer();
        fileWorker.sendCommandWithStringList(ctx, List.of(serverDir), signal);
    }

    @Override
    public void receive(ChannelHandlerContext ctx, ByteBuf buf, FileWorker fileWorker, Map<Integer,
            FileLoaded> uploadedFiles) {
        String serverDir = BufWorker.readFileListFromBuf(buf).get(0);
        fileWorker.setServerPath(serverDir);
    }
}
