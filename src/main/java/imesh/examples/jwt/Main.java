package imesh.examples.jwt;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Main {

  public static void main(String[] args) {
    try {
      // Generate an EC key pair
      ECKey keyPair = new ECKeyGenerator(Curve.P_256)
          .keyID("123456789")
          .algorithm(new Algorithm("ES256"))
          .keyUse(KeyUse.ENCRYPTION)
          .generate();
      System.out.println("EC Key Pair: " + keyPair);

      ECKey ecPublicKey = keyPair.toPublicJWK();
      System.out.println("EC Public Key: " + ecPublicKey);

      // Create the EC signer
      JWSSigner signer = new ECDSASigner(keyPair);

      // Prepare JWT with claims set
      LocalDate expirationLocalDate = LocalDate.now().plusMonths(1);
      Date expirationDate = Date
          .from(expirationLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject("jwk")
          .issuer("https://jwk.example.com")
          .expirationTime(expirationDate)
          .build();

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyPair.getKeyID()).build(),
          claimsSet);

      // Compute the EC signature
      signedJWT.sign(signer);

      // Serialize the JWS to compact form
      String jwt = signedJWT.serialize();
      System.out.println("JWT: " + jwt);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
