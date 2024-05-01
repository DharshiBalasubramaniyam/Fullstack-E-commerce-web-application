import { BrowserRouter } from "react-router-dom"
import AppRoutes from "./routes/routes"
import './assets/styles/index.css'
import { AuthContext, useAuth } from "./contexts/auth.context"
import CartService from "./api-service/cart.service";
import CartContext from "./contexts/cart.contect";

function App() {

  const {user, toggleUser} = useAuth();
  const { cart, cartError, isProcessingCart, addItemToCart, removeItemFromCart, getCartInformation } = CartService();

  return (
    <BrowserRouter>
      <AuthContext.Provider value={{user, toggleUser}}>
        <CartContext.Provider value={{ cart, cartError, isProcessingCart, addItemToCart, removeItemFromCart, getCartInformation }}>
          <AppRoutes/>
        </CartContext.Provider>
      </AuthContext.Provider>
    </BrowserRouter>
  )
}

export default App
