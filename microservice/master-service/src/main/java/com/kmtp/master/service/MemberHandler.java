package com.kmtp.master.service;

import com.kmtp.master.persistence.MemberEntity;
import com.kmtp.master.persistence.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class MemberHandler {

    final private MemberRepository memberRepository;

    @Autowired
    public MemberHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Mono<ServerResponse> getMember(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(memberRepository.findById(id), MemberEntity.class);
    }

    public Mono<ServerResponse> postMember(ServerRequest request) {

        return request.bodyToMono(Member.class)
                .map(MemberMapper.INSTANCE::apiToEntity)
                .flatMap(memberRepository::save)
                .flatMap(result -> ServerResponse
                        .created(URI.create(request.path()))
                        .build());
    }

    public Mono<ServerResponse> putMember(ServerRequest request) {

        return request.bodyToMono(Member.class)
                .log()
                .flatMap(memberRepository::updateMember);
    }

    public Mono<ServerResponse> deleteMember(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(memberRepository.deleteById(id)));
    }
}
