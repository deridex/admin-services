package cc.newmercy.contentservices.validation.collection;

/**
 * <p>
 * Target for annotations to apply to collection elements. Sub-class and annotate the <b>getter</b> with constraints.
 * All of the annotations on the getter must be in the default validation group. Use
 * {@link SupplementalCollectionConstraints#groups()} if you need to apply rules  * on a group-basis. Sub-classes must
 * have a default constructor or not declare any constructors. This indirection is necessary because the validation
 * framework does not provide a way to get a {@link javax.validation.ConstraintValidator ConstraintValidator} given an
 * annotation.
 * </p>
 *
 * <p>
 * For example:
 * </p>
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
 */
public abstract class ElementConstraint<ELEMENT> {

    private ELEMENT element;

    public ElementConstraint() { }

    /**
     * Override this in the sub-class and add validation annotations to it.
     */
    public ELEMENT getElement() {
        return element;
    }

    public final void setElement(ELEMENT element) {
        this.element = element;
    }

    @Override
    public final String toString() {
        return element == null ? "null" : element.toString();
    }
}
