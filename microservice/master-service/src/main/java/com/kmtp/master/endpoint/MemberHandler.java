package com.kmtp.master.endpoint;

import com.kmtp.master.persistence.MemberEntity;
import com.kmtp.master.persistence.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class MemberHandler {

    private MemberRepository memberRepository;

    @Autowired
    public MemberHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Mono<ServerResponse> getMember(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(memberRepository.findById(1l), MemberEntity.class);
    }
}
