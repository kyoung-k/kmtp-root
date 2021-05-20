package com.kmtp.reservation.service;

import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.RequestHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.endpoint.Discount;
import com.kmtp.reservation.endpoint.Goods;
import com.kmtp.reservation.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.Not;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class GoodsHandler {

    private GoodsRepository goodsRepository;
    private DiscountRepository discountRepository;
    private GenericValidator genericValidator;

    @Autowired
    public GoodsHandler(GoodsRepository goodsRepository
            , DiscountRepository discountRepository, GenericValidator genericValidator) {

        this.goodsRepository = goodsRepository;
        this.discountRepository = discountRepository;
        this.genericValidator = genericValidator;
    }

    public Mono<ServerResponse> list(ServerRequest request) {

        final Mono<Long> masterId = request.queryParam("masterId")
                .map(Long::parseLong)
                .map(Mono::just)
                .orElseGet(Mono::empty);

        final Mono<List<Goods>> listMono = masterId
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "query param master-id is required."))
                .flatMapMany(goodsRepository::findByMasterId)
                .map(GoodsMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(listMono);
    }

    public Mono<ServerResponse> get(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Goods> goodsMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods-id."))
                .map(GoodsMapper.INSTANCE::entityToApi);

        return ResponseHandler.ok(goodsMono);
    }

    public Mono<ServerResponse> post(ServerRequest request) {

        final Mono<DiscountEntity> goodsEntityMono = request.bodyToMono(Goods.class)
                .doOnNext(goods -> genericValidator.validate(goods, Goods.class))
                .doOnNext(goods -> genericValidator.validate(goods.getDiscount(), Discount.class))
                .flatMap(goods -> {

                    final Mono<Goods> goodsMono = Mono.just(goods);
                    final Mono<Discount> discountMono = Mono.just(goods.getDiscount());

                    return Mono.zip(goodsMono, discountMono);
                })
                .flatMap(tuple2 -> goodsRepository.save(GoodsMapper.INSTANCE.apiToEntity(tuple2.getT1()))
                        .flatMap(goodsEntity -> {

                            tuple2.getT2().setMasterId(goodsEntity.getMasterId());
                            tuple2.getT2().setGoodsId(goodsEntity.getId());

                            return discountRepository.save(DiscountMapper.INSTANCE.apiToEntity(tuple2.getT2()));
                        }));

        return ResponseHandler.created(goodsEntityMono, URI.create(request.path()));
    }

    public Mono<ServerResponse> put(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Goods> goodsMono = request.bodyToMono(Goods.class)
                .doOnNext(goods -> genericValidator.validate(goods, Goods.class));

        final Mono<GoodsEntity> goodsEntityMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods-id."));

        final Mono<GoodsEntity> updateGoodsMono = Mono.zip(goodsMono, goodsEntityMono)
                .doOnNext(tuple2 -> tuple2.getT2()
                        .change(goodsEntity -> goodsEntity.setName(tuple2.getT1().getName())))
                .flatMap(tuple2 -> goodsRepository.save(tuple2.getT2()));

        return ResponseHandler.noContent(updateGoodsMono);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {

        final Long id = Long.parseLong(request.pathVariable("id"));

        final Mono<Void> goodsMono = goodsRepository.findById(id)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods-id."))
                .then(goodsRepository.deleteById(id));

        return ResponseHandler.noContent(goodsMono);
    }
}
