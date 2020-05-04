package br.com.microservice.course.services;


import br.com.microservice.core.models.Course;
import br.com.microservice.core.repositories.CourseRepositoy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseService {

    private final CourseRepositoy microServiceRepositoy;

    public Page<Course> list(Pageable pageable) {
        return microServiceRepositoy.findAll(pageable);
    }
}
