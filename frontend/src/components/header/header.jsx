import { useContext, useEffect, useState } from "react";
import Logo from "../logo/logo";
import { Link, useAsyncError, useNavigate } from "react-router-dom";
import "../../assets/styles/index.css"
import "./header.css"
import Cart from "../cart/cart";
import { AuthContext } from "../../contexts/auth.context";
import CartContext from "../../contexts/cart.context";
import Assistant from "../assistant/Assistant";

function Header() {
    const navigate = useNavigate();
    const [isNavOpen, setIsNavOpen] = useState(false);
    const [isCartOpen, setCart] = useState(false);
    const [isAssistantOpen, setAssistant] = useState(false);
    const { cart, getCartInformation } = useContext(CartContext)
    const { user, toggleUser } = useContext(AuthContext)
    const [searchKey, setSearchKey] = useState("");

    const toggleNav = () => {
        setIsNavOpen(prev => !prev);
    };

    const toggleCart = () => {
        setCart(prev => !prev)
    }

    const toggleAssistant = () => {
        setAssistant(prev => !prev)
    }

    const onSearch = () => {
        searchKey !== "" && navigate(`/search/${searchKey}`)
    }

    const onSearchKeyChange = (newKey) => {
        setSearchKey(newKey)
    }

    const logout = () => {
        localStorage.removeItem("user")
        toggleUser()
        navigate("/")
    };

     useEffect(() => {
         getCartInformation();
     }, [user]) 

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

                <div className="search">
                    <input
                        type="search"
                        placeholder="Search entire wellness..."
                        value={searchKey}
                        onChange={(e) => onSearchKeyChange(e.target.value)}
                    />
                    <i
                        className="fa fa-search"
                        aria-hidden="true"
                        onClick={onSearch}
                    ></i>
                </div>

                <ul className={isNavOpen ? "nav-open" : "nav-close"}>
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
                                <Link to='/my/account' className="nav-link">My Account</Link>
                            </li>
                            <li onClick={logout} className="nav-link">
                                <Link className="nav-link">Logout</Link>
                            </li>
                        </>
                    )}
                </ul>

                <div>
                    <span onClick={toggleAssistant} style={{marginRight: "1em", fontSize: "1.2em", cursor: "pointer"}}>
                        <i className="fa fa-solid fa-robot" aria-hidden="true"></i>
                    </span>
                    <span onClick={toggleCart} style={{ cursor: "pointer"}}>
                        <i className="fa fa-shopping-cart" aria-hidden="true"></i>
                        <span>({cart.noOfCartItems || 0})</span>
                    </span>
                </div>
            </header>
            <header className="app-header bottom">
                <div className="search">
                    <input
                        type="search"
                        placeholder="Search entire wellness..."
                        value={searchKey}
                        onChange={(e) => onSearchKeyChange(e.target.value)}
                    />
                    <i
                        className="fa fa-search"
                        aria-hidden="true"
                        onClick={onSearch}
                    ></i>
                </div>
            </header>
            <Cart isCartOpen={isCartOpen} setIsCartOpen={setCart} onClose={() => setCart(false)} />
            <Assistant isOpen={isAssistantOpen} setIsOpen={setAssistant} onClose={() => setAssistant(false)} />
        </>
    )
}


export default Header;