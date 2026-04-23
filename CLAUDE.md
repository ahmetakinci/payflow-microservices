# PayFlow Microservices — Claude Context

## Proje Özeti
Spring Boot tabanlı mikroservis ödeme sistemi. Öğrenme/portföy projesi.

## Stack
JDK 21, Spring Boot 3.2.0, Spring Cloud 2023.0.0, PostgreSQL 16, Kafka (Confluent 7.5.0), Maven 3.9 multi-module, Docker Compose

## Servisler
| Servis | Port | DB | Durum |
|---|---|---|---|
| user-service | 8081 | payflow_user | ✓ Tamamlandı |
| account-service | 8082 | payflow_account | ✓ Tamamlandı |
| payment-service | 8083 | payflow_payment | ✓ Tamamlandı |
| notification-service | 8084 | — | ✓ Tamamlandı |
| api-gateway | 8080 | — | **SIRADAKI** |

## Kullanılan Pattern / Kavramlar
DTO, Builder, Singleton, Constructor Injection, Repository, @RestControllerAdvice, JPA Auditing, Stream API, WebClient (.block()), Database per Service, Circuit Breaker (Resilience4j), Kafka producer/consumer

## Docker
Tüm servisler container'da çalışır. Mac'te başka projeler port tutuyor:
- PostgreSQL host port: **5434** (5432/5433 başka proje tarafından alınmış)
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

## Önemli Notlar
- Mac'te JDK: sistem 17, Maven 25 kullanıyor → **yerel mvn build çalışmaz** (Lombok uyumsuz). Docker build JDK 21 ile çalışıyor.
- Lombok fix: root pom.xml'de `annotationProcessorPaths` explicit tanımlandı
- WebClient'larda `defaultHeaders(setBasicAuth)` var — servisler arası Basic Auth (JWT gelene kadar geçici)
- TransferRequest alan adları: `senderAccountNumber` / `receiverAccountNumber`

## Git Workflow
`main ← develop ← feature/*` — feature/docker-full PR'ı develop'a merge edilecek

## Sıradaki Adımlar
1. **api-gateway (8080)** — Spring Cloud Gateway, routing, JWT filter
2. JWT — user-service login endpoint + Gateway token filter
3. Unit test — JUnit + Mockito, ServiceImpl testleri



