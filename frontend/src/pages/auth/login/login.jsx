import "../auth.css"
import {useForm} from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import Logo from "../../../components/logo/logo";
import AuthService from "../../../api-service/auth.service";
import { useContext, useEffect } from "react";
import { AuthContext } from "../../../contexts/auth.context";

function Login() {

    const {register, handleSubmit,formState} = useForm();
    const { login, isLoading, error } = AuthService();
    const {user, toggleUser} = useContext(AuthContext)
    const navigate = useNavigate();

    useEffect(()=> {
        if (user && user.token) {
            navigate("/");
        }
    }, [user])

    console.log(user)

    const onSubmit = async (data) => {
        await login(data.email, data.password)
        toggleUser()
    }
    return (
        <main className='auth-container'>
            <div className='auth-wrapper'>
                <form onSubmit={handleSubmit(onSubmit)}>
                    <Logo />
                    <h2>Login to continue...</h2>
                    {
                        error && <p>{error}</p>
                    }

                    <div className='input-box'>
                        <label>
                            Email
                            <input
                                type='text'
                                placeholder="john@gmail.com"
                                {...register('email', {
                                    required: "Email is required!",
                                    pattern: { value: /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/g, message: "Invalid email address!" }
                                })}
                            />
                        </label>

                        {formState.errors.email && <small>{formState.errors.email.message}</small>}
                    </div>

                    <div className='input-box'>
                        <label>
                            Password
                            <input
                                type='password'
                                placeholder="********"
                                {
                                    ...register('password', {
                                        required: 'Password is required!'
                                })
                                }
                            />
                        </label>
                        {formState.errors.password && <small>{formState.errors.password.message}</small>}
                    </div>

                    <div className='input-box'>
                        <input type='submit' value={isLoading ? 'Logging in' : 'Login'} className={isLoading ? 'loading' : ''} />
                    </div>
                    <div className='msg'>New member? <Link to='/auth/register' className='auth-link'>Register Here</Link></div>
                </form>
            </div>
        </main>
    )
}

export default Login;