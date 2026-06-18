# 🌐 VaultNet

Application Android de démonstration de la communication réseau sécurisée — développée en Java avec OkHttp, Retrofit et Material Design 3.

---

## 📱 Aperçu

| Écran | Description |
|---|---|
| 📡 Retrofit + TLS | Requêtes HTTPS avec ConnectionSpec.MODERN_TLS |
| ⚡ HTTP/3 QUIC | Négociation HTTP/2 avec fallback automatique |
| 🔄 WebSocket | Communication bidirectionnelle wss:// en temps réel |
| 🔐 Mutual TLS | Authentification mutuelle client + serveur X.509 |
| 🛡️ DNS-over-HTTPS | Résolution DNS chiffrée via Google (dns.google) |
| 📜 Certificate Transparency | Vérification des certificats dans les logs CT |
| 🚀 Atelier Final | Requête combinant TLS + DoH + Certificate Pinning |

---

## ✨ Fonctionnalités

- **TLS moderne** — `ConnectionSpec.MODERN_TLS` force TLS 1.2+ et cipher suites robustes
- **HTTP/3 QUIC** — protocole sur UDP combinant transport et sécurité, élimine le head-of-line blocking
- **WebSocket sécurisé** — connexion `wss://` persistante bidirectionnelle avec gestion du cycle de vie
- **mTLS** — certificat client PKCS12 chargé depuis les assets, authentification mutuelle
- **DNS-over-HTTPS** — requêtes DNS chiffrées via `dns.google`, bootstrap sur `8.8.8.8` / `8.8.4.4`
- **Certificate Transparency** — détection de certificats frauduleux via logs CT publics
- **Network Security Config** — `cleartextTrafficPermitted="false"` + certificate pinning SHA-256
- **`allowBackup="false"`** — bloque l'extraction via `adb backup`

---

## 🗂️ Structure

```
app/src/main/java/com/example/vaultnet/
├── MainActivity.java                        # Hub — navigation vers les 7 modules
├── RetrofitActivity.java                    # Retrofit + OkHttp MODERN_TLS
├── Http3Activity.java                       # HTTP/2 + fallback HTTP/1.1
├── WebSocketActivity.java                   # WebSocket wss://echo.websocket.org
├── MtlsActivity.java                        # Guide configuration mTLS
├── DohActivity.java                         # DNS système vs DNS-over-HTTPS
├── CtActivity.java                          # Certificate Transparency checker
├── FinalActivity.java                       # Requête sécurisée combinée
├── network/
│   ├── HttpClientConfig.java                # OkHttp MODERN_TLS + logging
│   ├── ApiService.java                      # Interface Retrofit (posts, users)
│   ├── RetrofitClient.java                  # Singleton Retrofit
│   ├── WebSocketManager.java               # Gestionnaire WebSocket + Callback
│   └── DohClientConfig.java                # OkHttp + DnsOverHttps Google
├── model/
│   ├── Post.java                            # Modèle JSON post
│   └── User.java                            # Modèle JSON user
└── security/
    └── CertificateTransparencyChecker.java  # Analyse certificat X.509

res/xml/
└── network_security_config.xml              # Pinning SHA-256 + cleartext=false
```

---

## 🛡️ Mesures de Sécurité

| Attaque | Contre-mesure | Implémentation |
|---|---|---|
| MitM | TLS moderne | `ConnectionSpec.MODERN_TLS` |
| Downgrade protocole | Certificate Pinning | `network_security_config.xml` |
| DNS Spoofing | DNS-over-HTTPS | `DnsOverHttps` → `dns.google` |
| Trafic HTTP clair | Blocage cleartext | `cleartextTrafficPermitted="false"` |
| Usurpation serveur | mTLS | Certificat client PKCS12 |
| Certificat frauduleux | Certificate Transparency | Vérification logs CT publics |
| Extraction backup | `allowBackup="false"` | AndroidManifest.xml |

---

## 🔗 Chaîne de Sécurité Réseau

```
App → DnsOverHttps (dns.google) → IP résolue
         ↓
    OkHttp MODERN_TLS (TLS 1.2+)
         ↓
    Certificate Pinning (SHA-256)
         ↓
    mTLS (certificat client PKCS12)
         ↓
    Serveur API
```

---

## 🛠️ Stack

| Outil | Version |
|---|---|
| Java | 11 |
| Android SDK min | API 24 (Android 7.0) |
| `retrofit2` | 2.9.0 |
| `okhttp3` | 4.11.0 |
| `okhttp3:logging-interceptor` | 4.11.0 |
| `okhttp3:okhttp-dnsoverhttps` | 4.11.0 |
| `gson` | 2.10.1 |
| `material` | 1.9.0 |

---

## 🚀 Lancer le projet

```bash
git clone https://github.com/yasser-ch/VaultNet.git
```

Ouvrir dans Android Studio → **Run** sur émulateur ou appareil physique (API 24+).

> ⚠️ Pour tester mTLS en production, générer les certificats avec OpenSSL et placer `client.p12` dans `app/src/main/assets/`.

---

## 📚 Contexte

TP réalisé dans le cadre du cursus **Génie Cyberdéfense & Télécommunications Embarquées** à l'ENSA Marrakech.  
Concepts abordés : TLS/HTTPS, HTTP/3-QUIC, WebSockets, mTLS, DNS-over-HTTPS, Certificate Pinning, Certificate Transparency, Network Security Config.
