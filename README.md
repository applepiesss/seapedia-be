# SEAPEDIA E-Commerce Platform

SEAPEDIA is a comprehensive multi-role e-commerce system supporting public users, buyers, sellers, drivers, and administrators. This project implements a fully functional marketplace with role-based dashboards, single-store cart behavior, complex discount structures, order SLA tracking, and robust security.

## 🚀 Running the Project & Demo Setup

SEAPEDIA includes an automated demo seeder to make evaluation incredibly easy. 

### Starting the Backend with Demo Data
To instantly generate all required roles (Admin, Seller, Buyer, Driver) along with a pre-configured store and wallets, run the Spring Boot application with the following flag:

```bash
cd seapedia-be
./gradlew bootRun --args="--seed.demo=true"
```

### Demo Accounts Generated
| Role | Username | Password | Notes |
|------|----------|----------|-------|
| Admin | `admin` | `adminseapedia` | Full monitoring access |
| Seller | `seller1` | `sellerseapedia` | "Toko Seller Seapedia" auto-created |
| Buyer | `buyer1` | `buyerseapedia` | Rp 1.000.000 wallet balance auto-created |
| Driver | `driver1` | `driverseapedia` | Ready to take delivery jobs |

*Note: You can log into any of these accounts immediately on the frontend.*

---

## 📖 API Documentation (Swagger)

The backend exposes a fully interactive OpenAPI specification via Swagger UI.
Once the backend is running, navigate to:
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

SEAPEDIA employs strict security measures across both the frontend and backend to mitigate OWASP Top 10 vulnerabilities:

### 1. SQL Injection Prevention
- The backend leverages **Spring Data JPA** and Hibernate exclusively for database interactions.
- All dynamic query parameters are strictly bound via Prepared Statements, neutralizing SQL Injection (SQLi) attack vectors.
- **Attack Mitigated:** Submitting a username like `' OR '1'='1` or a review comment like `'); DROP TABLE application_reviews; --` is treated as literal text, preventing malicious database manipulation.

### 2. XSS (Cross-Site Scripting) Prevention
- The React frontend inherently escapes HTML entities in variables (e.g. `{review.comment}`), preventing script execution.
- As an added defense-in-depth layer, **DOMPurify** is utilized to rigorously sanitize public user inputs (such as Application Reviews) before they are sent to the server.
- The UI features a robust **Toast notification system** that actively warns users if their input contains malicious payloads.
- **Attack Mitigated:** Submitting a review comment containing `<script>alert('XSS')</script>` or `<img src="x" onerror="alert(1)">` will be immediately intercepted, sanitized, and blocked by the frontend, triggering a security warning toast.

### 3. Input Validation & Data Integrity
- **Backend**: Standardized `@Valid` constraints (`@NotBlank`, `@Min`, `@Max`, `@Email`) are enforced on all DTOs. Advanced regex (`@Pattern`) is utilized to validate phone numbers.
- **Frontend**: Client-side validation prevents invalid states (e.g. negative quantities, ratings > 5) from ever reaching the network layer.
- **Attack Mitigated:** Submitting a rating of `999` or attempting to bypass cart limitations via API tools (e.g., Postman) will be cleanly rejected with a `400 Bad Request` by the Spring Boot validation layer.

### 4. Role-Based Access Control (RBAC) & Session Hardening
- **Stateless Sessions**: Authentication is handled via stateless JWTs. Upon logout, the client actively destroys the token, fully invalidating the session from the browser's perspective.
- **Strict Role Verification**: Every protected API route enforces authorization via `@PreAuthorize` annotations tailored to the user's *currently active role*, ensuring users cannot escalate privileges even if they own multiple roles.
- **Resource Ownership**: Endpoints strictly verify resource ownership.
- **Attack Mitigated:** A user with a `BUYER` role attempting to call `POST /api/seller/products` using their valid JWT will be blocked with a `403 Forbidden`. Similarly, a `DRIVER` attempting to complete a job assigned to a different driver ID will be rejected.

---

## 🧪 End-to-End Testing Guide

Follow this quick guide to test the entire lifecycle:
1. **Seed Data**: Start backend with `--seed.demo=true`.
2. **Admin Action**: Login as `admin`. Create a Voucher and Promo in the discount dashboard.
3. **Seller Action**: Login as `seller1`. Go to Seller dashboard and add a new Product with stock.
4. **Buyer Action**: Login as `buyer1`. Add the product to cart, apply the created Voucher & Promo, add an address, and Checkout.
5. **Seller Action**: Login as `seller1`. Go to Orders, click **"Siap Dikirim"** to process the order.
6. **Driver Action**: Login as `driver1`. View available jobs, Take the job, and eventually Complete it.
7. **Simulation**: As `admin`, try "Simulate Next Day" on an unprocessed order to see the Auto-Refund logic trigger.
