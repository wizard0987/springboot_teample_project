package com.webservice.bookstore.web.controller;

import com.webservice.bookstore.service.CartService;
import com.webservice.bookstore.web.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /*
    장바구니 목록 조회 요청 핸들러
    */
    @GetMapping(value = "/cart/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CartDto>> getCartItemList() {

        // 세션에 저장된 로그인 계정 정보를 통해 장바구니 목록 조회 예정
        List<CartDto> cartList = cartService.findByMemberId((long) 1);

        return new ResponseEntity<>(cartList, HttpStatus.OK);
    }
}