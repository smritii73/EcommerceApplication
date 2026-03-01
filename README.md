# E-commerce Application 🛒

A production-ready microservices e-commerce platform built with Spring Boot, demonstrating distributed systems architecture, service orchestration, and cloud-native design patterns. Comprises two independent microservices: Product Service (catalog management) and Order Service (transaction processing) coordinated via Eureka service discovery.

**Project Status**: Development Complete | Microservices Architecture | Local Development  
**Architecture Pattern**: Client-Side Load Balancing via Eureka  
**Deployment**: Local Development (Spring Boot + MySQL)

---

## System Overview

The E-commerce platform implements a **distributed microservices architecture** where independent services communicate via REST APIs with intelligent client-side load balancing. Rather than traditional API gateways, this design leverages Eureka's service discovery for lightweight inter-service communication.

### Core Philosophy

**Decoupled Services**: Each service manages its own database, business logic, and deployment lifecycle  
**Service Discovery**: Automatic instance registration/discovery via Eureka eliminates hardcoded URLs  
**Client-Side Load Balancing**: Services discover and balance requests to available instances  
**Database per Service**: Product and Order data completely isolated (prevents tight coupling)

### Key Design Decisions

1. **No API Gateway Required** - For internal microservice-to-microservice communication, client-side load balancing via Eureka is sufficient. API gateways only become necessary when serving 3rd party clients or complex routing policies.

2. **@LoadBalanced RestTemplate** - Eureka-aware RestTemplate intercepts service name lookups and automatically resolves to available instances with round-robin load balancing.

3. **Flyway for Schema Management** - Versioned SQL migrations ensure consistent database evolution across deployments.

---

## Architecture (2 Independent Microservices)

### 1️⃣ Product Service (SpringEcommerce)

**Responsibility**: Product catalog, categories, inventory, and search functionality  
**Port**: Configurable (default 8000)  
**Database**: MySQL (ecommerce_db)

#### Core Features

**Product Management:**
- Create, retrieve, and filter products
- Support for rich product attributes (brand, color, model, discount, popularity)
- Full-text search across product names and descriptions
- Category-based filtering and product segmentation

**Search & Discovery:**
- **Price Filtering**: Find products above minimum price threshold
- **Brand Filtering**: Search by specific brand with price constraints
- **Keyword Search**: Full-text search using MySQL MATCH/AGAINST
- **Category Navigation**: Retrieve all products within a category
- **Product Details**: Fetch product with associated category information

**Data Model:**
```
Category (1) ──→ (Many) Product
├─ id (PK)           ├─ id (PK)
├─ name (UNIQUE)     ├─ title
└─ description       ├─ price, discount
                     ├─ image, color, model
                     ├─ brand, popular
                     ├─ description
                     ├─ category_id (FK)
                     └─ rating, expiry
```

#### Key Technical Implementations

**Dual Data Access Patterns:**
- **Database Service**: Retrieves from local MySQL with advanced querying
- **External API Service**: Integrates with FakeStore API for demo catalog
- **Strategy Pattern**: Service layer abstraction allows switching implementations

**Query Optimization:**
- HQL for type-safe, entity-aware queries
- Native SQL with FULLTEXT indexes for advanced search
- Strategic database indexing on frequently-accessed fields
- Lazy loading on relationships to minimize unnecessary data transfer

**API Endpoints:**
```
GET    /api/products/{id}                    → Fetch product details
GET    /api/products/{id}/details            → Product with category info
POST   /api/products                         → Create new product
GET    /api/products                         → Advanced filtering
  ?minPrice=5000&brand=Nike                  → Search by brand + price
  ?keyword=shoe                              → Full-text search
  ?categoryId=3                              → Category filtering

GET    /api/categories                       → List all categories
GET    /api/categories?name=Electronics      → Find category by name
POST   /api/categories                       → Create category
```

#### Service Integration Pattern

