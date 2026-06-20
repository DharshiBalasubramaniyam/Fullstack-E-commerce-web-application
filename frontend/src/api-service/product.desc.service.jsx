import { useState } from "react"
import API_BASE_URL from "./apiConfig";
import axios from 'axios';

function ProductDescriptionService() {
    const [isLoading, setLoading] = useState(false);
    const [id, setId] = useState(null);
    const [description, setDescription] = useState(null);
    const [colors, setColors] = useState(null);
    const [activeColor, setActiveColor] = useState(null);
    const [sizes, setSizes] = useState(null);
    const [activeSize, setActiveSize] = useState(null);
    const [error, setError] = useState(false);

    const getProductDescription = async (productId) => {
        setLoading(true)
        await axios.get(
            `${API_BASE_URL}/product-service/product/get/byId`,
            {
                params: {
                    id: productId
                }
            }
        )
            .then((response) => {
                setDescription(response.data.response);
                setColors(Array.from(new Set(response.data.response?.variants?.map(variant => variant.color))));
                setSizes(Array.from(new Set(response.data.response?.variants?.map(variant => variant.size))));
                setActiveColor(response.data.response?.variants[0]?.color);
                setActiveSize(response.data.response?.variants[0]?.size);
                setError(false)
            })
            .catch((error) => {
                setDescription(null)
                setColors(null)
                setSizes(null)
                setError(true)
            })
        setLoading(false)
    }

    return { getProductDescription, isLoading, description, error, colors, sizes, activeColor, setActiveColor, activeSize, setActiveSize };
}

export default ProductDescriptionService;