package com.Movieapi.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Movieapi.Dto.MovieDto;
import com.Movieapi.Dto.MoviePageResponse;
import com.Movieapi.Exception.EmptyFileException;
import com.Movieapi.Service.MovieService;
import com.Movieapi.utils.AppConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("api/v1/movie")
public class MovieController {
	
	
	private final MovieService movieService;
	
	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/add-movie")
	public ResponseEntity<MovieDto> addMovieHandler(@RequestPart("posterfile") MultipartFile posterfile,
			                                        @RequestPart("videofile") MultipartFile videofile,
			                                        @RequestPart("movieDto")String movieDto) throws IOException, EmptyFileException{
		if(posterfile.isEmpty()) {
			throw new EmptyFileException("File is empty! Please upload file");
		}
		if(videofile.isEmpty()) {
			throw new EmptyFileException("File is empty! Please upload file");
		}
		MovieDto dto = convertToMovieDto(movieDto);
		return new ResponseEntity<>(movieService.addMovie(dto,posterfile, videofile),HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{movieId}")
	public ResponseEntity<MovieDto> getMovieHandler(@PathVariable("movieId") Integer movieId){
		return ResponseEntity.ok(movieService.getMovie(movieId));
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<MovieDto>> getAllMoviesHandle(){
		return ResponseEntity.ok(movieService.getAllMovies());
	}
	
	@PutMapping("/update/{movieId}")
	public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable("movieId") Integer movieId,
			                                           @RequestPart("posterfile") MultipartFile posterfile,
			                                           @RequestPart("videofile") MultipartFile videofile,
			                                           @RequestPart("movieDtoObj") String movieDtoObj) throws IOException{
		if(posterfile.isEmpty()) posterfile =null;
		if(videofile.isEmpty()) videofile =null;
		MovieDto movieDto = convertToMovieDto(movieDtoObj);
		return ResponseEntity.ok(movieService.updateMovie(movieId,movieDto,posterfile,videofile));
	}
	
	@DeleteMapping("/delete/{movieId}")
	public ResponseEntity<String> deleteMovieHandler(@PathVariable("movieId") Integer movieId) throws IOException{
		return ResponseEntity.ok(movieService.deleteMovie(movieId));
		
	}
	
	@GetMapping("/allMoviepage")
	public ResponseEntity<MoviePageResponse> getMoviesWithpagination(
			 @RequestParam(value = "PageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false)Integer PageNumber,
			 @RequestParam(value = "PageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer PageSize
			 ){
		return ResponseEntity.ok(movieService.getAllMovieWithPagination(PageNumber,PageSize));
		
	}
	
	@GetMapping("/allMoviePageSort")
	public ResponseEntity<MoviePageResponse> getMoviesWithpaginationAndSorting(
			 @RequestParam(value = "PageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false)Integer PageNumber,
			 @RequestParam(value = "PageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer PageSize,
			 @RequestParam(value = "SortBy",defaultValue = AppConstants.SORT_BY,required = false) String SortBy,
			 @RequestParam(value = "SortDir",defaultValue = AppConstants.SORT_DIR,required = false) String SortDir
			 ){
		return ResponseEntity.ok(movieService.getAllMovieWithPaginationAndSorting(PageNumber,PageSize,SortBy,SortDir));
		
	}
	
	private MovieDto convertToMovieDto(String movieDtoObj) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		 return objectMapper.readValue(movieDtoObj, MovieDto.class);
	}
	
	

}
