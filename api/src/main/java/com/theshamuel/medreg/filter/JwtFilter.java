/**
 * This private project is a project which automatizate workflow in medical center AVESTA
 * (http://avesta-center.com) called "MedRegistry". The "MedRegistry" demonstrates my programming
 * skills to * potential employers.
 * <p>
 * Here is short description: ( for more detailed description please read README.md or go to
 * https://github.com/theshamuel/medregistry )
 * <p>
 * Front-end: JS, HTML, CSS (basic simple functionality) Back-end: Spring (Spring Boot, Spring IoC,
 * Spring Data, Spring Test), JWT library, Java8 DB: MongoDB Tools: git,maven,docker.
 * <p>
 * My LinkedIn profile: https://www.linkedin.com/in/alex-gladkikh-767a15115/
 */
package com.theshamuel.medreg.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;


/**
 * The Jwt filter class.
 *
 * @author Alex Gladkikh
 */
public class JwtFilter extends GenericFilterBean {

    @Override
    public void doFilter(final ServletRequest req,
            final ServletResponse res,
            final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        final String authHeader = request.getHeader("Authorization");
        if ((authHeader == null || !authHeader
                .startsWith("Bearer "))) { //&& request.getMethod()!="OPTIONS"
            ((HttpServletResponse) res).sendError(403, "Missing or invalid Authorization header.");
        } else if (authHeader != null && authHeader
                .startsWith("Bearer ")) { //&& request.getMethod()!="OPTIONS"
            final String token = authHeader.substring(7);
            try {
                final Claims claims = Jwts.parser().setSigningKey("secretkey")
                        .parseClaimsJws(token).getBody();
                request.setAttribute("claims", claims);
            } catch (SignatureException e) {
                ((HttpServletResponse) res)
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
                throw new SignatureException("Invalid signature of token.");
            } catch (ExpiredJwtException e) {
                ((HttpServletResponse) res).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Token session is expired.");
                throw new ExpiredJwtException(Jwts.parser().setSigningKey("secretkey")
                        .parseClaimsJws(token).getHeader(), (Claims) request.getAttribute("claims"),
                        "Token session is expired.");
            }
        }

        chain.doFilter(request, response);
    }

}
