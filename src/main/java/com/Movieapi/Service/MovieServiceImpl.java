package com.Movieapi.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Movieapi.Dto.MovieDto;
import com.Movieapi.Dto.MoviePageResponse;
import com.Movieapi.Exception.FileExistsException;
import com.Movieapi.Exception.MovieNotFoundException;
import com.Movieapi.Model.Movie;
import com.Movieapi.Repo.Movierepo;

@Service
public class MovieServiceImpl implements MovieService{
	
	@Autowired
	private Movierepo movierepo;
	
	@Autowired
	private FileService fileService;
	
	@Value("${file.upload.dir}")
	private String BASE_PATH;
	
	private static  final String base_Url ="http://localhost:8080";
	
	 public MovieServiceImpl(Movierepo movieRepository, FileService fileService) {
	        this.movierepo = movieRepository;
	        this.fileService = fileService;
	    }

	@Override
	public MovieDto addMovie(MovieDto movieDto, MultipartFile posterFile, MultipartFile videoFile) throws IOException  {
		String uploadedPosterFilename = uploadandValidateFile(posterFile, "posters", "Poster");
		String uploadedVideoFilename = uploadandValidateFile(videoFile, "videos", "Video");

		
		movieDto.setPoster(uploadedPosterFilename);
		movieDto.setVideo(uploadedVideoFilename);
		
		//mapping dto to movie object
		Movie movie = new Movie (
				null,
				movieDto.getTitle(),
				movieDto.getDirector(),
				movieDto.getStudio(),
				movieDto.getMovieCast(),
				movieDto.getReleaseYear(),
				movieDto.getPoster(),
				movieDto.getVideo()
				);
		
		// saving movie object
	      Movie savedMovie = movierepo.save(movie);
				
				//generating the urls
	      String posterUrl = base_Url + "/file/posters/" + uploadedPosterFilename;
	      String videoUrl = base_Url + "/file/videos/" + uploadedVideoFilename;
	      
	    //mapping movie object to DTO object
			MovieDto response = new MovieDto(
					savedMovie.getMovieId(),
					savedMovie.getTitle(),
					savedMovie.getDirector(),
					savedMovie.getStudio(),
					savedMovie.getMovieCast(),
					savedMovie.getReleaseYear(),
					savedMovie.getPoster(),
					posterUrl,
					savedMovie.getVideo(),
					videoUrl
					);
			return response;
	}
	
	@Override
	public MovieDto getMovie(Integer movieId) {
		// checking the movie in Db , exists fetch data 
		Movie movie =movierepo.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie NOT Found with id"+ movieId));
		
		//generating PosterUrl
		String posterUrl = base_Url + "/file/posters/" + movie.getPoster();
	    String videoUrl = base_Url + "/file/videos/" + movie.getVideo();
		
		// mapping to MovieDto object and returning it
		MovieDto response = new MovieDto(
				movie.getMovieId(),
				movie.getTitle(),
				movie.getDirector(),
				movie.getStudio(),
				movie.getMovieCast(),
				movie.getReleaseYear(),
				movie.getPoster(),
				posterUrl,
				movie.getVideo(),
				videoUrl
				);
		return response;
	}
	
	@Override
	public List<MovieDto> getAllMovies() {
		// fetch all data from DB
		List<Movie> movies = movierepo.findAll();
		
		List<MovieDto> movieDtos = new ArrayList<>();
		//iterate through the list
		for(Movie movie : movies) {
			//generating PosterUrl
			String posterUrl = base_Url + "/file/posters/" + movie.getPoster();
		    String videoUrl = base_Url + "/file/videos/" + movie.getVideo();
		     
			MovieDto movieDto = new MovieDto(
					movie.getMovieId(),
					movie.getTitle(),
					movie.getDirector(),
					movie.getStudio(),
					movie.getMovieCast(),
					movie.getReleaseYear(),
					movie.getPoster(),
					posterUrl,
					movie.getVideo(),
					videoUrl
					);
			movieDtos.add(movieDto);
		}
		return movieDtos;
	}
	


	private String uploadandValidateFile(MultipartFile file, String relativePath, String fileType) throws IOException {
	    if (file == null || file.isEmpty()) {
	        throw new IllegalArgumentException(fileType + " file cannot be empty!");
	    }

	    // Resolve the directory path using Paths.get() correctly
	    Path uploadDirectory = Paths.get(BASE_PATH, relativePath).normalize();
	    
	    // Ensure the directory exists
	    if (!Files.exists(uploadDirectory)) {
	        try {
	            Files.createDirectories(uploadDirectory); // Creating the directory if it does not exist
	            System.out.println("Directory created or already exists: " + uploadDirectory.toString()); // Debugging line
	        } catch (IOException e) {
	            throw new IOException("Error creating directory at " + uploadDirectory.toString(), e);
	        }
	    }

	    Path filePath = uploadDirectory.resolve(file.getOriginalFilename()).normalize(); // Correctly resolve file path
	    if (Files.exists(filePath)) {
	        throw new FileExistsException(fileType + " file already exists! Please enter another file name.");
	    }

	    // Proceed with file upload
	    return fileService.uploadFile(relativePath, file);
	}

