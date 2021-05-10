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
package com.kmtp.master.persistence;

import com.kmtp.master.endpoint.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<MemberEntity, Long> {

    default Mono<ServerResponse> updateMember(Member member) {

        return this.findById(member.getId())
                .flatMap(entity -> entity.change(me -> {
                    me.setEmail(member.getEmail());
                    me.setName(member.getName());
                    me.setAge(member.getAge());
                    me.setAddress(member.getAddress());
                }).persistenceMono(() -> this.save(entity)));
    }
}
