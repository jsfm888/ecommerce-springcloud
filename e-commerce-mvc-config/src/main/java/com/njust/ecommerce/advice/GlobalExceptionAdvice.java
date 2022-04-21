package com.njust.ecommerce.advice;

import com.njust.ecommerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {


    @ExceptionHandler(value = Exception.class)
    public CommonResponse<String> handleCommerceException(HttpServletRequest request,
                                                          Exception ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "bussiness error");
        response.setData(ex.getMessage());

        log.error("commerce service has error: [{}]", ex.getMessage(), ex);
        return response;
    }
}
