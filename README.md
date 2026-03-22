# Omnixys Observability

Enterprise-grade observability library for Spring Boot microservices.

## Features

- Structured logging
- Scoped operation logging
- OpenTelemetry context integration
- TraceId / SpanId enrichment
- Spring Boot auto-configuration
- Reusable across services

---

## Installation

```kotlin
dependencies {
    implementation("com.omnixys:omnixys-observability:1.0.0")
}
````

---

## Configuration

```yaml
omnixys:
  observability:
    service-name: user-service
```

---

## Usage

### Logger

```java
@Autowired
private ObservabilityLogger logger;

logger.info("User created", Map.of("userId", id));
```

### Scoped Logger

```java
var scope = logger.scope("createUser");

scope.start();
scope.info("Processing user", Map.of("id", id));
scope.success();
```

---

## Tracing

Uses OpenTelemetry automatically:

* traceId from active context
* spanId from active context
* no manual propagation required
