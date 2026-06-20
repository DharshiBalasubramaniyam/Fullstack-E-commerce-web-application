import { useState, useEffect } from "react"
import API_BASE_URL from "./apiConfig";
import axios from 'axios';

function CartService() {
    const [cart, setCart] = useState({})
    const [cartError, setError] = useState(false);
    const [isProcessingCart, setProcessing] = useState(false);
    const user = JSON.parse(localStorage.getItem("user"));

    const authHeader = () => {
        return { Authorization: `${user?.type}${user?.token}` };
    }

    const addItemToCart = async (productId, variant, quantity) => {
        console.log("Adding to cart start")
        setProcessing(true)
        await axios.post(
            `${API_BASE_URL}/cart-service/cart/add`,
            { productId, variant, quantity },
            { headers: authHeader() }
        )
            .then((response) => {
                setError(false)
            })
            .catch((error) => {
                setError(true)
            })
        console.log("Adding to cart end. get cart start: ", `${API_BASE_URL}/cart-service/cart/add`, { productId, variant, quantity }, { headers: authHeader() })
        setProcessing(false)
        getCartInformation()
    }

    const updateItemQuantity = async (productId, variant, quantity) => {
        setProcessing(true)
        await axios.put(
            `${API_BASE_URL}/cart-service/cart/qty`,
            { productId, variant, quantity },
            { headers: authHeader() }
        )
            .then((response) => {
                if (!response.data.isSuccess && response.data.message.startsWith("The maximum quantity")) {
                    alert(response.data.message);
                }
                setError(false)
            })
            .catch((error) => {
                setError(true)
            })
        setProcessing(false)
        getCartInformation()
    }

    const removeItemFromCart = async (productId, sku) => {
        setProcessing(true)
        await axios.delete(`${API_BASE_URL}/cart-service/cart/remove`, {
            headers: authHeader(),
            params: {
                productId: productId,
                sku: sku
            }
        })
            .then((response) => {
                setError(false)
            })
            .catch((error) => {
                setError(true)
            })
        getCartInformation()
    }

    const getCartInformation = async () => {
        if (!user?.token) {
            setCart({})
            setError(false)
            return
        }
        setProcessing(true)
        console.log("Get start")
        await axios.get(`${API_BASE_URL}/cart-service/cart/get/byUser`, {
            headers: authHeader()
        })
            .then((response) => {
                setError(false)
                setCart(response.data.response)
            })
            .catch((error) => {
                setCart({cartItems:[]})
                setError(true)
            })
        setProcessing(false)
        console.log("Get end")
    }

     useEffect(() => {
        getCartInformation()
     }, [])

    return { cart, cartError, isProcessingCart, addItemToCart, updateItemQuantity, removeItemFromCart, getCartInformation };

}

export default CartService;