package com.taxwiz.auth;

import com.taxwiz.service.auth.JwtSetup;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.taxwiz.utils.ErrorMessages.INVALID_TOKEN;
import static com.taxwiz.utils.ErrorMessages.TOKEN_EXPIRED;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtSetup jwtSetup;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        if (url.contains("/api/user/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        log.info("Intercepting request");
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                username = jwtSetup.extractClaim(token, Claims::getSubject);
            } catch (ExpiredJwtException e) {
                log.error("Token Expired");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, TOKEN_EXPIRED.name());
                return;
            } catch (Exception e) {
                log.error("Error while validating token {}",e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_TOKEN.name());
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if ( jwtSetup.isTokenValid(token, username) ) {
                Claims claims = jwtSetup.extractClaims(token);
                List<String> roles = claims.get("roles", List.class);
                log.info("Extracting roles");
                List<GrantedAuthority> grantedAuthorityList =
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                Authentication authentication = new UsernamePasswordAuthenticationToken(username,null,grantedAuthorityList);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.error("Invalid Token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_TOKEN.name());
                return;
            }
        }

        doFilter(request, response, filterChain);
    }

}
