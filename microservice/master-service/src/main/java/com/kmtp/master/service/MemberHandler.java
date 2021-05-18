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
package com.kmtp.master.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseErrorHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.master.endpoint.Master;
import com.kmtp.master.endpoint.Member;
import com.kmtp.master.persistence.MasterEntity;
import com.kmtp.master.persistence.MemberEntity;
import com.kmtp.master.persistence.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class MemberHandler {

    final private MemberRepository memberRepository;
    final private GenericValidator genericValidator;

    @Autowired
    public MemberHandler(MemberRepository memberRepository, GenericValidator genericValidator) {
        this.memberRepository = memberRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> get(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        final Mono<Member> memberMono = memberRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found member-id."))
                .map(MemberMapper.INSTANCE::entityToApi);

        return ResponseHandler.ok(memberMono);

    }

    public Mono<ServerResponse> post(ServerRequest request) {

        final Mono<MemberEntity> memberEntityMono = request.bodyToMono(Member.class)
                .doOnNext(member -> genericValidator.validate(member, Member.class))
                .map(MemberMapper.INSTANCE::apiToEntity)
                .flatMap(memberRepository::save);

        return ResponseHandler.created(memberEntityMono, URI.create(request.path()));
    }

    public Mono<ServerResponse> put(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));
        final Mono<Member> memberMono = request.bodyToMono(Member.class)
                .doOnNext(member -> genericValidator.validate(member, Member.class));

        final Mono<MemberEntity> memberEntityMono = memberRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id"));

        final Mono<MemberEntity> updateMemberMono = Mono.zip(memberMono, memberEntityMono)
                .flatMap(tuple2 -> {

                    tuple2.getT2().change(memberEntity -> {

                        memberEntity.setEmail(tuple2.getT1().getEmail());
                        memberEntity.setName(tuple2.getT1().getName());
                        memberEntity.setAge(tuple2.getT1().getAge());
                        memberEntity.setAddress(tuple2.getT1().getAddress());
                    });

                    return Mono.just(tuple2.getT2())
                            .flatMap(memberRepository::save);
                });

        return ResponseHandler.noContent(updateMemberMono);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong( request.pathVariable("id") );

        final Mono<Void> deleteMemberMono = memberRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found member-id."))
                .then(memberRepository.deleteById(id));

        return ResponseHandler.noContent(deleteMemberMono);
    }
}
