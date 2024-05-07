import { Link } from "react-router-dom";
import Logo from "../../components/logo/logo";
import { AuthContext } from "../../contexts/auth.context";
import { useContext } from "react";
import success from "../../assets/images/icons/success.gif"

function OrderSuccess() {

    const { user, toggleUser }= useContext(AuthContext)

    return(
        <main className='auth-container'>
            <div className='auth-wrapper'>
                <form style={{alignItems:'center'}}>
                    <Logo/>
                    <img src={success} size='20px'/>
                    <h4 style={{textAlign:"center", color: "green"}}>
                        Thank you for your order!<br/>
                        Your order has been successfully placed.
                    </h4>
                    <h4 style={{textAlign:"center", color: "green"}}>Order confirmation email has been sent to {user.email}.</h4>
                    {/* <div className="input-box"> */}
                        <Link to='/'><button>Go home</button></Link>
                    {/* </div> */}
                </form>
            </div>
        </main>
    )
}

export default OrderSuccess;