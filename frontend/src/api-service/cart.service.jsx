import { useState } from "react"
import API_BASE_URL from "./apiConfig";
import axios from 'axios';

function CartService() {
    const [cart, setCart] = useState({})
    const [cartError, setError] = useState(false);
    const [isProcessingCart, setProcessing] = useState(false);
    const {id, token, type} = JSON.parse(localStorage.getItem("user")) || {id: null, token: null, type:null}

    const authHeader = () => {
        return { Authorization: `${type}${token}` };
    }

    const addItemToCart = async (productId, quantity) => {
        setProcessing(true)
        const userId = id;
        await axios.post(
            `${API_BASE_URL}/cart/`,
            { userId, productId, quantity },
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
        const userId = id;
        await axios.post(
            `${API_BASE_URL}/cart/`,
            { userId, productId, quantity },
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
        await axios.delete(`${API_BASE_URL}/cart/remove`, {
            headers: authHeader(),
            params: {
                userId: id,
                productId: productId
            }
        })
            .then((response) => {
                setError(false)
                console.log(response.data)
            })
            .catch((error) => {
                setError(true)
            })
        getCartInformation()
    }

    const getCartInformation = async () => {

        if(!id) {
            setCart({})
            return
        }
        setProcessing(true)
        await axios.get(`${API_BASE_URL}/cart/byUser`, {
            headers: authHeader(),
            params: {
                id:id
            }
        })
            .then((response) => {
                setError(false)
                setCart(response.data.response)
            })
            .catch((error) => {
                setCart({})
                setError(true)
            })
        setProcessing(false)
    }

    return { cart, cartError, isProcessingCart, addItemToCart, updateItemQuantity, removeItemFromCart, getCartInformation };

}

export default CartService;