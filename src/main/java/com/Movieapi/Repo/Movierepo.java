package com.Movieapi.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Movieapi.Model.Movie;

@Repository
public interface Movierepo extends JpaRepository<Movie, Integer> {

}
