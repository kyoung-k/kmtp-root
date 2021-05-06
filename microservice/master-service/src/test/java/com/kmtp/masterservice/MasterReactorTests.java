package com.kmtp.masterservice;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MasterReactorTests {

    @Test
    void monoTest() {

        Mono<String> mono1 = Mono.just("바나나1");
        Mono<String> mono2 = Mono.just("사과1");

        Mono.zip(mono1, mono2)
                .log()
                .subscribe(tuple2 -> {
                    System.out.println("tuple2 :: " + tuple2.toString());
                    System.out.println("tuple2.getT1() :: " + tuple2.getT1());
                    System.out.println("tuple2.getT2() :: " + tuple2.getT2());

                    System.out.println("tuple2.getT1() :: " + tuple2.get(0));
                    System.out.println("tuple2.getT1() :: " + tuple2.get(1));
                    System.out.println("tuple2.getT1() :: " + tuple2.get(3));
                });
    }
}
