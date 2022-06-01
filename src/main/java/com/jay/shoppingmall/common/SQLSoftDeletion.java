package com.jay.shoppingmall.common;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target( {TYPE} )
@Retention( RUNTIME )
//왜 동작하지 않는지 알아볼것.
@Inherited
@Where(clause = "is_deleted = 0")
@SQLDelete(sql = "UPDATE cart SET is_deleted = 1, deleted_now = NOW() WHERE id = ?")
public @interface SQLSoftDeletion {
}
