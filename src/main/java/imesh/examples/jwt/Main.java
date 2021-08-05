package imesh.examples.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Date;

public class Main {

  public static void main(String[] args) {
    try {
      // Generate EC key pair
      ECKey keyPair = ECKeyManager.generateECKey("123456789", JWSAlgorithm.ES256, KeyUse.SIGNATURE);
      System.out.println("EC Key Pair: " + keyPair);
      System.out.println("EC Public Key: " + keyPair.toPublicJWK());

      // Generate JWT using EC key pair
      String subject = "jwt";
      String issuer = "https://jwk.example.com";
      String jwt = ECKeyManager.generateJWT(keyPair, subject, issuer);
      System.out.println("JWT: " + jwt);

      // Verify JWT and retrieve JWT claim set
      JWTClaimsSet jwtClaimsSet = ECKeyManager
          .verifyJWT(jwt, keyPair.toPublicJWK(), JWSAlgorithm.ES256, subject, issuer);

      // Verify the JWT claims and expiry date
      assertEquals(subject, jwtClaimsSet.getSubject());
      assertEquals(issuer, jwtClaimsSet.getIssuer());
      assertTrue(new Date().before(jwtClaimsSet.getExpirationTime()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
