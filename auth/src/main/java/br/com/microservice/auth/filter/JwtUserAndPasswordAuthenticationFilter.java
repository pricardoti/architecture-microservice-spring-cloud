package br.com.microservice.auth.filter;

import br.com.microservice.core.config.JwtConfiguration;
import br.com.microservice.core.models.ApplicationUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JwtUserAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfiguration jwtConfiguration;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("::: Attemping authentication... :::");
        ApplicationUser applicationUser = Optional.ofNullable(new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class))
                .orElseThrow(() -> new UsernameNotFoundException("Unable to retrive the 'username' or 'password'"));

        log.info("::: Creating the authentication object for the user '{}' and calling UserDetailServiceImpl.loadByUsername() :::", applicationUser);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                applicationUser.getUsername(),
                applicationUser.getPassword(),
                Collections.emptyList());
        usernamePasswordAuthenticationToken.setDetails(applicationUser);

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        log.info("::: Authentication was sucessful for the user '{}', generating JWT token :::", authResult.getName());
        SignedJWT signedJwt = createSignedJwt(authResult);
        String encryptedToken = encryptToken(signedJwt);

        log.info("::: Token generated sucessfully, add it to the response header :::");
        response.addHeader("Access-Control-Expose-Headers", String.format("XSRF-TOKEN, %s", jwtConfiguration.getHeader().getName()));
        response.addHeader(jwtConfiguration.getHeader().getName(), String.format("%s%s", jwtConfiguration.getHeader().getPrefix(), encryptedToken));
    }

    @SneakyThrows
    private SignedJWT createSignedJwt(Authentication auth) {
        log.info("::: Start to create the signed JWT :::");
        JWTClaimsSet jwtClaimsSet = createJwtClaimSet(auth, (ApplicationUser) auth.getPrincipal());
        KeyPair keyPair = generateKeyPair();

        log.info("::: Building JWK from the RSA Keys :::");
        JWK jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyID(UUID.randomUUID().toString())
                .build();

        log.info("::: Building JWSHeader from the RSA Keys :::");
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .jwk(jwk)
                .type(JOSEObjectType.JWT)
                .build();

        log.info("::: Sigining the token with the private RSA key :::");
        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
        signedJWT.sign(new RSASSASigner(keyPair.getPrivate()));

        log.info("::: Serialized toke '{}' :::", signedJWT.serialize());

        return signedJWT;
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        log.info("::: Generating RSA '{}' bits key :::", keySize);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);

        return keyPairGenerator.genKeyPair();
    }

    private JWTClaimsSet createJwtClaimSet(Authentication auth, ApplicationUser applicationUser) {
        log.info("::: Create the JwtClaimSet Object for '{}' :::", applicationUser);
        return new JWTClaimsSet.Builder()
                .subject(applicationUser.getUsername())
                .claim("authorities", auth.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuer("https://www.linkedin.com/in/pricardoti/")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + jwtConfiguration.getExpiration() * 1000))
                .build();
    }

    private String encryptToken(SignedJWT signedJwt) throws JOSEException {
        log.info("::: Stating the encrypt token method :::");
        DirectEncrypter directEncrypter = new DirectEncrypter(jwtConfiguration.getPrivateKey().getBytes());

        JWEObject jwtObject = new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
                .contentType("JWT")
                .build(), new Payload(signedJwt));

        log.info("::: Encrypt token with system´s private key :::");
        jwtObject.encrypt(directEncrypter);

        log.info("::: Token encrypted :::");
        return jwtObject.serialize();
    }
}
