### Creating a Custom Spend Offer ###

A Custom Spend Offer allows your users to unlock unique spending opportunities that you define within your app. Whereas [Built-in Offers](#adding-a-custom-spend-offer-to-the-kin-marketplace-offer-wall) appear in the Kin Marketplace Offer Wall, Custom Offers are created by your app. With Custom Offers, your app displays the offer and requests user approval. Finally, using the [Kin Purchase API](https://github.com/kinecosystem/kin-ecosystem-ios-sdk#using-kin-for-native-spend-experience), your app [requests payment](#requesting-purchase-payment-for-a-custom-spend-offer).

*To create a Custom Spend Offer:*


### Requesting Purchase Payment for a Custom Spend Offer ###

*To request payment for a Custom Spend Offer:*

1.	Using the header and payload templates below, create a [JWT](https://jwt.io/introduction/) that represents a Spend Offer that you signed. See [Generating the JWT Token](../README.md#generating-the-jwt-token) for further details about JWT structure.

**JWT Header:**
```
{
    "alg": "ES256", // Hash function
    "typ": "JWT",
    "kid": string" // identifier of the keypair used to sign the JWT. Signer authority will provide identifiers and public keys, which enables using multiple private/public key pairs. Note that the signer authority has to provide the verifier with a list of public keys, and their IDs, in advance. 
}
```

**JWT Payload:**
```
{
    // common/ standard fields
    iat: number;  // issued at - seconds from epoc
    iss: string; // issuer
    exp: number; // expiration
    sub: "spend"

   // application fields
   offer: {
           id: string; // offer ID is determined by the developer (internal)
           amount: number; // amount of Kin for this offer - price
   }

   sender: {
          user_id: string; // optional: ID of purchasing user
          title: string; // order title - appears in order history
          description: string; // order description (in order history)
   }
}
```
2.	Call `Kin.purchase(…)`, while passing the JWT you built and a callback function that will receive purchase confirmation.

>**NOTES:**
>* The following snippet is taken from the [SDK Sample App](https://github.com/kinecosystem/kin-ecosystem-android-sdk). In the Sample App, the JWT is created and signed, for presentation purposes only, by the Android client side. **Do not use this method in production!** Rather, in production, the JWT must be signed by the server, using a secure private key.
> * See [BlockchainException](COMMON_ERRORS.md#blockchainException--Represents-an-error-originated-with-kin-blockchain-error-code-might-be) and [ServiceException](COMMON_ERRORS.md#serviceexception---represents-an-error-communicating-with-kin-server-error-code-might-be) for possible errors.

```java
try {
  Kin.purchase(offerJwt, new KinCallback<OrderConfirmation>() {
  @Override public void onResponse(OrderConfirmation orderConfirmation) {
  // OrderConfirmation will be called once the Kin Ecosystem has received the payment transaction from the user.
  // OrderConfirmation can be maintaineed on the digital service side as a receipt proving the user received their Kin.

                // Send confirmation JWT back to the server in order prove that the user
                // completed the blockchain transaction and that the purchase can be unlocked for this user.
                System.out.println("Native spend created successfully.\n jwtConfirmation: " + orderConfirmation.getJwtConfirmation());
            }

            @Override
            public void onFailure(KinEcosystemException exception) {
                System.out.println("Failed - " + error.getMessage());
            }
        });
    } catch (ClientException e) {
        e.printStackTrace();
  }
```

3.	Once you receive confirmation from the Kin Server that the funds were transferred successfully, go ahead and complete the purchase.
