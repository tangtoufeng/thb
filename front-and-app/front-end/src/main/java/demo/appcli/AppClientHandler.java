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
package demo.appcli;

import demo.common.HttpResponseHelper;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles a client-side channel.
 */
@Sharable
public class AppClientHandler extends SimpleChannelInboundHandler<String> {
    private ConcurrentHashMap<String, ChannelHandlerContext> map;

    public AppClientHandler(ConcurrentHashMap<String, ChannelHandlerContext> map) {
        this.map = map;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (msg.contains("$|$")) {
            String[] idAndData = msg.split("\\$\\|\\$");
            String id = idAndData[0];
            String data = idAndData[1];
            ChannelHandlerContext httpCtx = map.remove(id);
            if (httpCtx != null) {
                HttpResponseHelper.responseOK(httpCtx, data);
            }
        }
    }

}
