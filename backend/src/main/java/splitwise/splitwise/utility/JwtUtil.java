package splitwise.splitwise.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static  final String SECRET = "thisiskeysecrjggsdhgsljgbjdbgbabgaged";
    private static final long EXPIRATION = 1000*60*60*5 ; //5 hour

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String subject, Long userid) {
        return Jwts.builder()
                .subject(subject) // can be either email or phone
                .claim("id",userid)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key,Jwts.SIG.HS256)
                .compact();
    }


    public String extractSubject(String token){
        return getParser()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public boolean isTokenValid(String token){
            getParser().parseSignedClaims(token);
            return true;
    }




    private JwtParser getParser(){
        return Jwts.parser()
                .verifyWith(key)
                .build();
    }
}
