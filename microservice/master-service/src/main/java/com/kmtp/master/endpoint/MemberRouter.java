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

import com.kmtp.master.service.MemberHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MemberRouter {

    final private MemberHandler memberHandler;

    @Autowired
    public MemberRouter(MemberHandler memberHandler) {
        this.memberHandler = memberHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> memberRoutes() {
        return RouterFunctions.route()
                .GET("/member/{id}", memberHandler::get)
                .POST("/member", memberHandler::post)
                .PUT("/member/{id}", memberHandler::put)
                .DELETE("/member/{id}", memberHandler::delete)
                .build();
    }
}
