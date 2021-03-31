package com.webservice.bookstore.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webservice.bookstore.domain.entity.member.AuthProvider;
import com.webservice.bookstore.domain.entity.member.Member;
import com.webservice.bookstore.domain.entity.member.MemberRole;
import lombok.*;

public class MemberDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Default {
        private Long id;
        private String email;
        private String nickName;
        private String password;
        private String address;
        private String role;
        private String provider;
        private boolean enabled;

        // Entity -> DTO
        public static MemberDto.Default of(Member member) {
            return MemberDto.Default.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickName(member.getEmail())
                    .password(member.getPassword())
                    .address(member.getAddress())
                    .role(String.valueOf(member.getRole()))
                    .provider(String.valueOf(member.getProvider()))
                    .enabled(member.getEnabled())
                    .build();
        }

        // DTO -> Entity
        public Member toEntity() {
            return Member.builder()
                         .id(this.id)
                         .email(this.email)
                         .nickName(this.nickName)
                         .password(this.password)
                         .address(this.address)
                         .role(MemberRole.valueOf(this.role))
                         .provider(AuthProvider.valueOf(this.provider))
                         .enabled(this.enabled)
                         .build();
        }

    }

    @Getter @Setter
    @Builder
    public static class MyInfoRequest {
        private String email;
        private String nickName;
        private String address;
        private String provider;
    }

}
