import './search.css'
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Header from "../../components/header/header";
import { useContext, useEffect, useState } from 'react';
import Loading from '../../components/loading/loading';
import Info from '../../components/info/info';
import ProductService from '../../api-service/product.service';
import CartContext from '../../contexts/cart.contect';
import { AuthContext } from '../../contexts/auth.context';
import Footer from '../../components/footer/footer';

function Search() {

    const { search } = useParams();
    const { searchProducts, isLoading, products, error } = ProductService()

    useEffect(() => {
        searchProducts(search)
    }, [search])

    return (
        <>
            <Header />
            {isLoading && <Loading />}
            {error && <Info message="Unable to search products right now. Try again later..."/>}
            {!isLoading && !error && (
                <>
                    <h3 className='search-result'>{products.length} product/s found for <q>{search}</q></h3>
                    <ProductsWrapper products={products} />
                </>
            )}
            <Footer/>
        </>
    )
}

export default Search;


function ProductsWrapper({ products }) {

    const { addItemToCart } = useContext(CartContext);
    const { user, toggleUser } = useContext(AuthContext)
    const [isLoading, setLoading] = useState(false);
    const navigate = useNavigate();

    const onAddToCart = async (productId) => {
        if (!user) {
            navigate("/auth/login")
            return
        }
        setLoading(true)
        await addItemToCart(productId, 1)
        setLoading(false)
    }

    return (
        <section className="products-container">
            <div className='products-wrapper'>
                {
                    isLoading ? <Loading /> :
                        products.map((product) => {
                            return (
                                <div className='box' key={product.id}>
                                    <img src={`${product.imageUrl}`} className="image" alt='product'></img>
                                    <div className='price' aria-label='image'>Rs. {product.price}</div>
                                    <div className='text-part'>
                                        <div className='name'>{product.productName}</div>
                                        <div className='description'>{product.description}</div>
                                    </div>
                                    <button
                                        onClick={() => onAddToCart(product.id)}
                                    >
                                        Add to cart
                                    </button>
                                </div>
                            )
                        })
                }
            </div>
        </section>
    )
}