**FakeStore Integration:**
```
ProductController
    ↓
IProductService (Interface)
    ├─→ ProductService (DB-backed)
    │   └─→ ProductRepository (JPA)
    │       └─→ MySQL
    │
    └─→ FakeStoreProductService (External API)
        └─→ FakeStoreProductGateway
            └─→ Retrofit2 HTTP Client
                └─→ FakeStore API
```

Supports seamless switching between local database and external API sources via Spring's @Qualifier annotation.

---

### 2️⃣ Order Service (EcommerceOrderServiceSpring)

**Responsibility**: Order processing, transaction management, and inter-service communication  
**Port**: Configurable (default 8082)  
**Database**: MySQL (order_db)  
**Depends On**: Product Service (via Eureka discovery)

#### Core Features

**Order Lifecycle:**
1. **Order Creation**: Accept order request with multiple items
2. **Product Validation**: Query Product Service via RestTemplate
3. **Price Calculation**: Fetch current prices from Product Service
4. **Order Persistence**: Store order and items atomically
5. **Status Tracking**: Monitor order progression (PENDING → COMPLETED/CANCELLED)

**Order Processing Flow:**
```
Client submits OrderRequest
    ↓
OrderController validates input
    ↓
OrderService orchestrates creation:
    ├─ For each item in order:
    │   ├─ Call ProductServiceClient.getProductById(productId)
    │   │   (RestTemplate resolves service name via Eureka)
    │   ├─ Retrieve current product price
    │   └─ Calculate item total (price × quantity)
    ├─ Create Order entity with PENDING status
    ├─ Create OrderItem entities with pricing
    └─ Persist order atomically
    ↓
Return CreateOrderResponseDTO with orderId + status
```

**Data Model:**
```
Order (1) ──→ (Many) OrderItem
├─ id (PK)           ├─ id (PK)
├─ userId            ├─ productId (FK ref to Product Service)
├─ orderStatus       ├─ quantity
│  (PENDING/          ├─ pricePerUnit (snapshot at order time)
│   COMPLETED/       ├─ totalPrice (calculated)
│   CANCELLED)       └─ order_id (FK to Order)
└─ items
```

#### Key Technical Implementations

**Service-to-Service Communication:**

```java
@Component
public class ProductServiceClient {
    private final RestTemplate restTemplate;
    
    public ProductDTO getProductById(Long productId) {
        // Eureka-aware RestTemplate resolves "ECOMMERCESPRING" to actual instance URL
        String url = "http://ECOMMERCESPRING/api/products/" + productId;
        ResponseEntity<ProductDTO> response = restTemplate.getForEntity(url, ProductDTO.class);
        return response.getBody();
    }
}

@Configuration
public class AppConfig {
    @Bean
    @LoadBalanced  // Enables Eureka service discovery + client-side load balancing
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

**How Eureka Service Discovery Works:**
1. Product Service registers with Eureka on startup: `ECOMMERCESPRING` → `[10.0.0.12:8000, 10.0.0.13:8000, ...]`
2. Order Service makes request to `http://ECOMMERCESPRING/api/products/1`
3. @LoadBalanced RestTemplate intercepts the request
4. Eureka client resolves logical name to available instances
5. Ribbon load balancer picks an instance (round-robin)
6. Request forwarded to actual IP:port

**Price Snapshot Pattern:**
- Order Service captures product price at order creation time
- Stored in OrderItem.pricePerUnit to prevent price manipulation
- Historical pricing immutable for auditing

**API Endpoints:**
```
POST   /api/orders                 → Create new order
       Request: { userId, items: [{ productId, quantity }] }
       Response: { orderId, orderStatus: PENDING }
```

#### Resilience & Error Handling

- **Null Checks**: RestTemplate returns null if service unavailable (currently no retry)
- **Exception Propagation**: IOException allows caller to handle service failures
- **Future Enhancement**: Circuit breaker pattern (Hystrix) for fault tolerance

---

