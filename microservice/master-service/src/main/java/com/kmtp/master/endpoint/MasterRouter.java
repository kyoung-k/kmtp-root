/*
 * Copyright (c) 2021-Present KYoung.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kmtp.master.endpoint;

import com.kmtp.master.service.MasterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MasterRouter {

    final private MasterHandler masterHandler;

    @Autowired
    public MasterRouter(MasterHandler masterHandler) {
        this.masterHandler = masterHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> masterRoutes() {
        return RouterFunctions.route()
                .GET("/master/{id}", masterHandler::get)
                .POST("/master", masterHandler::post)
                .PUT("/master", masterHandler::put)
                .DELETE("/master/{id}", masterHandler::delete)
                .build();
    }
}
