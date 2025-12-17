package org.nazarius.VovkORM.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.nazarius.VovkORM.enums.GenerationType;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    GenerationType strategy() default GenerationType.AUTO;
}
