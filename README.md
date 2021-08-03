# JWT JWK Key Generator

This is Java program written using [Nimbus JOSE+JWT](https://github.com/Connect2id/Nimbus-JWT) library based on this [article](https://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-ec-signature) for generating JWK keys.

## Code

  ```java
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
   
   `
   EC Key Pair: {"kty":"EC","d":"vqxhk8WBniOHK2hSUEVy1ZQ_LBd1swIRNdTbILfNZns","use":"enc","crv":"P-256","kid":"123456789","x":"lZcvsxZJPUcAKYyyQt5WYPWazvL16-I1k5L2RA-nk7I","y":"fthQypUg4QHWF-Tt1byXLR49vn3rqZhjR_3ZlBN0OUc","alg":"ES256"}
   EC Public Key: {"kty":"EC","use":"enc","crv":"P-256","kid":"123456789","x":"lZcvsxZJPUcAKYyyQt5WYPWazvL16-I1k5L2RA-nk7I","y":"fthQypUg4QHWF-Tt1byXLR49vn3rqZhjR_3ZlBN0OUc","alg":"ES256"}
   JWT: eyJraWQiOiIxMjM0NTY3ODkiLCJhbGciOiJFUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvandrLmV4YW1wbGUuY29tIiwic3ViIjoiandrIiwiZXhwIjoxNjMwNTkxMjAwfQ.BcJ4t4RrqHY8Uu-YF2M5vcwLpp1O80qNTTPj_yAs0IQlxCR7w26C3a-azt3yCc_AjI-2Gj2xYv07qds06eR4vg
   `

