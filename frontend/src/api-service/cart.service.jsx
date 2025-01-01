import { useEffect, useState } from "react"
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

    const addItemToCart = async (productId, quantity) => {
        setProcessing(true)
        await axios.post(
            `${API_BASE_URL}/cart-service/cart/add`,
            { productId, quantity },
            { headers: authHeader() }
        )
            .then((response) => {
                setError(false)
            })
            .catch((error) => {
                setError(true)
            })
        setProcessing(false)
        getCartInformation()
    }

    const updateItemQuantity = async (productId, quantity) => {
        setProcessing(true)
        await axios.post(
            `${API_BASE_URL}/cart-service/cart/add`,
            { productId, quantity },
            { headers: authHeader() }
        )
            .then((response) => {
                setError(false)
            })
            .catch((error) => {
                setError(true)
            })
        setProcessing(false)
        getCartInformation()
    }

    const removeItemFromCart = async (productId) => {
        setProcessing(true)
        await axios.delete(`${API_BASE_URL}/cart-service/cart/remove`, {
            headers: authHeader(),
            params: {
                productId: productId
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
        if (!user.token) {
            setCart({})
            setError(false)
            return
        }
        setProcessing(true)
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
    }

    useEffect(() => {
        getCartInformation()
    }, [])

    return { cart, cartError, isProcessingCart, addItemToCart, updateItemQuantity, removeItemFromCart, getCartInformation };

}

export default CartService;