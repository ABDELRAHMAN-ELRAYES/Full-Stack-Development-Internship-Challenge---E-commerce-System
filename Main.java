import java.util.*;

class Product {

    private String name;
    private int quantity;
    private double price;
    private boolean isExpired;
    private Date expirationDate;
    private boolean isShippable;
    private double weight;
    private double shippingFees;

    Product(String name, int quantity, double price, boolean isExpired, boolean isShippable, double weight,
            Date expirationDate, double shippingFees) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.isExpired = isExpired;
        this.expirationDate = expirationDate;
        this.isShippable = isShippable;
        this.weight = weight;
        this.shippingFees = shippingFees;
    }

    // Get the product price
    public double getPrice() {
        return this.price;
    }

    // Get the product name
    public String getName() {
        return this.name;
    }
    // Get the product quantity

    public int getQuantity() {
        return this.quantity;
    }

    public double getShippingFees() {
        return this.shippingFees;
    }

    public double getWeight() {
        return this.weight;
    }

    // Update the new quantity(if it's positive it means there is a quantity
    // restored, if it's negative it means that there is a requested quantity)
    public void setQuantity(int quantity) {
        this.quantity += quantity;
    }

    // Check if the requested Quantity
    boolean isAvailable(int requestedQuantity) {
        return this.quantity - requestedQuantity >= 0;

    }

    // Check if the product is not expired
    boolean isNotExpired() {
        if (this.isExpired) {
            Date now = new Date();
            return now.before(this.expirationDate);
        }
        return true;

    }

    // Check if the product is Shippable
    boolean isShippable() {
        return this.isShippable;
    }

}

class Bill {
    private List<Item> items;
    private List<Item> shippedItems;
    private double shippingFees;

    Bill(List<Item> items, List<Item> shippedItems, double shippingFees) {
        this.items = items;
        this.shippedItems = shippedItems;
        this.shippingFees = shippingFees;
    }

    // Get customer check total
    public double getCustomerSubTotalCartPrice() {
        double total = 0;
        for (int i = 0; i < this.items.size(); i++) {
            total += this.items.get(i).getTotalItemPrice();
        }
        return total;

    }

    public double getBillTotal() {
        return this.shippingFees + this.getCustomerSubTotalCartPrice();
    }

    public void printBill() {
        if (this.shippedItems.size() != 0) {
            double shippingTotalWeight = 0;
            System.out.println("** Shipment notice **");
            for (int i = 0; i < this.shippedItems.size(); i++) {
                Item item = this.shippedItems.get(i);
                Product product = item.getProduct();
                double itemWeight = product.getWeight();
                System.out.println(item.getRequestedQuantity() + "x " + product.getName() + " "
                        + itemWeight * item.getRequestedQuantity() + "g");
                shippingTotalWeight += (itemWeight * item.getRequestedQuantity());
            }
            String weightSymbol = (shippingTotalWeight >= 1000) ? "kg" : "g";

            System.out.println("Total package weight "
                    + (shippingTotalWeight >= 1000 ? shippingTotalWeight / 1000 : shippingTotalWeight) + weightSymbol);
        }
        System.out.println("** Checkout receipt **");
        for (int i = 0; i < this.items.size(); i++) {
            Item item = this.items.get(i);
            Product product = item.getProduct();
            System.out.println(item.getRequestedQuantity() + "x " + product.getName() + " "
                    + product.getPrice() * item.getRequestedQuantity());
        }
        System.out.println("-------------------------------");
        double subTotal = this.getCustomerSubTotalCartPrice();
        System.out.println("Subtotal " + subTotal);
        System.out.println("Shipping " + this.shippingFees);
        System.out.println("Amount " + this.getBillTotal());
    }
}

class Customer {
    private String name;
    private double balance;
    private List<Bill> bills;

    Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
        bills = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public boolean checkout(Bill bill) {
        boolean isPaymentDone = this.pay(bill.getBillTotal());
        if (isPaymentDone) {
            this.bills.add(bill);
            return true;
        } else {
            return false;
        }
    }

    public boolean pay(double billTotal) {
        boolean canAfford = billTotal <= this.balance;
        if (canAfford) {
            this.balance -= billTotal;
            return true;
        } else {
            return false;
        }
    }

}

class Item {
    private Product product;
    private int requestedQuantity;
    private double itemPrice;

    Item(Product product, int requestedQuantity) {
        this.product = product;
        this.requestedQuantity = requestedQuantity;
        this.itemPrice = product.getPrice() * requestedQuantity;
    }

    public Product getProduct() {
        return this.product;

    }

