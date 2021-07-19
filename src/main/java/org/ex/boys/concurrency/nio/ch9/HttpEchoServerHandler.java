package org.ex.boys.concurrency.nio.ch9;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.util.CharsetUtil;
import org.ex.boys.concurrency.nio.common.util.HttpProtocolHelper;
import org.ex.boys.concurrency.nio.common.util.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.POST;

/**
 * http echo handler
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/18 23:34
 */
public class HttpEchoServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (!request.decoderResult().isSuccess()) {
            HttpProtocolHelper.sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        // 暂存http协议的版本号
        HttpProtocolHelper.cacheHttpProtocol(ctx, request);
        //
        Map<String, Object> echo = new HashMap<>();
        // 1.获取uri
        String uri = request.uri();
        echo.put("Request URI", uri);
        // 2.获取请求方法
        HttpMethod method = request.method();
        echo.put("request method", method.toString());
        // 3.获取请求头
        Map<String, Object> echoHeaders = new HashMap<>();
        HttpHeaders httpHeaders = request.headers();
        for (Map.Entry<String, String> entry : httpHeaders.entries()) {
            echoHeaders.put(entry.getKey(), entry.getValue());
        }
        echo.put("request headers", echoHeaders);
        // 4.请求参数
        Map<String, Object> params = paramsFromUri(request);
        echo.put("request paramsFromUri", params);
        // 5.获取post请求的body内容
        if (POST.equals(request.method())) {
            Map<String, Object> dataParams = dataFromPost(request);
            echo.put("dataFromPost", dataParams);
        }
        // 6.回显内容转换为json串
        String echoJson = JsonUtil.pojoToJson(echo);
        HttpProtocolHelper.sendJsonContent(ctx, echoJson);
    }

    private Map<String, Object> paramsFromUri(FullHttpRequest request) {
        Map<String, Object> params = new HashMap<>();
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> paramList = decoder.parameters();
        for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
            params.put(entry.getKey(), entry.getValue().get(0));
        }
        return params;
    }

    private Map<String, Object> dataFromPost(FullHttpRequest request) {
        Map<String, Object> data = new HashMap<>();
        try {
            String contentType = request.headers().get("Content-Type").trim();
            switch (contentType) {
                case "application/x-www-form-urlencoded":
                case "multipart/form-data":
                    data = formBodyDecode(request);
                    break;
                case "application/json":
                    data = jsonBodyDecode(request);
                    break;
                case "text/plain":
                    ByteBuf content = request.content();
                    byte[] reqContent = new byte[content.readableBytes()];
                    content.readBytes(reqContent);
                    String text = new String(reqContent, StandardCharsets.UTF_8);
                    data.put("text", text);
                    break;
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> formBodyDecode(FullHttpRequest request) {
        Map<String, Object> params = new HashMap<>();

        try {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE), request, CharsetUtil.UTF_8);
            List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
            if (postData == null || postData.isEmpty()) {
                //  decoder = new HttpPostRequestDecoder(request);
                if (request.content().isReadable()) {
                    String json = request.content().toString(StandardCharsets.UTF_8);
                    params.put("body", json);
                }
                return params;
            }

            for (InterfaceHttpData data : postData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MixedAttribute attribute = (MixedAttribute) data;
                    params.put(attribute.getName(), attribute.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    private Map<String, Object> jsonBodyDecode(FullHttpRequest request) {
        Map<String, Object> param = new HashMap<>();

        ByteBuf content = request.content();
        byte[] reqContent = new byte[content.readableBytes()];
        content.readBytes(reqContent);
        String text = new String(reqContent, StandardCharsets.UTF_8);

        JSONObject jsonObject = JsonUtil.jsonToPojo(text, JSONObject.class);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            param.put(entry.getKey(), entry.getValue());
        }
        return param;
    }

}
