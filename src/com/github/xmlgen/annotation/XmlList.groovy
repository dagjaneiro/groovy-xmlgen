package com.github.xmlgen.annotation

/**
 * Created by daj on 21/07/2015.
 */
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


@Retention(RetentionPolicy.RUNTIME)
@interface XmlList {
    String value() default "[name]";
    String item()  default "item"
}
