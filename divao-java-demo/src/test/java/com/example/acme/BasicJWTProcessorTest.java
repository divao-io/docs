package com.example.acme;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.jwt.util.DateUtils;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockedStatic.Verification;
import static org.mockito.Mockito.*;

/**
 * Unit test.
 * 
 * @author Public Domain
 */
public class BasicJWTProcessorTest
{
    private static final String JWT = "eyJhbGciOiJFZERTQSIsImNydiI6IkVkMjU1MTkiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJodHRwczovL2FjbWUuZXhhbXBsZS5jb20iLCJleHAiOjE2Mzc2Njk5NDcsIm5iZiI6MTYwNjEzMzk0Nywic3ViIjoiYWxpY2VAZXhhbXBsZS5vcmcifQ.H6fq1dRowFv6x3qV3bMbR9x403IHWZ_D_C6E0ZM3IvYNjcSeQcIkdpMmkQDtygTX-eB0ahAyWzPdSNoZ2GyoAA";
    
    private static BasicJWTProcessor createFixture(final String audience) throws Exception
    {
        final String JWK = new String(Files.readAllBytes(Paths.get("divao-public-key.json")));
        final OctetKeyPair publicKey = OctetKeyPair.parse(JWK);
        final JWSVerifier jwsVerifier = new Ed25519Verifier(publicKey);
        final JWTClaimsSetVerifier<?> jwtClaimSetVerifier = new DefaultJWTClaimsVerifier(audience, null, null);
        return new BasicJWTProcessor<>(jwsVerifier, jwtClaimSetVerifier);
    }
    
    @SuppressWarnings("ThrowableResultIgnored")
    @Test
    void testFailExpired() throws Exception
    {
        try(final MockedStatic mocked = mockStatic(DateUtils.class, CALLS_REAL_METHODS))
        {
            final Verification isAfter = () -> DateUtils.isAfter(any(), any(), anyLong());
            mocked.when(isAfter).thenReturn(false);
            final BasicJWTProcessor fixture = createFixture("https://acme.example.com");
            assertThrows(BadJWTException.class, () -> fixture.process(JWT, null));
            mocked.verify(isAfter);
        }
    }
    
    @SuppressWarnings("ThrowableResultIgnored")
    @Test
    void testFailNotBefore() throws Exception
    {
        try(final MockedStatic mocked = mockStatic(DateUtils.class, CALLS_REAL_METHODS))
        {
            final Verification isBefore = () -> DateUtils.isBefore(any(), any(), anyLong());
            mocked.when(isBefore).thenReturn(false);
            final BasicJWTProcessor fixture = createFixture("https://acme.example.com");
            assertThrows(BadJWTException.class, () -> fixture.process(JWT, null));
            mocked.verify(isBefore);
        }
    }

    @SuppressWarnings("ThrowableResultIgnored")
    @Test
    void testFailOnInvalidAudience() throws Exception
    {
        final BasicJWTProcessor fixture = createFixture("https://acme.example.org");
        assertThrows(BadJWTException.class, () -> fixture.process(JWT, null));
    }
    
    @SuppressWarnings("ThrowableResultIgnored")
    @Test
    void testFailOnInvalidSignature() throws Exception
    {
        final BasicJWTProcessor fixture = createFixture("https://acme.example.com");
        assertThrows(BadJWSException.class, () -> fixture.process(JWT.replaceAll("..$", "BB"), null));
    }
}
