package splitwise.splitwise.utility;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import splitwise.splitwise.exception.ExpiredTokenException;
import splitwise.splitwise.exception.InvalidTokenException;
import splitwise.splitwise.model.User;
import splitwise.splitwise.repository.UserRepository;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = authHeader.substring(7);
        String subject = jwtUtil.extractSubject(jwt);

           if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               User user = userRepository.findByEmailOrPhone(subject, subject).orElse(null);

               if (user != null && jwtUtil.isTokenValid(jwt)) {
                   UsernamePasswordAuthenticationToken authToken =
                           new UsernamePasswordAuthenticationToken(user, null, null);
                   authToken.setDetails(
                           new WebAuthenticationDetailsSource().buildDetails(request));

                   SecurityContextHolder.getContext().setAuthentication(authToken);

               }
           }
           filterChain.doFilter(request, response);

    }
}
