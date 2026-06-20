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
    const { getProductDescription, isLoading, description, error, colors, sizes, activeColor, setActiveColor, activeSize, setActiveSize  } = ProductDescriptionService()

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
                        <img src={description?.imageUrl} className="image" alt='product'></img>
                    </div>
                    <div className="detail-wrapper">
                        <h2>{description?.productName || "Yoga Mat 4mm - with Bag"}</h2>
                        <p>{description?.description || "A high-quality yoga mat with a non-slip surface for enhanced stability and comfort."}</p>
                        <h3>Price: ${description?.price || "19.99"}</h3>
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
                            <button className='qty-btn'>-</button>
                            <span className='qty'>1</span>
                            <button className='qty-btn'>+</button>
                        </div>
                        <ul className='action-btns'>
                            <li className='btn add-to-cart'>Add to Cart</li>
                            <li className='btn buy-now'>Buy Now</li>
                        </ul>
                    </div>
                </div>
            )}
            <Footer />
        </>
    )
}

export default ProductDescription;