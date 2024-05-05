import React, { useContext, useEffect } from 'react';
import './cart.css'
import CartContext from '../../contexts/cart.contect';
import { AiOutlineClose, AiOutlinePlus, AiOutlineMinus } from "react-icons/ai";
import { RiDeleteBin6Line } from "react-icons/ri";
import Loading from '../loading/loading';
import Info from '../info/info';
import { AuthContext } from '../../contexts/auth.context';

function Cart({ isCartOpen, onClose }) {

    const { cart, cartError, isProcessingCart, addItemToCart, removeItemFromCart, getCartInformation } = useContext(CartContext)
    const {user, toggleUser} = useContext(AuthContext);

    useEffect(() => {
        getCartInformation()
    }, [user])

    const onProductRemove = (id) => {
        removeItemFromCart(id)
    }
    const onQuantityChange = (id, qty) => {
        addItemToCart(id, qty)
    }
    const onCheckout = () => {

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
                {!user && <Info message="Login to see your cart!" />}
                {isProcessingCart && <Loading />}
                {cartError && <Info message="Unable to process your cart! Try again later" />}
                {
                    !isProcessingCart && !cartError && cart.cartItems && (
                        <>
                            <div className="cart-products">
                                {cart.cartItems && cart.cartItems.map((cartItem) => (
                                    <div className="cart-product" key={cartItem.product.id}>
                                        <img src={`../../../public/products/${cartItem.product.imageUrl}`} alt={cartItem.product.productName} />
                                        <div className="product-info">
                                            <h4>
                                                {cartItem.product.productName}
                                                <div
                                                    className={cartItem.quantity === 20 ? "btn close-btn disable" : "btn close-btn"}
                                                    onClick={() => onProductRemove(cartItem.product.id)}
                                                >
                                                    <RiDeleteBin6Line size={20} />
                                                </div>

                                            </h4>
                                            <span className="product-price">
                                                {cartItem.product.price} x {cartItem.quantity} = Rs. {cartItem.totalPrice}
                                            </span>
                                            <div className="quantity-control">
                                                <span
                                                    className={cartItem.quantity === 1 ? "disable" : ""}
                                                    onClick={() => onQuantityChange(cartItem.product.id, -1)}
                                                >
                                                    <AiOutlineMinus size={18} />
                                                </span>
                                                <span className="count">{cartItem.quantity}</span>
                                                <span
                                                    className={cartItem.quantity === 20 ? "disable" : ""}
                                                    onClick={() => onQuantityChange(cartItem.product.id, 1)}
                                                >
                                                    <AiOutlinePlus size={18} />
                                                </span>
                                            </div>

                                        </div>

                                    </div>
                                ))}
                            </div>
                            <div className="cart-summary">
                                {cart.cartItems && cart.cartItems.length > 0 && (
                                    <>
                                        <h3>Subtotal: Rs. {parseFloat(cart.subtotal).toFixed(2)}</h3>
                                        <button className="btn checkout-btn" onClick={onCheckout}>Proceed to checkout</button>
                                    </>
                                )}
                            </div>
                        </>
                    )
                }
            </div>

        </>
    )
}

export default Cart;