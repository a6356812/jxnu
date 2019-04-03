package org.jxnu.stu.common;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Set;

@Component
public class ValidationImpl implements InitializingBean {

    private Validator validator;

    @Override
    public void afterPropertiesSet() throws Exception {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public ValidationResult validate(Object bean){
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<Object>> set = validator.validate(bean);
        if(set.size() > 0){
            result.setHasError(true);
            Iterator<ConstraintViolation<Object>> iterator = set.iterator();
            while (iterator.hasNext()){
                ConstraintViolation<Object> constraintViolation = iterator.next();
                String property = constraintViolation.getPropertyPath().toString();
                String message = constraintViolation.getMessage();
                result.getErrMsgMap().put(property,message);
            }
        }
        return  result;
    }
}
