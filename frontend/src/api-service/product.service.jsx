import { useState } from "react"
import API_BASE_URL from "./apiConfig";
import axios from 'axios';

function ProductService() {
    const [isLoading, setLoading] = useState(false);
    const [categories, setCategories] = useState([]);
    const [products, setProducts] = useState([]);
    const [error, setError] = useState(false);

    const getAllCategories = async () => {
        setLoading(true)
        await axios.get(`${API_BASE_URL}/category-service/category/get/all`)
            .then((response) => {
                setCategories(response.data.response);
                setError(false)
            })
            .catch((error) => {
                setCategories([])
                setError(true)
            })
        setLoading(false)
    }

    const getAllProducts = async () => {
        setLoading(true)
        setProducts([])
        await axios.get(`${API_BASE_URL}/product-service/product/get/all`)
            .then((response) => {
                    setProducts(response.data.response);
                    setError(false)
            })
            .catch((error) => {
                setProducts([])
                setError(true)
            })
        setLoading(false)
    }

    const getProductsByCategory = async (id) => {
        setLoading(true)
        await axios.get(`${API_BASE_URL}/product-service/product/get/byCategory`, {
            params:{
                id: id
            }
        })
            .then((response) => {
                    setProducts(response.data.response);
                    setError(false)
            })
            .catch((error) => {
                setProducts([])
                setError(true)
            })
        setLoading(false)
    }

    const searchProducts = async (key) => {
        setLoading(true)
        await axios.get(`${API_BASE_URL}/product-service/product/search`, {
            params:{
                searchKey: key
            }
        })
            .then((response) => {
                    setProducts(response.data.response);
                    setError(false)
            })
            .catch((error) => {
                setProducts([])
                setError(true)
            })
        setLoading(false)
    }

    return {getAllCategories, getAllProducts, getProductsByCategory, searchProducts, isLoading, categories, products, error};
}

export default ProductService;