## Technology Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| **Framework** | Spring Boot | 3.x | Application framework + auto-configuration |
| **Service Discovery** | Spring Cloud Eureka | Latest | Service registration and discovery |
| **Load Balancing** | Ribbon/Spring LB | Built-in | Client-side request balancing |
| **Web** | Spring MVC | 3.x | REST API controllers |
| **Data Access** | Spring Data JPA/Hibernate | 3.x | ORM and repository pattern |
| **Database** | MySQL | 8.0+ | Relational data store |
| **API Clients** | Retrofit2 | 2.9+ | Type-safe HTTP client (external APIs) |
| **RestTemplate** | Spring Web | 3.x | HTTP requests to microservices |
| **Build** | Gradle | 8.14+ | Dependency management and build |
| **Environment Config** | dotenv-java | Latest | .env file support for secrets |
| **Auditing** | Spring Data JPA Auditing | Built-in | Automatic createdAt/updatedAt tracking |
| **Lombok** | Lombok | 1.18+ | Reduce boilerplate code |
| **Database Migrations** | Flyway | Latest | Versioned schema management |

---

## Setup & Running Locally

### Prerequisites

- **Java 17+** (Spring Boot 3.x requires Java 17 minimum)
- **MySQL 8.0+** (local or Docker)
- **Gradle 8.14+** (or use included wrapper ./gradlew)
- **.env file** for environment variables
- **Eureka Server** (for service discovery; optional for basic testing)

### Installation

**1. Clone Repository**
```bash
git clone <repository-url>
cd ecommerce-microservices
```

**2. Install Dependencies**
```bash
# Both services use Gradle
./gradlew build
```

**3. Database Setup**

**Create MySQL Databases:**
```sql
CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4;
CREATE DATABASE order_db CHARACTER SET utf8mb4;
```

**4. Environment Configuration**

Create `.env` file in project root:
```bash
# Product Service (.env for SpringEcommerce)
sql_username=root
sql_password=your_password
PORT=8000
eureka_url=http://localhost:8761/eureka/

# Order Service (.env for EcommerceOrderServiceSpring)
sql_username=root
sql_password=your_password
PORT=8082
eureka_url=http://localhost:8761/eureka/

# External API Integration (FakeStore)
Fake_Store_Base_Url=https://fakestoreapi.in/api/
```

**5. Start MySQL**
```bash
# Using Docker
docker run --name mysql -e MYSQL_ROOT_PASSWORD=your_password \
  -e MYSQL_DATABASE=ecommerce_db \
  -p 3306:3306 -d mysql:8.0

# Or local MySQL
mysql -u root -p
```

**6. Start Eureka Server** (optional but recommended)

```bash
# Download Eureka Server or use Spring Cloud starter
# Default URL: http://localhost:8761

# Or create lightweight Eureka server with Spring Cloud
mkdir eureka-server && cd eureka-server
spring boot init --name eureka-server --dependencies 'eureka-server' ...
```

**7. Start Product Service**
```bash
cd SpringEcommerce
./gradlew bootRun
```

Expected output:
```
✅ Successfully Connected to Database
✅ Registered with Eureka as ECOMMERCESPRING
✅ Server running on port 8000
```

**8. Start Order Service** (new terminal)
```bash
cd EcommerceOrderServiceSpring
./gradlew bootRun
```

Expected output:
```
✅ Successfully Connected to Database
✅ Registered with Eureka as ECOMMERCEORDERSERVICESPRING
✅ Server running on port 8082
```

**9. Verify Services**
```bash
# Check Eureka dashboard
curl http://localhost:8761

# Test Product Service
curl http://localhost:8000/api/products/1

# Test Order Service (cross-service communication)
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      { "productId": 1, "quantity": 2 },
      { "productId": 5, "quantity": 1 }
    ]
  }'
```

---

## API Testing Examples

### Product Service

**Get Single Product:**
```bash
curl http://localhost:8000/api/products/1
```

**Response:**
```json
{
  "id": 1,
  "title": "iPhone 13",
  "price": 80000,
  "discount": 10,
  "brand": "Apple",
  "color": "Black",
  "image": "https://...",
  "description": "Latest iPhone model",
  "categoryId": 1,
  "popular": true
}
```

**Search by Price:**
```bash
curl "http://localhost:8000/api/products?minPrice=5000"
```