    public int getRequestedQuantity() {
        return this.requestedQuantity;
    }

    public double getTotalItemPrice() {
        return this.itemPrice;
    }

    public double getItemShippingFees() {
        return (this.product.isShippable()) ? this.product.getShippingFees() : 0;
    }

    // Change the quantity of customer cart item
    public void updateItemQuantity(int newQuantity) {
        // Check if the process for restoringg product quantity or request more quantity
        int quantityDifference = newQuantity - this.requestedQuantity;
        if (quantityDifference > 0) {
            /*
             * Check if the new stock quantity greater than or equal to the additional
             * difference
             */

            if (this.product.isAvailable(quantityDifference)) {
                this.product.setQuantity(-quantityDifference);
                this.requestedQuantity += quantityDifference;
                this.itemPrice += quantityDifference * this.product.getPrice();
                System.out.println(
                        "Item quantity of " + this.product.getName() + " updated successfully to " + newQuantity);
            } else {
                System.out.print("Sorry, Requested quantity of " + this.product.getName() + " isn't sufficient.");
                System.out.print(
                        "The available Quantity of " + this.product.getName() + " is : "
                                + this.product.getQuantity());
                return;
            }
        } else if (quantityDifference < 0) {
            // Return the difference to the product stock
            int excessQuantity = Math.abs(quantityDifference);
            this.product.setQuantity(excessQuantity);
            this.requestedQuantity = newQuantity;
            this.itemPrice = this.product.getPrice() * this.requestedQuantity;
            System.out
                    .println("Item quantity of " + this.product.getName() + " updated successfully to "
                            + newQuantity);
        } else {
            System.out.println("Quantity remains the same");
        }
        return;

    }
}

class Cart {
    // Seperate List for each customer Cart
    private static HashMap<String, List<Item>> cart;
    // List for each customer to store his shippable Items
    private static HashMap<String, List<Item>> shippableItems;
    static {
        cart = new HashMap<>();
        shippableItems = new HashMap<>();
    }

    // Add item to the cart
    public void addItem(Item item, Customer customer) {

        String customerName = customer.getName();
        if (!Cart.cart.containsKey(customerName)) {
            Cart.cart.put(customerName, new ArrayList<>());
        }

        if (!Cart.shippableItems.containsKey(customerName)) {
            Cart.shippableItems.put(customerName, new ArrayList<>());
        }

        Product product = item.getProduct();
        int requestedQuantity = item.getRequestedQuantity();

        if (!product.isAvailable(requestedQuantity)) {
            System.out.println("The requested quantity is more than quantity in stock");
            return;
        }
        if (!product.isNotExpired()) {
            System.out.println("Sorry, the product is Expired");
            return;
        }

        if (product.isShippable()) {
            Cart.shippableItems.get(customerName).add(item);
        }
        Cart.cart.get(customerName).add(item);
        System.out
                .println(product.getName() + " for Customer : " + customerName
                        + " is added to your cart successfully");
    }

    public void checkout(Customer customer) {
        // Get the customer name
        String customerName = customer.getName();
        // Calculate the shipping fees
        // Shipping all Shippable items from customer bill
        ShippingService shippingService = new ShippingService(Cart.shippableItems.get(customerName));

        double shippingFees = shippingService.getShippingTotalFees();

        // Generate a bill with the customer cart items
        Bill bill = new Bill(Cart.cart.get(customerName), Cart.shippableItems.get(customerName),
                shippingFees);

        // Check out and add the bill to the custmoer record

        boolean isCheckoutDone = customer.checkout(bill);
        if (isCheckoutDone) {
            // Print customer bill
            bill.printBill();
            // Remove customer items from the cart
            Cart.cart.remove(customerName);
            Cart.shippableItems.remove(customerName);
        } else {
            System.out.println("Sorrry, Your Balance isn't engough, you can get rid of some items from your cart.");
        }

    }
}

interface IShippedItem {

    public String getName();

    public double getWeight();
}

class ShippedItem implements IShippedItem {
    private Item item;

    ShippedItem(Item item) {
        this.item = item;

    }

    public String getName() {
        return this.item.getProduct().getName();
    }

    public double getWeight() {
        return this.item.getProduct().getWeight() * this.item.getRequestedQuantity();

    }
}

class ShippingService {
    private List<Item> shippedItems;

    ShippingService(List<Item> shippedItems) {
        this.shippedItems = shippedItems;
    }

