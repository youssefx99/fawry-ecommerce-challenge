(note that ONLY the ReadMe file is genrated by AI)

# Fawry E-commerce System Challenge

## About
This is my solution for the Fawry internship challenge. I built an e-commerce system that can handle different types of products, shopping cart, and checkout process.

## What it does
- Manages products with names, prices and stock
- Some products can expire (like food) and some dont (like electronics)
- Some products need shipping and some dont
- Customers can add items to cart
- Checkout process with validation and payment
- Calculates shipping fees for items that need it
- Prints receipt and shipping notice

## How to run
Make sure you have Java installed on your computer.

1. Download the ECommerceSystem.java file
2. Open terminal/command prompt
3. Navigate to the folder where you saved the file
4. Compile it:
   ```
   javac ECommerceSystem.java
   ```
5. Run it:
   ```
   java ECommerceSystem
   ```

## What you'll see
The program will run a test scenario where a customer buys cheese, biscuits, and a scratch card. It will show:
- Shipping notice for items that need shipping
- Receipt with all items and prices
- Total amount and customer balance

## Features implemented
- ✅ Product management (name, price, quantity)
- ✅ Expirable products (cheese, biscuits) 
- ✅ Non-expirable products (TV, scratch cards)
- ✅ Shipping for some products
- ✅ Shopping cart functionality
- ✅ Checkout with validation
- ✅ Error handling for empty cart, insufficient balance, expired products
- ✅ Shipping service with weight calculation
- ✅ Console output as required

## My assumptions
- Expirable products usually need shipping
- Shipping cost is 10 per kg
- Scratch cards dont need shipping (digital delivery)
- Prices are shown as integers in the output
- Customer balance is tracked and updated after purchase

## Code structure
- `Product` - base class for all products
- `ExpirableProduct` - products that can expire
- `NonExpirableProduct` - products that dont expire  
- `Customer` - holds customer info and balance
- `ShoppingCart` - manages cart items
- `ShippingService` - handles shipping calculations
- `ECommerceSystem` - main class with checkout logic

Built with Java using OOP principles as requested.
