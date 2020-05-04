package br.com.microservice.core.repositories;

import br.com.microservice.core.models.ApplicationUser;
import br.com.microservice.core.models.Course;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationUserRepositoy extends PagingAndSortingRepository<ApplicationUser, Long> {

    ApplicationUser findByUsername(String name);
}