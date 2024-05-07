import { Link } from "react-router-dom";
import Logo from "../../../components/logo/logo";

function NotFound() {
    return (
        <main className='auth-container'>
            <div className='auth-wrapper' style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '10px', gap: '20px', height: '100vh'}}>
                <Logo />
                <h1>404 - Not Found!</h1>
                <h3 style={{ textAlign: 'center' }}>Sorry! the page your are looking for was either not found or does not exist. Try refreshing the page or click the button below to go back to the home page.</h3>
                
                <Link to="/"><button>Go to home</button></Link>
            </div>
        </main>
    )
}

export default NotFound;