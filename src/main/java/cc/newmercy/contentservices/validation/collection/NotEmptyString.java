package cc.newmercy.contentservices.validation.collection;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Constraints collection elements to be non-empty strings.
 *
 * @see org.hibernate.validator.constraints.NotEmpty
 */
public class NotEmptyString extends ElementConstraint<String> {
    @NotEmpty
    @Override
    public String getElement() {
        return super.getElement();
    }
}
