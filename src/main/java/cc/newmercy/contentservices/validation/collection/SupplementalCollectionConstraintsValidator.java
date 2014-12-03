package cc.newmercy.contentservices.validation.collection;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidatorContext;
import java.util.Collection;
import java.util.Map;

public class SupplementalCollectionConstraintsValidator implements ConstraintValidator<SupplementalCollectionConstraints, Object> {

    private final ValidatorContext validatorContext;

    private ElementConstraintBridge bridge;

    @Autowired
    public SupplementalCollectionConstraintsValidator(ValidatorContext validatorContext) {
        this.validatorContext = Preconditions.checkNotNull(validatorContext, "validator context");
    }

    @Override
    public void initialize(SupplementalCollectionConstraints spec) {
        bridge = new ElementConstraintBridge(validatorContext, spec.value());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;

            return bridge.isValid(collection, context);
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;

            return bridge.isValid(map.values(), context);
        }

        throw new IllegalArgumentException(value.getClass().toString());
    }
}
