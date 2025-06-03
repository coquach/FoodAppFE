import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CheckoutDetails
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun saveCartItems(cartItems: List<CartItem>)
    suspend fun clearCartItems(cartItemsToRemove: List<CartItem>)
    fun getCartSize(): Flow<Int>
    suspend fun updateItemQuantity(id: Long, quantity: Int)
    suspend fun clearAll()
    fun getCheckoutDetails(): Flow<CheckoutDetails>

}