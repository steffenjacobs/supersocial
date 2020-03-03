package org.ollide.spring.discourse.sso;

import org.ollide.spring.discourse.sso.authentication.DiscourseSsoAuthentication;
import org.ollide.spring.discourse.sso.authentication.DiscoursePrincipal;
import org.ollide.spring.discourse.sso.security.DiscourseSigner;
import org.ollide.spring.discourse.sso.security.NonceUtils;
import org.ollide.spring.discourse.sso.util.SsoUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A Servlet filter that can be used to process Discourse's SSO response
 * and load an authentication object into the SecurityContext.
 */
public class DiscourseSsoVerificationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String PARAM_SSO = "sso";
    private static final String PARAM_SIGNATURE = "sig";

    private DiscourseSigner signer;

    public DiscourseSsoVerificationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(new NoopAuthenticationManager());
    }

    public void setSigner(DiscourseSigner signer) {
        this.signer = signer;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String sso = request.getParameter(PARAM_SSO);
        String signature = request.getParameter(PARAM_SIGNATURE);
        return super.requiresAuthentication(request, response) && sso != null && signature != null;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String sso = request.getParameter(PARAM_SSO);
        String signature = request.getParameter(PARAM_SIGNATURE);

        if (!signer.verify(sso, signature)) {
            throw new InvalidDiscourseResponseException("Invalid signature");
        }

        String loginQuery = new String(Base64Utils.decodeFromString(sso), CHARSET);

        Map<String, String> ssoResponseMap = SsoUtils.extractMap(loginQuery);

        if (!NonceUtils.isValid(ssoResponseMap.get(SsoUtils.NONCE))) {
            throw new InvalidDiscourseResponseException("Invalid nonce");
        }

        DiscoursePrincipal ssoResponse = extractSsoResponse(ssoResponseMap);
        return new DiscourseSsoAuthentication(ssoResponse, Collections.emptySet());
    }

    DiscoursePrincipal extractSsoResponse(Map<String, String> ssoResponseMap) {
        String delimitedGroups = ssoResponseMap.get(SsoUtils.GROUPS);
        String[] groupsArray = StringUtils.delimitedListToStringArray(delimitedGroups, ",");
        Set<String> groups = new HashSet<>(Arrays.asList(groupsArray));

        return new DiscoursePrincipal(
                ssoResponseMap.get(SsoUtils.EXTERNAL_ID),
                ssoResponseMap.get(SsoUtils.USERNAME),
                ssoResponseMap.get(SsoUtils.NAME),
                ssoResponseMap.get(SsoUtils.EMAIL),
                ssoResponseMap.get(SsoUtils.AVATAR_URL),
                Boolean.parseBoolean(ssoResponseMap.get(SsoUtils.ADMIN)),
                Boolean.parseBoolean(ssoResponseMap.get(SsoUtils.MODERATOR)),
                groups
        );
    }

    private static class NoopAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication authentication) {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }
    }

}
