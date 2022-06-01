package com.jay.shoppingmall.common;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) //이 애노테이션을 붙일 수 있는 곳 : PARAMETER
@Retention(RetentionPolicy.RUNTIME) // 언제까지 이 애노테이션 정보를 유지 : RUNTIME
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : user")
public @interface CurrentUser {
}