**Filter by Brand & Price:**
```bash
curl "http://localhost:8000/api/products?brand=Nike&minPrice=3000"
```

**Full-Text Search:**
```bash
curl "http://localhost:8000/api/products?keyword=shoe"
```

**Get Category Products:**
```bash
curl "http://localhost:8000/api/products?categoryId=2"
```

**Get Product with Category:**
```bash
curl http://localhost:8000/api/products/1/details
```

**Response:**
```json
{
  "id": 1,
  "title": "iPhone 13",
  "price": 80000,
  "category": {
    "id": 1,
    "name": "Electronics"
  }
}
```

**Create Category:**
```bash
curl -X POST http://localhost:8000/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphones",
    "description": "Mobile phones and devices"
  }'
```

### Order Service

**Create Order (Cross-Service Communication):**
```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 42,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      },
      {
        "productId": 5,
        "quantity": 1
      }
    ]
  }'
```

**Order Creation Flow:**
```
Request received with:
  - userId: 42
  - items: [
      {productId: 1, quantity: 2},
      {productId: 5, quantity: 1}
    ]

OrderService processes:
  1. Create Order with PENDING status
  2. For each item:
     a. Call ProductServiceClient.getProductById(1)
        → RestTemplate resolves "ECOMMERCESPRING" via Eureka
        → Fetches product data from Product Service
     b. Extract price: ₹80,000
     c. Calculate total: 80,000 × 2 = 160,000
     d. Create OrderItem with price snapshot
  3. Persist order atomically
  4. Return response

Response:
{
  "orderId": 101,
  "orderStatus": "PENDING"
}
```

---

## Project Structure

