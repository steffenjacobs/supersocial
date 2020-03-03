package org.ollide.spring.discourse.sso;

import org.ollide.spring.discourse.sso.security.DiscourseSigner;
import org.ollide.spring.discourse.sso.security.NonceUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@RequestMapping
public class SsoEndpoint {

    private final DiscourseSsoProperties properties;
    private final DiscourseSigner signer;

    public SsoEndpoint(DiscourseSsoProperties properties, DiscourseSigner signer) {
        this.properties = properties;
        this.signer = signer;
    }

    @GetMapping("${discourse.sso.loginPath:/login/discourse}")
    public ResponseEntity<Void> redirectToDiscourse(UriComponentsBuilder uriBuilder) {
        String returnUrl = uriBuilder.replacePath(properties.getReturnPath()).build().toUriString();
        String payload = "nonce=" + NonceUtils.createNonce() + "&return_sso_url=" + returnUrl;

        // Encode and sign
        String base64Payload = Base64Utils.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = signer.sign(base64Payload);

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(properties.getDiscourseUrl())
                .queryParam("sso", base64Payload)
                .queryParam("sig", signature)
                .encode(StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.status(HttpStatus.FOUND).location(uriComponents.toUri()).build();
    }

}
