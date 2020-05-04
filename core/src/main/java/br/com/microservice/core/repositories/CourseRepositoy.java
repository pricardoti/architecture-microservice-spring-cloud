package br.com.microservice.core.repositories;

import br.com.microservice.core.models.Course;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CourseRepositoy extends PagingAndSortingRepository<Course, Long> {
}
