package com.github.xmlgen.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by daj on 18/12/2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@interface XmlType {
    String value() default "[name]"
}
