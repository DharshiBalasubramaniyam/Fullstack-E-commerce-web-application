import { useContext } from 'react';
import { AuthContext } from '../../contexts/auth.context';
import './hero.css'
import {Link} from 'react-router-dom'

function Hero() {
    
    const {user, toggleUser} = useContext(AuthContext)

    return(
        <section className="hero-section" id='hero'>
            <h1>Welcome to leading health and wellness retailer.</h1>

            <h3>We believe in pure, natural goodness - plain and simple. We aim to partner with you on your unique wellness journey while honoring people and the planet in all that we do.</h3>
            <div>
                <Link to='/products/All'><button>Shop now</button></Link>
                
            </div>
        </section>
    )
}



export default Hero;