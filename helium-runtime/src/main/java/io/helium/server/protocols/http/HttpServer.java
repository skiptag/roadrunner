/*
 * Copyright 2012 The Helium Project
 *
 * The Helium Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.helium.server.protocols.http;

import io.helium.server.distributor.Endpoints;
import io.helium.server.protocols.websocket.WebsocketEndpoint;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class HttpServer extends Verticle {

    private int port;
    private String basePath;
    private String host;

    @Override
    public void start() {
        JsonObject httpConfig = container.config().getObject("http", new JsonObject());
        this.port = httpConfig.getInteger("port", 8080);
        this.basePath = httpConfig.getString("basepath", "http://localhost:8080/");
        this.host = httpConfig.getString("servername", "localhost");

        vertx.createHttpServer()
                .requestHandler(new RestHandler(vertx, basePath))
                .websocketHandler(socket -> {
                    WebsocketEndpoint endpoint = new WebsocketEndpoint(basePath, socket, vertx);
                    Endpoints.get().addEndpoint(endpoint);
                    /*socket.endHandler(() -> {
                        // TODO handle Close
                    });*/
                })
                .listen(port);

        System.out.println("Helium http server started");
        System.out.println("Open your browser and navigate to http://localhost:" + port + '/');
    }
}