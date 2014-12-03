package cc.newmercy.contentservices.validation.collection;

import com.google.common.base.Preconditions;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import java.util.Collection;
import java.util.Set;

class ElementConstraintBridge {

    private final ValidatorContext validatorContext;

    private final Class<? extends ElementConstraint<?>> constraint;

    public ElementConstraintBridge(ValidatorContext validatorContext, Class<? extends ElementConstraint<?>> constrainedElement) {
        this.validatorContext = Preconditions.checkNotNull(validatorContext, "validator context");
        this.constraint = Preconditions.checkNotNull(constrainedElement, "constrained element");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    boolean isValid(Collection<?> collection, ConstraintValidatorContext context) {
        int violationCount = 0;

        Validator validator = validatorContext.getValidator();

        ElementConstraint wrapper = newWrapper();

        for (Object element : collection) {
            wrapper.setElement(element);

            Set<ConstraintViolation<ElementConstraint>> elementViolations = validator.validate(wrapper);

            violationCount += elementViolations.size();

            for (ConstraintViolation<ElementConstraint> elementViolation : elementViolations) {
                context.buildConstraintViolationWithTemplate(elementViolation.getMessage()).addConstraintViolation();
            }
        }

        return violationCount == 0;
    }

    private ElementConstraint<?> newWrapper() {
        try {
            return constraint.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("could not instantiate wrapper", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("could not instantiate wrapper", e);
        }
    }
}
