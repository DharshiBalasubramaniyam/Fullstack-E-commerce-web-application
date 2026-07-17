import { useContext } from 'react';
import './cart.css'
import CartContext from '../../contexts/cart.context';
import { AiOutlineClose, AiOutlinePlus, AiOutlineMinus } from "react-icons/ai";
import { RiDeleteBin6Line } from "react-icons/ri";
import Loading from '../loading/loading';
import Info from '../info/info';
import { AuthContext } from '../../contexts/auth.context';
import { useNavigate } from "react-router-dom";

function Cart({ isCartOpen, onClose }) {

    const { cart, cartError, isProcessingCart, addItemToCart, removeItemFromCart, updateItemQuantity, getCartInformation } = useContext(CartContext)
    const {user} = useContext(AuthContext);
    const navigate = useNavigate()

    const onProductRemove = async (id, sku) => {
        await removeItemFromCart(id, sku)
    }
    const onQuantityChange = async (id, variant, qty) => {
        await updateItemQuantity(id, variant, qty)
    }
    const onCheckout = () => {
        navigate(`/order/checkout`)
    }

    const getVariantText = (variant) => {
        if (!variant) return ""
        let text = "";
        if (variant?.color) {
            text = text + variant.color;
            if (variant.size) { 
                text = text + " / "
            }
        }  
        if (variant?.size) {
            text = text + variant.size;
        }
        if (text.length > 0) return " [" + text + "]"
        return text
    }

    return (
        <>
            <div className={isCartOpen ? "shoppingCart active" : "shoppingCart"}>
                <div className="header">
                    <h2>Your cart</h2>
                    <div className="btn close-btn" onClick={onClose}>
                        <AiOutlineClose size={20} />
                    </div>
                </div>
                {isProcessingCart && <Loading />}
                {!isProcessingCart && !cart.cartItems && <Info message="No items in your cart!" />}
                {
                    !isProcessingCart && (
                        <>
                            <div className="cart-products">
                                {cart.cartItems && cart?.cartItems.map((cartItem, index) => (
                                    <div className="cart-product" key={cartItem.productId+index}>
                                        <img src={`${cartItem.imageUrl}`} alt={cartItem.productName} />
                                        <div className="product-info">
                                            <h4>
                                                <>
                                                    {cartItem.productName + getVariantText(cartItem.variant)}
                                                </>
                                                <div
                                                    className={cartItem.quantity === 20 ? "btn close-btn disable" : "btn close-btn"}
                                                    onClick={() => onProductRemove(cartItem.productId, cartItem.variant.sku)}
                                                >
                                                    <RiDeleteBin6Line size={20} />
                                                </div>

                                            </h4>
                                            
                                            <span className="product-price">
                                                {cartItem.price} x {cartItem.quantity} = Rs.  {parseFloat(cartItem.amount).toFixed(2)}
                                            </span>
                                            <div className="quantity-control">
                                                <span
                                                    className={cartItem.quantity === 1 ? "disable" : ""}
                                                    onClick={() => onQuantityChange(cartItem.productId, cartItem.variant, -1)}
                                                >
                                                    <AiOutlineMinus size={18} />
                                                </span>
                                                <span className="count">{cartItem.quantity}</span>
                                                <span
                                                    className={cartItem.quantity === 20 ? "disable" : ""}
                                                    onClick={() => onQuantityChange(cartItem.productId, cartItem.variant, 1)}
                                                >
                                                    <AiOutlinePlus size={18} />
                                                </span>
                                            </div>

                                        </div>

                                    </div>
                                ))}
                            </div>
                            
                                {cart.cartItems && (
                                    <div className="cart-summary">
                                        <h3>Subtotal: Rs. {parseFloat(cart.subtotal).toFixed(2)}</h3>
                                        <button className="btn checkout-btn" onClick={onCheckout}>Proceed to checkout</button>
                                        </div>
                                )}
                            
                        </>
                    )
                }
            </div>

        </>
    )
}

export default Cart;