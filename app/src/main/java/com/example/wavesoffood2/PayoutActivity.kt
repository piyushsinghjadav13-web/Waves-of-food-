package com.example.wavesoffood2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wavesoffood2.databinding.ActivityPayoutBinding
import com.example.wavesoffood2.modle.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PayoutActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPayoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var userId: String

    private var foodItemName = arrayListOf<String>()
    private var foodItemPrice = arrayListOf<String>()
    private var foodItemImage = arrayListOf<String>()
    private var foodItemQuantity = arrayListOf<String>()

    private var totalAmount = 0
    private var selectedPayment = "COD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Razorpay recommended
        Checkout.preload(applicationContext)

        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().reference
        userId = auth.currentUser?.uid ?: ""

        loadUserData()

        if (!receiveCartData()) {
            Toast.makeText(this, "No Cart Items!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.backbtn1.setOnClickListener { finish() }

        binding.payment.setOnClickListener {
            showPaymentDialog()
        }

        binding.payoutbtn.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etEmail.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPayment == "COD") {
                saveOrder(name, phone, address, false)
            } else {
                startOnlinePayment()
            }
        }
    }

    // ================= PAYMENT METHOD =================
    private fun showPaymentDialog() {
        val options = arrayOf("Cash on Delivery", "Online Payment")

        AlertDialog.Builder(this)
            .setTitle("Select Payment Method")
            .setItems(options) { _, which ->
                if (which == 0) {
                    selectedPayment = "COD"
                    binding.paymentText.text = "Cash on Delivery"
                    binding.paymentIcon.setImageResource(R.drawable.cod4)
                } else {
                    selectedPayment = "ONLINE"
                    binding.paymentText.text = "Online Payment"
                    binding.paymentIcon.setImageResource(R.drawable.onlinepay)
                }
            }
            .show()
    }

    // ================= USER DATA =================
    private fun loadUserData() {
        ref.child("user").child(userId).get().addOnSuccessListener {
            binding.etName.setText(it.child("name").value?.toString())
            binding.etPhone.setText(it.child("phone").value?.toString())
            binding.etEmail.setText(it.child("address").value?.toString())
        }
    }

    // ================= CART DATA =================
    private fun receiveCartData(): Boolean {
        foodItemName = intent.getStringArrayListExtra("FoodItemName") ?: arrayListOf()
        foodItemPrice = intent.getStringArrayListExtra("FoodItemPrice") ?: arrayListOf()
        foodItemImage = intent.getStringArrayListExtra("FoodItemImage") ?: arrayListOf()
        foodItemQuantity = intent.getStringArrayListExtra("FoodItemQuantity") ?: arrayListOf()

        if (foodItemName.isEmpty()) return false

        totalAmount = calculateTotal()
        binding.amount1.text = "₹$totalAmount"
        return true
    }

    private fun calculateTotal(): Int {
        var total = 0
        for (i in foodItemPrice.indices) {
            val price = foodItemPrice[i].replace("₹", "").trim().toIntOrNull() ?: 0
            val qty = foodItemQuantity.getOrNull(i)?.toIntOrNull() ?: 1
            total += price * qty
        }
        return total
    }

    // ================= RAZORPAY =================
    private fun startOnlinePayment() {

        if (totalAmount <= 0) {
            Toast.makeText(this, "Invalid Amount", Toast.LENGTH_SHORT).show()
            return
        }

        val checkout = Checkout()

        // 🔴 YAHAN APNI REAL TEST KEY DALO
        checkout.setKeyID("rzp_test_XXXXXXXXXX")

        try {
            val options = JSONObject()
            options.put("name", "Waves Of Food")
            options.put("description", "Food Order Payment")
            options.put("currency", "INR")
            options.put("amount", totalAmount * 100)
            options.put("theme.color", "#53E88B")

            val prefill = JSONObject()
            prefill.put("email", binding.etEmail.text.toString())
            prefill.put("contact", binding.etPhone.text.toString())
            options.put("prefill", prefill)

            val retry = JSONObject()
            retry.put("enabled", true)
            retry.put("max_count", 3)
            options.put("retry", retry)

            checkout.open(this, options)

        } catch (e: Exception) {
            Toast.makeText(this, "Payment Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // ================= PAYMENT CALLBACK =================
    override fun onPaymentSuccess(paymentId: String) {
        saveOrder(
            binding.etName.text.toString(),
            binding.etPhone.text.toString(),
            binding.etEmail.text.toString(),
            true
        )
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }

    // ================= SAVE ORDER =================
    private fun saveOrder(
        name: String,
        phone: String,
        address: String,
        paymentReceived: Boolean
    ) {
        val key = ref.child("orderDetails").push().key!!

        val order = OrderDetails(
            userUid = userId,
            userName = name,
            phoneNumber = phone,
            address = address,
            foodNames = foodItemName,
            foodPrices = foodItemPrice,
            foodImages = foodItemImage,
            foodQuantities = foodItemQuantity.toMutableList(),
            totalPrice = totalAmount.toString(),
            itemPushKey = key,
            paymentReceived = paymentReceived,
            orderAccepted = false,
            currentTime = System.currentTimeMillis()
        )

        ref.child("orderDetails").child(key).setValue(order)
        ref.child("user").child(userId).child("BuyHistory").child(key).setValue(order)
        ref.child("user").child(userId).child("CartItems").removeValue()

        BottomFragment().show(supportFragmentManager, "OrderPlaced")
        Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
    }
}
