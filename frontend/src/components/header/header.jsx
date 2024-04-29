import { useContext, useEffect, useState } from "react";
import Logo from "../logo/logo";
import { Link, useNavigate } from "react-router-dom";
import "../../assets/styles/index.css"
import "./header.css"
import Cart from "../cart/cart";
import { AuthContext } from "../../contexts/auth.context";
import CartContext from "../../contexts/cart.contect";


function Header() {
    const navigate = useNavigate();
    const [isNavOpen, setIsNavOpen] = useState(false);
    const [isCartOpen, setCart] = useState(false);
    const {cart}  = useContext(CartContext)
    const {user, toggleUser} = useContext(AuthContext)

    const toggleNav = () => {
        setIsNavOpen(prev => !prev);
    };

    const toggleCart = () => {
        setCart(prev => !prev)
    }

    const logout = () => {
        localStorage.removeItem("user")
        toggleUser()
        navigate("/")
    };

    useEffect(() => {
        toggleUser()
    }, [])


    return (
        <>
            <header className="app-header">
                <div className="logo-wrapper">
                    <span>
                        {isNavOpen ? (
                            <i className="fa fa-times" aria-hidden="true" onClick={toggleNav}></i>
                        ) : (
                            <i className="fa fa-bars" aria-hidden="true" onClick={toggleNav}></i>
                        )}
                    </span>
                    <span>
                        <Link to="/"> <Logo /></Link>
                    </span>
                </div>

                <ul className={isNavOpen ? "nav-open" : "nav-close"}>
                    <li>
                        <Link to="/" className="nav-link">Home</Link>
                    </li>
                    <li>
                        <Link to="/products/All" className="nav-link">Products</Link>
                    </li>
                    {!user && (
                        <li>
                            <Link to="/auth/login" className="nav-link">Login</Link>
                        </li>
                    )}
                    {user && (
                        <>
                            <li>
                                <Link to='/' className="nav-link">orders</Link>
                            </li>
                            <li onClick={logout} className="nav-link">
                                <Link className="nav-link">Logout</Link>
                            </li>
                        </>
                    )}
                </ul>

                <ul>
                    <li>
                        <Link to='/'>
                            <i className="fa fa-search" aria-hidden="true"></i>
                        </Link>
                    </li>
                    <li>
                        <Link onClick={toggleCart}>
                            <i className="fa fa-shopping-cart" aria-hidden="true"></i>
                            <span>({cart.noOfCartItems || 0})</span>
                        </Link>
                    </li>
                </ul>
            </header>
            <Cart isCartOpen={isCartOpen} setIsCartOpen={setCart} onClose={() => setCart(false)} />
        </>
    )
}


export default Header;