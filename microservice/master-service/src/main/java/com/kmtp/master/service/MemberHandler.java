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

import com.kmtp.common.api.Member;
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.master.persistence.MemberEntity;
import com.kmtp.master.persistence.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    /**
     * 회원 정보를 조회합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 회원 정보를 DB에서 조회합니다.<br> 회원 정보가 없을경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br> 응답 객체로 변환합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link Member}></{@link>
     */
    public Mono<ServerResponse> get(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong( request.pathVariable("id") );

        // (2)
        final Mono<Member> memberMono = memberRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found member-id."))
                .map(MemberMapper.INSTANCE::entityToApi);

        return ResponseHandler.ok(memberMono);

    }

    /**
     * 회원 정보를 등록합니다.
     * <p></p>
     * (1) request body 정보를 변환한뒤 유효성 체크를 진행합니다.<br> Entity 객체로 변환하고 회원 정보를 등록합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#CREATED}
     */
    public Mono<ServerResponse> post(ServerRequest request) {

        // (1)
        final Mono<MemberEntity> memberEntityMono = request.bodyToMono(Member.class)
                .doOnNext(member -> genericValidator.validate(member, Member.class))
                .map(MemberMapper.INSTANCE::apiToEntity)
                .flatMap(memberRepository::save);

        return ResponseHandler.created(memberEntityMono, URI.create(request.path()));
    }

    /**
     * 회원 정보를 수정합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) request body 정보를 변환한뒤 유효성 체크를 진행합니다.<br>
     * (3) 회원 정보를 DB에서 조회합니다.<br> 회원 정보가 없을경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * (4) request 정보와 DB 정보를 {@link Mono#zip} 하여 수정할 정보를 설정하고 DB에 저장합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> put(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong(request.pathVariable("id"));

        // (2)
        final Mono<Member> memberMono = request.bodyToMono(Member.class)
                .doOnNext(member -> genericValidator.validate(member, Member.class));

        // (3)
        final Mono<MemberEntity> memberEntityMono = memberRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item-id"));

        // (4)
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

    /**
     * 회원 정보를 삭제합니다.
     * <p></p>
     * (1) id값을 path variable에서 조회합니다.<br>
     * (2) 회원 정보를 DB에서 조회합니다.<br> 회원 정보가 없을경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br> 조회된 회원 정보를 삭제합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#NO_CONTENT}
     */
    public Mono<ServerResponse> delete(ServerRequest request) {

        // (1)
        final Long id = Long.parseLong( request.pathVariable("id") );

        // (2)
        final Mono<Void> deleteMemberMono = memberRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found member-id."))
                .then(memberRepository.deleteById(id));

        return ResponseHandler.noContent(deleteMemberMono);
    }
}
