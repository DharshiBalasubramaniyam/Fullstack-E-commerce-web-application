import { Link } from "react-router-dom";
import success from "../../../assets/images/icons/success.gif"
import Logo from "../../../components/logo/logo";

function RegistrationSuccessful() {
    return(

        <main className='order-success'>
            <div className='order-success-box'>
                <Logo />
                <img src={success} size='20px'/>
                <h4 style={{ textAlign: "center", color: "green" }}>
                    Congratulations, Your account has been successfully created!
                </h4>
                <Link to='/auth/login'><button>Login now</button></Link>
            </div>
        </main>
    )
}

export default RegistrationSuccessful;