```
ecommerce-microservices/
│
├── SpringEcommerce/                      # Product Service
│   ├── src/main/java/ecommercespring/
│   │   ├── controllers/
│   │   │   ├── ProductController.java     # REST endpoints for products
│   │   │   └── CategoryController.java    # REST endpoints for categories
│   │   │
│   │   ├── services/
│   │   │   ├── IProductService.java       # Product business logic interface
│   │   │   ├── ProductService.java        # Database-backed implementation
│   │   │   ├── FakeStoreProductService.java  # External API implementation
│   │   │   ├── ICategoryService.java      # Category interface
│   │   │   ├── CategoryService.java       # Category implementation
│   │   │   └── FakeStoreCategoryService.java # Category external API
│   │   │
│   │   ├── repository/
│   │   │   ├── ProductRepository.java     # JPA queries for products
│   │   │   └── CategoryRepository.java    # JPA queries for categories
│   │   │
│   │   ├── entity/
│   │   │   ├── BaseEntity.java            # Common fields (id, createdAt, updatedAt)
│   │   │   ├── Product.java               # Product entity with category FK
│   │   │   └── Category.java              # Category entity
│   │   │
│   │   ├── dto/
│   │   │   ├── ProductDTO.java            # Data transfer object
│   │   │   ├── CategoryDTO.java
│   │   │   ├── ProductWithCategoryDTO.java # Product + category combined
│   │   │   └── FakeStore*ResponseDTO.java # API response mappers
│   │   │
│   │   ├── gateway/                       # External API clients
│   │   │   ├── FakeStoreProductGateway.java
│   │   │   ├── FakeStoreCategoryGateway.java
│   │   │   ├── FakeStoreRestTemplateGateway.java
│   │   │   └── api/
│   │   │       ├── IFakeStoreProductApi.java   # Retrofit interface
│   │   │       └── IFakeStoreCategoryApi.java
│   │   │
│   │   ├── mappers/
│   │   │   ├── ProductMapper.java         # Entity ↔ DTO transformations
│   │   │   └── CategoryMapper.java
│   │   │
│   │   ├── configuration/
│   │   │   └── RetrofitConfig.java        # Retrofit2 bean setup
│   │   │
│   │   ├── exception/
│   │   │   ├── ProductNotFoundException.java
│   │   │   ├── CategoryNotFoundException.java
│   │   │   ├── ErrorResponse.java         # Standardized error format
│   │   │   └── GlobalExceptionHandler.java # @RestControllerAdvice
│   │   │
│   │   └── EcommerceSpringApplication.java
│   │
│   ├── src/main/resources/
│   │   ├── application.properties         # Database + Eureka config
│   │   └── db/migration/
│   │       ├── V1__create_category_table.sql
│   │       ├── V2__create_product_table.sql
│   │       ├── V3__add_rating_column_to_product.sql
│   │       ├── V4__add_description_to_category.sql
│   │       └── V5__add_product_expiry_to_product.sql
│   │
│   └── build.gradle                      # Gradle configuration
│
├── EcommerceOrderServiceSpring/           # Order Service
│   ├── src/main/java/ecommerceorderservicespring/
│   │   ├── controllers/
│   │   │   └── OrderController.java       # Create order endpoint
│   │   │
│   │   ├── services/
│   │   │   ├── IOrderService.java         # Order business logic
│   │   │   └── OrderService.java          # Orchestrates order creation
│   │   │
│   │   ├── repository/
│   │   │   └── OrderRepository.java       # JPA for orders
│   │   │
│   │   ├── entity/
│   │   │   ├── BaseEntity.java
│   │   │   ├── Order.java                 # Order aggregate root
│   │   │   └── OrderItem.java             # Items in order
│   │   │
│   │   ├── dto/
│   │   │   ├── OrderRequestDTO.java       # Create order input
│   │   │   ├── OrderItemRequestDTO.java   # Item in request
│   │   │   ├── CreateOrderResponseDTO.java # Order creation response
│   │   │   └── ProductDTO.java            # From Product Service
│   │   │
│   │   ├── clients/
│   │   │   └── ProductServiceClient.java  # Call Product Service via Eureka
│   │   │
│   │   ├── mapper/
│   │   │   ├── OrderMapper.java           # Entity ↔ DTO
│   │   │   └── OrderItemRequestMapper.java
│   │   │
│   │   ├── config/
│   │   │   └── AppConfig.java             # @LoadBalanced RestTemplate bean
│   │   │
│   │   ├── enums/
│   │   │   └── OrderStatus.java           # PENDING, COMPLETED, CANCELLED
│   │   │
│   │   └── EcommerceOrderServiceSpringApplication.java
│   │
│   ├── src/main/resources/
│   │   └── application.properties         # Order Service config
│   │
│   └── build.gradle
│
└── README.md
```

---

## Key Technical Patterns

### 1. Service Discovery with Eureka

**Problem**: Services running on multiple dynamic IPs. Hardcoding URLs is inflexible.

**Solution**: Service registers logical name with Eureka server

```java
// Product Service registers as "ECOMMERCESPRING"
eureka.instance.appname=ECOMMERCESPRING

// Order Service discovers via logical name
String url = "http://ECOMMERCESPRING/api/products/" + id;
ResponseEntity<ProductDTO> response = restTemplate.getForEntity(url, ProductDTO.class);

// @LoadBalanced RestTemplate handles:
// 1. Query Eureka for ECOMMERCESPRING instances
// 2. Get list: [10.0.0.12:8000, 10.0.0.13:8000, ...]
// 3. Apply load balancing (round-robin)
// 4. Forward request to selected instance
```

### 2. Database per Service Pattern

**Product Service Database**: ecommerce_db
```sql
- categories (id, name, description, created_at, updated_at)
- products (id, title, price, brand, category_id, created_at, updated_at, ...)
```

**Order Service Database**: order_db
```sql
- orders (id, user_id, order_status, created_at, updated_at)
- order_items (id, product_id, quantity, price_per_unit, total_price, order_id, ...)
```

**Benefits:**
- Services scale independently
- No shared database coupling
- Freedom to choose different databases per service
- Easier to manage schema changes

**Trade-off:** Order Service only stores `productId`, not full product data. Fetches from Product Service at order creation time for current pricing.

### 3. Data Transfer Object (DTO) Pattern

