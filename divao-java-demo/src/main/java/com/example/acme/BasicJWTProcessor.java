package com.example.acme;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.jwt.proc.JWTProcessor;
import java.text.ParseException;

/**
 * Basic signed JWT processor.
 * 
 * This class is thread safe.
 * 
 * @author Public Domain
 */
public class BasicJWTProcessor<C extends SecurityContext> implements JWTProcessor<C>
{
    /**
     * JWS verifier.
     */
    private final JWSVerifier jwsVerifier;
    
    /**
     * JWT claims verifier.
     */
    private final JWTClaimsSetVerifier<?> jtwClaimsSetVerifier;

    /**
     * Creates a new instance of this class.
     * 
     * @param jwsVerifier the JWS verifier.
     * @param jtwClaimsSetVerifier the JWT claims verifier.
     */
    public BasicJWTProcessor(final JWSVerifier jwsVerifier, final JWTClaimsSetVerifier<?> jtwClaimsSetVerifier)
    {
        this.jwsVerifier = jwsVerifier;
        this.jtwClaimsSetVerifier = jtwClaimsSetVerifier;
    }

    @Override
    public JWTClaimsSet process(final String jwtString, final C context) throws ParseException, BadJOSEException, JOSEException
    {
        final SignedJWT signedJWT = SignedJWT.parse(jwtString);
        return process(signedJWT, context);
    }

    @Override
    public JWTClaimsSet process(final JWT jwt, final C context) throws BadJOSEException, JOSEException
    {
        if(jwt instanceof SignedJWT)
        {
            return process((SignedJWT)jwt, context);
        }
        else
        {
            throw new BadJOSEException("unsupported JWT type");
        }
    }

    @Override
    public JWTClaimsSet process(final PlainJWT plainJWT, final C context) throws BadJOSEException, JOSEException
    {
        throw new BadJOSEException("unsupported JWT type");
    }

    @Override
    public JWTClaimsSet process(final SignedJWT signedJWT, final C context) throws BadJOSEException, JOSEException
    {
        if(signedJWT.verify(jwsVerifier))
        {
            try
            {
                final JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
                jtwClaimsSetVerifier.verify(claimsSet, null);
                return claimsSet;
            }
            catch(final ParseException e)
            {
                throw new BadJWTException("failed to parse JWT claims", e);
            }
        }
        else
        {
            throw new BadJWSException("invalid JWT signature");
        }
    }

    @Override
    public JWTClaimsSet process(final EncryptedJWT encryptedJWT, final C context) throws BadJOSEException, JOSEException
    {
        throw new BadJOSEException("unsupported JWT type");
    }
}
