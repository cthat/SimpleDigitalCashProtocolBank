package model;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSABlindingEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSABlindingFactorGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSABlindingParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.util.encoders.Base64;

public class Bank {

    private final AsymmetricCipherKeyPair keys;

    public Bank(AsymmetricCipherKeyPair keys) {
        this.keys = keys;
    }

    public RSAKeyParameters getPublic() {
        return (RSAKeyParameters) keys.getPublic();
    }

    public byte[] sign(Order order) {
        // Sign the coin request using our private key.
        byte[] message = order.getMessage();

        RSAEngine engine = new RSAEngine();
        engine.init(true, keys.getPrivate());

        return engine.processBlock(message, 0, message.length);
    }

    public boolean verify(Order moneyOrder) {
        // Verify that the coin has a valid signature using our public key.
        byte[] id = coin.getID();
        byte[] signature = coin.getSignature();

        PSSSigner signer = new PSSSigner(new RSAEngine(), new SHA1Digest(), 20);
        signer.init(false, keys.getPublic());

        signer.update(id, 0, id.length);

        return signer.verifySignature(signature);
    }
}
