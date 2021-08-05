package imesh.examples.jwt;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

public class ECKeyManager {

  /***
   * Generate EC key pair
   * @param keyId key id
   * @param algorithm algorithm
   * @param keyUse key usage
   * @return
   * @throws com.nimbusds.jose.JOSEException
   */
  static ECKey generateECKey(String keyId, Algorithm algorithm, KeyUse keyUse)
      throws com.nimbusds.jose.JOSEException {
    // Generate an EC key pair
    ECKey keyPair = new ECKeyGenerator(Curve.P_256)
        .keyID(keyId)
        .algorithm(algorithm)
        .keyUse(keyUse)
        .generate();
    return keyPair;
  }

  /***
   * Generate JWT using EC key pair
   * @param keyPair EC key pair
   * @param subject subject
   * @param issuer issuer
   * @return
   * @throws JOSEException
   */
  static String generateJWT(ECKey keyPair, String subject, String issuer) throws JOSEException {
    // Create the EC signer
    JWSSigner signer = new ECDSASigner(keyPair);

    // Prepare JWT with claims set
    LocalDate expirationLocalDate = LocalDate.now().plusMonths(1);
    Date expirationDate = Date
        .from(expirationLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(subject)
        .issuer(issuer)
        .expirationTime(expirationDate)
        .build();

    SignedJWT signedJWT = new SignedJWT(
        new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyPair.getKeyID()).build(),
        claimsSet);

    // Compute the EC signature
    signedJWT.sign(signer);

    // Serialize the JWS to compact form
    return signedJWT.serialize();
  }

  /***
   * Verify JWT using JWK public key and return JWT claim set
   * @param jwt JWT
   * @param jwkPublicKey JWK public key
   * @param expectedJWSAlg expected JWS algorithm
   * @param subject subject
   * @param issuer issuer
   * @return
   * @throws ParseException
   * @throws BadJOSEException
   * @throws JOSEException
   */
  static JWTClaimsSet verifyJWT(String jwt, JWK jwkPublicKey, JWSAlgorithm expectedJWSAlg,
      String subject, String issuer)
      throws ParseException, BadJOSEException, JOSEException {

    // Create a JWT processor for the access tokens
    ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
        new DefaultJWTProcessor<>();

    // The public RSA keys to validate the signatures will be sourced from the
    // OAuth 2.0 server's JWK set
    JWKSet jwkSet = new JWKSet(jwkPublicKey);
    JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(jwkSet);

    // Configure the JWT processor with a key selector to feed matching public
    // RSA keys sourced from the JWK set URL
    JWSKeySelector<SecurityContext> keySelector =
        new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);

    jwtProcessor.setJWSKeySelector(keySelector);

    // Set the required JWT claims for access tokens issued
    jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier(
        new JWTClaimsSet.Builder().issuer(issuer).subject(subject).build(),
        new HashSet<>(Arrays.asList("sub"))));

    // Process the token
    SecurityContext ctx = null; // optional context parameter, not required here
    JWTClaimsSet claimsSet = jwtProcessor.process(jwt, ctx);
    return claimsSet;
  }
}
