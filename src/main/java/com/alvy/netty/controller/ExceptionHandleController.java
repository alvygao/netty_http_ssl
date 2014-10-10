package com.alvy.netty.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.alvy.netty.server.ErrorResponse;

@ControllerAdvice
public class ExceptionHandleController {
	private static Logger logger = LoggerFactory
			.getLogger(ExceptionHandleController.class);

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody
	ErrorResponse handleGlobalException(HttpServletRequest request, Exception ex) {
		logger.error(ex.getMessage());
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorMessage(ex.getMessage());
		errorResponse.setCode(400);
		return errorResponse;
	}
}
