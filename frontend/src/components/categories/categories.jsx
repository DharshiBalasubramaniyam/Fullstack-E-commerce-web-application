import { useEffect } from 'react'
import './categories.css'
import { useNavigate } from 'react-router-dom'
import ProductService from '../../api-service/product.service'
import Loading from '../loading/loading'

function Categories() {

    const { getAllCategories, isLoading, categories, error } = ProductService()
    const navigate = useNavigate();

    useEffect(() => {
        getAllCategories()
    }, [])

    const onExplore = (id, name) => {
        navigate(`/products/${name}`, { state: { categoryId: id } })
    }

    return (
        <section class="banner-container">
            <h1>Browse wellness by categories</h1>

            {isLoading && <Loading />}

            {
                categories.map((category) => {
                    return (
                        <div class="banner" key={category.id}>
                            <img src={`../../categories/${category.imageUrl}`} alt="" />
                            <div class="b-content">
                                <h3>{category.categoryName}</h3>
                                <p>{category.description}</p>
                                <button onClick={() => onExplore(category.id, category.categoryName)}>Explore Now</button>
                            </div>
                        </div>
                    )
                })
            }


        </section>
    )
}


export default Categories;