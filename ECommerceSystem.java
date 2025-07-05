import java.time.LocalDate;
import java.util.*;

interface Shippable {
    String getName();
    double getWeight();
}

// base product class - all products inherit from this
abstract class Product {
    protected String productName;
    protected double productPrice;
    protected int availableStock;
    
    public Product(String productName, double productPrice, int availableStock) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.availableStock = availableStock;
    }
    
    // THe getters and setters
    public String getProductName() { return productName; }
    public double getProductPrice() { return productPrice; }
    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int stock) { this.availableStock = stock; }
    
    // Cehck some constrains 
    public abstract boolean checkIfExpired();
    public abstract boolean needsShipping();
    public abstract double getItemWeight();
}

class ExpirableProduct extends Product {
    private LocalDate expiry;
    private double itemWeight;
    
    public ExpirableProduct(String productName, double productPrice, int availableStock, 
                          LocalDate expiry, double itemWeight) {
        super(productName, productPrice, availableStock);
        this.expiry = expiry;
        this.itemWeight = itemWeight;
    }
    
    @Override
    public boolean checkIfExpired() {
        return LocalDate.now().isAfter(expiry);
    }
    
    @Override
    public boolean needsShipping() {
        return true; 
    }
    
    @Override
    public double getItemWeight() {
        return itemWeight;
    }
}

class NonExpirableProduct extends Product {
    private boolean requiresShipping;
    private double itemWeight;
    
    public NonExpirableProduct(String productName, double productPrice, int availableStock, 
                             boolean requiresShipping, double itemWeight) {
        super(productName, productPrice, availableStock);
        this.requiresShipping = requiresShipping;
        this.itemWeight = itemWeight;
    }
    
    @Override
    public boolean checkIfExpired() {
        return false; // that means that product never expires
    }
    
    @Override
    public boolean needsShipping() {
        return requiresShipping;
    }
    
    @Override
    public double getItemWeight() {
        return itemWeight;
    }
}

class CartItem {
    private Product prod;
    private int qty;
    
    public CartItem(Product prod, int qty) {
        this.prod = prod;
        this.qty = qty;
    }
    
    public Product getProduct() { return prod; }
    public int getQuantity() { return qty; }
    
    public double calculateTotal() { 
        return prod.getProductPrice() * qty; 
    }
}

class Customer {
    private String customerName;
    private double walletBalance;
    
    public Customer(String customerName, double walletBalance) {
        this.customerName = customerName;
        this.walletBalance = walletBalance;
    }
    
    public String getCustomerName() { return customerName; }
    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double balance) { this.walletBalance = balance; }
}

class ShoppingCart {
    private List<CartItem> cartItems;
    
    public ShoppingCart() {
        this.cartItems = new ArrayList<>();
    }
    
    public void addToCart(Product prod, int qty) throws Exception {
        if (prod.getAvailableStock() < qty) {
            throw new Exception("Not enough stock for " + prod.getProductName());
        }
        
        if (prod.checkIfExpired()) {
            throw new Exception("Product " + prod.getProductName() + " has expired");
        }
        
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem existingItem = cartItems.get(i);
            if (existingItem.getProduct().getProductName().equals(prod.getProductName())) {
                int newQty = existingItem.getQuantity() + qty;
                if (prod.getAvailableStock() < newQty) {
                    throw new Exception("Not enough stock for " + prod.getProductName());
                }
                cartItems.set(i, new CartItem(prod, newQty));
                return;
            }
        }
        
        cartItems.add(new CartItem(prod, qty));
    }
    
    public List<CartItem> getCartItems() { return cartItems; }
    public boolean isCartEmpty() { return cartItems.isEmpty(); }
    
    public double calculateSubtotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.calculateTotal();
        }
        return total;
    }
}

class ShippingItem implements Shippable {
    private String itemName;
    private double weight;
    
    public ShippingItem(String itemName, double weight) {
        this.itemName = itemName;
        this.weight = weight;
    }
    
    @Override
    public String getName() { return itemName; }
    
    @Override
    public double getWeight() { return weight; }
}

// service to handle shipping calculations
class ShippingService {
    private static final double SHIPPING_COST_PER_KG = 10.0;
    
