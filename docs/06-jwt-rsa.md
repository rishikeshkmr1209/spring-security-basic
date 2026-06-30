# JWT with RSA — Signing vs Encryption

## JWS — JSON Web Signature (Signing)

| Operation | Key Used |
|---|---|
| Sign | Private key |
| Verify | Public key |

**Purpose:** Integrity + Authenticity — proves the token was created by a trusted party and has not been tampered with.

The payload is **visible** to anyone who holds the token. Signing does not hide the data.

---

## JWE — JSON Web Encryption (Encryption)

| Operation | Key Used |
|---|---|
| Encrypt | Public key |
| Decrypt | Private key |

**Purpose:** Confidentiality — hides the payload so only the intended recipient can read it.

---

## The Key Distinction

**Signing proves who sent it. Encryption protects what's inside. They solve different problems.**

| | Signing (JWS) | Encryption (JWE) |
|---|---|---|
| Payload visible? | Yes | No |
| Guarantees | Authenticity, Integrity | Confidentiality |
| Who can verify/decrypt? | Anyone with the public key | Only the private key holder |

---

## Common Practice — Sign Then Encrypt

When both authenticity and confidentiality are needed:

```
1. Create JWT payload
2. Sign with sender's private key   → produces JWS (authenticity proven)
3. Encrypt with recipient's public key → produces JWE (payload hidden)

On receipt:
4. Decrypt with recipient's private key → recovers JWS
5. Verify signature with sender's public key → confirms authenticity
```

Signing first means the signature is also encrypted — an attacker cannot strip the signature without breaking the encryption.

---

## RSA Key Pair Recap

```
Private key — kept secret by the owner
  - Used to SIGN   (I am proving this came from me)
  - Used to DECRYPT (I am the only one who can read this)

Public key — shared freely
  - Used to VERIFY  (I am checking this really came from them)
  - Used to ENCRYPT (I am locking this so only they can open it)
```

The asymmetry is intentional: anyone can verify or encrypt, but only the owner can sign or decrypt.
