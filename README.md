# JWT JWK Key Generator

This is Java program written using [Nimbus JOSE+JWT](https://github.com/Connect2id/Nimbus-JWT) library based on this [article](https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-ec-signature) for generating JWK keys.

## Code

  ```java
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

  // Verify the JWT claims and verify expiry date
  assertEquals(subject, jwtClaimsSet.getSubject());
  assertEquals(issuer, jwtClaimsSet.getIssuer());
  assertTrue(new Date().before(jwtClaimsSet.getExpirationTime()));
  ```

## How to Run

1. Clone this Git repository:
   ```bash
   git clone https://github.com/imesh/jwt-jwk-key-generator
   ```

2. Execute using Maven:
   ```bash
   cd jwt-jwk-key-generator
   mvn exec:java -Dexec.mainClass="imesh.examples.jwt.Main"
   ```

   An example output:
   
   ```bash
    EC Key Pair: {
        "kty":"EC",
        "d":"gEJtMNAEKSnnNRfdwKVEtE9C2-Bdm3gvDbhU_53xk-I",
        "use":"sig",
        "crv":"P-256",
        "kid":"123456789",
        "x":"OqHlA-v92dZT1fJW5V_itda2PKfrQvS6QIDXOGiU44o","y":"MYkqs6sxJOQMc9R-jabCRcNRtjn8YBUNCrkp8C4n7rY","alg":"ES256"}

    EC Public Key: {
        "kty":"EC",
        "use":"sig",
        "crv":"P-256",
        "kid":"123456789",
        "x":"OqHlA-v92dZT1fJW5V_itda2PKfrQvS6QIDXOGiU44o","y":"MYkqs6sxJOQMc9R-jabCRcNRtjn8YBUNCrkp8C4n7rY","alg":"ES256"}
        
    JWT: eyJraWQiOiIxMjM0NTY3ODkiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvandrLmV4YW1wbGUuY29tIiwic3ViIjoiand0IiwiZXhwIjoxNjMwNzY0MDAwfQ.DasBQS2CG-GzkIB_OQ09OXP4lKzU7ce2L7rFFIf3XqKKsPdTQ-LI8dapOvhCa5MwH_uDhHmtNKw1D-qFmxnnlw
   ```

