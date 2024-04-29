import { Link } from "react-router-dom";
import success from "../../../assets/images/icons/success.gif"
import Logo from "../../../components/logo/logo";

function RegistrationSuccessful() {
    return(
        <main className='auth-container'>
            <div className='auth-wrapper'>
                <form style={{alignItems:'center'}}>
                    <Logo/>
                    <img src={success} size='20px'/>
                    <h4 style={{textAlign:"center", color: "green"}}>Congratulations, Your account has been successfully created!</h4>
                    <div className="input-box">
                        <Link to='/auth/login'><button className="button button-fill">Login now</button></Link>
                    </div>
                </form>
            </div>
        </main>
    )
}

export default RegistrationSuccessful;