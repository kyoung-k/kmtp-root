package com.kmtp.master.persistence;

import com.kmtp.master.service.Member;
import com.kmtp.master.service.MemberMapper;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<MemberEntity, Long> {

    default Mono<ServerResponse> updateMember(Member member) {

        return this.findById(member.getId())
                .flatMap(entity -> {

                    entity.setEmail(member.getEmail());
                    entity.setName(member.getName());
                    entity.setAge(member.getAge());
                    entity.setAddress(member.getAddress());

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.save(entity).map(MemberMapper.INSTANCE::entityToApi), Member.class);
                });
    }
}
