package com.Movieapi.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.Movieapi.Dto.MovieDto;
import com.Movieapi.Dto.MoviePageResponse;


public interface MovieService {
	
	MovieDto addMovie(MovieDto movieDto, MultipartFile posterFile, MultipartFile videoFile) throws IOException;
	
	MovieDto getMovie(Integer movieId);
	
	List<MovieDto> getAllMovies();
	
	MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile posterFile, MultipartFile videoFile)throws IOException;
	
	String deleteMovie(Integer movieId) throws IOException;
	
	MoviePageResponse getAllMovieWithPagination(Integer pageNumber, Integer pagesize);
	
	MoviePageResponse getAllMovieWithPaginationAndSorting(Integer pageNumber, Integer pagesize, String sortBy, String dir);

}

