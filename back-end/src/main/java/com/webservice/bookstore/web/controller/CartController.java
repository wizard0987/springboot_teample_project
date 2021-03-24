package com.webservice.bookstore.web.controller;

import com.webservice.bookstore.config.security.auth.CustomUserDetails;
import com.webservice.bookstore.domain.entity.cart.CartLinkResource;
import com.webservice.bookstore.domain.entity.member.Member;
import com.webservice.bookstore.exception.UnauthorizedException;
import com.webservice.bookstore.service.CartService;
import com.webservice.bookstore.web.dto.CartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Log4j2
@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000/"})
public class CartController {

    private final CartService cartService;

    /*
    장바구니 목록 조회 요청 핸들러
    */
    @GetMapping(value = "/cart/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getCartItemList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        // 세션에 저장된 로그인 계정 정보를 통해 장바구니 목록 조회 예정
        List<CartDto> cartList = null;
        try {
            cartList = cartService.findByMemberId(customUserDetails.getMember().getId());
        } catch (NullPointerException e) {
            // CustomUserDetails 객체가 null인 경우는 jwt 토큰으로 인증을 거치지 않았다는 의미
            throw new UnauthorizedException("인증 오류가 발생했습니다.", e.getCause());
        }

        List<CartLinkResource> emList = cartList.stream()
                .map(cartDto -> new CartLinkResource(cartDto,
                        linkTo(methodOn(ItemController.class).getItem(cartDto.getItem_id())).withRel("itemDetail")))
                .collect(Collectors.toList());

        CollectionModel<CartLinkResource> collectionModel = CollectionModel.of(emList);

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    /*
    장바구니 아이템 추가 요청 핸들러
    */
    @PostMapping(value = "/cart/{item_id}")
    public ResponseEntity<CartDto> addCartItem(@PathVariable("item_id") Long item_id,
                                               @RequestBody CartDto cartDto,
                                               @AuthenticationPrincipal CustomUserDetails customUserDetails) {
//        try {
            cartDto.setMember_id(customUserDetails.getMember().getId());
//        } catch (NullPointerException e) {
//            // CustomUserDetails 객체가 null인 경우는 jwt 토큰으로 인증을 거치지 않았다는 의미
//            throw new AuthenticationException("인증 오류가 발생했습니다.", e.getCause()) {};
//        }
        cartDto.setItem_id(item_id);

        CartDto resCartDto = null;
        try {
            resCartDto = cartService.addCartEntity(cartDto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(resCartDto, HttpStatus.OK);
    }

    /*
    장바구니 아이템 수량 업데이트 요청 핸들러
    */
    @PatchMapping(value = "/cart/{cart_id}/")
    public ResponseEntity updateCartItem(@PathVariable("cart_id") Long cart_id,
                                         @RequestBody CartDto cartDto) {

        cartService.updateQuantity(cart_id, cartDto.getQuantity());

        return new ResponseEntity("success", HttpStatus.OK);
    }

    /*
    장바구니 아이템 삭제 요청 핸들러
    */
    @DeleteMapping(value = "/cart/{cart_id}/")
    public ResponseEntity  deleteCartItem(@PathVariable("cart_id") Long cart_id) {

        cartService.deleteCartItem(cart_id);

        return new ResponseEntity("success", HttpStatus.OK);
    }
}
