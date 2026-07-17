import './productDescription.css';
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Header from "../../components/header/header";
import { useContext, useEffect, useState } from 'react';
import Loading from '../../components/loading/loading';
import Info from '../../components/info/info';
import ProductDescriptionService from '../../api-service/product.desc.service';
import CartContext from '../../contexts/cart.context';
import { AuthContext } from '../../contexts/auth.context';
import Footer from '../../components/footer/footer';

function ProductDescription() {

    const { productId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const { getProductDescription, isLoading, description, error, colors, sizes, activeColor, setActiveColor, activeSize, setActiveSize, variants, inventory } = ProductDescriptionService()
    const { user, toggleUser } = useContext(AuthContext)

    const [maxQty, setMaxQty] = useState(1);
    const [activeQty, setActiveQty] = useState(1);
    const [activePrice, setActivePrice] = useState(-1);

    const { cart, addItemToCart } = useContext(CartContext);
    const [addingToCart, setAddingToCart] = useState(false);

    const checkStock = () => {
        const _varient = variants?.find(v => v.size === activeSize && v.color === activeColor);
        console.log("Varient => ", _varient)
        if (!_varient) {
            setMaxQty(0);
            setActivePrice(-1)
            return;
        };

        const _inventory = inventory?.find(i => i.sku === _varient.sku) 
        console.log("inventory => ", _inventory)

        if (!_inventory) {
            setMaxQty(0);
            setActivePrice(-1)
            return;
        }
        if (_inventory || _inventory?.availableStock <= 0) setMaxQty(0);
        if (_inventory) {
            const existInCart = cart.cartItems.find(item => item?.productId === productId && item?.variant?.sku === _varient.sku);
            if (existInCart) {
                setMaxQty(Math.max(_inventory.availableStock - existInCart.quantity, 0))
            } else {
                setMaxQty(_inventory.availableStock)
            }
            if (maxQty == 0) {
                setActivePrice(-1)
            } else {
                setActivePrice(_varient.price)
            }
        }
    }

    const onAddToCart = async () => {
        if (!user) {
            navigate("/auth/login")
            return
        }
        const _varient = variants?.find(v => v.size === activeSize && v.color === activeColor);
        if (!_varient) {
            alert("The product is out of stock!")
        }
        setAddingToCart(true)
        console.log("Add start")
        await addItemToCart(productId, _varient, activeQty)
        console.log("Add end")
        setAddingToCart(false)
        setActiveQty(1)
    }

    useEffect(() => {
        checkStock()
    }, [activeSize, activeColor, cart])
    
    useEffect(() => {
        if (location.state) {
            getProductDescription(productId)
        }
    }, [productId])

    return (
        <>
            <Header />
            {isLoading && <Loading />}
            {error && <Info message="Unable to display product right now. Try again later..." />}
            {!isLoading && !error && (
                <div className='desc-wrapper'>
                    <div className="image-wrapper">
                        <img src={`https://idb.gov.lk/training/wp-content/uploads/2022/11/Cosmetics-Product-Development.png`} className="image" alt='product'></img>
                    </div>
                    <div className="detail-wrapper">
                        <h2>{description?.productName || "Yoga Mat 4mm - with Bag"}</h2>
                        <p>{description?.description || "A high-quality yoga mat with a non-slip surface for enhanced stability and comfort."}</p>
                        <h3>Price: ${activePrice > 0 ? activePrice : description?.price}</h3>
                        <div className='color-wrapper'>
                            <span>Color</span>
                            <ul className='color-selector'>
                                {colors?.map((color, index) => (
                                    <li 
                                        key={index} 
                                        className={`color ${activeColor === color ? 'active' : ''}`} 
                                        style={{ backgroundColor: color }}
                                        onClick={() => setActiveColor(color)}
                                    ></li>
                                ))}
                            </ul>
                        </div>
                        <div className='size-wrapper'>
                            <span>Size</span>
                            <ul className='size-selector'>
                                {
                                    sizes?.map((size, index) => (
                                        <li 
                                            key={index} 
                                            className={`size ${activeSize === size ? 'active' : ''}`}
                                            onClick={() => setActiveSize(size)}
                                        >{size}</li>
                                    ))
                                }
                            </ul>
                        </div>

                        <div className='qty-selector'>
                            <button 
                                className='qty-btn'
                                disabled={maxQty == 0 || activeQty==1}
                                onClick={() => {
                                    if (activeQty > 0) {
                                        setActiveQty(prev => prev-1)
                                    }
                                }}
                            >
                                -
                            </button>
                            <span className='qty'>{activeQty}</span>
                            <button 
                                className='qty-btn'
                                disabled={maxQty == 0 || activeQty==maxQty}
                                onClick={() => {
                                    if (activeQty < maxQty) {
                                        setActiveQty(prev => prev+1)
                                    }
                                }}
                            >
                                +
                            </button>
                            {
                                maxQty == 0 ? (
                                    <button 
                                        className='out-of-stock'
                                    >
                                        Out of stock
                                    </button>
                                ) : <></>
                            }
                        </div>
                        <ul className='action-btns'>
                            <li
                                className={maxQty > 0 && !addingToCart ? 'btn add-to-cart' : 'btn add-to-cart-disabled'}
                                onClick={onAddToCart}
                            >{addingToCart ? "Adding to Cart" : "Add to Cart"}</li>
                        </ul>
                    </div>
                </div>
            )}
            <Footer />
        </>
    )
}

export default ProductDescription;