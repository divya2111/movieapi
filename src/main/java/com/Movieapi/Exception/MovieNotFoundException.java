package com.Movieapi.Exception;

public class MovieNotFoundException extends RuntimeException{
	public MovieNotFoundException(String message) {
		super(message);
	}

}
