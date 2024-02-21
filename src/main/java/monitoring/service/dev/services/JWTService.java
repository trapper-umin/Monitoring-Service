package monitoring.service.dev.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import monitoring.service.dev.config.AppConstants;
import monitoring.service.dev.models.Person;
import monitoring.service.dev.repositories.IPeopleRepository;
import monitoring.service.dev.utils.exceptions.JWTException;
import monitoring.service.dev.utils.exceptions.NotFoundException;

public class JWTService {

    private final IPeopleRepository peopleRepository;

    public JWTService(IPeopleRepository peopleRepository){
        this.peopleRepository = peopleRepository;
    }

    public String generate(String username) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        long expMillis = nowMillis + AppConstants.EXPIRATION_JWT_TIME;
        Date exp = new Date(expMillis);

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(SignatureAlgorithm.HS256, AppConstants.SECRET_JWT_KEY)
            .compact();
    }

    public String getUsernameFromToken(String token) throws JWTException {
        try {
            Claims claims = Jwts.parser()
                .setSigningKey(AppConstants.SECRET_JWT_KEY)
                .parseClaimsJws(token)
                .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new JWTException("Incorrect token");
        }
    }

    public Person validate(String token) throws JWTException {
        String username = getUsernameFromToken(token);
        return peopleRepository.findByUsername(username)
            .orElseThrow(()-> new NotFoundException("user with username '" + username + "' was not found"));
    }

    public String extractToken(HttpServletRequest req) throws IllegalArgumentException {
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is required");
        }
        return token.substring(7);
    }
}