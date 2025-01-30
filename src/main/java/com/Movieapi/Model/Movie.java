package com.Movieapi.Model;

import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
public class Movie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer movieId;
	@Column(nullable = false, length = 200)
	@NotBlank(message = "Please Provide Movie Name!")
	private String title;
	@Column(nullable = false, length = 200)
	@NotBlank(message = "Please Provide Movie Director!")
	private String director;
	@Column(nullable = false)
	@NotBlank(message = "Please Provide Movie Studio!")
	private String studio;
	@ElementCollection
	@CollectionTable(name = "movie_cast")
	private Set<String> movieCast;
	@Column(nullable = false)
	@NotNull(message = "Please Provide Movie Release Year!")
	private Integer releaseYear;
	@Column(nullable = false)
	@NotBlank(message = "Please Provide movie's Poster!")
	private String poster;
	@Column(nullable = false)
	@NotBlank(message = "Please Provide movie's Video!")
	private String Video;
	
	

}
