# SEAPEDIA — Backend (Spring Boot)

Spring Boot REST API for the SEAPEDIA multi-role e-commerce platform.  
Handles authentication, products, orders, payments, delivery jobs, discounts, and admin monitoring.

---

## 🛠️ How to Run Locally

### Prerequisites

| Tool | Version |
|------|---------|
| **Java** | 17 or later |
| **Gradle** | Bundled (use `./gradlew`) |
| **PostgreSQL** | Any, or use [Supabase](https://supabase.com) |

---

### 1. Configure Environment Variables

```bash
cp .env.example .env
```

Open `.env` and fill in your values:

```env
# PostgreSQL / Supabase connection
SUPABASE_DB_URL=jdbc:postgresql://<host>:<port>/<database>?sslmode=require
SUPABASE_DB_USERNAME=postgres
SUPABASE_DB_PASSWORD=your_database_password

# JWT — generate with: openssl rand -base64 32
JWT_SECRET=your_jwt_secret_base64_encoded
JWT_EXPIRATION_MS=86400000
```

> **Using Supabase?**  
> Go to **Supabase Dashboard → Settings → Database → Connection String (URI)**.  
> Copy the URI and paste it as `SUPABASE_DB_URL`. The username and password are the same as shown there.

---

### 2. Run the Backend

```bash
./gradlew bootRun
```

The API will be available at **http://localhost:8080**.

> **First time running?** Seed demo accounts automatically:
> ```bash
> ./gradlew bootRun --args="--seed.demo=true"
> ```

---

### 3. Demo Accounts (after seeding)

| Role | Username | Password | Notes |
|------|----------|----------|-------|
| Admin | `admin` | `adminseapedia` | Full monitoring access |
| Seller | `seller1` | `sellerseapedia` | Store "Toko Seller Seapedia" auto-created |
| Buyer | `buyer1` | `buyerseapedia` | Rp 1.000.000 wallet balance auto-created |
| Driver | `driver1` | `driverseapedia` | Ready to take delivery jobs |

---

### 4. API Documentation (Swagger)

Once the backend is running, open:  
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 🏗️ Core Business Rules Documentation

### 1. Single-Store Checkout Rule
- A Buyer's cart may only contain products from a **single Seller store** at any given time.
- If a user attempts to add a product from a different store, the backend explicitly rejects the request.
- The UI prompts the user to either checkout the current store's items or clear their cart before proceeding with the new store.

### 2. Pricing, Discount, and PPN 12% Rules
- **Discounts**: Buyers can apply both a **Voucher** (flat amount reduction) and a **Promo** (percentage reduction) simultaneously. The Voucher is subtracted *before* the Promo percentage is applied.
- **PPN**: A flat **12% tax** is added to the total *after* all discounts are applied.
- **Delivery Fee**: The delivery fee is added *after* the PPN calculation.
- **Formula**:
  ```text
  Subtotal = SUM(Price * Quantity)
  Discounted Subtotal = (Subtotal - Voucher Amount) * (1 - Promo Percent)
  PPN = Discounted Subtotal * 0.12
  Final Total = Discounted Subtotal + PPN + Delivery Fee
  ```

### 3. Driver Earnings Rule
- Drivers earn exactly **100% of the Delivery Fee** paid by the Buyer during checkout.
- When an order reaches `PESANAN_SELESAI`, the delivery job is marked as `COMPLETED`, and the delivery fee amount is permanently added to the Driver's total earnings.

### 4. Overdue SLA Handling & Time Simulation
- Each delivery method has a maximum Service Level Agreement (SLA) time:
  - Instant: 24 Hours
  - Next Day: 48 Hours
  - Regular: 120 Hours
- If a Seller fails to process an order (`SEDANG_DIKEMAS`) or a Driver takes too long (`SEDANG_DIKIRIM`), the order becomes overdue.
- Overdue orders are automatically marked as `DIKEMBALIKAN` and the buyer receives a 100% refund to their wallet.
- **How to simulate time**: The Admin dashboard includes a **"Simulate Next Day"** action. This triggers a backend endpoint that retroactively shifts the `createdAt` timestamp of all active orders back by 24 hours and triggers the overdue verification job.

---

## 🔒 Security Hardening

SEAPEDIA employs strict security measures to mitigate OWASP Top 10 vulnerabilities:

### 1. SQL Injection Prevention
- The backend leverages **Spring Data JPA** and Hibernate exclusively for database interactions.
- All dynamic query parameters are strictly bound via Prepared Statements — no raw SQL.
- **Attack mitigated:** Submitting a username like `' OR '1'='1` or a review like `'); DROP TABLE application_reviews; --` is treated as literal text.

### 2. XSS (Cross-Site Scripting) Prevention
- React inherently escapes HTML entities in variables (e.g. `{review.comment}`), preventing script execution.
- **DOMPurify** sanitizes all public user inputs (e.g. Application Reviews) before they are sent to the server.
- The global Toast notification system warns users when a malicious payload is detected and blocked.
- **Attack mitigated:** A review containing `<script>alert('XSS')</script>` or `<img src="x" onerror="alert(1)">` is intercepted, sanitized, and blocked with a security warning toast.

### 3. Input Validation & Data Integrity
- **Backend:** Standardized `@Valid` constraints (`@NotBlank`, `@Min`, `@Max`, `@Email`, `@Pattern`) are enforced on all DTOs. Advanced regex validates phone numbers.
- **Frontend:** Client-side validation prevents invalid states (e.g. negative quantities, ratings > 5) from reaching the network layer.
- **Attack mitigated:** Submitting a rating of `999` or bypassing cart limits via API tools (e.g. Postman) will be cleanly rejected with `400 Bad Request`.

### 4. Role-Based Access Control (RBAC) & Session Hardening
- **Stateless JWTs:** Upon logout, the client destroys the token, fully invalidating the session from the browser's perspective.
- **Strict role verification:** Every protected route enforces authorization via `@PreAuthorize` annotations scoped to the user's *currently active role* — users cannot escalate privileges even if they own multiple roles.
- **Resource ownership:** Endpoints strictly verify resource ownership.
- **Attack mitigated:** A `BUYER` calling `POST /api/seller/products` with a valid JWT gets `403 Forbidden`. A `DRIVER` trying to complete a job assigned to a different driver is rejected.

---

## 🧪 End-to-End Test Flow

1. **Seed** — start with `--seed.demo=true`
2. **Admin** — create a Voucher and Promo
3. **Seller** — add a product with stock
4. **Buyer** — add to cart, apply discounts, add address, checkout
5. **Seller** — go to Orders → process the order ("Siap Dikirim")
6. **Driver** — find the job, take it, complete delivery
7. **Admin** — use "Simulate Next Day" to trigger the overdue auto-refund demo
