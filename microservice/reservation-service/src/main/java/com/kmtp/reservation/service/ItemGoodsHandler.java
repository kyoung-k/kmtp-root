package com.kmtp.reservation.service;

import com.kmtp.common.api.ItemGoods;
import com.kmtp.common.generic.GenericError;
import com.kmtp.common.generic.GenericValidator;
import com.kmtp.common.http.RequestHandler;
import com.kmtp.common.http.ResponseHandler;
import com.kmtp.reservation.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class ItemGoodsHandler {

    private ItemRepository itemRepository;
    private GoodsRepository goodsRepository;
    private ItemGoodsRepository itemGoodsRepository;
    private GenericValidator genericValidator;

    @Autowired
    public ItemGoodsHandler(ItemRepository itemRepository
            , GoodsRepository goodsRepository
            , ItemGoodsRepository itemGoodsRepository
            , GenericValidator genericValidator) {

        this.itemRepository = itemRepository;
        this.goodsRepository = goodsRepository;
        this.itemGoodsRepository = itemGoodsRepository;
        this.genericValidator = genericValidator;
    }

    /**
     * 아이템 기준의 아이템-상품 관계 목록을 조회합니다.
     * <p></p>
     * (1) item-id값을 path variable에서 조회합니다.<br>
     * (2) 아이템-상품 관계 목록을 조회하고 응답형태로 변환합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link ItemGoods}>></{@link>
     */
    public Mono<ServerResponse> itemList(ServerRequest request) {

        // (1)
        final Long itemId = Long.parseLong(request.pathVariable("itemId"));

        // (2)
        final Mono<List<ItemGoods>> listMono = itemGoodsRepository.findByItemId(itemId)
                .map(ItemGoodsMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(listMono);
    }

    /**
     * 상품 기준의 아이템-상품 관계 목록을 조회합니다.
     * <p></p>
     * (1) goods-id값을 path variable에서 조회합니다.<br>
     * (2) 아이템-상품 관계 목록을 조회하고 응답형태로 변환합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link Mono}<{@link List}<{@link ItemGoods}>></{@link>
     */
    public Mono<ServerResponse> goodsList(ServerRequest request) {

        // (1)
        final Long goodsId = Long.parseLong(request.pathVariable("goodsId"));

        // (2)
        final Mono<List<ItemGoods>> listMono = itemGoodsRepository.findByGoodsId(goodsId)
                .map(ItemGoodsMapper.INSTANCE::entityToApi)
                .collectList();

        return ResponseHandler.ok(listMono);
    }

    /**
     * 상품-할인율 관계 목록을 등록 합니다.
     * <p></p>
     * (1) goods-id값을 path variable에서 조회합니다.<br>
     * (2) 상품-할인율 관계 정보를 삭제합니다.<br>
     * request body 정보를 변환한뒤 유효성 체크를 진행합니다.<br>
     * 목록 형태를 {@link Flux}로 변환합니다.<br>
     * 아이템 정보를 조회하고 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * 상품 정보를 조회하고 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * 아이템 정보, 상품정보를 {@link Mono#zip}한뒤 저장 정보를 설정하고 DB에 저장합니다.
     * <p></p>
     * @param request {@link ServerRequest}
     * @return {@link HttpStatus#CREATED}
     */
    public Mono<ServerResponse> post(ServerRequest request) {

        // (1)
        final Long goodsId = Long.parseLong(request.pathVariable("goodsId"));

        // (2)
        final Mono<List<ItemGoodsEntity>> listMono = itemGoodsRepository.deleteByGoodsId(goodsId)
                .flatMap(delete -> RequestHandler.jsonBodyToList(request, ItemGoods[].class)
                        .doOnNext(itemGoods -> genericValidator.validateList(itemGoods, ItemGoods.class))
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(itemGoods -> {

                            final Mono<ItemEntity> itemEntityMono = itemRepository.findById(itemGoods.getItemId())
                                    .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found item."));

                            final Mono<GoodsEntity> goodsEntityMono = goodsRepository.findById(itemGoods.getGoodsId())
                                    .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found goods."));

                            return Mono.zip(itemEntityMono, goodsEntityMono);
                        })
                        .flatMap(tuple2 -> {

                            final ItemGoodsEntity itemGoodsEntity = ItemGoodsEntity.builder()
                                    .itemId(tuple2.getT1().getId())
                                    .goodsId(tuple2.getT2().getId())
                                    .build();

                            return itemGoodsRepository.save(itemGoodsEntity);
                        }).collectList());

        return ResponseHandler.created(listMono, URI.create(request.path()));
    }

    /**
     * 상품-할인율 관계 목록을 삭제 합니다.
     * <p></p>
     * (1) goods-id값을 path variable에서 조회합니다.<br>
     * (2) 상품-할인율 관계 목록을 조회하고 조회하지 못할 경우 {@link HttpStatus#NOT_FOUND} 상태로 응답합니다.<br>
     * 상품-할인율 관계 목록을 삭제 합니다.
     * <p></p>
     * @param request
     * @return
     */
    public Mono<ServerResponse> delete(ServerRequest request) {

        // (1)
        final Long goodsId = Long.parseLong(request.pathVariable("goodsId"));

        // (2)
        final Mono<Void> voidMono = itemGoodsRepository.findByGoodsId(goodsId)
                .switchIfEmpty(GenericError.of(HttpStatus.NOT_FOUND, "not found itemgoods."))
                .then(itemGoodsRepository.deleteByGoodsId(goodsId));

        return ResponseHandler.noContent(voidMono);
    }
}
