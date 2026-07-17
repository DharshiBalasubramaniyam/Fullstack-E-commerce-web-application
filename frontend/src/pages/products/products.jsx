import './products.css'
import { useLocation, useNavigate, useParams } from "react-router-dom";
import Header from "../../components/header/header";
import { useContext, useEffect, useState } from 'react';
import Loading from '../../components/loading/loading';
import Info from '../../components/info/info';
import ProductService from '../../api-service/product.service';
import CartContext from '../../contexts/cart.context';
import { AuthContext } from '../../contexts/auth.context';
import Footer from '../../components/footer/footer';
import Pagination from "react-js-pagination";

function Products() {

    const { category } = useParams();
    const location = useLocation();
    const { getAllCategories, getAllProducts, getProductsByCategory, setPageNumber, isLoading, categories, products, error, pageNumber, totalItemsCount, pageSize } = ProductService()

    useEffect(() => {
        getAllCategories()
        if (location.state) {
            setPageNumber(1)
            getProductsByCategory(location.state.categoryId)
        } else {
            getAllProducts()
        }
    }, [category, pageNumber])

    return (
        <>
            <Header />
            {isLoading && <Loading />}
            {error && <Info message="Unable to display product right now. Try again later..." />}
            {!isLoading && !error && (
                <section className="products-page">
                    <CategoryWrapper category={category} categoryList={categories} />
                    <div className="products-main">
                        <ProductsWrapper products={products} />
                        <Pagination
                            activePage={pageNumber}
                            itemsCountPerPage={pageSize}
                            totalItemsCount={totalItemsCount}
                            pageRangeDisplayed={5}
                            onChange={(pageNumber) => {
                                setPageNumber(pageNumber)
                            }}
                        />
                    </div>
                </section>
            )}
            <Footer />
        </>
    )
}

export default Products;


function CategoryWrapper({ category, categoryList }) {

    const [isOpen, setIsOpen] = useState(true);
    const navigate = useNavigate();

    const onSelect = (categoryName, categoryId) => {
        categoryId ?
            navigate(`/products/${categoryName}`, { state: { categoryId: categoryId } })
            :
            navigate(`/products/${categoryName}`)
    }

    return (
        <aside className={`category-sidebar ${isOpen ? 'open' : 'closed'}`}>
            {/* <div className="sidebar-header">
                <h2>Categories</h2>
                <button className="sidebar-toggle" onClick={() => setIsOpen(!isOpen)}>
                    {isOpen ? 'Close' : 'Open'}
                </button>
            </div> */}
            {isOpen && (
                <div className="category-list">
                    <div
                        className={category == "All" ? "category active" : "category"}
                        onClick={() => onSelect("All")}
                    >
                        All
                    </div>
                    {
                        categoryList.map((cat) => {
                            return (
                                <div
                                    className={category == cat.categoryName ? "category active" : "category"}
                                    key={cat.id}
                                    onClick={() => onSelect(cat.categoryName, cat.id)}
                                >
                                    {cat.categoryName}
                                </div>
                            )
                        })
                    }
                </div>
            )}
        </aside>
    )
}


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
            {/* <div className='products-wrapper'> */}
                {
                    isLoading ? <Loading /> :
                        products.map((product) => {
                            return (
                                <div 
                                    className='box' 
                                    key={product.id}
                                    onClick={() => navigate(`/product/view/${product.id}`, { state: { productId: product.id } })}
                                >
                                    <img src={`https://idb.gov.lk/training/wp-content/uploads/2022/11/Cosmetics-Product-Development.png`} className="image" alt='product'></img>
                                    <div className='price' aria-label='image'>Rs. {product.price}</div>
                                    {
                                        !product.inStock ? <div className='out-of-stock' aria-label='image'>Out of stock</div> : <></>
                                    }
                                    <div className='text-part'>
                                        <div className='name'>{product.productName}</div>
                                        <div className='description'>{product.description}</div>
                                    </div>
                                    {/* <button
                                        onClick={() => onAddToCart(product.id)}
                                    >
                                        Add to cart
                                    </button> */}
                                </div>
                            )
                        })
                }
            {/* </div> */}
        </section>
    )
}
