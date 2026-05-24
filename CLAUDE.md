# PayFlow Microservices — Claude Context

## Proje Özeti
Spring Boot tabanlı mikroservis ödeme sistemi. Öğrenme ve portföy amaçlı geliştiriliyor.

## Stack
JDK 21, Spring Boot 3.2.0, Spring Cloud 2023.0.0, PostgreSQL 16, Kafka (Confluent 7.5.0), Maven 3.9 multi-module, Docker Compose

## Servisler
| Servis | Port | DB | Durum |
|---|---|---|---|
| user-service | 8081 | payflow_user | ✓ Tamamlandı |
| account-service | 8082 | payflow_account | ✓ Tamamlandı |
| payment-service | 8083 | payflow_payment | ✓ Tamamlandı |
| notification-service | 8084 | — | ✓ Tamamlandı |
| api-gateway | 8080 | — | ✓ Tamamlandı |

## Kullanılan Pattern / Kavramlar
DTO, Builder, Singleton, Constructor Injection, Repository, @RestControllerAdvice, JPA Auditing, Stream API, WebClient (.block()), Database per Service, Circuit Breaker (Resilience4j), Kafka producer/consumer, JWT (jjwt 0.12), Spring Cloud Gateway (reactive GlobalFilter), Header-based auth (X-User-Id), Internal vs external endpoint split

## Docker
Tüm servisler container'da çalışır. Yerel makinede başka projeler port tutuyor olabilir:
- PostgreSQL host port: **5434** (5432/5433 başka projede kullanılıyor)
- Kafka host port: **29092** (Docker içi: kafka:9092)

```bash
docker compose up --build        # her şeyi başlat
docker compose up postgres zookeeper kafka   # sadece altyapı
```

### Env Vars (docker-compose override)
| Değişken | Docker | Yerel default |
|---|---|---|
| DB_HOST | postgres | localhost |
| KAFKA_BOOTSTRAP_SERVERS | kafka:9092 | localhost:29092 |
| USER_SERVICE_HOST | user-service | localhost |
| ACCOUNT_SERVICE_HOST | account-service | localhost |
| PAYMENT_SERVICE_HOST | payment-service | localhost |
| JWT_SECRET | (shared default, override prod) | (yml default) |
| JWT_EXPIRATION_MS | 3600000 | 3600000 |

## Auth & Routing
- **api-gateway (8080)** tek dış giriş. JWT doğrular, `X-User-Id` / `X-User-Email` / `X-User-Role` header'larını downstream'e enjekte eder.
- **Whitelist** (token'sız geçer): `/api/auth/**`, `/api/users/register`. Diğer hepsi → 401 (token yok/bozuk/expired).
- JWT user-service tarafından üretilir (`POST /api/auth/login`, HS256). Gateway sadece doğrular. Aynı `JWT_SECRET` paylaşılır.
- Downstream'ler `X-User-Id`'den userId okur, ownership check yapar. **Cross-user erişim → 404** (varlık sızıntısı yok).
- **Endpoint split**:
  - `/api/**` = user-facing (gateway route eder)
  - `/internal/**` = inter-service (gateway route etmez → otomatik dışa kapalı)
- payment-service `AccountServiceClient`, `/internal/accounts/...` çağırır (debit, credit, by-user, lookup by number).

## Önemli Notlar
- Yerel sistemde JDK/Maven sürüm uyumsuzluğu olabilir → **yerel mvn build çalışmayabilir** (Lombok uyumsuzluğu). Docker build JDK 21 ile çalışıyor.
- Lombok fix: root pom.xml'de `annotationProcessorPaths` explicit tanımlandı
- WebClient'larda `defaultHeaders(setBasicAuth)` var — gateway zaten dışarıyı koruduğu için **fazlalık**, sonraki cleanup adımı (kaldırılacak).
- TransferRequest alan adları: `senderAccountNumber` / `receiverAccountNumber`

## Git Workflow
`main ← develop ← feature/*`

## Sıradaki Adımlar
1. Inter-service Basic Auth temizliği (WebClient `setBasicAuth` kaldır; gateway zaten koruyor)
2. Downstream port'larını host'tan kapat (sadece 8080 expose; 8081/8082/8083 docker network içinde kalır)
3. Unit testler — JUnit + Mockito, ServiceImpl testleri
