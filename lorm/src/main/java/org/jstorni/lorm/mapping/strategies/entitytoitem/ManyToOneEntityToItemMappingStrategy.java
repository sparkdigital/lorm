package org.jstorni.lorm.mapping.strategies.entitytoitem;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.jstorni.lorm.ReflectionSupport;
import org.jstorni.lorm.exceptions.DataValidationException;
import org.jstorni.lorm.schema.AttributeDefinition;
import org.jstorni.lorm.schema.AttributeType;
import org.jstorni.lorm.schema.validation.EntityFieldAsAttribute;

public class ManyToOneEntityToItemMappingStrategy extends
		DefaultEntityToItemMappingStrategy {

	public ManyToOneEntityToItemMappingStrategy(
			ReflectionSupport reflectionSupport) {
		super(reflectionSupport);
	}

	@Override
	protected boolean checkAttribute(Field field, AttributeType attrType) {
		return attrType.equals(AttributeType.STRING);
	}

	@Override
	public boolean apply(Field field) {
		return field.getAnnotation(ManyToOne.class) != null;
	}

	@Override
	protected AttributeDefinition buildAttributeDefinition(Field field) {
		Field refIdField = reflectionSupport.getFieldWithAnnotation(
				field.getType(), Id.class);

		return new AttributeDefinition(field.getName() + "."
				+ refIdField.getName(), AttributeType.STRING, null);
	}

	@Override
	public void map(Object entity, Field field,
			Map<AttributeDefinition, Object> attributes) {
		// TODO it is assumed that the reference was or will be
		// persisted by the specific repository
		Object refValue = reflectionSupport.getValueOfField(field, entity);
		if (Collection.class.isAssignableFrom(field.getType())) {
			throw new DataValidationException(
					"Collection not expected for a @ManyToOne relationship");
		}

		Field refIdField = reflectionSupport.getFieldWithAnnotation(
				field.getType(), Id.class);
		Object value;
		if (refValue != null) {
			value = reflectionSupport.getValueOfField(refIdField, refValue);
		} else {
			value = null;
		}

		AttributeDefinition attrDef = buildAttributeDefinition(field);
		attributes.put(attrDef, value);
	}

	@Override
	public EntityFieldAsAttribute getEntityFieldAsAttribute(Field field) {
		Field refIdField = reflectionSupport.getFieldWithAnnotation(
				field.getType(), Id.class);

		return new EntityFieldAsAttribute(String.class, field.getName() + "."
				+ refIdField.getName());
	}

}