package com.Movieapi.Dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

	private Integer movieId;
	
	@NotBlank(message = "Please Provide Movie Name!")
	private String title;
	
	@NotBlank(message = "Please Provide Movie Director!")
	private String director;
	
	@NotBlank(message = "Please Provide Movie Studio!")
	private String studio;
	
	private Set<String> movieCast;
	
	
	private Integer releaseYear;
	
	@NotBlank(message = "Please Provide movie's Poster!")
	private String poster;
	
	@NotBlank(message = "Please Provide Poster's URL!")
	private String posterUrl;
	
	@NotBlank(message = "Please Provide movie's Video!")
	private String video;
	
	@NotBlank(message = "Please Provide Video's URL!")
	private String videoUrl;
	
}