    public static double getShippingFee(List<Shippable> itemsToShip) {
        if (itemsToShip.isEmpty()) return 0;
        
        double totalWeight = 0;
        for (Shippable item : itemsToShip) {
            totalWeight += item.getWeight();
        }
        return totalWeight * SHIPPING_COST_PER_KG;
    }
    
    public static void printShippingNotice(List<Shippable> itemsToShip, Map<String, Integer> itemQuantities) {
        if (itemsToShip.isEmpty()) return;
        
        System.out.println("** Shipment notice **");
        double totalPackageWeight = 0;
        
        for (Shippable item : itemsToShip) {
            int itemQty = itemQuantities.get(item.getName());
            double weightForThisItem = item.getWeight() * itemQty;
            totalPackageWeight += weightForThisItem;
            
            int weightInGrams = (int)(weightForThisItem * 1000);
            System.out.println(itemQty + "x " + item.getName() + " " + weightInGrams + "g");
        }
        
        System.out.println("Total package weight " + totalPackageWeight + "kg");
    }
}

public class ECommerceSystem {
    
    public static void processCheckout(Customer customer, ShoppingCart cart) throws Exception {
        if (cart.isCartEmpty()) {
            throw new Exception("Cannot checkout - cart is empty");
        }
        
        for (CartItem item : cart.getCartItems()) {
            Product prod = item.getProduct();
            if (prod.getAvailableStock() < item.getQuantity()) {
                throw new Exception("Product " + prod.getProductName() + " is out of stock");
            }
            if (prod.checkIfExpired()) {
                throw new Exception("Product " + prod.getProductName() + " has expired");
            }
        }
        
        double subtotal = cart.calculateSubtotal();
        
        // collect items that need shipping
        List<Shippable> itemsToShip = new ArrayList<>();
        Map<String, Integer> itemQuantities = new HashMap<>();
        
        for (CartItem item : cart.getCartItems()) {
            Product prod = item.getProduct();
            if (prod.needsShipping()) {
                itemsToShip.add(new ShippingItem(prod.getProductName(), prod.getItemWeight()));
                itemQuantities.put(prod.getProductName(), item.getQuantity());
            }
        }
        
        double shippingFee = ShippingService.getShippingFee(itemsToShip);
        double totalAmount = subtotal + shippingFee;
        
        if (customer.getWalletBalance() < totalAmount) {
            throw new Exception("Insufficient balance in customer account");
        }
        
        customer.setWalletBalance(customer.getWalletBalance() - totalAmount);
        
        for (CartItem item : cart.getCartItems()) {
            Product prod = item.getProduct();
            prod.setAvailableStock(prod.getAvailableStock() - item.getQuantity());
        }
        
        ShippingService.printShippingNotice(itemsToShip, itemQuantities);
        
        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getCartItems()) {
            System.out.println(item.getQuantity() + "x " + 
                             item.getProduct().getProductName() + " " + 
                             (int)item.calculateTotal());
        }
        System.out.println("----------------------");
        System.out.println("Subtotal " + (int)subtotal);
        System.out.println("Shipping " + (int)shippingFee);
        System.out.println("Amount " + (int)totalAmount);
        System.out.println("Customer balance after payment: " + customer.getWalletBalance());
    }
    
    public static void main(String[] args) {
        try {
            Product cheeseProduct = new ExpirableProduct("Cheese", 100, 10, 
                                                       LocalDate.now().plusDays(7), 0.2);
            Product biscuitsProduct = new ExpirableProduct("Biscuits", 150, 5, 
                                                         LocalDate.now().plusDays(30), 0.7);
            Product tvProduct = new NonExpirableProduct("TV", 500, 3, true, 15.0);
            Product scratchCardProduct = new NonExpirableProduct("Mobile Scratch Card", 50, 20, false, 0.0);
            
            // create a customer with some balance
            Customer johnDoe = new Customer("John Doe", 1000);
            
            ShoppingCart customerCart = new ShoppingCart();
            customerCart.addToCart(cheeseProduct, 2);
            customerCart.addToCart(biscuitsProduct, 1);
            customerCart.addToCart(scratchCardProduct, 1);
            
            processCheckout(johnDoe, customerCart);
            
            System.out.println("END.");
            
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}