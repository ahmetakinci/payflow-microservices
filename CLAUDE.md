# PayFlow Microservices — Claude Context

## Proje Özeti
Spring Boot tabanlı mikroservis ödeme sistemi. Öğrenme/portföy projesi.

## Stack
- JDK 21, Spring Boot 3.2.0, Spring Cloud 2023.0.0
- PostgreSQL 16, Apache Kafka (Confluent 7.5.0)
- Maven 3.9.2 (multi-module), Docker + Docker Compose
- Lombok, Resilience4j, Spring Security

## Servisler

| Servis | Port | DB | Açıklama |
|---|---|---|---|
| user-service | 8081 | payflow_user | User CRUD, BCrypt, JWT henüz yok |
| account-service | 8082 | payflow_account | Account CRUD, debit/credit, Circuit Breaker |
| payment-service | 8083 | payflow_payment | Transfer, rollback, Kafka producer |
| notification-service | 8084 | — | Kafka consumer, log only (email yok) |
| api-gateway | 8080 | — | **SIRADAKI** — Spring Cloud Gateway, routing, JWT filter |

## Kullanılan Pattern ve Kavramlar
- DTO pattern: RegisterRequest, UserResponse, CreateAccountRequest, AccountResponse, TransferRequest, PaymentResponse, PaymentEvent
- Builder, Singleton (Spring bean), Constructor Injection (@RequiredArgsConstructor)
- Repository pattern, @RestControllerAdvice global exception handling
- JPA Auditing (@EnableJpaAuditing + @EntityListeners)
- Stream API (.map, .filter, .toList())
- WebClient (.block() ile sync) — RestTemplate değil
- Database per Service pattern
- Environment variable ile credential: ${DB_PASSWORD:postgres}
- HTTP status: 201 Created, 204 No Content, 404, 422, 503

## Docker Setup (Tam)
Her şey container içinde çalışır. Tek komutla ayağa kalkar:

```bash
docker-compose up --build
```

### Servis URL'leri (Docker içi)
- postgres: `postgres:5432`
- kafka: `kafka:9092` (Docker içi), `localhost:29092` (host erişimi)
- user-service: `user-service:8081`
- account-service: `account-service:8082`
- payment-service: `payment-service:8083`
- notification-service: `notification-service:8084`

### Sadece Altyapıyı Başlat (IntelliJ'den geliştirme için)
```bash
docker-compose up postgres zookeeper kafka
```
Servisler IntelliJ'den çalıştırılırken Kafka için `localhost:29092` kullan.

## Environment Variables (application.yml override'ları)
| Değişken | Docker değeri | Yerel default |
|---|---|---|
| DB_HOST | postgres | localhost |
| DB_USERNAME | postgres | postgres |
| DB_PASSWORD | postgres | postgres |
| USER_SERVICE_HOST | user-service | localhost |
| ACCOUNT_SERVICE_HOST | account-service | localhost |
| KAFKA_BOOTSTRAP_SERVERS | kafka:9092 | localhost:29092 |

## Git Workflow
- Branch stratejisi: `main ← develop ← feature/*`
- Feature → commit → push → PR → develop merge → develop pull
- Tüm tamamlanan özellikler merge edildi, develop güncel


- Öğreterek ilerle, salt kod üretme


- Yanlış/eksik cevaplarda doğrudan düzelt, yaltaklanma
- Kısa ve öz cevaplar
- Mevcut mimariyi koru, yeni soyutlama icat etme

## Sıradaki Adımlar
1. **api-gateway (8080)** — Spring Cloud Gateway, routing, JWT filter ← DEVAM
2. JWT implementasyonu — user-service login, Gateway token filter
3. (Opsiyonel) SAGA pattern
4. (Opsiyonel) Flyway/Liquibase
5. Unit test — JUnit + Mockito ile ServiceImpl testleri