**Why DTOs?**
- Decouples internal entity structure from API contract
- Prevents exposure of internal relationships
- Allows selective field inclusion

```java
// Entity (internal)
@Entity
public class Product extends BaseEntity {
    private String title;
    private int price;
    @ManyToOne
    private Category category;  // Full object reference
}

// DTO (API contract)
@Builder
public class ProductDTO {
    private String title;
    private int price;
    private Long categoryId;    // Only ID, not full category
}

// Controller returns DTO
@GetMapping("/{id}")
public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
    Product product = productService.getProductById(id);
    return ResponseEntity.ok(ProductMapper.toDto(product));
}
```

### 4. Strategy Pattern for Multiple Implementations

```
IProductService (Interface)
├── ProductService (Queries local MySQL)
├── FakeStoreProductService (Calls external API)
└── Controller chooses via @Qualifier

@RestController
public class ProductController {
    public ProductController(
        @Qualifier("productService") IProductService service) {
        this.productService = service;
    }
}
```

Allows seamless switching between implementations without changing controller code.

### 5. Mapper Pattern for Entity ↔ DTO Conversion

```java
public class ProductMapper {
    public static ProductDTO toDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .price(product.getPrice())
                .categoryId(product.getCategory().getId())
                .build();
    }
    
    public static Product toEntity(ProductDTO dto, Category category) {
        return Product.builder()
                .title(dto.getTitle())
                .price(dto.getPrice())
                .category(category)
                .build();
    }
    
    public static List<ProductDTO> toDtoList(List<Product> products) {
        return products.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }
}
```

**Benefits:**
- Single responsibility (conversion logic isolated)
- Reusable across service layer
- Easy to test
- Prevents null pointer exceptions with builder pattern

### 6. Base Entity with Automatic Auditing

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;  // Set once, never updated
    
    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;  // Updated on every change
    
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
```

All entities extending BaseEntity automatically track timestamps without boilerplate.

### 7. Custom JPA Query Methods

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Method naming convention - JPA auto-generates SQL
    Optional<Category> findByName(String name);
    
    // HQL for complex queries
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice")
    List<Product> findExpensiveProducts(@Param("minPrice") double minPrice);
    
    // Native SQL for advanced features (FULLTEXT)
    @Query(
        value = "SELECT * FROM product WHERE MATCH(title, description) AGAINST (:keyword)",
        nativeQuery = true
    )
    List<Product> searchFullText(@Param("keyword") String keyword);
    
    // Multiple parameters
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice AND p.brand = :brand")
    List<Product> searchByBrandAndMinPrice(
        @Param("brand") String brand,
        @Param("minPrice") double minPrice
    );
    
    // Relationship traversal
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> getAllProductsOfACategory(@Param("categoryId") Long categoryId);
}
```

### 8. Flyway Database Migrations

**Version Control for Databases:**
```
src/main/resources/db/migration/
├── V1__create_category_table.sql          (First deployment)
├── V2__create_product_table.sql           (Add products)
├── V3__add_rating_column_to_product.sql   (Enhancement)
├── V4__add_description_to_category.sql    (New feature)
└── V5__add_product_expiry_to_product.sql  (Future)
```

**Benefits:**
- Schema changes tracked like code
- Reproducible deployments
- Easy to rollback (create new downgrade script)
- Team synchronization

**How it works:**
1. Application starts
2. Flyway scans `db/migration` folder
3. Checks `flyway_schema_history` table
4. Executes only new scripts (V3, V4, V5 if V1, V2 already run)
5. Updates history with checksums for integrity

---

## Configuration Management

### Environment Variables (.env)

```bash
# Database Configuration
sql_username=root
sql_password=your_secure_password
# Note: Uses dotenv-java to load .env file at startup

# Eureka Service Discovery
eureka_url=http://localhost:8761/eureka/

# Port Configuration (per service)
PORT=8000  # Product Service
PORT=8082  # Order Service

# External API
Fake_Store_Base_Url=https://fakestoreapi.in/api/
```

### Application Properties (application.properties)

