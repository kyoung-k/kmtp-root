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
                .GET("/member/{id}", memberHandler::getMember)
                .POST("/member", memberHandler::postMember)
                .PUT("/member/{id}", memberHandler::putMember)
                .DELETE("/member/{id}", memberHandler::deleteMember)
                .build();
    }
}
