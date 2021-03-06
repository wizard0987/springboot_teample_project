package com.webservice.bookstore.web.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.webservice.bookstore.domain.entity.board.Board;
import com.webservice.bookstore.domain.entity.cart.Cart;
import com.webservice.bookstore.domain.entity.image.Image;
import com.webservice.bookstore.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {

    private Long id;

    private String title;

    private String content;

    private int replyCount; //댓글수

    private String category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifiedDate;

    private Long memberId;

    private String memberEmail;

    private String nickName;

    @Builder.Default
    private List<ImageDTO> imageList = new ArrayList<>();

    public static Board toEntity(BoardDTO boardDTO, Member member) {
        return Board.builder().id(boardDTO.getId())
                .category(boardDTO.getCategory())
                .content(boardDTO.getContent())
                .title(boardDTO.getTitle())
                .member(member)
                .build();
    }

    public static BoardDTO entityToDTO(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .category(board.getCategory())
                .content(board.getContent())
                .title(board.getTitle())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .memberEmail(board.getMember().getEmail())
                .memberId(board.getMember().getId())
                .nickName(board.getMember().getNickName())
                .build();
    }

    public static BoardDTO entityToDTO(Board board, List<Image> imageList) {
        return BoardDTO.builder()
                .id(board.getId())
                .category(board.getCategory())
                .content(board.getContent())
                .title(board.getTitle())
                .createdDate(board.getCreatedDate())
                .modifiedDate(board.getModifiedDate())
                .imageList(imageList.stream().map(
                        image -> {
                            return ImageDTO.builder().path(image.getPath())
                                    .fileName(image.getFileName())
                                    .uuid(image.getUuid())
                                    .build(); }).collect(Collectors.toList()))
                .build();
    }
}
