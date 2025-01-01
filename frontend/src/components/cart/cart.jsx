import React, { useContext, useEffect } from 'react';
import './cart.css'
import CartContext from '../../contexts/cart.contect';
import { AiOutlineClose, AiOutlinePlus, AiOutlineMinus } from "react-icons/ai";
import { RiDeleteBin6Line } from "react-icons/ri";
import Loading from '../loading/loading';
import Info from '../info/info';
import { AuthContext } from '../../contexts/auth.context';
import { useNavigate } from "react-router-dom";

function Cart({ isCartOpen, onClose }) {

    const { cart, cartError, isProcessingCart, addItemToCart, removeItemFromCart, getCartInformation } = useContext(CartContext)
    const {user, toggleUser} = useContext(AuthContext);
    const navigate = useNavigate()

    useEffect(() => {
        // getCartInformation()
    }, [user])

    const onProductRemove = (id) => {
        removeItemFromCart(id)
    }
    const onQuantityChange = (id, qty) => {
        addItemToCart(id, qty)
    }
    const onCheckout = () => {
        navigate(`/order/checkout`)
    }

    useEffect(() => {

    }, [])
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
                                {cart.cartItems && cart?.cartItems.map((cartItem) => (
                                    <div className="cart-product" key={cartItem.productId}>
                                        <img src={`${cartItem.imageUrl}`} alt={cartItem.productName} />
                                        <div className="product-info">
                                            <h4>
                                                {cartItem.productName}
                                                <div
                                                    className={cartItem.quantity === 20 ? "btn close-btn disable" : "btn close-btn"}
                                                    onClick={() => onProductRemove(cartItem.productId)}
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
                                                    onClick={() => onQuantityChange(cartItem.productId, -1)}
                                                >
                                                    <AiOutlineMinus size={18} />
                                                </span>
                                                <span className="count">{cartItem.quantity}</span>
                                                <span
                                                    className={cartItem.quantity === 20 ? "disable" : ""}
                                                    onClick={() => onQuantityChange(cartItem.productId, 1)}
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