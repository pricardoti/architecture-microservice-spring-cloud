package br.com.microservice.auth.user;

import br.com.microservice.core.models.ApplicationUser;
import br.com.microservice.core.repositories.ApplicationUserRepositoy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsImpl implements UserDetailsService {

    private final ApplicationUserRepositoy applicationUserRepositoy;

    @Override
    public UserDetails loadUserByUsername(String username) {
        ApplicationUser applicationUser = applicationUserRepositoy.findByUsername(username);
        return new CustomUserDetails(Optional.ofNullable(applicationUser)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Application user '%s' not found.", username))));
    }
}
