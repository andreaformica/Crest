package hep.crest.data.config;

import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.property.IntrospectorPropertyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to map from an object to a builder who follows Builder Pattern.
 *
 * <pre>
 * MapperFactory factory = new DefaultMapperFactory.Builder()
 *  .propertyResolverStrategy(new BuilderPropertyResolver())
 *  .build();
 *  factory.registerObjectFactory((Object o, MappingContext mappingContext)
 *  -&gt; new MyBuilder(), TypeFactory.valueOf(MyBuilder.class));
 *  </pre>
 * <p>
 * Inspired from https://www.isaacnote.com/2017/06/lombok-orika-builder-pattern.html
 */
public class BuilderPropertyResolver extends IntrospectorPropertyResolver {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(BuilderPropertyResolver.class);

    @Override
    protected void collectProperties(Class<?> type, Type<?> referenceType, Map<String, Property> properties) {
        super.collectProperties(type, referenceType, properties);
        if (properties.size() > 1) {
            // If properties contains more items than the "class" element, then most likely it will have found the
            // getters and setters, skip the rest of this code
            return;
        }
        log.debug("Calling collectProperties with args: type={} referenceType={}", type, referenceType);
        log.debug("properties filled by super is: {}", properties);
        Stream<Field> fieldNames = Arrays.stream(type.getDeclaredFields());
        fieldNames.map(field -> accessorMethods(type, field))
                .filter(Accessors::isValid)
                .forEach(acc -> {
                    String name = acc.field.getName();
                    Property.Builder builder = new Property.Builder();
                    builder.expression(name);
                    builder.name(name);
                    builder.setter(name + "(%s)");
                    builder.getter(name + "()");
                    builder.type(this.resolvePropertyType(acc.getter, acc.field.getType(), type, referenceType));
                    Property property = builder.build(this);
                    log.debug("Putting property: {}", property);
                    properties.put(name, property);
                });
    }

    /**
     * Accessor static class.
     */
    private static class Accessors {
        /**
         * the field.
         */
        private Field field;
        /**
         * the getter.
         */
        private Method getter;
        /**
         * the setter.
         */
        private Method setter;

        /**
         *
         * @param field
         * @param getter
         * @param setter
         */
        Accessors(Field field, Method getter, Method setter) {
            this.field = field;
            this.getter = getter;
            this.setter = setter;
        }

        /**
         *
         * @return Field
         */
        public Field getField() {
            return field;
        }

        /**
         *
         * @return Method
         */
        public Method getGetter() {
            return getter;
        }

        /**
         *
         * @return Method
         */
        public Method getSetter() {
            return setter;
        }

        /**
         *
         * @return boolean
         */
        public boolean isValid() {
            return field != null && getter != null && setter != null;
        }
    }

    /**
     * @param type
     * @param field
     * @return Accessors
     */
    private Accessors accessorMethods(Class<?> type, Field field) {
        return new Accessors(field, getterMethod(type, field), setterMethod(type, field));
    }

    /**
     *
     * @param type
     * @param field
     * @return Method
     */
    private Method getterMethod(Class<?> type, Field field) {
        List<Method> getters = Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.getName().equals(field.getName()))
                .filter(method -> method.getParameterTypes().length == 0)
                .filter(method -> method.getReturnType() == field.getType())
                .collect(Collectors.toList());
        if (getters.size() == 1) {
            return getters.get(0);
        }
        return null;
    }

    /**
     *
     * @param type
     * @param field
     * @return Method
     */
    private Method setterMethod(Class<?> type, Field field) {
        List<Method> setters = Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.getName().equals(field.getName()))
                .filter(method -> method.getParameterTypes().length == 1)
                .filter(method -> method.getParameterTypes()[0] == field.getType())
                .filter(method -> method.getReturnType() == type)
                .collect(Collectors.toList());
        if (setters.size() == 1) {
            return setters.get(0);
        }
        return null;
    }
}