	@Override
	public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile posterFile, MultipartFile videoFile) throws IOException {
		// checking movie object exists
				Movie mv =movierepo.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie NOT Found with id"+ movieId));
				
				// Update the poster file if provided
			    String updatedPosterFileName = mv.getPoster();
			    if (posterFile != null && !posterFile.isEmpty()) {
			        Files.deleteIfExists(Paths.get(BASE_PATH + "posters" + File.separator + updatedPosterFileName));
			        updatedPosterFileName = fileService.uploadFile(BASE_PATH + "posters", posterFile);
			    }

			    // Update the video file if provided
			    String updatedVideoFileName = mv.getVideo();
			    if (videoFile != null && !videoFile.isEmpty()) {
			        Files.deleteIfExists(Paths.get(BASE_PATH + "videos" + File.separator + updatedVideoFileName));
			        updatedVideoFileName = fileService.uploadFile(BASE_PATH + "videos", videoFile);
			    }
			    
			  //set movieDto poster Value
				movieDto.setPoster(updatedPosterFileName);
				movieDto.setVideo(updatedVideoFileName);
				
				// mapping it to Movie object
				Movie movie = new Movie(
						mv.getMovieId(),
						movieDto.getTitle(),
						movieDto.getDirector(),
						movieDto.getStudio(),
						movieDto.getMovieCast(),
						movieDto.getReleaseYear(),
						movieDto.getPoster(),
						movieDto.getVideo()
						);
				
				//save the movie
				Movie updatedmovie = movierepo.save(movie);
				
				//generating PosterUrl
				String posterUrl = base_Url + "/file/posters/" + movie.getPoster();
			    String videoUrl = base_Url + "/file/videos/" + movie.getVideo();
			    
			    MovieDto response = new MovieDto(
						movie.getMovieId(),
						movie.getTitle(),
						movie.getDirector(),
						movie.getStudio(),
						movie.getMovieCast(),
						movie.getReleaseYear(),
						movie.getPoster(),
						posterUrl,
						movie.getVideo(),
						videoUrl
						);
	
		return response ;
	}
	
	@Override
	public String deleteMovie(Integer movieId) throws IOException {
		// checking if movie exits in DB
		Movie mv =movierepo.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie NOT Found with id"+ movieId));
		Integer id = mv.getMovieId();
		// Delete the poster file if it exists
	    if (mv.getPoster() != null) {
	        Files.deleteIfExists(Paths.get(BASE_PATH + "posters" + File.separator + mv.getPoster()));
	    }

	    // Delete the video file if it exists
	    if (mv.getVideo() != null) {
	        Files.deleteIfExists(Paths.get(BASE_PATH + "videos" + File.separator + mv.getVideo()));
	    }
		
	    // Delete the movie object from the database
	    movierepo.delete(mv);

	    return "Movie deleted with ID = " + movieId;
	}
	
	@Override
	public MoviePageResponse getAllMovieWithPagination(Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber,pageSize);
		Page<Movie> moviePages = movierepo.findAll(pageable);
		List<Movie> movies= moviePages.getContent();
		List<MovieDto> movieDtos = new ArrayList<>();
		//iterate through the list
		for(Movie movie : movies) {
			
			String posterUrl = base_Url + "/file/posters/" + movie.getPoster();
		    String videoUrl = base_Url + "/file/videos/" + movie.getVideo();
		    
			MovieDto movieDto = new MovieDto(
					movie.getMovieId(),
					movie.getTitle(),
					movie.getDirector(),
					movie.getStudio(),
					movie.getMovieCast(),
					movie.getReleaseYear(),
					movie.getPoster(),
					posterUrl,
					movie.getVideo(),
					videoUrl
					);
			movieDtos.add(movieDto);
		}
		return new MoviePageResponse(movieDtos, pageNumber,pageSize,
				                      moviePages.getTotalElements(),
				                      moviePages.getTotalPages(),
				                      moviePages.isLast());
	}
	
	@Override
	public MoviePageResponse getAllMovieWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy,
			String dir) {
		Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber,pageSize);
		Page<Movie> moviePages = movierepo.findAll(pageable);
		List<Movie> movies= moviePages.getContent();
		List<MovieDto> movieDtos = new ArrayList<>();
		//iterate through the list
		//iterate through the list
				for(Movie movie : movies) {
					
					String posterUrl = base_Url + "/file/posters/" + movie.getPoster();
				    String videoUrl = base_Url + "/file/videos/" + movie.getVideo();
				    
					MovieDto movieDto = new MovieDto(
							movie.getMovieId(),
							movie.getTitle(),
							movie.getDirector(),
							movie.getStudio(),
							movie.getMovieCast(),
							movie.getReleaseYear(),
							movie.getPoster(),
							posterUrl,
							movie.getVideo(),
							videoUrl
							);
					movieDtos.add(movieDto);
				}
		return new MoviePageResponse(movieDtos, pageNumber,pageSize,
				                      moviePages.getTotalElements(),
				                      moviePages.getTotalPages(),
				                      moviePages.isLast());
	
	}

}
