package org.ex.boys.concurrency.nio.ch3;

import lombok.extern.slf4j.Slf4j;
import org.ex.boys.concurrency.common.IOUtil;
import org.ex.boys.concurrency.nio.NioConfig;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * socket channel client
 * 上传文件到服务端
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/1 0:10
 */
@Slf4j
public class NioSocketClient {
    private static final Charset charset = StandardCharsets.UTF_8;

    public void sendFile() {
        String sourcePath = NioConfig.SOCKET_SEND_FILE;
        String srcPath = IOUtil.getResourcePath(sourcePath);
        log.info("{}", srcPath);

    }
}