    public double getShippingTotalWeight() {
        double weight = 0;
        for (int i = 0; i < this.shippedItems.size(); i++) {
            Item item = shippedItems.get(i);
            double itemWeight = item.getProduct().getWeight();
            int itemQuantity = item.getRequestedQuantity();
            weight += (itemWeight * itemQuantity);
        }
        return weight;
    }

    // Get the customer bill shipping fees
    public double getShippingTotalFees() {
        double shippingFees = 0;

        for (int i = 0; i < this.shippedItems.size(); i++) {
            shippingFees += this.shippedItems.get(i).getItemShippingFees();
        }
        return shippingFees;
    }
}

public class Main {
    public static void main(String[] args) {
        // ? NOTICE : only test cases generated by AI

        // Setup Products
        Product book = new Product("Book", 10, 50.0, false, true, 500, new Date(System.currentTimeMillis() + 86400000),
                10); // Not expired
        Product milk = new Product("Milk", 5, 20.0, true, true, 1000, new Date(System.currentTimeMillis() - 86400000),
                5); // Expired
        Product laptop = new Product("Laptop", 2, 5000.0, false, false, 3000,
                new Date(System.currentTimeMillis() + 86400000), 0); // Not shippable
        Product toy = new Product("Toy", 3, 100.0, false, true, 250, new Date(System.currentTimeMillis() + 86400000),
                15);
        Product chocolate = new Product("Chocolate", 10, 50.0, false, true, 100,
                new Date(System.currentTimeMillis() + 86400000), 10);
        Product coffee = new Product("Coffee", 5, 120.0, false, true, 200,
                new Date(System.currentTimeMillis() + 86400000), 5);

        // Setup Customers
        Customer ali = new Customer("Ali", 300); // Enough balance
        Customer sara = new Customer("Sara", 50); // Not enough balance
        Customer bob = new Customer("Bob", 10000); // Rich customer
        Customer lina = new Customer("Lina", 1000);

        // Initialize cart
        Cart cart = new Cart();

        // TEST 1: Add valid item
        Item bookItem = new Item(book, 2);
        cart.addItem(bookItem, ali); // ✅ should succeed

        // TEST 2: Add expired item
        Item milkItem = new Item(milk, 1);
        cart.addItem(milkItem, ali); // ❌ should fail

        // TEST 3: Add more quantity than in stock
        Item tooManyToys = new Item(toy, 10);
        cart.addItem(tooManyToys, ali); // ❌ should fail

        // TEST 4: Add non-shippable item
        Item laptopItem = new Item(laptop, 1);
        cart.addItem(laptopItem, ali); // ✅ should succeed

        // TEST 5: Try checkout (Ali has enough balance)

        System.out.println("*******************************************************");
        System.out.println("*********************  ALI  ***************************");
        System.out.println("*******************************************************");

        cart.checkout(ali);
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        // TEST 6: Add item to Sara (low balance)
        Item toyItem = new Item(toy, 2);
        cart.addItem(toyItem, sara); // ✅

        // TEST 7: Try checkout with low balance
        System.out.println("*******************************************************");
        System.out.println("*********************  SARA  ***************************");
        System.out.println("*******************************************************");

        cart.checkout(sara); // ❌ should fail due to balance
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        // TEST 8: Add item to Bob (rich)
        Item expensiveLaptop = new Item(laptop, 1);
        cart.addItem(expensiveLaptop, bob);

        System.out.println("*******************************************************");
        System.out.println("*********************  BOB  ***************************");
        System.out.println("*******************************************************");

        cart.checkout(bob); // ✅ should succeed
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        // TEST 9: Test quantity update (valid increase)
        Product toy2 = new Product("ToyCar", 5, 200, false, true, 300, new Date(System.currentTimeMillis() + 86400000),
                20);
        Item toyItem2 = new Item(toy2, 2);
        toyItem2.updateItemQuantity(4); // ✅ should succeed

        // TEST 10: Quantity update (insufficient stock)
        toyItem2.updateItemQuantity(10); // ❌ should fail

        // TEST 11: Quantity update (decrease)
        toyItem2.updateItemQuantity(1); // ✅ should return to stock

        // TEST 12: Quantity update (same)
        toyItem2.updateItemQuantity(1);

        // TEST 13: Empty cart checkout
        cart.checkout(sara);

        cart.addItem(new Item(chocolate, 3), lina);
        cart.addItem(new Item(coffee, 2), lina);

        System.out.println("*******************************************************");
        System.out.println("*********************  LINA  ***************************");
        System.out.println("*******************************************************");

        cart.checkout(lina);
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");
        System.out.println("*******************************************************");

        System.out.println("\n✔️ All test scenarios executed.");
    }
}