**Database Connection:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

**Hibernate Configuration:**
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate  # Don't auto-generate, use Flyway
```

**Flyway Migrations:**
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

**Eureka Client:**
```properties
eureka.client.service-url.defaultZone=${eureka_url}
```

---

## Database Schema

### Product Service (ecommerce_db)

**Categories Table:**
```sql
CREATE TABLE category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  description VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Products Table:**
```sql
CREATE TABLE product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  image VARCHAR(255),
  color VARCHAR(50),
  price INT,
  description TEXT,
  discount INT,
  model VARCHAR(100),
  title VARCHAR(255),
  brand VARCHAR(100),
  popular BOOLEAN DEFAULT FALSE,
  rating INT DEFAULT 0,
  expiry TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  category_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id),
  FULLTEXT INDEX ft_title_description (title, description)
);
```

### Order Service (order_db)

**Orders Table:**
```sql
CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  order_status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Order Items Table:**
```sql
CREATE TABLE order_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  price_per_unit DOUBLE NOT NULL,
  total_price DOUBLE NOT NULL,
  order_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

---

## Error Handling

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(
        ProductNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

**Error Response Format:**
```json
{
  "status": 404,
  "message": "Product with ID 999 not found",
  "timestamp": "2025-03-01T10:30:00"
}
```

### Custom Exceptions

```java
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
```

**Why RuntimeException?** It's unchecked, so callers aren't forced to explicitly handle it. This allows optional exception handling while maintaining clean APIs.

---

## Performance Optimizations

### 1. Database Indexing

**Full-Text Search Index:**
```sql
CREATE FULLTEXT INDEX ft_title_description ON product(title, description);
```

Enables fast searching across product names and descriptions without table scans.

**Query with Index:**
```sql
SELECT * FROM product 
WHERE MATCH(title, description) AGAINST ('laptop');
```

### 2. Lazy Loading

```java
@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
private List<Product> products;
```

Products aren't loaded until explicitly accessed. Prevents N+1 query problems.

### 3. DTO Selective Loading

Instead of loading full entity graphs, services fetch only necessary fields via projections.

### 4. Connection Pooling

Spring Data JPA automatically configures HikariCP for connection pooling, handling multiple concurrent requests efficiently.

---

## Testing

### Unit Test Example

```java
@SpringBootTest
class ProductServiceTest {
    
    @Autowired
    private ProductService productService;
    
    @MockBean
    private ProductRepository productRepository;
    
    @Test
    void testGetProductById_Success() {
        // Arrange
        Long productId = 1L;
        Product mockProduct = Product.builder()
            .id(productId)
            .title("iPhone")
            .price(80000)
            .build();
        
        when(productRepository.findById(productId))
            .thenReturn(Optional.of(mockProduct));
        
        // Act
        ProductDTO result = productService.getProductById(productId);
        
        // Assert
        assertEquals("iPhone", result.getTitle());
        assertEquals(80000, result.getPrice());
    }
}
```
---

## Deployment Architecture

### Current Setup (Local Development)

```
localhost:8761 ─ Eureka Server
    │
    ├─ localhost:8000 ─ Product Service (MySQL ecommerce_db)
    │
    └─ localhost:8082 ─ Order Service (MySQL order_db)
```

All services running locally on single machine with Gradle build tool. MySQL database connections configured via .env file.

---

## Code Quality & Best Practices

### Architecture Principles Applied

**Separation of Concerns:**
- Controllers handle HTTP concerns only
- Services contain business logic
- Repositories handle data access
- Mappers handle transformation

**Dependency Injection:**
- All dependencies injected via constructor
- No `new` keyword in business code
- Enables testing and loose coupling

**Interface-Driven Design:**
- IProductService, ICategoryService interfaces
- Multiple implementations (DB-backed, API-backed)
- Easy to swap implementations

**DRY Principle:**
- Mapper classes eliminate repetitive transformations
- Base entity for common auditing fields
- Repository custom methods for query reuse

---
Development Status: Complete & Functional  
Deployment Status: Local Development Only
