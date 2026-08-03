import { BrowserRouter } from "react-router-dom"
import AppRoutes from "./routes/routes"
import './assets/styles/index.css'
import { AuthContext, useAuth } from "./contexts/auth.context"
import CartService from "./api-service/cart.service";
import CartContext from "./contexts/cart.context";
import AssistantService from "./api-service/assistance.service";
import AssistantContext from "./contexts/assistant.context";

function App() {

  const { user, toggleUser } = useAuth();
  const { cart, cartError, isProcessingCart, addItemToCart, updateItemQuantity, removeItemFromCart, getCartInformation } = CartService();
  const { chats, interrupt, queryLoading, sessionLoading, sendQuery } = AssistantService();

  return (
    <BrowserRouter>
      <AuthContext.Provider value={{ user, toggleUser }}>
        <CartContext.Provider value={{ cart, cartError, isProcessingCart, addItemToCart, updateItemQuantity, removeItemFromCart, getCartInformation }}>
          <AssistantContext.Provider value={{ chats, interrupt, queryLoading, sessionLoading, sendQuery }}>
            <AppRoutes />
          </AssistantContext.Provider>
        </CartContext.Provider>
      </AuthContext.Provider>
    </BrowserRouter>
  )
}

export default App
