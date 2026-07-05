## Level 3 Buyer Wallet, Cart, and Checkout

### Buyer Wallet
Buyers have a wallet balance stored in the backend. The top-up flow is a dummy simulation, but every top-up is stored as a wallet transaction. Checkout payments are also recorded as wallet transactions.

### Delivery Address
Each Buyer can save one delivery address containing recipient name, phone number, and full address. The address is required before checkout.

### Single-Store Checkout Rule
SEAPEDIA is a multi-seller marketplace, but one cart may only contain products from one Seller store at a time. If a Buyer tries to add a product from a different store, the backend rejects the request with a clear error. The Buyer must clear or remove existing cart items before adding products from another store.

### Delivery Fees
Delivery methods and fees:
- Instant: Rp20.000
- Next Day: Rp12.000
- Regular: Rp8.000

### PPN 12%
PPN is calculated as 12% of the product subtotal. Delivery fee is added after the PPN calculation.

Formula:
```text
subtotal = sum(product price * quantity)
ppn = subtotal * 12%
final total = subtotal + ppn + delivery fee
```

### Checkout Rules
A Buyer cannot checkout if:
- wallet balance is insufficient
- cart is empty
- product stock is insufficient
- stock reduction would make stock negative

After successful checkout:
- wallet balance is reduced
- product stock is reduced
- cart is cleared
- order status starts as `SEDANG_DIKEMAS`
- initial status history is stored with timestamp

### Seller Incoming Orders
Sellers can view incoming orders for their own store only. Seller order processing is introduced in Level 4.
