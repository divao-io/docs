package com.example.acme;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Divao JWT verification demo.
 * 
 * @author Public Domain
 */
public class Demo
{
    public static BasicJWTProcessor createJWTProcessor() throws IOException, JOSEException, ParseException
    {
        final String audience = "https://acme.example.com";
        final String JWK = new String(Files.readAllBytes(Paths.get("divao-public-key.json")));
        final OctetKeyPair publicKey = OctetKeyPair.parse(JWK);
        final JWSVerifier jwsVerifier = new Ed25519Verifier(publicKey);
        final JWTClaimsSetVerifier<?> jwtClaimSetVerifier = new DefaultJWTClaimsVerifier(audience, null, null);
        return new BasicJWTProcessor<>(jwsVerifier, jwtClaimSetVerifier);
    }
    
    public static void main(final String[] args) throws BadJOSEException, IOException, JOSEException, ParseException
    {
        final String JWT = "eyJhbGciOiJFZERTQSIsImNydiI6IkVkMjU1MTkiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJodHRwczovL2FjbWUuZXhhbXBsZS5jb20iLCJleHAiOjE2Mzc2Njk5NDcsIm5iZiI6MTYwNjEzMzk0Nywic3ViIjoiYWxpY2VAZXhhbXBsZS5vcmcifQ.H6fq1dRowFv6x3qV3bMbR9x403IHWZ_D_C6E0ZM3IvYNjcSeQcIkdpMmkQDtygTX-eB0ahAyWzPdSNoZ2GyoAA";
        final BasicJWTProcessor basicJWTProcessor = createJWTProcessor();
        final JWTClaimsSet claims = basicJWTProcessor.process(JWT, null);
        if(claims == null)
        {
            System.out.println("JWT is not valid");
        }
        else
        {
            System.out.printf("JWT is valid, subject is %s\n", claims.getSubject());
        }
    }
}
