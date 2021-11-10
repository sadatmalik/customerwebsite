package com.sadatmalik.customerwebsite.security;

import com.sadatmalik.customerwebsite.model.Authority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            System.out.println("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);

//        HttpSession session = request.getSession(false);
//        if (session == null) {
//            return;
//        }
//        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

    }

    private String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
        for (final GrantedAuthority auth : auths) {
            String authName = auth.getAuthority();
            if (authName.equals(Authority.RoleEnum.USER_ROLE.toString())) {
                return "/user-dashboard";
            }
            if (authName.equals(Authority.RoleEnum.ADMIN_ROLE.toString())) {
                return "/admin-dashboard";
            }
        }

        throw new IllegalStateException();
    }


}
