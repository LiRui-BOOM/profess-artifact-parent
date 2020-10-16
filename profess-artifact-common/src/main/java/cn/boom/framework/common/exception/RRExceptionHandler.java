/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package cn.boom.framework.common.exception;


import cn.boom.framework.common.response.R;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理器
 */
@RestControllerAdvice
public class RRExceptionHandler {

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RRException.class)
	public R handleRRException(RRException e){
		R r = new R();
		r.put("code", e.getCode());
		r.put("msg", e.getMessage());
		return r;
	}

	/**
	 * 处理校验失败
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public R handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		Map<String,Object> errorMap = new HashMap<String,Object>();

		BindingResult bindingResult = e.getBindingResult();
		bindingResult.getFieldErrors().forEach((item)->{
			errorMap.put(item.getField(), item.getDefaultMessage());
		});

		return R.error(ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getCode(), ExceptionCodeEnum.ILLEGAL_ARGS_EXCEPTION.getMsg()).put("data", errorMap);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public R handlerNoFoundException(Exception e) {
		return R.error(404, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public R HttpMessageNotReadableException(Exception e) {
		return R.error(404, "Json格式错误");
	}

	@ExceptionHandler(IllegalStateException.class)
	public R IllegalStateException(Exception e) {
		return R.error(404, "状态非法，请检查参数是否完整！");
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public R HttpRequestMethodNotSupportedException(Exception e) {
		return R.error(404, "http请求类型错误！");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public R AccessDeniedException(AccessDeniedException e){
		return R.error(ExceptionCodeEnum.ILLEGAL_ACCESS_EXCEPTION.getCode(), "权限不足，不允许访问！");
	}

	@ExceptionHandler(Exception.class)
	public R handleException(Exception e){
		return R.error();
	}
}
