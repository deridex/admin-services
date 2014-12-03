package cc.newmercy.contentservices.validation.collection;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Defines supplemental constraints on collections that {@link javax.validation.Valid Valid} cannot impose.
 */
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupplementalCollectionConstraintsValidator.class)
@Documented
public @interface SupplementalCollectionConstraints {
    String message() default "{cc.newmercy.contentservices.validation.collection.SupplementalCollectionConstraints.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Returns a {@link ElementConstraint} that provides the constraints that will be applied to each collection
     * element. For example:
     *
     * <pre>
     * public class RequiredEMail extends ElementConstraint&lt;String&gt; {
     *     &#064;NonNull
     *     &#064;EMail
     *     &#064;Override
     *     public String getElement() {
     *         return super.getElement();
     *     }
     * }
     * </pre>
     *
     * And then in your class:
     *
     * <pre>
     * public class BusinessClass {
     *     &#064;{@link SupplementalCollectionConstraints}(RequiredEMail.class)
     *     List&lt;String&gt; eMails;
     * }
     * </pre>
     */
    Class<? extends ElementConstraint<?>> value();

    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        SupplementalCollectionConstraints[] value();
    }
}
